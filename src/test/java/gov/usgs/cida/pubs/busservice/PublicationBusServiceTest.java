package gov.usgs.cida.pubs.busservice;

import static org.junit.Assert.assertEquals;
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

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.dao.intfc.IPublicationDao;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.pw.PwPublicationTest;

//The Dao mocking works because the getDao() methods are all static and JAVA/Spring don't redo them 
//for each reference. This does mean that we need to let Spring know that the context is now dirty...
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class PublicationBusServiceTest extends BaseSpringTest {

	protected PublicationBusService service;
	protected Publication<?> pub;
	protected Map<String, Object> filters = new HashMap<>();
	
	@Mock
	protected IPublicationDao publicationDao;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		service = new PublicationBusService();
		pub = PwPublicationTest.buildAPub(1);
		pub.setPublicationDao(publicationDao);
	}

	@Test
	public void getObjectsTest() {
		when(publicationDao.getByMap(anyMap())).thenReturn(Arrays.asList(pub));
		List<Publication<?>> pubs = service.getObjects(filters);
		assertEquals(1, pubs.size());
		assertEquals(pub, pubs.get(0));
		verify(publicationDao).getByMap(anyMap());
	}

	@Test
	public void getObjectCountTest() {
		when(publicationDao.getObjectCount(anyMap())).thenReturn(15);
		assertEquals(15, service.getObjectCount(filters).intValue());
		verify(publicationDao).getObjectCount(anyMap());
	}

}
