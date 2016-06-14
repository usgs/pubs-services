package gov.usgs.cida.pubs.busservice;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

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
//for each reference. This does mean that we nned to let Spring know that the context is now dirty...
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class PublicationBusServiceTest extends BaseSpringTest {

	protected PublicationBusService service;
	protected Publication<?> pub;

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
		when(publicationDao.getByMap(anyMapOf(String.class, Object.class))).thenReturn(Arrays.asList(pub));
		List<Publication<?>> pubs = service.getObjects(null);
		assertEquals(1, pubs.size());
		assertEquals(pub, pubs.get(0));
		verify(publicationDao).getByMap(anyMapOf(String.class, Object.class));
	}

	@Test
	public void getObjectCountTest() {
		when(publicationDao.getObjectCount(anyMapOf(String.class, Object.class))).thenReturn(15);
		assertEquals(15, service.getObjectCount(null).intValue());
		verify(publicationDao).getObjectCount(anyMapOf(String.class, Object.class));
	}

}
