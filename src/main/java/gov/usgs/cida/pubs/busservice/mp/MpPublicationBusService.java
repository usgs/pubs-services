package gov.usgs.cida.pubs.busservice.mp;

//import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
//import gov.usgs.cida.pubs.domain.LinkClass;
//import gov.usgs.cida.pubs.domain.LinkDim;
//import gov.usgs.cida.pubs.domain.MpLinkDim;
//import gov.usgs.cida.pubs.domain.MpList;
//import gov.usgs.cida.pubs.domain.MpListPubsRel;
//import gov.usgs.cida.pubs.domain.MpSupersedeRel;
//import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.validation.constraint.DeleteChecks;
import gov.usgs.cida.pubs.validation.constraint.UpdateChecks;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.groups.Default;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author drsteini
 *
 */
public class MpPublicationBusService extends MpBusService<MpPublication> implements IMpPublicationBusService {

    public final static String CROSS_REF = "10.3133";

//    @Autowired
//    private ICrossRefBusService crossRefBusService;

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.busservice.intfc.IBusService#getObject(java.lang.Integer)
     */
    @Override
    public MpPublication getObject(Integer objectId) {
        MpPublication pub = MpPublication.getDao().getById(objectId);
        return pub;
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.busservice.intfc.IBusService#getObjects(java.util.Map)
     */
    @Override
    public List<MpPublication> getObjects(Map<String, Object> filters) {
        List<MpPublication> pubs = MpPublication.getDao().getByMap(filters);
        return pubs;
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.busservice.intfc.IBusService#createObject(java.lang.Object)
     */
    @Override
    @Transactional
    public MpPublication createObject(MpPublication object) {
        MpPublication rtnPub = publicationPreProcessing(object);
        Set<ConstraintViolation<MpPublication>> validations = validator.validate(rtnPub);
        if (!validations.isEmpty()) {
            rtnPub.setValidationErrors(validations);
        } else {
            Integer id = MpPublication.getDao().add(rtnPub);
            rtnPub = publicationPostProcessing(MpPublication.getDao().getById(id));
        }
        return rtnPub;
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.busservice.intfc.IBusService#updateObject(java.lang.Object)
     */
    @Override
    @Transactional
    public MpPublication updateObject(MpPublication object) {
        beginPublicationEdit(object.getId());
        MpPublication rtnPub = publicationPreProcessing(object);
        Set<ConstraintViolation<MpPublication>> validations = validator.validate(rtnPub, Default.class, UpdateChecks.class);
        if (!validations.isEmpty()) {
            rtnPub.setValidationErrors(validations);
        } else {
            MpPublication.getDao().update(rtnPub);
            rtnPub = publicationPostProcessing(MpPublication.getDao().getById(object.getId()));
        }
        return rtnPub;
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.busservice.intfc.IBusService#deleteObject(java.lang.Object)
     */
    @Override
    @Transactional
    public ValidationResults deleteObject(MpPublication object) {
        MpPublication rtnPub = MpPublication.getDao().getById(object.getId());
        if (null == rtnPub) {
            rtnPub = new MpPublication();
        } else {
            //only delete if we found it...
            Set<ConstraintViolation<MpPublication>> validations = validator.validate(rtnPub, DeleteChecks.class);
            if (!validations.isEmpty()) {
                rtnPub.setValidationErrors(validations);
            } else {
//                MpSupersedeRel.getDao().deleteByParent(object.getId());
//                MpLinkDim.getDao().deleteByParent(object.getId());
                MpPublication.getDao().delete(object);
            }
        }
        return rtnPub.getValidationErrors();
    }

    protected MpPublication publicationPreProcessing(final MpPublication inPublication) {
        MpPublication outPublication = inPublication;

        if (null == outPublication.getId()) {
            outPublication.setId(MpPublication.getDao().getNewProdId());
        }

        //TODO is this still necessary? or should we have a better cleansing mechanism for (all) fields?
        if (null != outPublication.getTitle()) {
            outPublication.setTitle(outPublication.getTitle().replace("\n", "").replace("\r", ""));
        }

        PwPublication published = null;
        //TODO add back in when we have the published pub. PwPublication published = PwPublication.getDao().getById(outPublication.getId());
        if (null == published) {
            //Only auto update index ID if publication in not in the warehouse.
            String indexId = outPublication.getId().toString();
            String doi = null;
            if (isUsgsNumberedSeries(outPublication.getPublicationSubtype())) {
                //Only USGS Numbered Series get a "special" index ID
                indexId = getUsgsNumberedSeriesIndexId(outPublication.getPublicationSeries(), outPublication.getSeriesNumber());
                doi = getDoiName(indexId);
            } else if (isUsgsUnnumberedSeries(outPublication.getPublicationSubtype())) {
                doi = getDoiName(indexId);
            }

            outPublication.setIndexId(indexId);
            outPublication.setDoiName(doi);
        } else {
            //Otherwise overlay with the values from the published pub.
            outPublication.setIndexId(published.getIndexId());
            if (null != published.getDoiName() && 0 < published.getDoiName().length()) {
                outPublication.setDoiName(published.getDoiName());
            }
        }

        return outPublication;
    }

    protected boolean isUsgsNumberedSeries(final PublicationSubtype pubSubtype) {
        boolean rtn = false;
        if (null != pubSubtype
                && PublicationSubtype.USGS_NUMBERED_SERIES == pubSubtype.getId()) {
            rtn = true;
        }
        return rtn;
    }

    protected boolean isUsgsUnnumberedSeries(final PublicationSubtype pubSubtype) {
        boolean rtn = false;
        if (null != pubSubtype
                && PublicationSubtype.USGS_UNNUMBERED_SERIES == pubSubtype.getId()) {
            rtn = true;
        }
        return rtn;
    }

    public String getUsgsNumberedSeriesIndexId(final PublicationSeries pubSeries, final String seriesNumber) {
        String indexId = null;
        if (null != pubSeries
                && null != seriesNumber) {
            PublicationSeries series = PublicationSeries.getDao().getById(pubSeries.getId());
            if (null != series 
                    && null != series.getCode()) {
                indexId = series.getCode().toLowerCase() + seriesNumber.replace("-", "").replace(",", "").replace(" ", "");
            }
        }
        return indexId;
    }

    public static String getDoiName(final String inIndexId) {
        String rtn = null;
        if (null != inIndexId && 0 < inIndexId.length()) {
            rtn = CROSS_REF + "/" + inIndexId;
        }
        return rtn;
    }

    protected MpPublication publicationPostProcessing(final MpPublication inPublication) {
        MpPublication outPublication = inPublication;
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
//
        //TODO reactivate or remove when doi/link is finalized.
//        //This is a "temporary" bit of code to keep the doi link in sync with the doiName.
//        //It can be removed once the warehouse is updated to use doiName.
//        MpLinkDim doiLink = new MpLinkDim();
//        Map<String, Object> filters = new HashMap<String, Object>();
//        filters.put("prodId", outPublication.getId());
//        filters.put("doiLink", MpLinkDim.DOI_LINK_SITE);
//        List<MpLinkDim> doiLinks = MpLinkDim.getDao().getByMap(filters);
//        if (null != doiLinks && 1 <= doiLinks.size()) {
//            doiLink = doiLinks.get(0);
//        } else {
//            doiLink.setProdId(outPublication.getId().toString());
//        }
//        doiLink.setLinkClass(LinkClass.DIGITAL_OBJECT_IDENTIFIER.getValue());
//        if (null == outPublication.getDoiName() 
//                || (null != outPublication.getPublicationTypeId() 
//                    && (outPublication.getPublicationTypeId().contentEquals(PublicationType.USGS_NUMBERED_SERIES)
//                            || outPublication.getPublicationTypeId().contentEquals(PublicationType.USGS_UNNUMBERED_SERIES)))) {
//            //We no longer (or never) had a doiName OR it is a USGS Series
//            if (null != doiLink.getId()) {
//                //We had better "delete" the existing doiLink
//                MpLinkDim.getDao().delete(doiLink);
//            }
//        } else {
//            doiLink.setLink(MpLinkDim.DOI_LINK_SITE + outPublication.getDoiName());
//            //We have a doiName so...
//            if (null == doiLink.getId()) {
//                //Add a new doiLink
//                MpLinkDim.getDao().add(doiLink);
//            } else {
//                //Update an existing doiLink
//                MpLinkDim.getDao().update(doiLink);
//            }
//        }
        //end bit

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
