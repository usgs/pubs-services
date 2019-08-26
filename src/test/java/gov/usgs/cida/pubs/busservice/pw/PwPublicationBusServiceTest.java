package gov.usgs.cida.pubs.busservice.pw;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.dao.intfc.IPublicationDao;
import gov.usgs.cida.pubs.dao.intfc.IPwPublicationDao;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublicationTest;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={PwPublicationBusService.class, PwPublication.class})
public class PwPublicationBusServiceTest extends BaseTest {

	@Autowired
	protected PwPublicationBusService service;
	protected PwPublication pub;
	protected Map<String, Object> filters = new HashMap<>();

	@MockBean(name="pwPublicationDao")
	protected IPwPublicationDao pwPublicationDao;
	@MockBean(name="publicationDao")
	protected IPublicationDao publicationDao;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		service = new PwPublicationBusService();
		pub = PwPublicationTest.buildAPub(1);

		reset(pwPublicationDao, publicationDao);
	}


	@Test
	public void getObjectTest() {
		when(pwPublicationDao.getById(any(Integer.class))).thenReturn(pub);
		PwPublication pubTest = service.getObject(5);
		assertEquals(pub, pubTest);
		verify(pwPublicationDao).getById(5);
	}

	@Test
	public void getObjectsTest() {
		when(pwPublicationDao.getByMap(anyMap())).thenReturn(List.of(pub));
		List<PwPublication> pubs = service.getObjects(filters);
		assertEquals(1, pubs.size());
		assertEquals(pub, pubs.get(0));
		verify(pwPublicationDao).getByMap(anyMap());
	}

	@Test
	public void getObjectCountTest() {
		when(pwPublicationDao.getObjectCount(anyMap())).thenReturn(15);
		assertEquals(15, service.getObjectCount(filters).intValue());
		verify(pwPublicationDao).getObjectCount(anyMap());
	}

	@Test
	public void getByIndexIdTest() {
		when(pwPublicationDao.getByIndexId(any(String.class))).thenReturn(pub);
		PwPublication pubTest = service.getByIndexId("ab123");
		assertEquals(pub, pubTest);
		verify(pwPublicationDao).getByIndexId("ab123");
	}

}
