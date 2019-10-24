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
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.busservice.PublicationBusService;
import gov.usgs.cida.pubs.busservice.pw.PwPublicationBusService;
import gov.usgs.cida.pubs.busservice.xml.XmlBusService;
import gov.usgs.cida.pubs.dao.ContributorTypeDao;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDao;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.springinit.DbTestConfig;
import gov.usgs.cida.pubs.springinit.FreemarkerConfig;
import gov.usgs.cida.pubs.springinit.SpringConfig;
import gov.usgs.cida.pubs.utility.CustomStringToArrayConverter;
import gov.usgs.cida.pubs.utility.CustomStringToStringConverter;
import gov.usgs.cida.pubs.utility.StringArrayCleansingConverter;

@EnableWebMvc
@AutoConfigureMockMvc(secure=false)
@SpringBootTest(webEnvironment=WebEnvironment.MOCK,
	classes={DbTestConfig.class, ConfigurationService.class, PwPublicationMvcService.class,
			PwPublicationBusService.class, XmlBusService.class, LocalValidatorFactoryBean.class,
			FreemarkerConfig.class, PublicationBusService.class, PwPublication.class,
			PwPublicationDao.class, PublicationDao.class, SpringConfig.class,
			CustomStringToArrayConverter.class, StringArrayCleansingConverter.class,
			CustomStringToStringConverter.class, ContributorType.class, ContributorTypeDao.class})
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
	private static DocumentBuilder docBuilder;

	@Autowired
	private MockMvc mockMvc;

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
				.accept(PubsConstantsHelper.MEDIA_TYPE_CROSSREF)
			)
			.andExpect(status().isOk())
			.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_CROSSREF))
			.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
			.andExpect(header().string(MIME.CONTENT_DISPOSITION, "attachment; filename=publications." + PubsConstantsHelper.MEDIA_TYPE_CROSSREF_EXTENSION))
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
				.accept(PubsConstantsHelper.MEDIA_TYPE_CROSSREF)
			)
			.andExpect(status().isOk())
			.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_CROSSREF))
			.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
			.andExpect(header().string(MIME.CONTENT_DISPOSITION, "attachment; filename=publications." + PubsConstantsHelper.MEDIA_TYPE_CROSSREF_EXTENSION))
			.andReturn();
		String resultText = result.getResponse().getContentAsString();
		assertNotNull(resultText);
		assertTrue("expects non-empty response", 0 < resultText.length());
		assertWellFormed(resultText);
	}

}
