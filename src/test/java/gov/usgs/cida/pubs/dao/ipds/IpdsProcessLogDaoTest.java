package gov.usgs.cida.pubs.dao.ipds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.dao.BaseDaoTest;
import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.ipds.IpdsProcessLog;

import org.junit.Ignore;
import org.junit.Test;

//TODO Why am I not rolling back tests?
@Ignore
public class IpdsProcessLogDaoTest extends BaseDaoTest {

    @Test
    public void testDaoNotNull() throws Exception {
        IDao<IpdsProcessLog> dao = IpdsProcessLog.getDao();
        assertNotNull(dao);
    }

    @Test
    public void testDao_getById_valueNotFound() throws Exception {
        IDao<IpdsProcessLog> dao = IpdsProcessLog.getDao();
        Object domain = dao.getById(-1);
        assertTrue(null==domain);
    }

    @Test
    public void testAddGet() {
        IpdsProcessLog log = new IpdsProcessLog();
        log.setUri("inUri");
        log.setIpdsNumber("ipdsNumber");
        log.setMessage("<message/>");
        IpdsProcessLog persisted = IpdsProcessLog.getDao().getById(IpdsProcessLog.getDao().add(log));
        assertNotNull(persisted);
        assertNotNull(persisted.getId());
        assertEquals(log.getUri(), persisted.getUri());
        assertEquals(log.getIpdsNumber(), persisted.getIpdsNumber());
    }

}
