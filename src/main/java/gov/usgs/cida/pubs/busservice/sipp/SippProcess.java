package gov.usgs.cida.pubs.busservice.sipp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.client.RestTemplate;

import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.busservice.intfc.ISippProcess;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.PublicationSeriesDao;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.ipds.IpdsPubTypeConv;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.sipp.InformationProduct;
import gov.usgs.cida.pubs.utility.PubsUtils;

@Service
public class SippProcess implements ISippProcess {
	private static final Logger LOG = LoggerFactory.getLogger(DisseminationListService.class);

	private final ConfigurationService configurationService;
	private final RestTemplate restTemplate;
	private final IMpPublicationBusService pubBusService;
	private final PlatformTransactionManager txnMgr;

	public SippProcess(
			final ConfigurationService configurationService,
			final RestTemplate restTemplate,
			final IMpPublicationBusService pubBusService,
			final PlatformTransactionManager transactionManager
			) {
		this.configurationService = configurationService;
		this.restTemplate = restTemplate;
		this.pubBusService = pubBusService;
		this.txnMgr = transactionManager;
	}

	public ProcessSummary processPublication(ProcessType inProcessType, String ipNumber) {
		ProcessSummary rtn = new ProcessSummary();
		StringBuilder processingDetails = new StringBuilder(ipNumber + ":");
		TransactionDefinition txDef = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = txnMgr.getTransaction(txDef);
		String indexId = null;

		try {
			InformationProduct informationProduct = getIpdsProduct(ipNumber);
			IpdsPubTypeConv ipdsPubTypeConv = IpdsPubTypeConv.getDao().getByIpdsValue(informationProduct.getProductType());
			PublicationSeries seriesTitle = getSeriesTitle(ipdsPubTypeConv.getPublicationSubtype(), informationProduct.getUsgsSeriesType());
			boolean isUsgsNumberedSeries = PubsUtils.isUsgsNumberedSeries(ipdsPubTypeConv.getPublicationSubtype());
			if (isUsgsNumberedSeries) {
				indexId = pubBusService.getUsgsNumberedSeriesIndexId(seriesTitle, informationProduct.getUsgsSeriesNumber(), informationProduct.getUsgsSeriesLetter(), null);
			}
			MpPublication mpPublication = getFromMp(informationProduct.getIpNumber(), indexId);
	
			if (okToProcess(inProcessType, informationProduct, isUsgsNumberedSeries, mpPublication, indexId)) {
//				processPublication(inProcessType, ipdsPub, informationProduct, mpPublication);
			} else {
				processingDetails.append("\n\t" + "IPDS record not processed (" + inProcessType + ")-");
				if (null != ipdsPubTypeConv.getPublicationType()) {
					processingDetails.append(" Publication Type: " + ipdsPubTypeConv.getPublicationType().getText());
				}
				if (null != ipdsPubTypeConv.getPublicationSubtype()) {
					processingDetails.append(" PublicationSubtype: " + ipdsPubTypeConv.getPublicationSubtype().getText());
				}
				if (null != seriesTitle) {
					processingDetails.append(" Series: " + seriesTitle.getText());
				}
				processingDetails.append(" Process State: " + informationProduct.getTask() + " DOI: "
						+ informationProduct.getDigitalObjectIdentifier());
			}
			if (!txStatus.isRollbackOnly()) {
				txnMgr.commit(txStatus);
			} else {
				throw new RuntimeException("Transaction set to rollbackOnly!!");
			}
		} catch (Exception e) {
			String msg = "ERROR: Trouble processing pub: " + ipNumber +  " - ";
			LOG.info(msg, e);
			processingDetails.append("\n\t").append(msg).append(e.getMessage());
			rtn.setErrors(rtn.getErrors() + 1);
			txnMgr.rollback(txStatus);
		}

		rtn.setProcessingDetails(processingDetails.append("\n\n").toString());
		return rtn;
	}

	protected InformationProduct getIpdsProduct(String ipNumber) {
		InformationProduct informationProduct = null;

		String infoProductUrl = configurationService.getInfoProductUrl() + ipNumber;
		ResponseEntity<InformationProduct> response = restTemplate.getForEntity(infoProductUrl, InformationProduct.class);
		if (response.getStatusCode() == HttpStatus.OK
				&& response.hasBody()) {
			informationProduct = response.getBody();
		} else {
			LOG.info("Error getting InfoProduct:{} : {}", ipNumber, response.getStatusCodeValue());
		}
		return informationProduct;
	}

