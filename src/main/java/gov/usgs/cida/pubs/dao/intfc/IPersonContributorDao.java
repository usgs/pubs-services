package gov.usgs.cida.pubs.dao.intfc;

import java.util.List;

import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.PersonContributor;

public interface IPersonContributorDao extends IDao<Contributor<?>> {

	/** 
	 * Get domain objects by the ORCID.
	 * @param filter - filter for the contributor record(s) to retrieve. 
	 * @return a list of contributor records, with the preferred record first.
	 */
	List<Contributor<?>> getByPreferred(PersonContributor<?> filter);

	/**
	 * Adds an affiliation to the contributor's list
	 * @param contributorId
	 * @param affiliationId
	 */
	void addAffiliation(Integer contributorId, Integer affiliationId);

	/**
	 * Removes all affiliations from the contributor's list
	 * @param contributorId
	 */
	void removeAffiliations(Integer contributorId);
}
