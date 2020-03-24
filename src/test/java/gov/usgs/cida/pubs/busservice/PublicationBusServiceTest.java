package gov.usgs.cida.pubs.busservice;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

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
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublicationTest;
import gov.usgs.cida.pubs.domain.query.MpPublicationFilterParams;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={ConfigurationService.class, PwPublication.class, Publication.class})
public class PublicationBusServiceTest extends BaseTest {

	@Autowired
	protected ConfigurationService configurationService;
	protected PublicationBusService service;
	protected Publication<?> pub;
	protected MpPublicationFilterParams filters = new MpPublicationFilterParams();
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
		when(publicationDao.getByFilter(any(MpPublicationFilterParams.class))).thenReturn(List.of(pub));
		List<Publication<?>> pubs = service.getObjects(filters);
		assertEquals(1, pubs.size());
		assertEquals(pub, pubs.get(0));
		verify(publicationDao).getByFilter(any(MpPublicationFilterParams.class));
	}

	@Test
	public void getObjectCountTest() {
		when(publicationDao.getCountByFilter(any(MpPublicationFilterParams.class))).thenReturn(15);
		assertEquals(15, service.getObjectCount(filters).intValue());
		verify(publicationDao).getCountByFilter(any(MpPublicationFilterParams.class));
	}
}
