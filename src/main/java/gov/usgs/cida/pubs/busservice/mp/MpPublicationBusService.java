package gov.usgs.cida.pubs.busservice.mp;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.busservice.intfc.IListBusService;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.dao.mp.MpPublicationLinkDao;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.PublicationIndex;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.utility.PubsUtilities;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.validation.ValidatorResult;
import gov.usgs.cida.pubs.validation.constraint.DeleteChecks;
import gov.usgs.cida.pubs.validation.constraint.PublishChecks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author drsteini
 *
 */
public class MpPublicationBusService extends MpBusService<MpPublication> implements IMpPublicationBusService {

    //This can/should be overridden from JNDI. 
    protected Integer lockTimeoutHours = PubsConstants.DEFAULT_LOCK_TIMEOUT_HOURS;

    private final ICrossRefBusService crossRefBusService;

    protected final IListBusService<PublicationCostCenter<MpPublicationCostCenter>> costCenterBusService;

    protected final IListBusService<PublicationLink<MpPublicationLink>> linkBusService;

    protected final IListBusService<PublicationContributor<MpPublicationContributor>> contributorBusService;

    @Autowired
    MpPublicationBusService(final Validator validator,
    		final Integer lockTimeoutHours,
            final ICrossRefBusService crossRefBusService,
            @Qualifier("mpPublicationCostCenterBusService")
            IListBusService<PublicationCostCenter<MpPublicationCostCenter>> costCenterBusService,
            @Qualifier("mpPublicationLinkBusService")
            IListBusService<PublicationLink<MpPublicationLink>> linkBusService,
            @Qualifier("mpPublicationContributorBusService")
            IListBusService<PublicationContributor<MpPublicationContributor>> contributorBusService) {
    	this.lockTimeoutHours = lockTimeoutHours;
        this.validator = validator;
        this.crossRefBusService = crossRefBusService;
        this.costCenterBusService = costCenterBusService;
        this.linkBusService = linkBusService;
        this.contributorBusService = contributorBusService;
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.busservice.intfc.IBusService#getObject(java.lang.Integer)
     */
    @Override
    public MpPublication getObject(Integer objectId) {
        beginPublicationEdit(objectId);
        return MpPublication.getDao().getById(objectId);
    }

    @Override
    public Integer getObjectCount(Map<String, Object> filters) {
        return MpPublication.getDao().getObjectCount(filters);
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.busservice.intfc.IBusService#getObjects(java.util.Map)
     */
    @Override
    public List<MpPublication> getObjects(Map<String, Object> filters) {
        return MpPublication.getDao().getByMap(filters);
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.busservice.intfc.IBusService#createObject(java.lang.Object)
     */
    @Override
    @Transactional
    public MpPublication createObject(MpPublication object) {
        if (null != object) {
            MpPublication pub = publicationPreProcessing(object);
            pub.setValidationErrors(validator.validate(pub));
            if (pub.getValidationErrors().isEmpty()) {
                MpPublication.getDao().add(pub);
                pub = publicationPostProcessing(pub);
            }
            return pub;
        }
        return null;
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.busservice.intfc.IBusService#updateObject(java.lang.Object)
     */
    @Override
    @Transactional
    public MpPublication updateObject(MpPublication object) {
        if (null != object) {
            beginPublicationEdit(object.getId());
            MpPublication pub = publicationPreProcessing(object);
            pub.setValidationErrors(validator.validate(pub));
            if (pub.getValidationErrors().isEmpty()) {
                MpPublication.getDao().update(pub);
                pub = publicationPostProcessing(pub);
            }
            return pub;
        }
        return null;
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.busservice.intfc.IBusService#deleteObject(Integer)
     */
    @Override
    @Transactional
    public ValidationResults deleteObject(Integer objectId) {
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
                	MpListPublication.getDao().deleteByParent(objectId);
                    MpPublicationContributor.getDao().deleteByParent(objectId);
                    MpPublicationCostCenter.getDao().deleteByParent(objectId);
                    MpPublicationLink.getDao().deleteByParent(objectId);
                    MpPublication.getDao().delete(pub);
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
                if (PubsUtilities.isUsgsNumberedSeries(outPublication.getPublicationSubtype())) {
                    //Only USGS Numbered Series get a "special" index ID
                    indexId = getUsgsNumberedSeriesIndexId(outPublication);
                    doi = getDoiName(indexId);
                } else if (PubsUtilities.isUsgsUnnumberedSeries(outPublication.getPublicationSubtype())) {
                    doi = getDoiName(indexId);
                }

                outPublication.setIndexId(indexId);
                outPublication.setDoi(doi);
            } else {
                //Otherwise overlay with the values from the published pub.
                outPublication.setIndexId(published.getIndexId());
                if ((PubsUtilities.isUsgsNumberedSeries(outPublication.getPublicationSubtype())
                		|| PubsUtilities.isUsgsUnnumberedSeries(outPublication.getPublicationSubtype()))
                		&& (null != published.getDoi() && 0 < published.getDoi().length())) {
                	//USGS Numbered and Unnumbered Series with a published DOI keep it, everyone else can update from the UI input.
                    outPublication.setDoi(published.getDoi());
                }
            }
        }
        return outPublication;
    }

    public String getUsgsNumberedSeriesIndexId(final MpPublication pub) {
        StringBuilder indexId = new StringBuilder();
        if (null != pub && null != pub.getSeriesTitle()
                && null != pub.getSeriesNumber()) {
            PublicationSeries series = PublicationSeries.getDao().getById(pub.getSeriesTitle().getId());
            if (null != series && null != series.getCode()) {
                indexId.append(cleanseIndexField(series.getCode().toLowerCase()));
                indexId.append(cleanseIndexField(pub.getSeriesNumber()));
                if (null != pub.getChapter()) {
                    indexId.append(cleanseIndexField(pub.getChapter().toUpperCase()));
                }
                if (null != pub.getSubchapterNumber()) {
                    indexId.append(cleanseIndexField(pub.getSubchapterNumber()));
                }
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
            rtn = PubsConstants.DOI_PREFIX + "/" + inIndexId;
        }
        return rtn;
    }

    protected MpPublication publicationPostProcessing(final MpPublication inPublication) {
    	MpPublication outPublication = null;
        if (null != inPublication) {

        	setList(inPublication);
        	contributorBusService.merge(inPublication.getId(), inPublication.getContributors());

            costCenterBusService.merge(inPublication.getId(), inPublication.getCostCenters());
            linkBusService.merge(inPublication.getId(), inPublication.getLinks());

            outPublication = MpPublication.getDao().getById(inPublication.getId());
        }
        return outPublication;
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.busservice.intfc.IMpBusService#publish(Integer)
     */
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
		            if ((PubsUtilities.isUsgsNumberedSeries(mpPub.getPublicationSubtype())
		                    || PubsUtilities.isUsgsUnnumberedSeries(mpPub.getPublicationSubtype()))
		                    && StringUtils.isNotBlank(mpPub.getDoi())) {
		                crossRefBusService.submitCrossRef(mpPub);
		            }
		            deleteObject(publicationId);
		            PublicationIndex.getDao().publish(publicationId);
		            if (PubsUtilities.isSpnUser()) {
		            	//Pubs published by this role should be put back in MyPubs and in the USGS Series list
		            	beginPublicationEdit(publicationId);
		            	setList(MpPublication.getDao().getById(publicationId), MpList.IPDS_USGS_NUMBERED_SERIES);
		            }
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
	            if (PubsUtilities.isUsgsNumberedSeries(mpPub.getPublicationSubtype())
	            		|| PubsUtilities.isUsgsUnnumberedSeries(mpPub.getPublicationSubtype())) {
	                thumbnail.setUrl(MpPublicationLink.USGS_THUMBNAIL);
	            } else {
	                thumbnail.setUrl(MpPublicationLink.EXTERNAL_THUMBNAIL);
	            }
	            MpPublicationLink.getDao().add(thumbnail);
	        }
    	}
    }

    protected void setList(MpPublication inPublication) {
	    if (null != inPublication && null == PwPublication.getDao().getById(inPublication.getId()) ) {
	    	String listId = MpList.IPDS_OTHER_PUBS;
	        if (PubsUtilities.isPublicationTypeArticle(inPublication.getPublicationType())) {
	        	listId = MpList.IPDS_JOURNAL_ARTICLES;
	        } else if (PubsUtilities.isSpnProduction(inPublication.getIpdsReviewProcessState())) {
	        	//Default to the old list if we don't have a PSC.
        		listId = MpList.PENDING_USGS_SERIES;
        		if (null != inPublication.getPublishingServiceCenter() && null != inPublication.getPublishingServiceCenter().getId()) {
		        	MpList spnList = MpList.getDao().getByIpdsId(inPublication.getPublishingServiceCenter().getId());
		        	if (null != spnList && null != spnList.getId()) {
		        		listId = spnList.getId().toString();
		        	}
        		}
            } else if (PubsUtilities.isUsgsNumberedSeries(inPublication.getPublicationSubtype())) {
            	listId = MpList.IPDS_USGS_NUMBERED_SERIES;
	        }
	        setList(inPublication, listId);
	    }
    }
    
    protected void setList(MpPublication inPublication, String listId) {
	    if (null != inPublication && null != inPublication.getId() && null != listId) {
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
    	LocalDateTime now = new LocalDateTime();
    	MpPublication mpPub = MpPublication.getDao().getById(publicationId);
		if (null == mpPub) {
			//Not in MpPublication, so available (ok to edit)
			available = true;
		} else {
			//We found it, so check if it is already locked.
			if (StringUtils.isNotBlank(mpPub.getLockUsername())) {
				//Now, was it locked by the current user.
				if (PubsUtilities.getUsername().equalsIgnoreCase(mpPub.getLockUsername())) {
					//Yes, this user locked it so we are ok to edit.
					available = true;
				} else if (null == mpPub.getUpdateDate() || 0 < now.compareTo(mpPub.getUpdateDate().plusHours(lockTimeoutHours))) {
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
}
