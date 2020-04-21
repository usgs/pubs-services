package gov.usgs.cida.pubs.webservice.pw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.entity.mime.MIME;
import org.apache.ibatis.session.ResultHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;

import freemarker.template.Configuration;
import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.busservice.intfc.IPwPublicationBusService;
import gov.usgs.cida.pubs.busservice.intfc.IXmlBusService;
import gov.usgs.cida.pubs.dao.ContributorTypeDao;
import gov.usgs.cida.pubs.dao.ContributorTypeDaoIT;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.SearchResults;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.query.PwPublicationFilterParams;

@ContextConfiguration(classes = FreeMarkerAutoConfiguration.class)
@SpringBootTest(webEnvironment=WebEnvironment.MOCK,
	classes={ConfigurationService.class, ContributorType.class,
			PwPublicationFilterParams.class})
public class PwPublicationMvcServiceTest extends BaseTest {

	@Autowired
	public ConfigurationService configurationService;
	@MockBean
	private ContentNegotiationStrategy contentStrategy;
	@MockBean
	private IXmlBusService xmlBusService;

	@MockBean
	private IPwPublicationBusService busService;

	private PwPublicationMvcService mvcService;
	private PwPublicationFilterParams filters;

	@Autowired
	private Configuration templateConfiguration;
	@MockBean(name="contributorTypeDao")
	private ContributorTypeDao contributorTypeDao;

	private MockHttpServletRequest request;

	@BeforeEach
	public void setup() {
		mvcService = new PwPublicationMvcService(
			busService,
			xmlBusService,
			configurationService,
			templateConfiguration,
			contentStrategy
		);

		filters = new PwPublicationFilterParams();
		filters.setPage_size("13");
		filters.setPage_row_start("4");
		filters.setPage_number("8");
		filters.setMimetype("json");
		when(contributorTypeDao.getById(ContributorType.AUTHORS)).thenReturn(ContributorTypeDaoIT.getAuthor());
		when(contributorTypeDao.getById(ContributorType.EDITORS)).thenReturn(ContributorTypeDaoIT.getEditor());
		when(contributorTypeDao.getById(ContributorType.COMPILERS)).thenReturn(ContributorTypeDaoIT.getCompiler());
		reset(busService);

		request = new MockHttpServletRequest();
	}

