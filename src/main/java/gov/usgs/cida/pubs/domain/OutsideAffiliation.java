package gov.usgs.cida.pubs.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.usgs.cida.pubs.dao.intfc.IDao;


@Component
public class OutsideAffiliation extends Affiliation<OutsideAffiliation> {

    private static IDao<Affiliation<?>> outsideAffiliationDao;

    public OutsideAffiliation() {
        usgs = false;
        active = true;
    }

    public static IDao<Affiliation<?>> getDao() {
        return outsideAffiliationDao;
    }

    @Autowired
    @Qualifier("outsideAffiliationDao")
    public void setOutsideAffiliationDao(final IDao<Affiliation<?>> inOutsideAffiliationDao) {
        outsideAffiliationDao = inOutsideAffiliationDao;
    }

}
