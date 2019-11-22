package gov.usgs.cida.pubs.busservice.mp;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.busservice.intfc.IListBusService;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.dao.mp.MpPublicationLinkDao;
import gov.usgs.cida.pubs.domain.DeletedPublication;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.utility.PubsUtils;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.validation.ValidatorResult;
import gov.usgs.cida.pubs.validation.constraint.DeleteChecks;
import gov.usgs.cida.pubs.validation.constraint.PublishChecks;
import gov.usgs.cida.pubs.validation.constraint.PurgeChecks;

@Service
public class MpPublicationBusService extends MpBusService<MpPublication> implements IMpPublicationBusService {

	protected ConfigurationService configurationService;

	private final ICrossRefBusService crossRefBusService;

	protected final IListBusService<PublicationCostCenter<MpPublicationCostCenter>> costCenterBusService;

	protected final IListBusService<PublicationLink<MpPublicationLink>> linkBusService;

	protected final IListBusService<PublicationContributor<MpPublicationContributor>> contributorBusService;

	@Autowired
	MpPublicationBusService(final Validator validator,
			final ConfigurationService configurationService,
			final ICrossRefBusService crossRefBusService,
			@Qualifier("mpPublicationCostCenterBusService")
			IListBusService<PublicationCostCenter<MpPublicationCostCenter>> costCenterBusService,
			@Qualifier("mpPublicationLinkBusService")
			IListBusService<PublicationLink<MpPublicationLink>> linkBusService,
			@Qualifier("mpPublicationContributorBusService")
			IListBusService<PublicationContributor<MpPublicationContributor>> contributorBusService) {
		this.configurationService = configurationService;
		this.validator = validator;
		this.crossRefBusService = crossRefBusService;
		this.costCenterBusService = costCenterBusService;
		this.linkBusService = linkBusService;
		this.contributorBusService = contributorBusService;
	}

	@Override
	public MpPublication getObject(Integer objectId) {
		beginPublicationEdit(objectId);
		return MpPublication.getDao().getById(objectId);
	}

	@Override
	public Integer getObjectCount(Map<String, Object> filters) {
		return MpPublication.getDao().getObjectCount(filters);
	}

	@Override
	public List<MpPublication> getObjects(Map<String, Object> filters) {
		return MpPublication.getDao().getByMap(filters);
	}

	@Override
	@Transactional
	public MpPublication createObject(MpPublication object, Class<?>... groups) {
		if (null != object) {
			MpPublication pub = publicationPreProcessing(object);
			pub.setValidationErrors(validator.validate(pub));
			if (pub.isValid()) {
				MpPublication.getDao().add(pub);
				MpPublication.getDao().lockPub(pub.getId());
				pub = publicationPostProcessing(pub);
			}
			return pub;
		}
		return null;
	}

	@Override
	@Transactional
	public MpPublication updateObject(MpPublication object, Class<?>... groups) {
		if (null != object) {
			beginPublicationEdit(object.getId());
			MpPublication pub = publicationPreProcessing(object);
			pub.setValidationErrors(validator.validate(pub));
			if (pub.isValid()) {
				MpPublication.getDao().update(pub);
				pub = publicationPostProcessing(pub);
			}
			return pub;
		}
		return null;
	}

	@Override
	@Transactional
	public ValidationResults deleteObject(Integer objectId, Class<?>... groups) {
		if (null != objectId) {
			MpPublication pub = MpPublication.getDao().getById(objectId);
			if (null == pub) {
				pub = new MpPublication();
			} else {
				//only delete if we found it...
				Set<ConstraintViolation<MpPublication>> validations = validator.validate(pub, DeleteChecks.class);
				if (!validations.isEmpty()) {
					pub.setValidationErrors(validations);
				} else {
					deleteMpPublication(objectId);
				}
			}
			return pub.getValidationErrors();
		}
		return null;
	}

