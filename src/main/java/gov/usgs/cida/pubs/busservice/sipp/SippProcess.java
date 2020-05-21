package gov.usgs.cida.pubs.busservice.sipp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.busservice.ext.ExtPublicationService;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.busservice.intfc.ISippProcess;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.PublicationSeriesDao;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.sipp.InformationProduct;
import gov.usgs.cida.pubs.domain.sipp.IpdsPubTypeConv;
import gov.usgs.cida.pubs.domain.sipp.RollbackException;
import gov.usgs.cida.pubs.utility.PubsUtils;
import gov.usgs.cida.pubs.validation.ValidatorResult;

@Service
public class SippProcess implements ISippProcess {
	private static final Logger LOG = LoggerFactory.getLogger(SippProcess.class);

	private final ExtPublicationService extPublicationBusService;
	private final IMpPublicationBusService pubBusService;
	private final SippConversionService sippConversionService;
	protected PlatformTransactionManager txnMgr;

	@Autowired
	public SippProcess(
			final ExtPublicationService extPublicationBusService,
			final IMpPublicationBusService pubBusService,
			final SippConversionService sippConversionService,
			final PlatformTransactionManager transactionManager
			) {
		this.extPublicationBusService = extPublicationBusService;
		this.pubBusService = pubBusService;
		this.sippConversionService = sippConversionService;
		this.txnMgr = transactionManager;
	}

	public MpPublication processInformationProduct(ProcessType processType, String ipNumber) {
		TransactionDefinition txDef = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = txnMgr.getTransaction(txDef);
		MpPublication rtn = new MpPublication();

		try {
			rtn = processInNewTransaction(processType, ipNumber);
			if (rtn.isValid() && !txStatus.isRollbackOnly()) {
				txnMgr.commit(txStatus);
			} else {
				throw new RollbackException("Transaction set to rollbackOnly!!" + ipNumber, null);
			}
		} catch (RollbackException e) {
			LOG.debug(e.getLocalizedMessage());
			txnMgr.rollback(txStatus);
		} catch (Exception e) {
			rtn.addValidatorResult(new ValidatorResult("MpPublication", e.getLocalizedMessage(), SeverityLevel.FATAL, ipNumber));
			LOG.info(ipNumber + ": " + e.getLocalizedMessage(), e);
			txnMgr.rollback(txStatus);
		}

		return rtn;
	}

	protected MpPublication processInNewTransaction(ProcessType processType, String ipNumber) {
		MpPublication rtn = null;
		MpPublication mpPublication = null;
		InformationProduct informationProduct = getInformationProduct(ipNumber);

		if(informationProduct != null) {
			mpPublication = getMpPublication(informationProduct);
		}

		ValidatorResult processDetermination = okToProcess(processType, informationProduct, mpPublication);
		if (null == processDetermination) {
				Integer prodId = null == mpPublication ? null : mpPublication.getId();
				rtn = processPublication(processType, informationProduct, prodId);
		} else {
			if(mpPublication == null) {
				rtn = new MpPublication();
			} else {
				rtn = mpPublication;
			}

			rtn.addValidatorResult(processDetermination);
		}
		return rtn;
	}

	protected InformationProduct getInformationProduct(String ipNumber) {
		InformationProduct informationProduct = InformationProduct.getDao().getInformationProduct(ipNumber);

		if (null != informationProduct) {
			IpdsPubTypeConv idsPubTypeConv = IpdsPubTypeConv.getDao().getByIpdsValue(informationProduct.getProductType());
			if (null != idsPubTypeConv) {
				informationProduct.setPublicationType(idsPubTypeConv.getPublicationType());
				informationProduct.setPublicationSubtype(idsPubTypeConv.getPublicationSubtype());

				if (StringUtils.isNotBlank(informationProduct.getJournalTitle())
						&& PubsUtils.isPublicationTypeArticle(idsPubTypeConv.getPublicationType())
						&& null != idsPubTypeConv.getPublicationSubtype()) {
					informationProduct.setUsgsSeriesTitle(getSeriesTitle(idsPubTypeConv.getPublicationSubtype(),
							informationProduct.getJournalTitle()));
				} else {
					informationProduct.setUsgsSeriesTitle(getSeriesTitle(idsPubTypeConv.getPublicationSubtype(),
							informationProduct.getUsgsSeriesType()));
				}

				informationProduct.setUsgsNumberedSeries(
					PubsUtils.isUsgsNumberedSeries(idsPubTypeConv.getPublicationSubtype()));
			} else {
				LOG.debug("IpdsPubTypeConv not found for: {} {}", informationProduct.getIpNumber(), informationProduct.getProductType());
			}

			if (informationProduct.isUsgsNumberedSeries()) {
				informationProduct.setIndexId(
						pubBusService.getUsgsNumberedSeriesIndexId(informationProduct.getUsgsSeriesTitle(),
								informationProduct.getUsgsSeriesNumber(), informationProduct.getUsgsSeriesLetter(), null));
			}
		}
		return informationProduct;
	}

