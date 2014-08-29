package gov.usgs.cida.pubs.busservice.mp;

import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.busservice.intfc.IListBusService;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.validation.constraint.DeleteChecks;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author drsteini
 *
 */
public class MpPublicationBusService extends MpBusService<MpPublication> implements IMpPublicationBusService {

    public static final String DOI_PREFIX = "10.3133";

    private final ICrossRefBusService crossRefBusService;

    protected final IListBusService<PublicationCostCenter<MpPublicationCostCenter>> costCenterBusService;

    protected final IListBusService<PublicationLink<MpPublicationLink>> linkBusService;

    @Autowired
    MpPublicationBusService(final Validator validator,
            final ICrossRefBusService crossRefBusService,
            @Qualifier("mpPublicationCostCenterBusService")
            IListBusService<PublicationCostCenter<MpPublicationCostCenter>> costCenterBusService,
            @Qualifier("mpPublicationLinkBusService")
            IListBusService<PublicationLink<MpPublicationLink>> linkBusService) {
        this.validator = validator;
        this.crossRefBusService = crossRefBusService;
        this.costCenterBusService = costCenterBusService;
        this.linkBusService = linkBusService;
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.busservice.intfc.IBusService#getObject(java.lang.Integer)
     */
    @Override
    public MpPublication getObject(Integer objectId) {
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
//                    MpPublicationContributor.getDao().deleteByParent(object.getId());
                    MpPublicationCostCenter.getDao().deleteByParent(object.getId());
                    MpPublicationLink.getDao().deleteByParent(object.getId());
    //                MpSupersedeRel.getDao().deleteByParent(object.getId());
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
                if (isUsgsNumberedSeries(outPublication.getPublicationSubtype())) {
                    //Only USGS Numbered Series get a "special" index ID
                    indexId = getUsgsNumberedSeriesIndexId(outPublication);
                    doi = getDoiName(indexId);
                } else if (isUsgsUnnumberedSeries(outPublication.getPublicationSubtype())) {
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

    protected boolean isUsgsNumberedSeries(final PublicationSubtype pubSubtype) {
        boolean rtn = false;
        if (null != pubSubtype
                && PublicationSubtype.USGS_NUMBERED_SERIES.equals(pubSubtype.getId())) {
            rtn = true;
        }
        return rtn;
    }

    protected boolean isUsgsUnnumberedSeries(final PublicationSubtype pubSubtype) {
        boolean rtn = false;
        if (null != pubSubtype
                && PublicationSubtype.USGS_UNNUMBERED_SERIES.equals(pubSubtype.getId())) {
            rtn = true;
        }
        return rtn;
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
            rtn = DOI_PREFIX + "/" + inIndexId;
        }
        return rtn;
    }

    protected MpPublication publicationPostProcessing(final MpPublication inPublication) {
    	MpPublication outPublication = null;
        if (null != inPublication) {

        //TODO Reactive when lists are implemented.
//        if (null != outPublication.getIpdsId() 
//                && null == PwPublication.getDao().getById(outPublication.getId()) ) {
//            MpListPubsRel newListEntry = new MpListPubsRel();
//            newListEntry.setProdId(outPublication.getId().toString());
//            if (outPublication.getPublicationTypeId().contentEquals(PublicationType.ARTICLE)) {
//                newListEntry.setListId(MpList.IPDS_JOURNAL_ARTICLES);
//            } else {
//                if (outPublication.getPublicationTypeId().contentEquals(PublicationType.USGS_NUMBERED_SERIES)) {
//                    if (null != outPublication.getIpdsReviewProcessState() &&
//                            ProcessType.SPN_PRODUCTION.getIpdsValue().contentEquals(outPublication.getIpdsReviewProcessState())) {
//                        newListEntry.setListId(MpList.PENDING_USGS_SERIES);
//                    } else {
//                        newListEntry.setListId(MpList.IPDS_USGS_NUMBERED_SERIES);
//                    }
//                } else {
//                    if (null != outPublication.getIpdsReviewProcessState() &&
//                            ProcessType.SPN_PRODUCTION.getIpdsValue().contentEquals(outPublication.getIpdsReviewProcessState())) {
//                        newListEntry.setListId(MpList.PENDING_USGS_SERIES);
//                    } else {
//                        newListEntry.setListId(MpList.IPDS_OTHER_PUBS);
//                    }
//                }
//            }
//
//            //Check for existing list entry
//            Map<String, Object> params = new HashMap<String, Object>();
//            params.put("prodId", newListEntry.getProdId());
//            params.put("listId", newListEntry.getListId());
//            List<MpListPubsRel> listEntries = MpListPubsRel.getDao().getByMap(params);
//            if (0 == listEntries.size()) {
//                MpListPubsRel.getDao().add(newListEntry);
//            } else {
//                MpListPubsRel.getDao().update(newListEntry);
//            }
//        }

            costCenterBusService.merge(inPublication.getId(), inPublication.getCostCenters());
            linkBusService.merge(inPublication.getId(), inPublication.getLinks());
            //TODO Add other child object merges here.

            outPublication = MpPublication.getDao().getById(inPublication.getId());
        }
        return outPublication;
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.busservice.intfc.IMpBusService#publish(Integer)
     */
    @Override
    @Transactional
    public ValidationResults publish(final Integer prodId) {
        //TODO reactivate when implementing the new publish routine.
        ValidationResults validationResults = new ValidationResults();
//        //One last guarantee that all of the warehouse data is covered in mp
//        beginPublicationEdit(prodId);
//        MpPublication mpPub = MpPublication.getDao().getById(prodId);
//
//        Set<ConstraintViolation<MpPublication>> validations = validator.validate(mpPub);
//        if ( validations.isEmpty() ) {
//            Map<String, Object> filters = new HashMap<String, Object>();
//            filters.put("prodId", prodId);
//            List<MpSupersedeRel> supers = MpSupersedeRel.getDao().getByMap(filters);
//            for (MpSupersedeRel mpSuper : supers) {
//                mpSuper.setValidationErrors(validator.validate(mpSuper));
//                validationResults.addValidationResults(mpSuper.getValidationErrors());
//            }
//
//            defaultThumbnail(mpPub);
//
//            List<MpLinkDim> links = MpLinkDim.getDao().getByMap(filters);
//            for (MpLinkDim link : links) {
//                if (null != link.getLink() && !link.getLink().contains(LinkDim.DOI_LINK_SITE)) {
//                    //Skip validating the DOI link.
//                    link.setValidationErrors(validator.validate(link));
//                    validationResults.addValidationResults(link.getValidationErrors());
//                }
//            }
//        } else {
//            mpPub.setValidationErrors(validations);
//            validationResults.addValidationResults(mpPub.getValidationErrors());
//        }
//
//        if (validationResults.isEmpty()) {
//            MpPublication.getDao().publishToPw(prodId);
//            MpSupersedeRel.getDao().publishToPw(prodId);
//            MpLinkDim.getDao().publishToPw(prodId);
//            if ((PublicationType.USGS_NUMBERED_SERIES.contentEquals(mpPub.getPublicationTypeId())
//                    || PublicationType.USGS_UNNUMBERED_SERIES.contentEquals(mpPub.getPublicationTypeId()))
//                    && (null != mpPub.getDoiName() && 0 < mpPub.getDoiName().length())) {
//                crossRefBusService.submitCrossRef(mpPub);
//            }
//            deleteObject(mpPub);
//        }
//
        return validationResults;
    }

    /**
     * For linkDim, on publish, we create a placeholder thumbnail link if none already exist.
     */
    @Transactional
    private void defaultThumbnail(final MpPublication mpPub) {
        //TODO reactivate when implementing the new publish routine.
//        Map<String, Object> filters = new HashMap<String, Object>();
//        filters.put("linkClass", LinkClass.THUMBNAIL.toString());
//        filters.put("prodId", mpPub.getId());
//        List<MpLinkDim> thumbnails = MpLinkDim.getDao().getByMap(filters);
//        if (0 == thumbnails.size()) {
//            MpLinkDim thumbnail = new MpLinkDim();
//            thumbnail.setProdId(mpPub.getId().toString());
//            thumbnail.setLinkClass(LinkClass.THUMBNAIL.toString());
//            if (null != mpPub.getPublicationTypeId() &&
//                    (PublicationType.USGS_NUMBERED_SERIES.contentEquals(mpPub.getPublicationTypeId())
//                            || PublicationType.USGS_UNNUMBERED_SERIES.contentEquals(mpPub.getPublicationTypeId()))) {
//                thumbnail.setLink(MpLinkDim.USGS_THUMBNAIL);
//            } else {
//                thumbnail.setLink(MpLinkDim.EXTERNAL_THUMBNAIL);
//            }
//            MpLinkDim.getDao().add(thumbnail);
//        }
    }

}