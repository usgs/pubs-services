package gov.usgs.cida.pubs.busservice;

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
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.domain.pw.PwPublicationTest;
import java.util.ArrayList;
import java.util.Collection;
import static org.junit.Assert.assertEquals;

//The Dao mocking works because the getDao() methods are all static and JAVA/Spring don't redo them 
//for each reference. This does mean that we need to let Spring know that the context is now dirty...
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class PublicationBusServiceTest extends BaseSpringTest {

	protected PublicationBusService service;
	protected Publication<?> pub;
	protected Map<String, Object> filters = new HashMap<>();
	protected final String WAREHOUSE_ENDPOINT = "https://pubs.er.usgs.gov/";
	@Mock
	protected IPublicationDao publicationDao;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		service = new PublicationBusService(WAREHOUSE_ENDPOINT);
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

	@Test
	public void getIndexPageTest() {
		assertEquals("", service.getIndexPage(null));

		MpPublication pub = new MpPublication();
		assertEquals("", service.getIndexPage(pub));
		
		pub.setIndexId("abcdef123");
		String newUrl = WAREHOUSE_ENDPOINT+"/publication/abcdef123";
		assertEquals(newUrl, service.getIndexPage(pub));
		
		Collection<PublicationLink<?>> links = new ArrayList<>();
		pub.setLinks(links);
		assertEquals(newUrl, service.getIndexPage(pub));

		PublicationLink<?> link = new MpPublicationLink();
		links.add(link);
		assertEquals(newUrl, service.getIndexPage(pub));

		link.setUrl("xyz");
		assertEquals(newUrl, service.getIndexPage(pub));

		LinkType linkType = new LinkType();
		linkType.setId(LinkType.THUMBNAIL);
		link.setLinkType(linkType);
		assertEquals(newUrl, service.getIndexPage(pub));

		PublicationLink<?> link2 = new MpPublicationLink();
		LinkType linkType2 = new LinkType();
		linkType2.setId(LinkType.INDEX_PAGE);
		link2.setLinkType(linkType2);
		link2.setUrl("http://pubs.usgs.gov/of/2013/1259/");
		links.add(link2);
		assertEquals("http://pubs.usgs.gov/of/2013/1259/", service.getIndexPage(pub));
	}
}
