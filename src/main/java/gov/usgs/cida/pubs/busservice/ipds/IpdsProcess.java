package gov.usgs.cida.pubs.busservice.ipds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.busservice.intfc.IIpdsProcess;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;
import gov.usgs.cida.pubs.domain.ipds.PublicationMap;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.utility.PubsUtilities;

@Service
public class IpdsProcess implements IIpdsProcess {

	private static final Logger LOG = LoggerFactory.getLogger(IpdsProcess.class);
	private static ThreadLocal<StringBuilder> stringBuilder = new ThreadLocal<>();
	private static ThreadLocal<Integer> additions = new ThreadLocal<>();
	private static ThreadLocal<Integer> errors = new ThreadLocal<>();
	private static ThreadLocal<String> context = new ThreadLocal<>();

	private final ICrossRefBusService crossRefBusService;
	private final IpdsBinding binder;
	private final IpdsWsRequester requester;
	private final IMpPublicationBusService pubBusService;

	protected PlatformTransactionManager txnMgr;


	@Autowired
	public IpdsProcess(final ICrossRefBusService crossRefBusService,
			final IpdsBinding binder,
			final IpdsWsRequester requester,
			final IMpPublicationBusService pubBusService,
			final PlatformTransactionManager transactionManager) {
		this.crossRefBusService = crossRefBusService;
		this.binder = binder;
		this.requester = requester;
		this.pubBusService = pubBusService;
		this.txnMgr = transactionManager;
	}

	public String processLog(final ProcessType inProcessType, final int logId, String context) {
		setStringBuilder(new StringBuilder());
		setAdditions(0);
		setErrors(0);
		setContext(context);

		List<Map<String, Object>> ipdsPubs = IpdsMessageLog.getDao().getFromIpds(logId);

		for (Map<String, Object> ipdsPub : ipdsPubs) {
			processIpdsPublication(inProcessType, ipdsPub);
		}

		String counts = "Summary:\n\tTotal Entries: " + ipdsPubs.size() + "\n\tPublications Added: " + getAdditions() + "\n\tErrors Encountered: " + getErrors() + "\n\n";

		getStringBuilder().insert(0, counts);

		return getStringBuilder().toString();
	}

	protected void processIpdsPublication(final ProcessType inProcessType, final Map<String, Object> ipdsPub) {
		getStringBuilder().append(ipdsPub.get(IpdsMessageLog.IPNUMBER) + ":");
		TransactionDefinition txDef = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = txnMgr.getTransaction(txDef);

		try {
			MpPublication newMpPub = binder.bindPublication(ipdsPub, getContext());

			MpPublication existingMpPub = getFromMp(newMpPub);

			if (okToProcess(inProcessType, newMpPub, existingMpPub)) {
				processPublication(inProcessType, ipdsPub, newMpPub, existingMpPub);
			} else {
				getStringBuilder().append("\n\t" + "IPDS record not processed (" + inProcessType + ")-");
				if (null != newMpPub.getPublicationType()) {
					getStringBuilder().append(" Publication Type: " + newMpPub.getPublicationType().getText());
				}
				if (null != newMpPub.getPublicationSubtype()) {
					getStringBuilder().append(" PublicationSubtype: " + newMpPub.getPublicationSubtype().getText());
				}
				if (null != newMpPub.getSeriesTitle()) {
					getStringBuilder().append(" Series: " + newMpPub.getSeriesTitle().getText());
				}
				getStringBuilder().append(" Process State: " + newMpPub.getIpdsReviewProcessState() + " DOI: " + newMpPub.getDoi());
			}
			if (!txStatus.isRollbackOnly()) {
				txnMgr.commit(txStatus);
			} else {
				throw new RuntimeException("Transaction set to rollbackOnly!!");
			}
		} catch (Exception e) {
			String msg = "ERROR: Trouble processing pub: " + ipdsPub.get(IpdsMessageLog.IPNUMBER) +  " - ";
			LOG.info(msg, e);
			getStringBuilder().append("\n\t").append(msg).append(e.getMessage());
			setErrors(getErrors() + 1);
			txnMgr.rollback(txStatus);
		}

		getStringBuilder().append("\n\n");
	}