	protected MpPublication publicationPreProcessing(final MpPublication inPublication) {
		MpPublication outPublication = inPublication;

		if (null != outPublication) {
			if (null == outPublication.getId()) {
				outPublication.setId(MpPublication.getDao().getNewProdId());
			}

			//TODO is this still necessary? or should we have a better cleansing mechanism for (all) fields?
			if (null != outPublication.getTitle()) {
				outPublication.setTitle(outPublication.getTitle().replace("\n", "").replace("\r", ""));
			}

			PwPublication published = PwPublication.getDao().getById(outPublication.getId());
			if (null == published) {
				//Only auto update index ID if publication in not in the warehouse.
				String indexId = outPublication.getId().toString();
				String doi = outPublication.getDoi();
				if (PubsUtils.isUsgsNumberedSeries(outPublication.getPublicationSubtype())) {
					//Only USGS Numbered Series get a "special" index ID
					indexId = getUsgsNumberedSeriesIndexId(outPublication);
					doi = getDoiName(indexId);
				} else if (PubsUtils.isUsgsUnnumberedSeries(outPublication.getPublicationSubtype())) {
					doi = getDoiName(indexId);
				}

				outPublication.setIndexId(indexId);
				outPublication.setDoi(doi);
			} else {
				//Otherwise overlay with the values from the published pub.
				outPublication.setIndexId(published.getIndexId());
				if ((PubsUtils.isUsgsNumberedSeries(outPublication.getPublicationSubtype())
						|| PubsUtils.isUsgsUnnumberedSeries(outPublication.getPublicationSubtype()))
						&& (null != published.getDoi() && 0 < published.getDoi().length())) {
					//USGS Numbered and Unnumbered Series with a published DOI keep it, everyone else can update from the UI input.
					outPublication.setDoi(published.getDoi());
				}
				if (!StringUtils.isBlank(published.getIpdsId())) {
					outPublication.setIpdsId(published.getIpdsId()); // only block edits to ipdsId if published and not blank
				}
			}
		}
		return outPublication;
	}

	public String getUsgsNumberedSeriesIndexId(final MpPublication pub) {
		String rtn = null;
		if (null != pub && null != pub.getSeriesTitle()) {
			rtn = getUsgsNumberedSeriesIndexId(PublicationSeries.getDao().getById(pub.getSeriesTitle().getId()),
					pub.getSeriesNumber(), pub.getChapter(), pub.getSubchapterNumber());
		}
		return rtn;
	}

	public String getUsgsNumberedSeriesIndexId(PublicationSeries series, String seriesNumber,
			String chapter, String subchapterNumber) {
		StringBuilder indexId = new StringBuilder();
		if (null != series && null != series.getCode()
				&& null != seriesNumber) {
			indexId.append(cleanseIndexField(series.getCode().toLowerCase()));
			indexId.append(cleanseIndexField(seriesNumber));
			if (null != chapter) {
				indexId.append(cleanseIndexField(chapter.toUpperCase()));
			}
			if (null != subchapterNumber) {
				indexId.append(cleanseIndexField(subchapterNumber));
			}
		}
		return 0 == indexId.length() ? null : indexId.toString();
	}

	protected String cleanseIndexField(final String value) {
		if (null != value) {
			return value.replace("-", "").replace(",", "").replace(" ", "");
		}
		return null;
	}

	public static String getDoiName(final String inIndexId) {
		String rtn = null;
		if (null != inIndexId && 0 < inIndexId.length()) {
			rtn = PubsConstantsHelper.DOI_PREFIX + "/" + inIndexId;
		}
		return rtn;
	}

	protected MpPublication publicationPostProcessing(final MpPublication inPublication) {
		MpPublication outPublication = null;
		if (null != inPublication && null != inPublication.getId()) {

			setList(inPublication);
			contributorBusService.merge(inPublication.getId(), inPublication.getContributors());
			costCenterBusService.merge(inPublication.getId(), inPublication.getCostCenters());
			linkBusService.merge(inPublication.getId(), inPublication.getLinks());

			outPublication = MpPublication.getDao().getById(inPublication.getId());
		}
		return outPublication;
	}

