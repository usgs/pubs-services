package gov.usgs.cida.pubs.webservice.pw;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.StringReader;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.mime.MIME;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.xml.sax.InputSource;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.PubsConstantsHelper;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment=WebEnvironment.MOCK)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/contributor.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/crossrefDataset.xml")
})
public class PwPublicationMvcServiceBulkCrossrefIT extends BaseIT {
	private static final String URL = "/publication/crossref";

	@Autowired
	private MockMvc mockMvc;

	@BeforeAll
	public static void setUpClass() {
		setUpDocBuilder();
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
				.accept(PubsConstantsHelper.MEDIA_TYPE_CROSSREF)
			)
			.andExpect(status().isOk())
			.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_CROSSREF))
			.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
			.andExpect(header().string(MIME.CONTENT_DISPOSITION, "attachment; filename=publications." + PubsConstantsHelper.MEDIA_TYPE_CROSSREF_EXTENSION))
			.andReturn();
		String resultText = result.getResponse().getContentAsString();
		assertTrue(StringUtils.isNotBlank(resultText), "The XML was null or empty.");
		assertDoesNotThrow(() -> {
			docBuilder.parse(new InputSource(new StringReader(resultText)));
		}, "The XML is not well-formed.");
	}

	@Test
	public void getBulkCrossrefXMLWhenQueryStringAsksForCrossref() throws Exception {
		MvcResult result = mockMvc.perform(
				get(URL + "?mimeType=crossref.xml")
				.accept(PubsConstantsHelper.MEDIA_TYPE_CROSSREF)
			)
			.andExpect(status().isOk())
			.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_CROSSREF))
			.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
			.andExpect(header().string(MIME.CONTENT_DISPOSITION, "attachment; filename=publications." + PubsConstantsHelper.MEDIA_TYPE_CROSSREF_EXTENSION))
			.andReturn();
		String resultText = result.getResponse().getContentAsString();
		assertTrue(StringUtils.isNotBlank(resultText), "The XML was null or empty.");
		assertDoesNotThrow(() -> {
			docBuilder.parse(new InputSource(new StringReader(resultText)));
		}, "The XML is not well-formed.");
	}

}
