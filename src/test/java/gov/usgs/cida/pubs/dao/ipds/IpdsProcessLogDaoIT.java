package gov.usgs.cida.pubs.dao.ipds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.ipds.IpdsProcessLog;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, IpdsProcessLogDao.class})
public class IpdsProcessLogDaoIT extends BaseIT {

	@Autowired
	IpdsProcessLogDao ipdsProcessLogDao;

	@Test
	public void testDaoNotNull() throws Exception {
		IDao<IpdsProcessLog> dao = ipdsProcessLogDao;
		assertNotNull(dao);
	}

	@Test
	public void testDao_getById_valueNotFound() throws Exception {
		IDao<IpdsProcessLog> dao = ipdsProcessLogDao;
		Object domain = dao.getById(-1);
		assertTrue(null==domain);
	}

	@Test
	public void testAddGet() {
		IpdsProcessLog log = new IpdsProcessLog();
		log.setUri("inUri");
		log.setIpdsNumber(656);
		log.setMessage("<message/>");
		IpdsProcessLog persisted = ipdsProcessLogDao.getById(ipdsProcessLogDao.add(log));
		assertNotNull(persisted);
		assertNotNull(persisted.getId());
		assertEquals(log.getUri(), persisted.getUri());
		assertEquals(log.getIpdsNumber(), persisted.getIpdsNumber());
	}

}
