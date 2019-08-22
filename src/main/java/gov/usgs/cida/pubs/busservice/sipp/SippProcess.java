package gov.usgs.cida.pubs.busservice.sipp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
import gov.usgs.cida.pubs.domain.sipp.ProcessSummary;
import gov.usgs.cida.pubs.utility.PubsUtils;

@Service
public class SippProcess implements ISippProcess {

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
	public ProcessSummary processInformationProduct(ProcessType processType, String ipNumber) {
		ProcessSummary rtn = new ProcessSummary();
		StringBuilder processingDetails = new StringBuilder(ipNumber + ":");

		InformationProduct informationProduct = getInformationProduct(ipNumber);
		MpPublication mpPublication = getMpPublication(informationProduct);

		if (okToProcess(processType, informationProduct, mpPublication)) {
			Integer prodId = null == mpPublication ? null : mpPublication.getId();
			ProcessSummary publicationSummary = processPublication(processType, informationProduct, prodId);
			processingDetails.append(publicationSummary.getProcessingDetails());
			rtn.incrementAdditions(publicationSummary.getAdditions());
			rtn.incrementErrors(publicationSummary.getErrors());
		} else {
			processingDetails.append(buildNotOkDetails(processType, informationProduct));
		}

		rtn.setProcessingDetails(processingDetails.append("\n\n").toString());
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
			rtn = false;
		} else if (null != getPwPublication(informationProduct)) {
			//Do not proceed if the pub has been published
			rtn = false;
		} else if (informationProduct.isUsgsNumberedSeries()
				&& StringUtils.isEmpty(informationProduct.getUsgsSeriesLetter())) {
			//Do not process USGS numbered series without an actual series.
			rtn = false;
		} else if (null == mpPublication) {
			//OK to process at this point if we have no record of the pub
			rtn = true;
		} else if (StringUtils.isEmpty(mpPublication.getIpdsReviewProcessState())
				|| ProcessType.SPN_PRODUCTION.getIpdsValue().contentEquals(mpPublication.getIpdsReviewProcessState())) {
			//It is ok to process a publication already in MyPubs if has no review state or is in the SPN Production state.
			rtn = true;
		}
		return rtn;
	}

	protected boolean okToProcessSpnProduction(InformationProduct informationProduct) {
		boolean rtn = false;
		if (null != informationProduct) {
			if (StringUtils.isNotBlank(informationProduct.getDigitalObjectIdentifier())) {
				//Skip if we have already assigned a DOI (shouldn't happen as we are querying for null DOI publications)
				rtn = false;
			} else if (null == informationProduct.getTask() || !ProcessType.SPN_PRODUCTION.getIpdsValue().contentEquals(informationProduct.getTask())) {
				//Skip if not in SPN Production (shouldn't happen as we are querying SPN Production only)
				rtn = false;
			} else if (informationProduct.isUsgsNumberedSeries()) {
				rtn = true;
			}
		}
		return rtn;
	}

	protected ProcessSummary processPublication(ProcessType processType, InformationProduct informationProduct, Integer prodId) {
		//TODO??		newMpPub.setIpdsReviewProcessState(processType.getIpdsValue());
		//We only keep the prodID from the original MP record. The delete is to make sure we kill all child objects.
		if (null != prodId) {
			pubBusService.deleteObject(prodId);
		}

		MpPublication mpPublication = sippConversionService.buildMpPublication(informationProduct, prodId);

		MpPublication rtnPub = extPublicationBusService.create(mpPublication);

		//TODO		if (rtnPub.isValid() && ProcessType.SPN_PRODUCTION.equals(processType)) {
		//TODO				updateIpdsWithDoi(rtnPub);
		//TODO		}
		return buildPublicationProcessSummary(rtnPub);
	}

	protected ProcessSummary buildPublicationProcessSummary(MpPublication mpPublication) {
		ProcessSummary processSummary = new ProcessSummary();
		if (mpPublication.isValid()) {
			processSummary.setAdditions(1);
			processSummary.setProcessingDetails("\n\tAdded to MyPubs as ProdId: " + mpPublication.getId());
		} else {
			processSummary.setErrors(1);
			processSummary.setProcessingDetails("\nERROR: Failed validation.\n\t"
					+ mpPublication.getValidationErrors().toString().replaceAll("\n", "\n\t"));
		}
		return processSummary;
	}

	protected String buildNotOkDetails(ProcessType processType, InformationProduct informationProduct) {
		StringBuilder notOkDetails = new StringBuilder("\n\t").append("IPDS record not processed (\"").append(processType).append("\") -");
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
		return notOkDetails.toString();
	}
}
