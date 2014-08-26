package gov.usgs.cida.pubs.busservice.mp;

import gov.usgs.cida.pubs.busservice.intfc.IListBusService;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.validation.constraint.DeleteChecks;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;

public class MpPublicationCostCenterBusService implements IListBusService<PublicationCostCenter<MpPublicationCostCenter>> {

    protected Validator validator;

    @Autowired
    MpPublicationCostCenterBusService(final Validator validator) {
        this.validator = validator;
    }

    /** 
     * Cost center merge needs to deal with the association id not being preserved.
     */
    @Override
    public void merge(Integer parentId, Collection<? super PublicationCostCenter<MpPublicationCostCenter>> collection) {
        //First grab the current collection from the database.
        Map<String, Object> filters = new HashMap<>();
        filters.put("publicationId", parentId);
        List<MpPublicationCostCenter> mpccs = MpPublicationCostCenter.getDao().getByMap(filters);

        //Now put into a map keyed by costCenterId.
        Map<Integer, MpPublicationCostCenter> map = new HashMap<>();
        for (MpPublicationCostCenter pubCostCenter : mpccs) {
            map.put(pubCostCenter.getCostCenter().getId(), pubCostCenter);
        }

        //And do the merge.
        if (null != collection && 0 < collection.size()) {
            for (Object pubObject : collection) {
                PublicationCostCenter<?> pubCostCenter = (PublicationCostCenter<?>) pubObject;
                if (map.containsKey(pubCostCenter.getCostCenter().getId())) {
                    //Just remove from the map - there isn't really anything to update.
                    map.remove(pubCostCenter.getCostCenter().getId());
                } else {
                    //Add in the new cost center.
                    //pubCostCenter.setPublicationId(parentId);
                    MpPublicationCostCenter pcc = new MpPublicationCostCenter();
                    pcc.setPublicationId(parentId);
                    pcc.setCostCenter(pubCostCenter.getCostCenter());
                    //MpPublicationCostCenter.getDao().add(pubCostCenter);
                    MpPublicationCostCenter.getDao().add(pcc);
                }
            }
        }

        //Delete any left overs (would only apply to applications which are not sending the delete like they should.
        if (!map.isEmpty()) {
           for (MpPublicationCostCenter pubCostCenter : map.values()) {
               deleteObject(pubCostCenter);
            }
        }
    }

    /** 
     * Cost center delete needs to deal with the association id possibly not being preserved.
     */
    @Override
    public ValidationResults deleteObject(PublicationCostCenter<MpPublicationCostCenter> object) {
        MpPublicationCostCenter pubCostCenter = null;
        if (null != object && null != object.getId()) {
            pubCostCenter = MpPublicationCostCenter.getDao().getById(object.getId());
        } else if (null != object && null != object.getPublicationId() && null != object.getCostCenter().getId()) {
            Map<String, Object> filters = new HashMap<>();
            filters.put("publicationId", object.getPublicationId());
            filters.put("costCenterId", object.getCostCenter().getId());
            List<MpPublicationCostCenter> pubCostCenters = MpPublicationCostCenter.getDao().getByMap(filters);
            if (null != pubCostCenters && 0 < pubCostCenters.size()) {
                //we should really only have one.
                pubCostCenter = pubCostCenters.get(0);
            }
        }
        if (null == pubCostCenter) {
            pubCostCenter = new MpPublicationCostCenter();
        } else {
            //only delete if we found it...
            Set<ConstraintViolation<MpPublicationCostCenter>> validations = validator.validate(pubCostCenter, DeleteChecks.class);
            if (!validations.isEmpty()) {
                pubCostCenter.setValidationErrors(validations);
            } else {
                MpPublicationCostCenter.getDao().delete(pubCostCenter);
            }
        }
        return pubCostCenter.getValidationErrors();
    }

}
