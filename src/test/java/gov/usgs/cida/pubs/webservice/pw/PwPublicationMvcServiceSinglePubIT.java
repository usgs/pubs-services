package gov.usgs.cida.pubs.webservice.pw;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;

import org.apache.http.entity.mime.MIME;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import io.micrometer.core.instrument.util.StringUtils;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment=WebEnvironment.MOCK)
@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
@DatabaseSetup("classpath:/testData/publicationType.xml")
@DatabaseSetup("classpath:/testData/publicationSubtype.xml")
@DatabaseSetup("classpath:/testData/publicationSeries.xml")
@DatabaseSetup("classpath:/testData/contributor/")
@DatabaseSetup("classpath:/testData/crossrefDataset.xml")
public class PwPublicationMvcServiceSinglePubIT extends BaseIT {

	private static final String CROSSREF_PUB_ID = "sir2";
	private static final String CROSSREF_PUB_JSON_FILE = "pwPublication/sir2.json";

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void getJSONWhenNoAcceptHeaderIsSpecified () throws Exception {
		MvcResult result = mockMvc.perform(get("/publication/" + CROSSREF_PUB_ID))
			.andExpect(status().isOk())
			.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
			.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
			.andReturn();
		JSONObject content = new JSONObject(result.getResponse().getContentAsString());
		assertNotNull(content, "expects non-empty response");
		assertThat(content,
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
			.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
			.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
			.andReturn();
		JSONObject content = new JSONObject(result.getResponse().getContentAsString());
		assertNotNull(content, "expects non-empty response");
		assertThat(content,
			sameJSONObjectAs(
				new JSONObject(getCompareFile(CROSSREF_PUB_JSON_FILE))
			).allowingAnyArrayOrdering()
		);
	}

	@Test
	public void getJSONWhenAcceptHeaderAsksForJSON () throws Exception {
		MvcResult result = mockMvc.perform(get("/publication/" + CROSSREF_PUB_ID)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
			.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
			.andReturn();
		JSONObject content = new JSONObject(result.getResponse().getContentAsString());
		assertNotNull(content, "expects non-empty response");
		assertThat(content,
			sameJSONObjectAs(
				new JSONObject(getCompareFile(CROSSREF_PUB_JSON_FILE))
			).allowingAnyArrayOrdering()
		);
	}

	@Test
	public void getJSONWhenQueryStringAsksForJSON () throws Exception {
		MvcResult result = mockMvc.perform(get("/publication/" + CROSSREF_PUB_ID + "?" + PubsConstantsHelper.CONTENT_PARAMETER_NAME +"=" + PubsConstantsHelper.MEDIA_TYPE_JSON_EXTENSION))
			.andExpect(status().isOk())
			.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
			.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
			.andReturn();
		JSONObject content = new JSONObject(result.getResponse().getContentAsString());
		assertNotNull(content, "expects non-empty response");
		assertThat(content,
			sameJSONObjectAs(
				new JSONObject(getCompareFile(CROSSREF_PUB_JSON_FILE))
			).allowingAnyArrayOrdering()
		);
	}

	@Test
	public void notFoundTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/publication/nonExistentPubId?mimetype=json").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();
		assertTrue(StringUtils.isBlank(rtn.getResponse().getContentAsString()));
	}

	@Test
	public void getCrossrefXMLWhenAcceptHeaderAsksForCrossref() throws Exception {
		MvcResult result = mockMvc.perform(get("/publication/" + CROSSREF_PUB_ID)
			.accept(PubsConstantsHelper.MEDIA_TYPE_CROSSREF)
			)
			.andExpect(status().isOk())
			.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_CROSSREF))
			.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
			.andExpect(header().string(MIME.CONTENT_DISPOSITION, "inline"))
			.andReturn();
		assertNotNull(result.getResponse().getContentAsString(), "expects non-empty response");
	}

	@Test
	public void getCrossrefXMLWhenQueryStringAsksForCrossref() throws Exception {
		MvcResult result = mockMvc.perform(get("/publication/" + CROSSREF_PUB_ID + "?" + PubsConstantsHelper.CONTENT_PARAMETER_NAME +"=" + PubsConstantsHelper.MEDIA_TYPE_CROSSREF_EXTENSION))
			.andExpect(status().isOk())
			.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_CROSSREF))
			.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
			.andExpect(header().string(MIME.CONTENT_DISPOSITION, "inline"))
			.andReturn();
		assertNotNull(result.getResponse().getContentAsString(), "expects non-empty response");
	}
}