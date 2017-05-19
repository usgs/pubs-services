package gov.usgs.cida.pubs.webservice.pw;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.PubsConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.apache.http.entity.mime.MIME;
import org.json.JSONObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;

@Category(IntegrationTest.class)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/contributor.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/crossrefDataset.xml")
})
public class PwPublicationMvcServiceSinglePubTest extends BaseSpringTest {

	private MockMvc mockMvc;
	private static final String CROSSREF_PUB_ID = "sir2";
	private static final String CROSSREF_PUB_JSON_FILE = "pwPublication/sir2.json";
	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}
	
	@Test
	public void getJSONWhenNoAcceptHeaderIsSpecified () throws Exception {
		MvcResult result = mockMvc.perform(get("/publication/" + CROSSREF_PUB_ID))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
			.andReturn();
		JSONObject content = getRtnAsJSONObject(result);
		assertTrue("expects non-empty response",  0 < content.length());
		assertThat(getRtnAsJSONObject(result),
			sameJSONObjectAs(
				new JSONObject(getCompareFile(CROSSREF_PUB_JSON_FILE))
			).allowingAnyArrayOrdering()
		);
	}
	
	@Test
	public void getJSONWhenUsingBrowserDefaultAcceptHeader () throws Exception {
		//Simulate a browser's default accept header value
		MvcResult result = mockMvc.perform(get("/publication/" + CROSSREF_PUB_ID)
			.accept(
				"text/html",
				"application/xhtml+xml",
				"application/xml;q=0.9",
				"*/*;q=0.8"
			))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
			.andReturn();
		JSONObject content = getRtnAsJSONObject(result);
		assertTrue("expects non-empty response",  0 < content.length());
		assertThat(getRtnAsJSONObject(result),
			sameJSONObjectAs(
				new JSONObject(getCompareFile(CROSSREF_PUB_JSON_FILE))
			).allowingAnyArrayOrdering()
		);
	}
	
	@Test
	public void getJSONWhenAcceptHeaderAsksForJSON () throws Exception {
		MvcResult result = mockMvc.perform(get("/publication/" + CROSSREF_PUB_ID)
			.accept(
				MediaType.APPLICATION_JSON_UTF8
			))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
			.andReturn();
		JSONObject content = getRtnAsJSONObject(result);
		assertTrue("expects non-empty response",  0 < content.length());
		assertThat(getRtnAsJSONObject(result),
			sameJSONObjectAs(
				new JSONObject(getCompareFile(CROSSREF_PUB_JSON_FILE))
			).allowingAnyArrayOrdering()
		);
	}
	
	@Test
	public void getJSONWhenQueryStringAsksForJSON () throws Exception {
		MvcResult result = mockMvc.perform(get("/publication/" + CROSSREF_PUB_ID + "?" + PubsConstants.CONTENT_PARAMETER_NAME +"=" + PubsConstants.MEDIA_TYPE_JSON_EXTENSION))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
			.andReturn();
		JSONObject content = getRtnAsJSONObject(result);
		assertTrue("expects non-empty response",  0 < content.length());
		assertThat(getRtnAsJSONObject(result),
			sameJSONObjectAs(
				new JSONObject(getCompareFile(CROSSREF_PUB_JSON_FILE))
			).allowingAnyArrayOrdering()
		);
	}
	
	@Test
	public void notFoundTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/publication/nonExistentPubId?mimetype=json").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
				.andReturn();
		assertEquals(0, rtn.getResponse().getContentAsString().length());
	}
	
	@Test
	public void getCrossrefXMLWhenAcceptHeaderAsksForCrossref () throws Exception {
		MvcResult result = mockMvc.perform(get("/publication/" + CROSSREF_PUB_ID)
			.accept(PubsConstants.MEDIA_TYPE_CROSSREF)
			)
			.andExpect(status().isOk())
			.andExpect(content().contentType(PubsConstants.MEDIA_TYPE_CROSSREF))
			.andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
			.andExpect(header().string(MIME.CONTENT_DISPOSITION, "inline"))
			.andReturn();
		String resultText = result.getResponse().getContentAsString();
		
		assertTrue("expects non-empty response",  0 < resultText.length());
	}
	
	@Test
	public void getCrossrefXMLWhenQueryStringAsksForCrossref () throws Exception {
		MvcResult result = mockMvc.perform(get("/publication/" + CROSSREF_PUB_ID + "?" + PubsConstants.CONTENT_PARAMETER_NAME +"=" + PubsConstants.MEDIA_TYPE_CROSSREF_EXTENSION))
			.andExpect(status().isOk())
			.andExpect(content().contentType(PubsConstants.MEDIA_TYPE_CROSSREF))
			.andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
			.andExpect(header().string(MIME.CONTENT_DISPOSITION, "inline"))
			.andReturn();
		String resultText = result.getResponse().getContentAsString();
		
		assertTrue("expects non-empty response",  0 < resultText.length());
	}
	

}