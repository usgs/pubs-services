package gov.usgs.cida.pubs.busservice.mp;

import gov.usgs.cida.pubs.busservice.intfc.IListBusService;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
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

public class MpPublicationLinkBusService implements IListBusService<PublicationLink<MpPublicationLink>> {

    protected final Validator validator;

    @Autowired
    MpPublicationLinkBusService(final Validator validator) {
    	this.validator = validator;
    }

    @Override
    public void merge(Integer parentId, Collection<? super PublicationLink<MpPublicationLink>> collection) {
    	if (null != parentId) {
	        //First grab the current collection from the database.
	        Map<String, Object> filters = new HashMap<>();
	        filters.put("publicationId", parentId);
	        List<MpPublicationLink> mpLinks = MpPublicationLink.getDao().getByMap(filters);
	
	        //Now put into a map keyed by id.
	        Map<Integer, MpPublicationLink> map = new HashMap<>();
	        for (MpPublicationLink pubLink : mpLinks) {
	            map.put(pubLink.getId(), pubLink);
	        }
	
	        //And do the merge.
	        if (null != collection && !collection.isEmpty()) {
	            for (Object pubObject : collection) {
	                MpPublicationLink pubLink = (MpPublicationLink) pubObject;
	                if (map.containsKey(pubLink.getId())) {
	                    //update and remove from the map.
	                	MpPublicationLink.getDao().update(pubLink);
	                    map.remove(pubLink.getId());
	                } else {
	                    //Add in the new link.
	                    MpPublicationLink.getDao().add(pubLink);
	                }
	            }
	        }
	
	        //Delete any left overs (would only apply to applications which are not sending the delete like they should.
	        if (!map.isEmpty()) {
	           for (MpPublicationLink pubLink : map.values()) {
	               deleteObject(pubLink);
	            }
	        }
    	}
    }

    @Override
    public ValidationResults deleteObject(PublicationLink<MpPublicationLink> object) {
        if (null != object && null != object.getId()) {
            MpPublicationLink pubLink = MpPublicationLink.getDao().getById(object.getId());
	        if (null != pubLink) {
	            //only try the delete if we found it...
	            Set<ConstraintViolation<MpPublicationLink>> validations = validator.validate(pubLink, DeleteChecks.class);
	            if (!validations.isEmpty()) {
	                pubLink.setValidationErrors(validations);
	                return pubLink.getValidationErrors();
	            } else {
	                MpPublicationLink.getDao().delete(pubLink);
	            }
	        }
        }
        return new MpPublicationLink().getValidationErrors();
    }


}
