package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.OutsideAffiliationDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
public class OutsideAffiliation extends Affiliation<OutsideAffiliation> {

    @Autowired
    @Qualifier("outsideAffiliationDao")
    private static OutsideAffiliationDao outsideAffiliationDao;

    public OutsideAffiliation() {
        usgs = false;
        active = true;
    }

    public static OutsideAffiliationDao getDao() {
        return outsideAffiliationDao;
    }
}
