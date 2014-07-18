package gov.usgs.cida.pubs.dao.intfc;

import gov.usgs.cida.pubs.domain.mp.MpPublication;

public interface IMpPublicationDao extends IMpDao<MpPublication>{

    /** 
     * Get the next Prod ID from the database
     * @return the next prodId
     */
    Integer getNewProdId();

}
