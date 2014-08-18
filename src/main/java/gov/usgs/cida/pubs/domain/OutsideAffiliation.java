package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.intfc.IDao;


public class OutsideAffiliation extends Affiliation<OutsideAffiliation> {

    public OutsideAffiliation() {
        usgs = false;
    }

    private static IDao<Affiliation<?>> outsideAffiliationDao;

    public static IDao<Affiliation<?>> getDao() {
        return outsideAffiliationDao;
    }

    public void setOutsideAffiliationDao(final IDao<Affiliation<?>> inOutsideAffiliationDao) {
        outsideAffiliationDao = inOutsideAffiliationDao;
    }

}
