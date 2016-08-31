package gov.usgs.cida.pubs.dao.intfc;

import gov.usgs.cida.pubs.domain.Contributor;

public interface IPersonContributorDao extends IDao<Contributor<?>> {

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
