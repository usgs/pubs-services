package gov.usgs.cida.pubs.webservice.pw;

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
import gov.usgs.cida.pubs.busservice.intfc.IPwPublicationBusService;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.SearchResults;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.transform.TransformerFactory;
import gov.usgs.cida.pubs.transform.intfc.ITransformer;
import java.io.IOException;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import org.springframework.web.accept.ContentNegotiationStrategy;

public class PwPublicationMvcServiceTest extends BaseSpringTest {

	@Autowired
	public String warehouseEndpoint;
	@Mock
	private IPwPublicationBusService busService;
	@Mock
	private PwPublicationMvcService mvcService;

	private Map<String, Object> filters;
	
	@Autowired
	private ContentNegotiationStrategy contentStrategy;
	
	@Mock
	private TransformerFactory transformerFactory;
	@Mock
	private TransformerFactory mockTransformerFactory;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		/**
		 * Since we're testing the conditional logic of the MvcService,
		 * rather than the transformation from pub to XML, CSV, etc., we
		 * always return a no-op transformer from the transformer factory
		 */
		when(transformerFactory.getTransformer(anyString(), any(), any())).thenReturn(mock(ITransformer.class));
		mvcService = new PwPublicationMvcService(
			busService,
			warehouseEndpoint,
			contentStrategy,
			transformerFactory
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
		when(busService.getByIndexId(anyString())).thenReturn(null);
		HttpServletResponse response = new MockHttpServletResponse();
		mvcService.getPwPublicationCrossRef("non-existent pub", response);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
	}
	
	@Test
	public void testCrossrefNonUsgsSeriesPubFound() throws IOException {
		PwPublication nonUsgsSeriesPub = new PwPublication();
		PublicationSubtype subtype = new PublicationSubtype();
		
		//can use any PublicationSubtype other than USGS_NUMBERED_SERIES
		//and USGS_UNNUMBERED_SERIES
		subtype.setId(PublicationSubtype.USGS_DATA_RELEASE);
		nonUsgsSeriesPub.setPublicationSubtype(subtype);
		when(busService.getByIndexId(anyString())).thenReturn(nonUsgsSeriesPub);

		HttpServletResponse response = new MockHttpServletResponse();
		
		mvcService.getPwPublicationCrossRef("existent non-USGS series pub", response);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
	}
	
	@Test
	public void testCrossrefUsgsSeriesPubFound() throws IOException {
		/**
		 * Since we're testing the conditional logic of the MvcService,
		 * rather than the retrieval of the correct publications through
		 * the business service, we return a mock publication
		 */
		PwPublication usgsSeriesPub = new PwPublication();
		PublicationSubtype subtype = new PublicationSubtype();
		subtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		usgsSeriesPub.setPublicationSubtype(subtype);
		when(busService.getByIndexId(anyString())).thenReturn(usgsSeriesPub);
		
		HttpServletResponse response = new MockHttpServletResponse();
		mvcService.getPwPublicationCrossRef("existent non-USGS series pub", response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
	}

}
