package gov.usgs.cida.pubs.dao.ipds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import gov.usgs.cida.pubs.dao.BaseSpringDaoTest;
import gov.usgs.cida.pubs.domain.ipds.IpdsPubTypeConv;

import org.junit.Test;

public class IpdsPubTypeConvDaoTest extends BaseSpringDaoTest {

    @Test
    public void getByIpdsValueTest() {
        assertNull(IpdsPubTypeConv.getDao().getByIpdsValue(null));

        IpdsPubTypeConv conv = IpdsPubTypeConv.getDao().getByIpdsValue("Atlas");
        assertEquals(1, conv.getId().intValue());
        assertEquals(4, conv.getPublicationType().getId().intValue());
        assertNull(conv.getPublicationSubtype());

        conv = IpdsPubTypeConv.getDao().getByIpdsValue("USGS series");
        assertEquals(13, conv.getId().intValue());
        assertEquals(18, conv.getPublicationType().getId().intValue());
        assertEquals(5, conv.getPublicationSubtype().getId().intValue());
    }
}
