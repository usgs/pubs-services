package gov.usgs.cida.pubs.busservice.mp;

import gov.usgs.cida.pubs.busservice.intfc.IListBusService;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
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

public class MpPublicationContributorBusService implements IListBusService<PublicationContributor<MpPublicationContributor>> {

    protected final Validator validator;

    @Autowired
    MpPublicationContributorBusService(final Validator validator) {
    	this.validator = validator;
    }

    @Override
    public void merge(Integer parentId, Collection<? super PublicationContributor<MpPublicationContributor>> collection) {
    	if (null != parentId) {
	        //First grab the current collection from the database.
	        Map<String, Object> filters = new HashMap<>();
	        filters.put("publicationId", parentId);
	        List<MpPublicationContributor> mpContribs = MpPublicationContributor.getDao().getByMap(filters);
	
	        //Now put into a map keyed by id.
	        Map<Integer, MpPublicationContributor> map = new HashMap<>();
	        for (MpPublicationContributor pubContrib : mpContribs) {
	            map.put(pubContrib.getId(), pubContrib);
	        }
	
	        //And do the merge.
	        if (null != collection && !collection.isEmpty()) {
	            for (Object pubObject : collection) {
	                MpPublicationContributor pubContrib = (MpPublicationContributor) pubObject;
	                if (map.containsKey(pubContrib.getId())) {
	                    //update and remove from the map.
	                	MpPublicationContributor.getDao().update(pubContrib);
	                    map.remove(pubContrib.getId());
	                } else {
	                    //Add in the new contributor.
	                    MpPublicationContributor.getDao().add(pubContrib);
	                }
	            }
	        }
	
	        //Delete any left overs (would only apply to applications which are not sending the delete like they should.
	        if (!map.isEmpty()) {
	           for (MpPublicationContributor pubContrib : map.values()) {
	               deleteObject(pubContrib);
	            }
	        }
    	}
    }

    @Override
    public ValidationResults deleteObject(PublicationContributor<MpPublicationContributor> object) {
        if (null != object && null != object.getId()) {
            MpPublicationContributor pubContrib = MpPublicationContributor.getDao().getById(object.getId());
	        if (null != pubContrib) {
	            //only try the delete if we found it...
	            Set<ConstraintViolation<MpPublicationContributor>> validations = validator.validate(pubContrib, DeleteChecks.class);
	            if (!validations.isEmpty()) {
	                pubContrib.setValidationErrors(validations);
	                return pubContrib.getValidationErrors();
	            } else {
	                MpPublicationContributor.getDao().delete(pubContrib);
	            }
	        }
        }
        return new MpPublicationContributor().getValidationErrors();
    }

}