	@Test
	public void getCountAndPagingTest() {
		when(busService.getObjectCount(any(PwPublicationFilterParams.class))).thenReturn(18);

		SearchResults sr = mvcService.getCountAndPaging(filters);
		assertEquals("13", sr.getPageSize());
		assertEquals("91", sr.getPageRowStart());
		assertEquals("8", sr.getPageNumber());
		assertEquals(18, sr.getRecordCount().intValue());
		assertNull(sr.getRecords());

		filters.setPage_number(null);

		sr = mvcService.getCountAndPaging(filters);
		assertEquals("13", sr.getPageSize());
		assertEquals("4", sr.getPageRowStart());
		assertEquals("1", sr.getPageNumber());
		assertEquals(18, sr.getRecordCount().intValue());
		assertNull(sr.getRecords());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void streamResultsTsvTest() {
		HttpServletResponse response = new MockHttpServletResponse();
		filters.setMimeType(PubsConstantsHelper.MEDIA_TYPE_TSV_EXTENSION);
		mvcService.streamResults(filters, response);
		assertEquals(PubsConstantsHelper.DEFAULT_ENCODING, response.getCharacterEncoding());
		assertEquals("attachment; filename=publications.tsv", response.getHeader(MIME.CONTENT_DISPOSITION));
		assertEquals(PubsConstantsHelper.MEDIA_TYPE_TSV_VALUE, response.getContentType());
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		verify(busService).stream(eq("pwPublication.getStreamByMap"), any(PwPublicationFilterParams.class), any(ResultHandler.class));
		verify(busService, never()).getObjectCount(any(PwPublicationFilterParams.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void streamResultsCsvTest() {
		HttpServletResponse response = new MockHttpServletResponse();
		filters.setMimeType(PubsConstantsHelper.MEDIA_TYPE_CSV_EXTENSION);
		mvcService.streamResults(filters, response);
		assertEquals(PubsConstantsHelper.DEFAULT_ENCODING, response.getCharacterEncoding());
		assertEquals("attachment; filename=publications.csv", response.getHeader(MIME.CONTENT_DISPOSITION));
		assertEquals(PubsConstantsHelper.MEDIA_TYPE_CSV_VALUE, response.getContentType());
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		verify(busService).stream(eq("pwPublication.getStreamByMap"), any(PwPublicationFilterParams.class), any(ResultHandler.class));
		verify(busService, never()).getObjectCount(any(PwPublicationFilterParams.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void streamResultsXlsxTest() {
		HttpServletResponse response = new MockHttpServletResponse();
		filters.setMimeType(PubsConstantsHelper.MEDIA_TYPE_XLSX_EXTENSION);
		mvcService.streamResults(filters, response);
		assertEquals(PubsConstantsHelper.DEFAULT_ENCODING, response.getCharacterEncoding());
		assertEquals("attachment; filename=publications.xlsx", response.getHeader(MIME.CONTENT_DISPOSITION));
		assertEquals(PubsConstantsHelper.MEDIA_TYPE_XLSX_VALUE, response.getContentType());
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		verify(busService).stream(eq("pwPublication.getStreamByMap"), any(PwPublicationFilterParams.class), any(ResultHandler.class));
		verify(busService, never()).getObjectCount(any(PwPublicationFilterParams.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void streamResultsJsonTest() {
		HttpServletResponse response = new MockHttpServletResponse();
		filters.setMimeType(PubsConstantsHelper.MEDIA_TYPE_JSON_EXTENSION);
		mvcService.streamResults(filters, response);
		assertEquals(PubsConstantsHelper.DEFAULT_ENCODING, response.getCharacterEncoding());
		assertEquals(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE, response.getContentType());
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		verify(busService).stream(eq("pwPublication.getByMap"), any(PwPublicationFilterParams.class), any(ResultHandler.class));
		verify(busService).getObjectCount(any(PwPublicationFilterParams.class));
	}

	@Test
	public void testCrossrefPubNotFound() throws IOException {
		when(busService.getByIndexId(anyString())).thenReturn(null);
		HttpServletResponse response = new MockHttpServletResponse();
		HttpServletRequest request = new MockHttpServletRequest();
		mvcService.getPwPublicationCrossref(request, response, "non-existent pub");
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
		HttpServletRequest request = new MockHttpServletRequest();

		mvcService.getPwPublicationCrossref(request, response, "existent non-USGS series pub");
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
		HttpServletRequest request = new MockHttpServletRequest();
		mvcService.getPwPublicationCrossref(request, response, "existent non-USGS series pub");
		assertEquals(HttpStatus.OK.value(), response.getStatus());
	}

	@Test
	public void determineMimeTypeTest() throws HttpMediaTypeNotAcceptableException {
		when(contentStrategy.resolveMediaTypes(any(NativeWebRequest.class)))
				.thenReturn(Arrays.asList(PubsConstantsHelper.MEDIA_TYPE_CSV))
				.thenReturn(Arrays.asList(PubsConstantsHelper.MEDIA_TYPE_TSV))
				.thenReturn(Arrays.asList(PubsConstantsHelper.MEDIA_TYPE_XLSX))
				.thenReturn(Arrays.asList(MediaType.APPLICATION_JSON));

		assertEquals("junk", mvcService.determineMimeType(request, "junk"));
		verify(contentStrategy, never()).resolveMediaTypes(any(NativeWebRequest.class));

		assertEquals(PubsConstantsHelper.MEDIA_TYPE_JSON_EXTENSION, mvcService.determineMimeType(request, "json"));
		verify(contentStrategy, never()).resolveMediaTypes(any(NativeWebRequest.class));

		assertEquals(PubsConstantsHelper.MEDIA_TYPE_CSV_EXTENSION, mvcService.determineMimeType(request, null));
		verify(contentStrategy).resolveMediaTypes(any(NativeWebRequest.class));

		assertEquals(PubsConstantsHelper.MEDIA_TYPE_TSV_EXTENSION, mvcService.determineMimeType(request, null));
		verify(contentStrategy, times(2)).resolveMediaTypes(any(NativeWebRequest.class));

		assertEquals(PubsConstantsHelper.MEDIA_TYPE_XLSX_EXTENSION, mvcService.determineMimeType(request, null));
		verify(contentStrategy, times(3)).resolveMediaTypes(any(NativeWebRequest.class));

		assertEquals(PubsConstantsHelper.MEDIA_TYPE_JSON_EXTENSION, mvcService.determineMimeType(request, null));
		verify(contentStrategy, times(4)).resolveMediaTypes(any(NativeWebRequest.class));

		assertEquals(PubsConstantsHelper.MEDIA_TYPE_JSON_EXTENSION, mvcService.determineMimeType(request, null));
		verify(contentStrategy, times(5)).resolveMediaTypes(any(NativeWebRequest.class));
	}
}
