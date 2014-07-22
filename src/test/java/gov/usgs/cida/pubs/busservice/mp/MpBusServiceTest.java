package gov.usgs.cida.pubs.busservice.mp;

import static org.junit.Assert.assertNull;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDaoTest;
import gov.usgs.cida.pubs.domain.mp.MpPublication;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class MpBusServiceTest extends BaseSpringTest {

    private class TMpBusService extends MpBusService<TMpBusService> {

    }

    @Test
    public void beginPublicationEditTest() {
        //This one should change nothing
        TMpBusService busService = new TMpBusService();
        MpPublication mpPub2before = MpPublication.getDao().getById(2);
        busService.beginPublicationEdit(mpPub2before.getId());
        MpPublication mpPub2after = MpPublication.getDao().getById(mpPub2before.getId());
        List<String> ignore = Arrays.asList("validationErrors");
        assertDaoTestResults(MpPublication.class, mpPub2before, mpPub2after, ignore, false, false);

        //This one is in PW, not MP and should be moved
        MpPublication mpPub4before = MpPublication.getDao().getById(4);
        assertNull(mpPub4before);
        busService.beginPublicationEdit(4);
        MpPublication mpPub4after = MpPublication.getDao().getById(4);
        PwPublicationDaoTest.assertPwPub4(mpPub4after);
//        assertPwPub4Children(mpPub4after);
    }

}
