package gov.usgs.cida.pubs.dao.intfc;


/**
 * @author drsteini
 *
 */
public interface IMpDao<D> extends IDao<D> {

    /** 
     * Copy domain object from pw to mp.
     * @param prodID ID of the publication we are copying info for.
     */
    void copyFromPw(Integer prodID);

    /** 
     * Publish domain object to pw from mp.
     * @param prodID ID of the publication we are publishing info for.
     */
    void publishToPw(Integer prodID);

}