	protected PublicationSeries getSeriesTitle(PublicationSubtype pubSubtype, String text) {
		if (null != pubSubtype && null != pubSubtype.getId() && StringUtils.isNotBlank(text)) {
			//Only hit the DB if both fields have values - otherwise the db call will return incorrect results.
			Map<String, Object> filters = new HashMap<>();
			filters.put(PublicationSeriesDao.SUBTYPE_SEARCH, pubSubtype.getId());
			filters.put(PublicationSeriesDao.TEXT_SEARCH, text);
			List<PublicationSeries> pubSeries = PublicationSeries.getDao().getByMap(filters);
			if (!pubSeries.isEmpty()) {
				//We should really only get one, so just take the first...
				return pubSeries.get(0);
			}
		}
		return null;
	}

	protected MpPublication getFromMp(String ipNumber, String indexId) {
		//IPDS_ID and index ID are alternate keys, so there should only be 0 or 1 in each table.
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(PublicationDao.IPDS_ID, new String[]{ipNumber});
		List<MpPublication> mpPublications = pubBusService.getObjects(filters);
		MpPublication mpPublication = null == mpPublications ? null : mpPublications.isEmpty() ? null : mpPublications.get(0);

		if (null == mpPublication && null != indexId) {
			//It's a USGS Numbered Series, to try again by index ID if not found by IPDS ID
			filters.clear();
			filters.put(PublicationDao.INDEX_ID, new String[]{indexId});
			mpPublications = pubBusService.getObjects(filters);
			mpPublication = null == mpPublications ? null : mpPublications.isEmpty() ? null : mpPublications.get(0);
		}
		return mpPublication;
	}

	protected PwPublication getFromPw(String ipNumber, String indexId) {
		PwPublication pwPublication = PwPublication.getDao().getByIpdsId(ipNumber);
		if (null == pwPublication && null != indexId) {
			//It's a USGS Numbered Series, to try again by index ID if not found by IPDS ID
			pwPublication = PwPublication.getDao().getByIndexId(indexId);
		}
		return pwPublication;
	}

	protected boolean okToProcess(ProcessType inProcessType, InformationProduct informationProduct, boolean isUsgsNumberedSeries, MpPublication mpPublication, String indexId) {
		if (null != inProcessType && null != informationProduct && null != informationProduct.getProductType()) {
			switch (inProcessType) {
			case DISSEMINATION:
				return okToProcessDissemination(informationProduct, isUsgsNumberedSeries, mpPublication, indexId);
			case SPN_PRODUCTION:
				return okToProcessSpnProduction(informationProduct, isUsgsNumberedSeries);
			default:
				break;
			}
		}
		return false;
	}

	protected boolean okToProcessDissemination(InformationProduct informationProduct, boolean isUsgsNumberedSeries, MpPublication mpPublication, String indexId) {
		boolean rtn = false;
		if (null == informationProduct) {
			//Do not proceed if the new data is null
		} else if (null != getFromPw(informationProduct.getIpNumber(), indexId)) {
			//Do not proceed if the pub has been published
		} else if (isUsgsNumberedSeries
				&& null == informationProduct.getUsgsSeriesLetter()) {
			//Do not process USGS numbered series without an actual series.
		} else if (null == mpPublication) {
			//OK to process at this point if we have no record of the pub
			rtn = true;
		} else if (null == mpPublication.getIpdsReviewProcessState()
				|| ProcessType.SPN_PRODUCTION.getIpdsValue().contentEquals(mpPublication.getIpdsReviewProcessState())) {
			//It is ok to process a publication already in MyPubs if has no review state or is in the SPN Production state.
			rtn = true;
		}
		return rtn;
	}

	protected boolean okToProcessSpnProduction(InformationProduct informationProduct, boolean isUsgsNumberedSeries) {
		boolean rtn = false;
		if (null != informationProduct) {
			if (StringUtils.isNotBlank(informationProduct.getDigitalObjectIdentifier())) {
				//Skip if we have already assigned a DOI (shouldn't happen as we are querying for null DOI publications)
			} else if (null == informationProduct.getTask() || !ProcessType.SPN_PRODUCTION.getIpdsValue().contentEquals(informationProduct.getTask())) {
				//Skip if not in SPN Production (shouldn't happen as we are querying SPN Production only)
			} else if (isUsgsNumberedSeries) {
				rtn = true;
			}
		}
		return rtn;
	}

}