	@Override
	@Transactional
	public ValidationResults publish(final Integer publicationId) {
		ValidationResults validationResults = new ValidationResults();
		if (null != publicationId) {
			//One last guarantee that all of the warehouse data is covered in mp
			beginPublicationEdit(publicationId);
			MpPublication mpPub = MpPublication.getDao().getById(publicationId);

			if (null != mpPub) {
				Set<ConstraintViolation<MpPublication>> validations = validator.validate(mpPub, Default.class, PublishChecks.class);
				if (validations.isEmpty()) {
					defaultThumbnail(mpPub);

					MpPublication.getDao().publishToPw(publicationId);
					MpPublicationCostCenter.getDao().publishToPw(publicationId);
					MpPublicationLink.getDao().publishToPw(publicationId);
					MpPublicationContributor.getDao().publishToPw(publicationId);
					if ((PubsUtils.isUsgsNumberedSeries(mpPub.getPublicationSubtype())
							|| PubsUtils.isUsgsUnnumberedSeries(mpPub.getPublicationSubtype()))
							&& StringUtils.isNotBlank(mpPub.getDoi())) {
						crossRefBusService.submitCrossRef(mpPub);
					}
					deleteObject(publicationId);
					if (PubsUtils.isSpnUser(configurationService)) {
						//Pubs published by this role should be put back in MyPubs and in the USGS Series list
						beginPublicationEdit(publicationId);
						setList(MpPublication.getDao().getById(publicationId), MpList.IPDS_USGS_NUMBERED_SERIES);
					}
					PwPublication.getDao().refreshTextIndex();
				} else {
					mpPub.setValidationErrors(validations);
					validationResults.addValidationResults(mpPub.getValidationErrors());
				}
			} else {
				validationResults.addValidatorResult(new ValidatorResult("Publication", "Publication does not exist.", SeverityLevel.FATAL, publicationId.toString()));
			}
		}
		return validationResults;
	}

	/**
	 * For publicationLink, on publish, we create a placeholder thumbnail link if none already exist.
	 */
	protected void defaultThumbnail(final MpPublication mpPub) {
		if (null != mpPub && null != mpPub.getId()) {
			Map<String, Object> filters = new HashMap<String, Object>();
			filters.put(MpPublicationLinkDao.LINK_TYPE_SEARCH, LinkType.THUMBNAIL);
			filters.put(MpPublicationLinkDao.PUB_SEARCH, mpPub.getId());
			List<MpPublicationLink> thumbnails = MpPublicationLink.getDao().getByMap(filters);
			if (thumbnails.isEmpty()) {
				MpPublicationLink thumbnail = new MpPublicationLink();
				thumbnail.setPublicationId(mpPub.getId());
				thumbnail.setLinkType(LinkType.getDao().getById(LinkType.THUMBNAIL.toString()));
				if (PubsUtils.isUsgsNumberedSeries(mpPub.getPublicationSubtype())
						|| PubsUtils.isUsgsUnnumberedSeries(mpPub.getPublicationSubtype())) {
					thumbnail.setUrl(configurationService.getWarehouseEndpoint() + MpPublicationLink.USGS_THUMBNAIL);
				} else {
					thumbnail.setUrl(configurationService.getWarehouseEndpoint() + MpPublicationLink.EXTERNAL_THUMBNAIL);
				}
				MpPublicationLink.getDao().add(thumbnail);
			}
		}
	}

	protected void setList(MpPublication inPublication) {
		if (null != inPublication && null == PwPublication.getDao().getById(inPublication.getId()) ) {
			int listId = MpList.IPDS_OTHER_PUBS;
			if (PubsUtils.isPublicationTypeArticle(inPublication.getPublicationType())) {
				listId = MpList.IPDS_JOURNAL_ARTICLES;
			} else if (PubsUtils.isSpnProduction(inPublication.getIpdsReviewProcessState())) {
				//Default to the old list if we don't have a PSC.
				listId = MpList.PENDING_USGS_SERIES;
			} else if (PubsUtils.isUsgsNumberedSeries(inPublication.getPublicationSubtype())) {
				listId = MpList.IPDS_USGS_NUMBERED_SERIES;
			} else if (PubsUtils.isPublicationTypeUSGSDataRelease(inPublication.getPublicationSubtype())) {
				listId = MpList.USGS_DATA_RELEASES;
			} else if (PubsUtils.isPublicationTypeUSGSWebsite(inPublication.getPublicationSubtype())) {
				listId = MpList.USGS_WEBSITE;
			}
			setList(inPublication, listId);
		}
	}