	protected MpPublication getFromMp(MpPublication pub) {
		//IPDS_ID and index ID are alternate keys, so there should only be 0 or 1 in each table.
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(PublicationDao.IPDS_ID, new String[]{pub.getIpdsId()});
		List<MpPublication> existingMpPubs = pubBusService.getObjects(filters);
		MpPublication existingMpPub = null == existingMpPubs ? null : existingMpPubs.isEmpty() ? null : existingMpPubs.get(0);
		if (null == existingMpPub && PubsUtilities.isUsgsNumberedSeries(pub.getPublicationSubtype())) {
			//It's a USGS Numbered Series, to try again by index ID if not found by IPDS ID
			filters.clear();
			filters.put(PublicationDao.INDEX_ID, new String[]{pubBusService.getUsgsNumberedSeriesIndexId(pub)});
			existingMpPubs = pubBusService.getObjects(filters);
			existingMpPub = null == existingMpPubs ? null : existingMpPubs.isEmpty() ? null : existingMpPubs.get(0);
		}
		return existingMpPub;
	}

	protected boolean okToProcess(final ProcessType inProcessType, final MpPublication newMpPub, final MpPublication existingMpPub) {
		if (null != inProcessType && null != newMpPub && null != newMpPub.getPublicationType()) {
			switch (inProcessType) {
			case DISSEMINATION:
				return okToProcessDissemination(newMpPub, existingMpPub);
			case SPN_PRODUCTION:
				return okToProcessSpnProduction(newMpPub);
			default:
				break;
			}
		}
		return false;
	}

	protected boolean okToProcessDissemination(final MpPublication newMpPub, final MpPublication existingMpPub) {
		boolean rtn = false;
		if (null == newMpPub) {
			//Do not proceed if the new data is null
		} else if (null != getFromPw(newMpPub)) {
			//Do not proceed if the pub has been published
		} else if (PubsUtilities.isUsgsNumberedSeries(newMpPub.getPublicationSubtype())
				&& null == newMpPub.getSeriesTitle()) {
			//Do not process USGS numbered series without an actual series.
		} else if (null == existingMpPub) {
			//OK to process at this point if we have no record of the pub
			rtn = true;
		} else if (null == existingMpPub.getIpdsReviewProcessState()
				|| ProcessType.SPN_PRODUCTION.getIpdsValue().contentEquals(existingMpPub.getIpdsReviewProcessState())) {
			//It is ok to process a publication already in MyPubs if has no review state or is in the SPN Production state.
			rtn = true;
		}
		return rtn;
	}

	protected boolean okToProcessSpnProduction(final MpPublication newMpPub) {
		boolean rtn = false;
		if (null != newMpPub) {
			if (StringUtils.isNotBlank(newMpPub.getDoi())) {
				//Skip if we have already assigned a DOI (shouldn't happen as we are querying for null DOI publications)
			} else if (null == newMpPub.getIpdsReviewProcessState() || !ProcessType.SPN_PRODUCTION.getIpdsValue().contentEquals(newMpPub.getIpdsReviewProcessState())) {
				//Skip if not in SPN Production (shouldn't happen as we are querying SPN Production only)
			} else if (PubsUtilities.isUsgsNumberedSeries(newMpPub.getPublicationSubtype())) {
				rtn = true;
			}
		}
		return rtn;
	}

	protected PwPublication getFromPw(MpPublication newMpPub) {
		if (null != newMpPub) {
			PwPublication existingPwPub = PwPublication.getDao().getByIpdsId(newMpPub.getIpdsId());
			if (null == existingPwPub && PubsUtilities.isUsgsNumberedSeries(newMpPub.getPublicationSubtype())) {
				//It's a USGS Numbered Series, to try again by index ID if not found by IPDS ID
				existingPwPub = PwPublication.getDao().getByIndexId(pubBusService.getUsgsNumberedSeriesIndexId(newMpPub));
			}
			return existingPwPub;
		} else {
			return null;
		}
	}

