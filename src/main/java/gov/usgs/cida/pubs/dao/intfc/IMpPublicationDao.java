package gov.usgs.cida.pubs.dao.intfc;

import gov.usgs.cida.pubs.domain.mp.MpPublication;

public interface IMpPublicationDao extends IMpDao<MpPublication> {

    /** 
     * Get the next Prod ID from the database
     * @return the next prodId
     */
    Integer getNewProdId();

    /**
     * Lock a publication by the username.
     * @param domainId - id of the pub to lock.
     */
    void lockPub(Integer domainId);

    /**
     * Release all publication locks held by the username.
     * @param lockUsername for which to remove locks.
     */
    void releaseLocksUser(String lockUsername);

    /**
     * Release publication lock.
     * @param domainId for which to remove lock.
     */
    void releaseLocksPub(Integer domainId);

    /** 
     * Get an mpPublication by it's index id.
     * @param indexId - index id of the object to retrieve. 
     * @return the domain object.
     */
	MpPublication getByIndexId(String indexId);

}
