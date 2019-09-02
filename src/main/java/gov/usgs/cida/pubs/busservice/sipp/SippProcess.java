package gov.usgs.cida.pubs.busservice.sipp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
import gov.usgs.cida.pubs.utility.PubsUtils;
import gov.usgs.cida.pubs.validation.ValidatorResult;

@Service
public class SippProcess implements ISippProcess {
	private static final Logger LOG = LoggerFactory.getLogger(SippProcess.class);

	private final ExtPublicationService extPublicationBusService;
	private final IMpPublicationBusService pubBusService;
	private final SippConversionService sippConversionService;

	@Autowired
	public SippProcess(
			final ExtPublicationService extPublicationBusService,
			final IMpPublicationBusService pubBusService,
			final SippConversionService sippConversionService
			) {
		this.extPublicationBusService = extPublicationBusService;
		this.pubBusService = pubBusService;
		this.sippConversionService = sippConversionService;
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public MpPublication processInformationProduct(ProcessType processType, String ipNumber) {
		MpPublication rtn = null;

		MpPublication mpPublication = null;
		InformationProduct informationProduct = getInformationProduct(ipNumber);

		if(informationProduct != null) {
			mpPublication = getMpPublication(informationProduct);
		}

		if (okToProcess(processType, informationProduct, mpPublication)) {
			Integer prodId = null == mpPublication ? null : mpPublication.getId();
			rtn = processPublication(processType, informationProduct, prodId);
		} else {
			String errMess = buildNotOkDetails(processType, informationProduct, ipNumber);
			ValidatorResult validatorResult = new ValidatorResult("MpPublication", errMess, SeverityLevel.FATAL, ipNumber);
			if(mpPublication == null) {
				rtn = new MpPublication();
			} else {
				rtn = mpPublication;
			}

			rtn.addValidatorResult(validatorResult);
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

				informationProduct.setUsgsSeriesTitle(
						getSeriesTitle(idsPubTypeConv.getPublicationSubtype(),
							informationProduct.getUsgsSeriesType()));

				informationProduct.setUsgsNumberedSeries(
					PubsUtils.isUsgsNumberedSeries(idsPubTypeConv.getPublicationSubtype()));
			} else {
				LOG.info("IpdsPubTypeConv not found for: {} {}", informationProduct.getIpNumber(), informationProduct.getProductType());
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

	protected boolean okToProcess(ProcessType inProcessType, InformationProduct informationProduct, MpPublication mpPublication) {
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
		return false;
	}

	protected boolean okToProcessDissemination(InformationProduct informationProduct, MpPublication mpPublication) {
		boolean rtn = false;
		if (null == informationProduct) {
			//Do not proceed if the new data is null
			LOG.info("Not okToProcessDissemination - new data is null");
		} else if (null != getPwPublication(informationProduct)) {
			//Do not proceed if the pub has been published
			LOG.info("Not okToProcessDissemination - Pub {} already published", informationProduct.getIpNumber());
		} else if (informationProduct.isUsgsNumberedSeries()
				&& null == informationProduct.getUsgsSeriesTitle()) {
			//Do not process USGS numbered series without an actual series.
			LOG.info("Not okToProcessDissemination - Pub {} is a USGS Nmber Series, but Series Title is empty", informationProduct.getIpNumber());
		} else if (null == mpPublication) {
			//OK to process at this point if we have no record of the pub
			LOG.info("okToProcessDissemination - Pub {} not in MP", informationProduct.getIpNumber());
			rtn = true;
		} else if (StringUtils.isEmpty(mpPublication.getIpdsReviewProcessState())
				|| ProcessType.SPN_PRODUCTION.getIpdsValue().contentEquals(mpPublication.getIpdsReviewProcessState())) {
			//It is ok to process a publication already in MyPubs if has no review state or is in the SPN Production state.
			LOG.info("okToProcessDissemination - Pub {} in SPN_PRODUCTION", informationProduct.getIpNumber());
			rtn = true;
		} else {
			LOG.info("Not okToProcessDissemination - did not hit either OK options {}", informationProduct.getIpNumber());
		}
		return rtn;
	}

	protected boolean okToProcessSpnProduction(InformationProduct informationProduct) {
		boolean rtn = false;
		if (null != informationProduct) {
			if (StringUtils.isNotBlank(informationProduct.getDigitalObjectIdentifier())) {
				//Skip if we have already assigned a DOI (shouldn't happen as we are querying for null DOI publications)
				LOG.info("Not okToProcessSpnProduction - Pub {} already has a DOI assigned", informationProduct.getIpNumber());
			} else if (null == informationProduct.getTask() || !ProcessType.SPN_PRODUCTION.getIpdsValue().contentEquals(informationProduct.getTask())) {
				//Skip if not in SPN Production (shouldn't happen as we are querying SPN Production only)
				LOG.info("Not okToProcessSpnProduction - Pub {} is in {} status, rather than SPN_PRODUCTION", informationProduct.getIpNumber(), informationProduct.getTask());
			} else if (informationProduct.isUsgsNumberedSeries()) {
				rtn = true;
			}
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

		MpPublication rtnPub = null;
		try {
			rtnPub = extPublicationBusService.create(mpPublication);
		} catch (Exception e) {
			rtnPub = mpPublication;
			ValidatorResult validatorResult = new ValidatorResult("MpPublication", e.getLocalizedMessage(), SeverityLevel.FATAL, mpPublication.getIpdsId());
			rtnPub.addValidatorResult(validatorResult);
		}

		//TODO		if (rtnPub.isValid() && ProcessType.SPN_PRODUCTION.equals(processType)) {
		//TODO				updateIpdsWithDoi(rtnPub);
		//TODO		}
		return rtnPub;
	}

	protected String buildNotOkDetails(ProcessType processType, InformationProduct informationProduct, String ipNumber) {
		String productType = informationProduct == null || informationProduct.getProductType() == null
				? "[Not Found]" : informationProduct.getProductType();

		String notProcessedMess = String.format("IPDS record for IPNumber '%s' not processed", ipNumber);

		StringBuilder notOkDetails = new StringBuilder("\n\t").append(notProcessedMess).append(" (\"").append(processType).append("\") -")
				.append(" ProductType: ").append(productType);

		if(informationProduct != null) {
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
		return notOkDetails.toString();
	}
}