	protected PublicationSeries getSeriesTitle(PublicationSubtype pubSubtype, String text) {
		PublicationSeries ret = null;
		if (null != pubSubtype && null != pubSubtype.getId() && StringUtils.isNotBlank(text)) {
			//Only hit the DB if both fields have values - otherwise the db call will return incorrect results.
			Map<String, Object> filters = new HashMap<>();
			filters.put(PublicationSeriesDao.SUBTYPE_SEARCH, pubSubtype.getId());
			filters.put(PublicationSeriesDao.TEXT_SEARCH, text);
			List<PublicationSeries> pubSeries = PublicationSeries.getDao().getByMap(filters);
			if (!pubSeries.isEmpty()) {
				//We should really only get one, so just take the first...
				ret = pubSeries.get(0);
			} else {
				PublicationSeries publicationSeries = new PublicationSeries();
				publicationSeries.setText(text);
				publicationSeries.setPublicationSubtype(pubSubtype);
				publicationSeries.setActive(true);
				int id = PublicationSeries.getDao().add(publicationSeries);
				ret = PublicationSeries.getDao().getById(id);
			}
		}
		return ret;
	}

	protected MpPublication getMpPublication(InformationProduct informationProduct) {
		//IPDS_ID and index ID are alternate keys, so there should only be 0 or 1 in each table.
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(PublicationDao.IPDS_ID, new String[]{informationProduct.getIpNumber()});
		List<MpPublication> mpPublications = pubBusService.getObjects(filters);
		MpPublication mpPublication = null == mpPublications ? null : mpPublications.isEmpty() ? null : mpPublications.get(0);

		if (null == mpPublication && null != informationProduct.getIndexId()) {
			//It's a USGS Numbered Series, to try again by index ID if not found by IPDS ID
			filters.clear();
			filters.put(PublicationDao.INDEX_ID, new String[]{informationProduct.getIndexId()});
			mpPublications = pubBusService.getObjects(filters);
			mpPublication = null == mpPublications ? null : mpPublications.isEmpty() ? null : mpPublications.get(0);
		}
		return mpPublication;
	}

	protected PwPublication getPwPublication(InformationProduct informationProduct) {
		PwPublication pwPublication = PwPublication.getDao().getByIpdsId(informationProduct.getIpNumber());
		if (null == pwPublication && null != informationProduct.getIndexId()) {
			//It's a USGS Numbered Series, to try again by index ID if not found by IPDS ID
			pwPublication = PwPublication.getDao().getByIndexId(informationProduct.getIndexId());
		}
		return pwPublication;
	}

	protected ValidatorResult okToProcess(ProcessType inProcessType, InformationProduct informationProduct, MpPublication mpPublication) {
		if (null != inProcessType && null != informationProduct && null != informationProduct.getPublicationType()) {
			switch (inProcessType) {
			case DISSEMINATION:
				return okToProcessDissemination(informationProduct, mpPublication);
			case SPN_PRODUCTION:
				return okToProcessSpnProduction(informationProduct);
			default:
				break;
			}
		}
		return new ValidatorResult("MpPublication",
				String.format("ProcessType (%s) or PublicationType (%s) invalid (ProductType: %s).",
						inProcessType,
						informationProduct == null ? "null" : informationProduct.getPublicationType(),
						informationProduct == null ? "null" : informationProduct.getProductType()),
				SeverityLevel.SKIPPED, informationProduct == null ? "null" : informationProduct.getIpNumber());
	}

	protected ValidatorResult okToProcessDissemination(InformationProduct informationProduct, MpPublication mpPublication) {
		ValidatorResult rtn = null;
		if (null == informationProduct) {
			//Do not proceed if the new data is null
			rtn = buildNotOkDetails(ProcessType.DISSEMINATION, informationProduct, "Not okToProcessDissemination - new data is null.");
		} else if (null != getPwPublication(informationProduct)) {
			//Do not proceed if the pub has been published
			rtn = buildNotOkDetails(ProcessType.DISSEMINATION, informationProduct, "Not okToProcessDissemination - Pub is already published.");
		} else if (informationProduct.isUsgsNumberedSeries()
				&& null == informationProduct.getUsgsSeriesTitle()) {
			//Do not process USGS numbered series without an actual series.
			rtn = buildNotOkDetails(ProcessType.DISSEMINATION, informationProduct, "Not okToProcessDissemination - Pub is a USGS Nmber Series, but Series Title is empty.");
		} else if (null == mpPublication) {
			//OK to process at this point if we have no record of the pub
			LOG.debug("Is okToProcessDissemination - Pub {} not in MP", informationProduct.getIpNumber());
			rtn = null;
		} else if (StringUtils.isEmpty(mpPublication.getIpdsReviewProcessState())
				|| ProcessType.SPN_PRODUCTION.getIpdsValue().contentEquals(mpPublication.getIpdsReviewProcessState())) {
			//It is ok to process a publication already in MyPubs if has no review state or is in the SPN Production state.
			LOG.debug("Is okToProcessDissemination - Pub {} in SPN_PRODUCTION", informationProduct.getIpNumber());
			rtn = null;
		} else {
			rtn = buildNotOkDetails(ProcessType.SPN_PRODUCTION, informationProduct, "Not okToProcessDissemination - Already in Manager.");
		}
		return rtn;
	}