	protected void setList(MpPublication inPublication, int listId) {
		if (null != inPublication && null != inPublication.getId()) {
			MpListPublication newListEntry = new MpListPublication();
			newListEntry.setMpPublication(inPublication);
			newListEntry.setMpList(MpList.getDao().getById(listId));

			//Check for existing list entry
			Map<String, Object> params = new HashMap<>();
			params.put("publicationId", newListEntry.getMpPublication().getId());
			params.put("mpListId", newListEntry.getMpList().getId());
			List<MpListPublication> listEntries = MpListPublication.getDao().getByMap(params);
			if (listEntries.isEmpty()) {
				MpListPublication.getDao().add(newListEntry);
			} else {
				MpListPublication.getDao().update(newListEntry);
			}
		}
	}

	@Override
	public ValidatorResult checkAvailability(Integer publicationId) {
		boolean available = false;
		LocalDateTime now = LocalDateTime.now();
		MpPublication mpPub = MpPublication.getDao().getById(publicationId);
		if (null == mpPub) {
			//Not in MpPublication, so available (ok to edit)
			available = true;
		} else {
			//We found it, so check if it is already locked.
			if (StringUtils.isNotBlank(mpPub.getLockUsername()) 
					&& !PubsConstantsHelper.ANONYMOUS_USER.contentEquals(mpPub.getLockUsername())) {
				//Now, was it locked by the current user.
				if (PubsUtils.getUsername().equalsIgnoreCase(mpPub.getLockUsername())) {
					//Yes, this user locked it so we are ok to edit.
					available = true;
				} else if (null == mpPub.getUpdateDate() || 0 < now.compareTo(mpPub.getUpdateDate().plusHours(configurationService.getLockTimeoutHours()))) {
					//The lock has expired, so let this person edit it.
					available = true;
				}
			} else {
				//Not already locked, so let this person edit it.
				available = true;
			}
		}

		if (available) {
			return null;
		} else {
			return new ValidatorResult("Publication", "This Publication is being edited by " + mpPub.getLockUsername(),
					SeverityLevel.FATAL, mpPub.getLockUsername());
		}
	}

	@Override
	public void releaseLocksUser(String lockUsername) {
		MpPublication.getDao().releaseLocksUser(lockUsername);
	}

	@Override
	public void releaseLocksPub(Integer publicationId) {
		MpPublication.getDao().releaseLocksPub(publicationId);
	}

	@Override
	public MpPublication getByIndexId(String indexId) {
		return MpPublication.getDao().getByIndexId(indexId);
	}

	@Override
	@Transactional
	public ValidationResults purgePublication(Integer publicationId) {
		ValidationResults validationResults = new ValidationResults();
		if (null != publicationId) {
			PwPublication pwPub = PwPublication.getDao().getById(publicationId);
			MpPublication mpPub = MpPublication.getDao().getById(publicationId);
			if (null == pwPub && null == mpPub) {
				validationResults.addValidatorResult(new ValidatorResult("Publication", "Publication does not exist.", SeverityLevel.FATAL, publicationId.toString()));
			} else {
				validationResults = validateAndDelete(pwPub, mpPub);
			}
		}
		return validationResults;
	}

	protected ValidationResults validateAndDelete(PwPublication pwPub, MpPublication mpPub) {
		ValidationResults validationResults = new ValidationResults();
		PwPublication pubToValidate = pwPub != null ? pwPub : new PwPublication(mpPub.getId(), mpPub.getIndexId());
		pubToValidate.setValidationErrors(validator.validate(pubToValidate, PurgeChecks.class));
		if (pubToValidate.isValid()) {
			if (null != mpPub) {
				deleteMpPublication(mpPub.getId());
			}

			if (null != pwPub) {
				DeletedPublication.getDao().add(new DeletedPublication(pwPub));
				deletePwPublication(pwPub.getId());
			}
		} else {
			validationResults = pubToValidate.getValidationErrors();
		}
		return validationResults;
	}

	protected void deleteMpPublication(Integer publicationId) {
		MpPublication.getDao().purgePublication(publicationId);
	}

	protected void deletePwPublication(Integer publicationId) {
		PwPublication.getDao().purgePublication(publicationId);
	}
}
