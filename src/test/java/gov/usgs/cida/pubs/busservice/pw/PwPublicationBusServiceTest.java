package gov.usgs.cida.pubs.busservice.pw;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import gov.usgs.cida.pubs.dao.intfc.IPwPublicationDao;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublicationTest;

public class PwPublicationBusServiceTest {

	private PwPublicationBusService service;
	protected PwPublication pub;

	@Mock
	protected IPwPublicationDao pwPubDao;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		service = new PwPublicationBusService();
		pub = PwPublicationTest.buildAPub(1);
		pub.setPwPublicationDao(pwPubDao);
	}


	@Test
	public void getObjectTest() {
		when(pwPubDao.getById(any(Integer.class))).thenReturn(pub);
		PwPublication pubTest = service.getObject(5);
		assertEquals(pub, pubTest);
		verify(pwPubDao).getById(5);
	}

	@Test
	public void getObjectsTest() {
		when(pwPubDao.getByMap(anyMapOf(String.class, Object.class))).thenReturn(Arrays.asList(pub));
		List<PwPublication> pubs = service.getObjects(null);
		assertEquals(1, pubs.size());
		assertEquals(pub, pubs.get(0));
		verify(pwPubDao).getByMap(anyMapOf(String.class, Object.class));
	}

	@Test
	public void getObjectCountTest() {
		when(pwPubDao.getObjectCount(anyMapOf(String.class, Object.class))).thenReturn(15);
		assertEquals(15, service.getObjectCount(null).intValue());
		verify(pwPubDao).getObjectCount(anyMapOf(String.class, Object.class));
	}

	@Test
	public void getByIndexIdTest() {
		when(pwPubDao.getByIndexId(any(String.class))).thenReturn(pub);
		PwPublication pubTest = service.getByIndexId("ab123");
		assertEquals(pub, pubTest);
		verify(pwPubDao).getByIndexId("ab123");
	}

}
