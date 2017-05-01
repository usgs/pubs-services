package gov.usgs.cida.pubs.webservice.pw;

import freemarker.template.Configuration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.entity.mime.MIME;
import org.apache.ibatis.session.ResultHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.busservice.intfc.IPublicationBusService;
import gov.usgs.cida.pubs.busservice.intfc.IPwPublicationBusService;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.SearchResults;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import org.springframework.mock.web.MockHttpServletRequest;

public class PwPublicationMvcServiceTest extends BaseSpringTest {

	@Autowired
	public String warehouseEndpoint;
	@Mock
	private IPwPublicationBusService busService;
	@Mock
	private PwPublicationMvcService mvcService;

	private Map<String, Object> filters;
	
	@Mock
	private Configuration templateConfiguration;
	
	@Mock
	private IPublicationBusService pubBusService;
	
	private final String TEST_EMAIL = "nobody@usgs.gov";
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvcService = new PwPublicationMvcService(
			busService,
			warehouseEndpoint,
			templateConfiguration,
			TEST_EMAIL,
			pubBusService
		);

		filters = new HashMap<>();
		filters.put(BaseDao.PAGE_SIZE, "13");
		filters.put(BaseDao.PAGE_ROW_START, "4");
		filters.put(BaseDao.PAGE_NUMBER, "8");
	}

	@Test
	public void getCountAndPagingTest() {
		when(busService.getObjectCount(anyMap())).thenReturn(18);

		SearchResults sr = mvcService.getCountAndPaging(filters);
		assertEquals("13", sr.getPageSize());
		assertEquals("4", sr.getPageRowStart());
		assertEquals("8", sr.getPageNumber());
		assertEquals(18, sr.getRecordCount().intValue());
		assertNull(sr.getRecords());

		filters.remove(BaseDao.PAGE_NUMBER);

		sr = mvcService.getCountAndPaging(filters);
		assertEquals("13", sr.getPageSize());
		assertEquals("4", sr.getPageRowStart());
		assertNull(sr.getPageNumber());
		assertEquals(18, sr.getRecordCount().intValue());
		assertNull(sr.getRecords());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void streamResultsTsvTest() {
		HttpServletResponse response = new MockHttpServletResponse();
		mvcService.streamResults(filters, PubsConstants.MEDIA_TYPE_TSV_EXTENSION, response);
		assertEquals(PubsConstants.DEFAULT_ENCODING, response.getCharacterEncoding());
		assertEquals("attachment; filename=publications.tsv", response.getHeader(MIME.CONTENT_DISPOSITION));
		assertEquals(PubsConstants.MEDIA_TYPE_TSV_VALUE, response.getContentType());
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		verify(busService).stream(eq("pwPublication.getStreamByMap"), anyMap(), any(ResultHandler.class));
		verify(busService, never()).getObjectCount(anyMap());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void streamResultsCsvTest() {
		HttpServletResponse response = new MockHttpServletResponse();
		mvcService.streamResults(filters, PubsConstants.MEDIA_TYPE_CSV_EXTENSION, response);
		assertEquals(PubsConstants.DEFAULT_ENCODING, response.getCharacterEncoding());
		assertEquals("attachment; filename=publications.csv", response.getHeader(MIME.CONTENT_DISPOSITION));
		assertEquals(PubsConstants.MEDIA_TYPE_CSV_VALUE, response.getContentType());
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		verify(busService).stream(eq("pwPublication.getStreamByMap"), anyMap(), any(ResultHandler.class));
		verify(busService, never()).getObjectCount(anyMap());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void streamResultsXlsxTest() {
		HttpServletResponse response = new MockHttpServletResponse();
		mvcService.streamResults(filters, PubsConstants.MEDIA_TYPE_XLSX_EXTENSION, response);
		assertEquals(PubsConstants.DEFAULT_ENCODING, response.getCharacterEncoding());
		assertEquals("attachment; filename=publications.xlsx", response.getHeader(MIME.CONTENT_DISPOSITION));
		assertEquals(PubsConstants.MEDIA_TYPE_XLSX_VALUE, response.getContentType());
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		verify(busService).stream(eq("pwPublication.getStreamByMap"), anyMap(), any(ResultHandler.class));
		verify(busService, never()).getObjectCount(anyMap());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void streamResultsJsonTest() {
		HttpServletResponse response = new MockHttpServletResponse();
		mvcService.streamResults(filters, PubsConstants.MEDIA_TYPE_JSON_EXTENSION, response);
		assertEquals(PubsConstants.DEFAULT_ENCODING, response.getCharacterEncoding());
		assertEquals(MediaType.APPLICATION_JSON_UTF8_VALUE, response.getContentType());
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		verify(busService).stream(eq("pwPublication.getByMap"), anyMap(), any(ResultHandler.class));
		verify(busService).getObjectCount(anyMap());
	}
	
	@Test
	public void testCrossrefPubNotFound() throws IOException {
		IPwPublicationBusService mockBusService = mock(IPwPublicationBusService.class);
		when(mockBusService.getByIndexId(anyString())).thenReturn(null);
		PwPublicationMvcService instance = new PwPublicationMvcService(
			mockBusService,
			warehouseEndpoint,
			templateConfiguration,
			TEST_EMAIL,
			pubBusService
		);
		HttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();
		
		instance.getPwPublicationCrossRef(request, response, "non-existent pub");
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
	}
	
	@Test
	public void testCrossrefNonUsgsSeriesPubFound() throws IOException {
		IPwPublicationBusService mockBusService = mock(IPwPublicationBusService.class);
		PwPublication nonUsgsSeriesPub = new PwPublication();
		PublicationSubtype subtype = new PublicationSubtype();
		subtype.setId(PublicationSubtype.USGS_DATA_RELEASE);
		nonUsgsSeriesPub.setPublicationSubtype(subtype);
		when(mockBusService.getByIndexId(anyString())).thenReturn(nonUsgsSeriesPub);
		PwPublicationMvcService instance = new PwPublicationMvcService(
			mockBusService,
			warehouseEndpoint,
			templateConfiguration,
			TEST_EMAIL,
			pubBusService
		);
		HttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();
		
		instance.getPwPublicationCrossRef(request, response, "existent non-USGS series pub");
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
	}
	
	@Test
	public void testCrossrefUsgsSeriesPubFound() throws IOException {
		IPwPublicationBusService mockBusService = mock(IPwPublicationBusService.class);
		PwPublication usgsSeriesPub = new PwPublication();
		PublicationSubtype subtype = new PublicationSubtype();
		subtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		usgsSeriesPub.setPublicationSubtype(subtype);
		when(mockBusService.getByIndexId(anyString())).thenReturn(usgsSeriesPub);
		PwPublicationMvcService instance = new PwPublicationMvcService(
			mockBusService,
			warehouseEndpoint,
			templateConfiguration,
			TEST_EMAIL,
			pubBusService
		) { 
			/**
			 * We're testing the conditional logic of the MvcService,
			 * not the transformation from pub to Crossref XML, so 
			 * we set a non-failing status code.
			 */
			@Override
			protected void writeCrossrefForPub(HttpServletResponse response, PwPublication pub) throws IOException {
				response.setStatus(HttpStatus.OK.value());
				response.getOutputStream().println("OK");
				response.getOutputStream().close();
			}
		};
		HttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();
		
		instance.getPwPublicationCrossRef(request, response, "existent non-USGS series pub");
		assertEquals(HttpStatus.OK.value(), response.getStatus());
	}

}