	protected void processPublication(final ProcessType processType, final Map<String, Object> ipdsPub, final MpPublication newMpPub, final MpPublication existingMpPub) {
		newMpPub.setIpdsReviewProcessState(processType.getIpdsValue());
		//We only keep the prodID from the original MP record. The delete is to make sure we kill all child objects.
		if (null != existingMpPub) {
			newMpPub.setId(existingMpPub.getId());
			pubBusService.deleteObject(existingMpPub.getId());
		}

		newMpPub.setContributors(getContributors(newMpPub));

		newMpPub.setCostCenters(getCostCenters(ipdsPub));

		newMpPub.setNotes(getNotes(newMpPub));

		newMpPub.setLinks(binder.bindPublishedURL(ipdsPub));

		MpPublication rtnPub = pubBusService.createObject(newMpPub);

		if (rtnPub.isValid()) {
			setAdditions(getAdditions() + 1);
			getStringBuilder().append("\n\tAdded to MyPubs as ProdId: " + rtnPub.getId());
			switch (processType) {
			case SPN_PRODUCTION:
				updateIpdsWithDoi(rtnPub);
				break;
			case DISSEMINATION:
				if ((PubsUtilities.isUsgsNumberedSeries(rtnPub.getPublicationSubtype())
						|| PubsUtilities.isUsgsUnnumberedSeries(rtnPub.getPublicationSubtype()))
						&& (null != rtnPub.getDoi() && 0 < rtnPub.getDoi().length())) {
					crossRefBusService.submitCrossRef(rtnPub);
				}
				break;
			default:
				break;
			}
		} else {
			setErrors(getErrors() + 1);
			getStringBuilder().append("\nERROR: Failed validation.\n\t" + rtnPub.getValidationErrors().toString().replaceAll("\n", "\n\t"));
		}
	}

	protected Collection<PublicationContributor<?>> getContributors(MpPublication newMpPub) {
		// get contributors from web service
		Collection<PublicationContributor<?>> contributors = null;
		String contributorXml = requester.getContributors(newMpPub.getIpdsId(), getContext());
		try {
			contributors = binder.bindContributors(contributorXml, getContext());
		} catch (Exception e) {
			String msg = "Trouble getting authors/editors: ";
			LOG.info(msg, e);
			getStringBuilder().append("\n\t").append(msg).append(e.getMessage());
			setErrors(getErrors() + 1);
		}
		return contributors;
	}

	protected Collection<PublicationCostCenter<?>> getCostCenters(Map<String, Object> ipdsPub) {
		//get contributingOffice from web service
		Collection<PublicationCostCenter<?>> costCenters = null;
		try {
			Affiliation<?> costCenter = binder.getOrCreateCostCenter(ipdsPub);
			if (null != costCenter) {
				if (costCenter.isValid()) {
					PublicationCostCenter<?> pubCostCenter = new MpPublicationCostCenter();
					pubCostCenter.setCostCenter((CostCenter) costCenter);
					costCenters = new ArrayList<>();
					costCenters.add(pubCostCenter);
				} else {
					throw new RuntimeException("Cost Center invalid: " + costCenter.getValidationErrors());
				}
			}
		} catch (Exception e) {
			String msg = "Trouble getting cost center: ";
			LOG.info(msg, e);
			getStringBuilder().append("\n\t").append(msg).append(e.getMessage());
			setErrors(getErrors() + 1);
		}
		return costCenters;
	}

	protected String getNotes(MpPublication newMpPub) {
		StringBuilder notes = new StringBuilder(null == newMpPub.getNotes() ? "" : newMpPub.getNotes());
		//get notes from web service
		String notesXml = requester.getNotes(newMpPub.getIpdsId(), getContext());
		Set<String> notesTags = new HashSet<String>();
		notesTags.add("NoteComment");
		try {
			PublicationMap pm = binder.bindNotes(notesXml, notesTags);
			if (null != pm.get("NoteComment") && 0 < pm.get("NoteComment").length()) {
				if (0 < notes.length()) {
					notes.append("\n\t");
				}
				notes.append(pm.get("NoteComment"));
			}
		} catch (Exception e) {
			String msg = "Trouble getting comment: ";
			LOG.info(msg, e);
			getStringBuilder().append("\n\t").append(msg).append(e.getMessage());
			setErrors(getErrors() + 1);
		}
		return notes.toString();
	}

	protected void updateIpdsWithDoi(final MpPublication inPub) {
		String result = requester.updateIpdsDoi(inPub, getContext());
		if (null == result || result.contains("ERROR")) {
			setErrors(getErrors() + 1);
		}
		getStringBuilder().append("\n\t").append(result);
	}

	public static StringBuilder getStringBuilder() {
		return stringBuilder.get();
	}

	public static void setStringBuilder(StringBuilder inStringBuilder) {
		stringBuilder.set(inStringBuilder);
	}

	public static Integer getAdditions() {
		return additions.get();
	}

	public static void setAdditions(Integer inAdditions) {
		additions.set(inAdditions);
	}

	public static Integer getErrors() {
		return errors.get();
	}

	public static void setErrors(Integer inErrors) {
		errors.set(inErrors);
	}

	public static String getContext() {
		return context.get();
	}

	public static void setContext(String inContext) {
		context.set(inContext);
	}

}
