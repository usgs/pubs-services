package gov.usgs.cida.pubs.busservice;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
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
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.dao.intfc.IPublicationDao;
import gov.usgs.cida.pubs.dao.intfc.IPwPublicationDao;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublicationTest;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={ConfigurationService.class, PwPublication.class, Publication.class})
public class PublicationBusServiceTest extends BaseTest {

	@Autowired
	protected ConfigurationService configurationService;
	protected PublicationBusService service;
	protected Publication<?> pub;
	protected Map<String, Object> filters = new HashMap<>();
	@MockBean(name="publicationDao")
	protected IPublicationDao publicationDao;
	@MockBean(name="pwPublicationDao")
	protected IPwPublicationDao pwPublicationDao;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		service = new PublicationBusService(configurationService);
		pub = PwPublicationTest.buildAPub(1);
		pub.setPublicationDao(publicationDao);

		reset(pwPublicationDao, publicationDao);
	}

	@Test
	public void getObjectsTest() {
		when(publicationDao.getByMap(anyMap())).thenReturn(List.of(pub));
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

	@Test
	public void getWarehousePageTest() {
		assertEquals("", service.getWarehousePage(null));

		MpPublication pub = new MpPublication();
		assertEquals("", service.getWarehousePage(pub));
		
		pub.setIndexId("abcdef123");
		String newUrl = configurationService.getWarehouseEndpoint()+"/publication/abcdef123";
		assertEquals(newUrl, service.getWarehousePage(pub));

	}
}
