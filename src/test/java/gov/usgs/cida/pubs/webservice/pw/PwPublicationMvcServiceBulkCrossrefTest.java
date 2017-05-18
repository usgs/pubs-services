package gov.usgs.cida.pubs.webservice.pw;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.PubsConstants;
import org.apache.http.entity.mime.MIME;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Category(IntegrationTest.class)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/contributor.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/crossrefDataset.xml")
})
public class PwPublicationMvcServiceBulkCrossrefTest extends BaseSpringTest {
	private MockMvc mockMvc;
	private static final String CROSSREF_PUB_ID = "sir2";
	private static final String CROSSREF_PUB_JSON_FILE = "pwPublication/sir2.json";
	private static final String URL = "/publication/crossref";
	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}
	
	
	@Test
	public void getBulkCrossRefWithNoContentTypeSpecified() throws Exception{
		mockMvc.perform(get(URL))
			.andExpect(status().isNotFound());
	}
	
	@Test
	public void getBulkCrossRefWithAcceptHeaderSpecified() throws Exception {
		MvcResult result = mockMvc.perform(
				get(URL)
				.accept(PubsConstants.MEDIA_TYPE_CROSSREF)
			)
			.andExpect(status().isOk())
			.andExpect(content().contentType(PubsConstants.MEDIA_TYPE_CROSSREF))
			.andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
			.andExpect(header().string(MIME.CONTENT_DISPOSITION, "attachment; filename=publications." + PubsConstants.MEDIA_TYPE_CROSSREF_EXTENSION))
			.andReturn();
		String resultText = result.getResponse().getContentAsString();
		assertNotNull(resultText);
		assertTrue("expects non-empty response", 0 < resultText.length());
	}
	
	@Test
	public void getBulkCrossrefXMLWhenQueryStringAsksForCrossref() throws Exception {
		MvcResult result = mockMvc.perform(
				get(URL + "?mimeType=crossref.xml")
				.accept(PubsConstants.MEDIA_TYPE_CROSSREF)
			)
			.andExpect(status().isOk())
			.andExpect(content().contentType(PubsConstants.MEDIA_TYPE_CROSSREF))
			.andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
			.andExpect(header().string(MIME.CONTENT_DISPOSITION, "attachment; filename=publications." + PubsConstants.MEDIA_TYPE_CROSSREF_EXTENSION))
			.andReturn();
		String resultText = result.getResponse().getContentAsString();
		assertNotNull(resultText);
		assertTrue("expects non-empty response", 0 < resultText.length());
	}
	
}
