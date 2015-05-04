package gov.usgs.cida.pubs.webservice.pw;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.intfc.IPwPublicationBusService;

import org.apache.http.entity.mime.MIME;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

@Category(IntegrationTest.class)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testData/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationStream.xml")
})
public class PwPublicationMvcServiceStreamingTest extends BaseSpringTest {

	@Autowired
    public String warehouseEndpoint;

	@Mock
    private IPwPublicationBusService busService;

    private MockMvc mockMvc;

    private PwPublicationMvcService mvcService;
    
    @Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
    	mvcService = new PwPublicationMvcService(busService, warehouseEndpoint);
    	mockMvc = MockMvcBuilders.standaloneSetup(mvcService).build();
    }


	@Test
	public void getAsCsvTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/publication?mimeType=csv"))
	        .andExpect(status().isOk())
	        .andExpect(content().contentType(PubsConstants.MEDIA_TYPE_CSV_VALUE))
	        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
	        .andExpect(header().string(MIME.CONTENT_DISPOSITION, "attachment; filename=publications.csv"))
//	        .andExpect(header().string(HttpConstants.HEADER_CORS_METHODS, HttpConstants.HEADER_CORS_METHODS_VALUE))
//	        .andExpect(header().string(HttpConstants.HEADER_CORS_MAX_AGE, HttpConstants.HEADER_CORS_MAX_AGE_VALUE))
//		    .andExpect(header().string(HttpConstants.HEADER_CORS_ALLOW_HEADERS, HttpConstants.HEADER_CORS_ALLOW_HEADERS_VALUE))
//	        .andExpect(header().string(HEADER_CORS, HEADER_CORS_VALUE))
	        .andReturn();
	
//	    assertEquals(acceptHeaders,	rtn.getResponse().getHeaderValues("Access-Control-Expose-Headers"));
		assertEquals(getCompareFile("stream.csv"), rtn.getResponse().getContentAsString());
	}

	@Test
	public void getAsTsvTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/publication?mimeType=tsv"))
	        .andExpect(status().isOk())
	        .andExpect(content().contentType(PubsConstants.MEDIA_TYPE_TSV_VALUE))
	        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
	        .andExpect(header().string(MIME.CONTENT_DISPOSITION, "attachment; filename=publications.tsv"))
//	        .andExpect(header().string(HttpConstants.HEADER_CORS_METHODS, HttpConstants.HEADER_CORS_METHODS_VALUE))
//	        .andExpect(header().string(HttpConstants.HEADER_CORS_MAX_AGE, HttpConstants.HEADER_CORS_MAX_AGE_VALUE))
//		    .andExpect(header().string(HttpConstants.HEADER_CORS_ALLOW_HEADERS, HttpConstants.HEADER_CORS_ALLOW_HEADERS_VALUE))
//	        .andExpect(header().string(HEADER_CORS, HEADER_CORS_VALUE))
	        .andReturn();
	
//	    assertEquals(acceptHeaders,	rtn.getResponse().getHeaderValues("Access-Control-Expose-Headers"));
		assertEquals(getCompareFile("stream.tsv"), rtn.getResponse().getContentAsString());
	}

	@Test
	public void getAsXlsxTest() throws Exception {
		mockMvc.perform(get("/publication?mimeType=xlsx"))
	        .andExpect(status().isOk())
	        .andExpect(content().contentType(PubsConstants.MEDIA_TYPE_XLSX_VALUE))
	        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
	        .andExpect(header().string(MIME.CONTENT_DISPOSITION, "attachment; filename=publications.xlsx"));
//	        .andExpect(header().string(HttpConstants.HEADER_CORS_METHODS, HttpConstants.HEADER_CORS_METHODS_VALUE))
//	        .andExpect(header().string(HttpConstants.HEADER_CORS_MAX_AGE, HttpConstants.HEADER_CORS_MAX_AGE_VALUE))
//		    .andExpect(header().string(HttpConstants.HEADER_CORS_ALLOW_HEADERS, HttpConstants.HEADER_CORS_ALLOW_HEADERS_VALUE))
//	        .andExpect(header().string(HEADER_CORS, HEADER_CORS_VALUE))
//	        .andReturn();
	
//	    assertEquals(acceptHeaders,	rtn.getResponse().getHeaderValues("Access-Control-Expose-Headers"));
//		assertEquals(getCompareFile("stream.csv"), rtn.getResponse().getContentAsString());
	}

}