	protected ValidatorResult okToProcessSpnProduction(InformationProduct informationProduct) {
		ValidatorResult rtn = null;
		if (null == informationProduct) {
			//Do not proceed if the new data is null
			rtn = buildNotOkDetails(ProcessType.SPN_PRODUCTION, informationProduct, "Not okToProcessSpnProduction - new data is null.");
		} else if (StringUtils.isNotBlank(informationProduct.getDigitalObjectIdentifier())) {
			//Skip if we have already assigned a DOI (shouldn't happen as we are querying for null DOI publications)
			rtn = buildNotOkDetails(ProcessType.SPN_PRODUCTION, informationProduct, "Not okToProcessSpnProduction - Pub already has a DOI assigned.");
		} else if (null == informationProduct.getTask() || !ProcessType.SPN_PRODUCTION.getIpdsValue().contentEquals(informationProduct.getTask())) {
			//Skip if not in SPN Production (shouldn't happen as we are querying SPN Production only)
			rtn = buildNotOkDetails(ProcessType.SPN_PRODUCTION, informationProduct,
					String.format("Not okToProcessSpnProduction - Pub is in %s status, rather than SPN_PRODUCTION.", informationProduct.getTask()));
		} else if (informationProduct.isUsgsNumberedSeries()) {
			LOG.debug("Is okToProcessSpnProduction - isUsgsNumberedSeries", informationProduct.getIpNumber());
			rtn = null;
		} else {
			rtn = buildNotOkDetails(ProcessType.SPN_PRODUCTION, informationProduct, "Not okToProcessSpnProduction - Not a USGS Number Series.");
		}
		return rtn;
	}

	protected MpPublication processPublication(ProcessType processType, InformationProduct informationProduct, Integer prodId) {
		//TODO??		newMpPub.setIpdsReviewProcessState(processType.getIpdsValue());
		//We only keep the prodID from the original MP record. The delete is to make sure we kill all child objects.
		if (null != prodId) {
			pubBusService.deleteObject(prodId);
		}

		MpPublication mpPublication = sippConversionService.buildMpPublication(informationProduct, prodId);
		mpPublication.setSeriesTitle(informationProduct.getUsgsSeriesTitle());

		MpPublication rtnPub = null;
		try {
			rtnPub = extPublicationBusService.create(mpPublication);
		} catch (Exception e) {
			LOG.error("Error encountered in Sipp process while creating publication " + mpPublication.getId(), e);
			rtnPub = mpPublication;
			ValidatorResult validatorResult = new ValidatorResult("MpPublication", e.getLocalizedMessage(), SeverityLevel.FATAL, mpPublication.getIpdsId());
			rtnPub.addValidatorResult(validatorResult);
		}

		//TODO		if (rtnPub.isValid() && ProcessType.SPN_PRODUCTION.equals(processType)) {
		//TODO				updateIpdsWithDoi(rtnPub);
		//TODO		}
		return rtnPub;
	}

	protected ValidatorResult buildNotOkDetails(ProcessType processType, InformationProduct informationProduct, String msg) {
		String productType = informationProduct == null || informationProduct.getProductType() == null
				? "[Not Found]" : informationProduct.getProductType();

		StringBuilder notOkDetails = new StringBuilder(msg).append(" ProductType: ").append(productType);

		if (informationProduct != null) {
			if (null != informationProduct.getPublicationType()) {
				notOkDetails.append(" Publication Type: ").append(informationProduct.getPublicationType().getText());
			}
			if (null != informationProduct.getPublicationSubtype()) {
				notOkDetails.append(" PublicationSubtype: ").append(informationProduct.getPublicationSubtype().getText());
			}
			if (null != informationProduct.getUsgsSeriesTitle()) {
				notOkDetails.append(" Series: ").append(informationProduct.getUsgsSeriesTitle().getText());
			}
			notOkDetails.append(" Process State: ").append(informationProduct.getTask()).append(" DOI: ").append(informationProduct.getDigitalObjectIdentifier());
		}
		return new ValidatorResult("MpPublication", notOkDetails.toString(), SeverityLevel.SKIPPED, informationProduct == null ? null : informationProduct.getIpNumber());
	}
}
