package gov.usgs.cida.pubs.busservice.pw;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import gov.usgs.cida.pubs.dao.intfc.IPwPublicationDao;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublicationTest;

//The Dao mocking works because the getDao() methods are all static and JAVA/Spring don't redo them 
//for each reference. This does mean that we need to let Spring know that the context is now dirty...
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class PwPublicationBusServiceTest {

	private PwPublicationBusService service;
	protected PwPublication pub;
	protected Map<String, Object> filters = new HashMap<>();

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
		when(pwPubDao.getByMap(anyMap())).thenReturn(Arrays.asList(pub));
		List<PwPublication> pubs = service.getObjects(filters);
		assertEquals(1, pubs.size());
		assertEquals(pub, pubs.get(0));
		verify(pwPubDao).getByMap(anyMap());
	}

	@Test
	public void getObjectCountTest() {
		when(pwPubDao.getObjectCount(anyMap())).thenReturn(15);
		assertEquals(15, service.getObjectCount(filters).intValue());
		verify(pwPubDao).getObjectCount(anyMap());
	}

	@Test
	public void getByIndexIdTest() {
		when(pwPubDao.getByIndexId(any(String.class))).thenReturn(pub);
		PwPublication pubTest = service.getByIndexId("ab123");
		assertEquals(pub, pubTest);
		verify(pwPubDao).getByIndexId("ab123");
	}

}
