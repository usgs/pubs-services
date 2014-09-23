package gov.usgs.cida.pubs.busservice.mp;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.BusService;
import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.busservice.intfc.IListBusService;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.domain.ContributorType;
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
import gov.usgs.cida.pubs.utility.PubsUtilities;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.validation.ValidatorResult;
import gov.usgs.cida.pubs.validation.constraint.DeleteChecks;
import gov.usgs.cida.pubs.validation.constraint.PublishChecks;

import java.util.ArrayList;
import java.util.Collection;
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
public class MpPublicationBusService extends BusService<MpPublication> implements IMpPublicationBusService {

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
            if (pub.getValErrors().isEmpty()) {
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
            if (pub.getValErrors().isEmpty()) {
                MpPublication.getDao().update(pub);
                pub = publicationPostProcessing(pub);
            }
            return pub;
        }
        return null;
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.busservice.intfc.IBusService#deleteObject(java.lang.Object)
     */
    @Override
    @Transactional
    public ValidationResults deleteObject(MpPublication object) {
        if (null != object) {
            MpPublication pub = MpPublication.getDao().getById(object.getId());
            if (null == pub) {
                pub = new MpPublication();
            } else {
                //only delete if we found it...
                Set<ConstraintViolation<MpPublication>> validations = validator.validate(pub, DeleteChecks.class);
                if (!validations.isEmpty()) {
                    pub.setValidationErrors(validations);
                } else {
                	MpPublicationLink.getDao().deleteByParent(object.getId());
                    MpPublicationContributor.getDao().deleteByParent(object.getId());
                    MpPublicationCostCenter.getDao().deleteByParent(object.getId());
                    MpPublicationLink.getDao().deleteByParent(object.getId());
                    MpPublication.getDao().delete(object);
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
                String doi = null;
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
                if (null != published.getDoi() && 0 < published.getDoi().length()) {
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

        	Collection<PublicationContributor<?>> publicationContributors = new ArrayList<>();
        	if (null != inPublication.getAuthors() && !inPublication.getAuthors().isEmpty()) {
        		//Deserialize currently doesn't give the contributor type...
        		ContributorType authors = new ContributorType();
        		authors.setId(ContributorType.AUTHORS);
	            for (PublicationContributor<?> author : inPublication.getAuthors()) {
	            	author.setContributorType(authors);
	            	publicationContributors.add(author);
	            }
        	}
        	if (null != inPublication.getEditors() && !inPublication.getEditors().isEmpty()) {
        		//Deserialize currently doesn't give the contributor type...
        		ContributorType editors = new ContributorType();
        		editors.setId(ContributorType.EDITORS);
	            for (PublicationContributor<?> editor : inPublication.getEditors()) {
	            	editor.setContributorType(editors);
	            	publicationContributors.add(editor);
	            }
        	}
        	contributorBusService.merge(inPublication.getId(), publicationContributors);

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
        //One last guarantee that all of the warehouse data is covered in mp
        beginPublicationEdit(publicationId);
        MpPublication mpPub = MpPublication.getDao().getById(publicationId);

        Set<ConstraintViolation<MpPublication>> validations = validator.validate(mpPub, Default.class, PublishChecks.class);
        if (validations.isEmpty()) {
            defaultThumbnail(mpPub);

            MpPublication.getDao().publishToPw(publicationId);
            MpPublicationCostCenter.getDao().publishToPw(publicationId);
            MpPublicationLink.getDao().publishToPw(publicationId);
            MpPublicationContributor.getDao().publishToPw(publicationId);
            if ((PubsUtilities.isUsgsNumberedSeries(mpPub.getPublicationSubtype())
                    || PubsUtilities.isUsgsUnnumberedSeries(mpPub.getPublicationSubtype()))
                    && (null != mpPub.getDoi() && StringUtils.isNotEmpty(mpPub.getDoi()))) {
                crossRefBusService.submitCrossRef(mpPub);
            }
            deleteObject(mpPub);
        } else {
            mpPub.setValidationErrors(validations);
            validationResults.addValidationResults(mpPub.getValidationErrors());
        }

        return validationResults;
    }
    
    /**
     * Make sure the pw publication information exists in mp before working with it in mp.
     * @param publicationId
     */
    protected void beginPublicationEdit(Integer publicationId) {
        if (null != publicationId) {
            //Look in MP to see if this key exists
            MpPublication mpPub = MpPublication.getDao().getById(publicationId);
            if (null == mpPub) {
                //Didn't find it in MP, look in PW
                PwPublication pwPub = PwPublication.getDao().getById(publicationId);
                if (null != pwPub) {
                    //There it is, copy it!
                    MpPublication.getDao().copyFromPw(publicationId);
                    MpPublicationCostCenter.getDao().copyFromPw(publicationId);
                    MpPublicationLink.getDao().copyFromPw(publicationId);
                    MpPublicationContributor.getDao().copyFromPw(publicationId);
                }
            } else {
            	MpPublication.getDao().lockPub(publicationId);
            }
        }
    }
    
    /**
     * For publicationLink, on publish, we create a placeholder thumbnail link if none already exist.
     */
    protected void defaultThumbnail(final MpPublication mpPub) {
    	if (null != mpPub && null != mpPub.getId()) {
	        Map<String, Object> filters = new HashMap<String, Object>();
	        filters.put("linkTypeId", LinkType.THUMBNAIL);
	        filters.put("publicationId", mpPub.getId());
	        List<MpPublicationLink> thumbnails = MpPublicationLink.getDao().getByMap(filters);
	        if (0 == thumbnails.size()) {
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
	    if (null != inPublication && null != inPublication.getIpdsId() && null != inPublication.getId()
	            && null == PwPublication.getDao().getById(inPublication.getId()) ) {
	        MpListPublication newListEntry = new MpListPublication();
	        newListEntry.setMpPublication(inPublication);
	        if (PubsUtilities.isPublicationTypeArticle(inPublication.getPublicationType())) {
	            newListEntry.setMpList(MpList.getDao().getById(MpList.IPDS_JOURNAL_ARTICLES));
	        } else {
	            if (PubsUtilities.isUsgsNumberedSeries(inPublication.getPublicationSubtype())) {
	                if (PubsUtilities.isSpnProduction(inPublication.getIpdsReviewProcessState())) {
	                    newListEntry.setMpList(MpList.getDao().getById(MpList.PENDING_USGS_SERIES));
	                } else {
	                    newListEntry.setMpList(MpList.getDao().getById(MpList.IPDS_USGS_NUMBERED_SERIES));
	                }
	            } else {
	                if (PubsUtilities.isSpnProduction(inPublication.getIpdsReviewProcessState())) {
	                    newListEntry.setMpList(MpList.getDao().getById(MpList.PENDING_USGS_SERIES));
	                } else {
	                    newListEntry.setMpList(MpList.getDao().getById(MpList.IPDS_OTHER_PUBS));
	                }
	            }
	        }
	
	        //Check for existing list entry
	        Map<String, Object> params = new HashMap<>();
	        params.put("publicationId", newListEntry.getMpPublication().getId());
	        params.put("mpListId", newListEntry.getMpList().getId());
	        List<MpListPublication> listEntries = MpListPublication.getDao().getByMap(params);
	        if (0 == listEntries.size()) {
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
			if (StringUtils.isNotEmpty(mpPub.getLockUsername())) {
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
					"fatal", mpPub.getLockUsername());
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
}
