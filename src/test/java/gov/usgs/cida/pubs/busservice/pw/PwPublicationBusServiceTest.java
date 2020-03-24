package gov.usgs.cida.pubs.busservice.pw;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
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
import gov.usgs.cida.pubs.dao.pw.PwPublicationDao;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublicationTest;
import gov.usgs.cida.pubs.domain.query.IFilterParams;
import gov.usgs.cida.pubs.domain.query.PwPublicationFilterParams;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={PwPublicationBusService.class, PwPublication.class,
			ConfigurationService.class, PwPublicationFilterParams.class})
public class PwPublicationBusServiceTest extends BaseTest {

	private class TestResultHandler<T> implements ResultHandler<T> {
		public ArrayList<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		@Override
		@SuppressWarnings("unchecked")
		public void handleResult(ResultContext<? extends T> context) {
			results.add((Map<String, Object>) context.getResultObject());
		}
	}

	@Autowired
	protected PwPublicationBusService service;
	protected PwPublication pub;
	protected PwPublicationFilterParams filters;

	@MockBean(name="pwPublicationDao")
	protected IPwPublicationDao pwPublicationDao;
	@MockBean(name="publicationDao")
	protected IPublicationDao publicationDao;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		service = new PwPublicationBusService();
		pub = PwPublicationTest.buildAPub(1);
		filters = new PwPublicationFilterParams();
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
	public void getObjectCountTest() {
		when(pwPublicationDao.getCountByFilter(any(IFilterParams.class))).thenReturn(15);
		assertEquals(15, service.getObjectCount(filters).intValue());
		verify(pwPublicationDao).getCountByFilter(any(IFilterParams.class));
	}

	@Test
	public void getByIndexIdTest() {
		when(pwPublicationDao.getByIndexId(any(String.class))).thenReturn(pub);
		PwPublication pubTest = service.getByIndexId("ab123");
		assertEquals(pub, pubTest);
		verify(pwPublicationDao).getByIndexId("ab123");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void streamTest() {
		TestResultHandler<PwPublication> handler = new TestResultHandler<>();
		service.stream(PwPublicationDao.NS + PwPublicationDao.GET_STREAM_BY_MAP, filters, handler);
		verify(pwPublicationDao).stream(any(String.class),
				any(PwPublicationFilterParams.class), any(ResultHandler.class));
	}

	@Test
	public void getObjectsTest() {
		when(pwPublicationDao.getByFilter(any(IFilterParams.class))).thenReturn(List.of(pub));
		List<PwPublication> pubs = service.getObjects(filters);
		assertEquals(1, pubs.size());
		assertEquals(pub, pubs.get(0));
		verify(pwPublicationDao).getByFilter(any(IFilterParams.class));
	}
}
