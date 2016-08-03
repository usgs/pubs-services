package gov.usgs.cida.pubs.dao.intfc;


/**
 * @author drsteini
 *
 */
public interface IMpDao<D> extends IDao<D> {

	/** 
	 * Copy domain object from pw to mp.
	 * @param publicationId ID of the publication we are copying info for.
	 */
	void copyFromPw(Integer publicationId);

	/** 
	 * Publish domain object to pw from mp.
	 * @param publicationId ID of the publication we are publishing info for.
	 */
	void publishToPw(Integer publicationId);

}
