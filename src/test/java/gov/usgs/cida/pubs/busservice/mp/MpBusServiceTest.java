package gov.usgs.cida.pubs.busservice.mp;

import static org.junit.Assert.assertNull;
import gov.usgs.cida.pubs.dao.BaseSpringDaoTest;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDaoTest;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDaoTest;
import gov.usgs.cida.pubs.domain.mp.MpPublication;

import org.junit.Test;

public class MpBusServiceTest extends BaseSpringDaoTest {

    private class TMpBusService extends MpBusService<TMpBusService> {

    }

    @Test
    public void beginPublicationEditTest() {
        //This one should change nothing
        TMpBusService busService = new TMpBusService();
        busService.beginPublicationEdit(2);
        MpPublication mpPub2after = MpPublication.getDao().getById(2);
        MpPublicationDaoTest.assertMpPub2(mpPub2after);
        MpPublicationDaoTest.assertMpPub2Children(mpPub2after);

        //This one is in PW, not MP and should be moved
        MpPublication mpPub4before = MpPublication.getDao().getById(4);
        assertNull(mpPub4before);
        busService.beginPublicationEdit(4);
        MpPublication mpPub4after = MpPublication.getDao().getById(4);
        PwPublicationDaoTest.assertPwPub4(mpPub4after);
        PwPublicationDaoTest.assertPwPub4Children(mpPub4after);
    }

}
