package gov.usgs.cida.pubs.webservice.pw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.entity.mime.MIME;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.PubsConstants;

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
	private static final String URL = "/publication/crossref";
	private static DocumentBuilder docBuilder;

	@BeforeClass
	public static void setUpClass() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		try {
			docBuilder = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	/**
	 * TODO: replace this with more robust XML schema validation
	 * @param xml 
	 */
	private void assertWellFormed(String xml) {
		String errorMsg = "";
		
		try{
			docBuilder.parse(new InputSource(new StringReader(xml)));
		} catch(SAXParseException e){
			errorMsg = e.getMessage();
		} catch (SAXException | IOException ex) {
			throw new RuntimeException(ex);
		}
		//assert there are no error messages
		assertEquals("The XML is not well-formed.", "", errorMsg);
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
		assertWellFormed(resultText);
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
		assertWellFormed(resultText);
	}

}
