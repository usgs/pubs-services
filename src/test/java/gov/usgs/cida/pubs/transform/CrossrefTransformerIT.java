package gov.usgs.cida.pubs.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import freemarker.template.Configuration;
import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.busservice.PublicationBusService;
import gov.usgs.cida.pubs.busservice.intfc.IPublicationBusService;
import gov.usgs.cida.pubs.dao.ContributorTypeDao;
import gov.usgs.cida.pubs.dao.ContributorTypeDaoIT;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.springinit.DbTestConfig;
import gov.usgs.cida.pubs.springinit.FreemarkerConfig;
import gov.usgs.cida.pubs.springinit.TestSpringConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, ConfigurationService.class, TestSpringConfig.class, FreemarkerConfig.class,
			PublicationBusService.class, ContributorType.class, ContributorTypeDao.class})
public class CrossrefTransformerIT extends BaseIT {

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	@Qualifier("freeMarkerConfiguration")
	private Configuration templateConfig;

	@Autowired
	@Qualifier("testOneUnNumberedSeriesPubXml")
	private String testOneUnNumberedSeriesXml;

	@Autowired
	@Qualifier("testOneNumberedSeriesPubXml")
	private String testOneNumberedSeriesXml;

	private CrossrefTransformer instance;
	private ByteArrayOutputStream target;
	private static DocumentBuilder docBuilder; 

	@Autowired
	private IPublicationBusService publicationBusService;

	private static final String TEST_TIMESTAMP = "1493070447545";
	private static final String TEST_BATCH_ID = "82adfd8d-1737-4e62-86bc-5e7be1c07b7d";

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
	public void setUp() {
		this.target = new ByteArrayOutputStream();
		instance = new CrossrefTransformer(this.target, templateConfig, configurationService, publicationBusService,
				ContributorTypeDaoIT.AUTHOR_KEY, ContributorTypeDaoIT.EDITOR_KEY){
			/**
			 * Override randomly-generated value so we can compare
			 * consistent values over time
			 * @return 
			 */
			@Override
			public String getBatchId() {
				return TEST_BATCH_ID;
			}

			/**
			 * Override time-dependent value so we can compare
			 * consistent values over time
			 * @return 
			 */
			@Override
			public String getTimestamp() {
				return TEST_TIMESTAMP;
			}
		};
	}

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

	/**
	 * While in most test cases we override the getTimestamp() method to get
	 * a consistent result over time, here we construct our own instance 
	 * that does not override the default to ensure the value is being
	 * initialized during instantiation.
	 * @throws IOException 
	 */
	@Test
	public void testTimestampIsInitialized() throws IOException {
		CrossrefTransformer myInstance = new CrossrefTransformer(this.target, templateConfig, configurationService, publicationBusService,
				ContributorTypeDaoIT.AUTHOR_KEY, ContributorTypeDaoIT.EDITOR_KEY);
		assertNotNull(myInstance.getTimestamp());
		assertTrue("timestamp should be of nonzero length", 0 < myInstance.getTimestamp().length());
		myInstance.close();
	}

	/**
	 * While in most test cases we override the getBatchId() method to get
	 * a consistent result over time, here we construct our own instance 
	 * that does not override the method to ensure the value is being
	 * initialized during instantiation.
	 * @throws IOException 
	 */
	@Test
	public void testBatchIdIsInitialized() throws IOException {
		CrossrefTransformer myInstance = new CrossrefTransformer(this.target, templateConfig, configurationService, publicationBusService,
				ContributorTypeDaoIT.AUTHOR_KEY, ContributorTypeDaoIT.EDITOR_KEY);
		assertNotNull(myInstance.getBatchId());
		assertTrue("batch id should be of nonzero length", 0 < myInstance.getBatchId().length());
		myInstance.close();
	}

	/**
	 * Test of init method, of class CrossrefTransformer.
	 */
	@Test
	public void testNoPubs() throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException {
		instance.end();
		String output = new String(target.toByteArray(), PubsConstantsHelper.DEFAULT_ENCODING);
		assertNotNull(output);
		assertTrue(0 < output.length());
		assertWellFormed(output);
	}

	@Test
	public void testPubWithoutContributors() throws IOException, UnsupportedEncodingException {
		Publication<?> pub = CrossrefTestPubBuilder.buildUnNumberedSeriesPub(new Publication<>());
		pub.setContributors(Collections.emptyList());
		boolean success = instance.writeResult(pub);
		assertFalse("A publication should not have an entry written if it has no contributors", success);

		//we expect the rest of the document to get written, even if
		//one publication can't be successfully written

		instance.end();
		String output = new String(target.toByteArray(), PubsConstantsHelper.DEFAULT_ENCODING);
	
		assertNotNull(output);
		assertTrue(0 < output.length());
		assertWellFormed(output);
	
		assertTrue("There should be a minimal comment about the missing publication in the output.",
			output.contains(instance.wrapInComment(instance.getExcludedErrorMessage(pub)))
		);
	}

	@Test
	public void testPubWithoutSeriesTitle() throws IOException, UnsupportedEncodingException {
		Publication<?> pub = CrossrefTestPubBuilder.buildUnNumberedSeriesPub(new Publication<>());
		pub.setSeriesTitle(null);
		boolean success = instance.writeResult(pub);
		assertFalse("A publication should not have an entry written if it has no associated series", success);

		//we expect the rest of the document to get written, even if
		//one publication can't be successfully written

		instance.end();
		String output = new String(target.toByteArray(), PubsConstantsHelper.DEFAULT_ENCODING);

		assertNotNull(output);
		assertTrue(0 < output.length());
		assertWellFormed(output);
		
		assertTrue("There should be a minimal comment about the missing publication in the output.",
			output.contains(instance.wrapInComment(instance.getExcludedErrorMessage(pub)))
		);
	}

	/**
	 * Test one pub
	 */
	@Test
	public void testOneUnNumberedSeriesPub() throws IOException {
		Publication<?> pub = CrossrefTestPubBuilder.buildUnNumberedSeriesPub(new Publication<>());
		boolean success = instance.writeResult(pub);
		assertTrue("should be able to write a valid publication", success);
		instance.end();
		String output = new String(target.toByteArray(), PubsConstantsHelper.DEFAULT_ENCODING);
		assertNotNull(output);
		assertTrue(0 < output.length());
		assertWellFormed(output);
		String expected = harmonizeXml(testOneUnNumberedSeriesXml);
		String actual = harmonizeXml(output);
		assertEquals(expected, actual);
	}

	/**
	 * Test one pub
	 */
	@Test
	public void testOneNumberedSeriesPub() throws IOException {
		Publication<?> pub = CrossrefTestPubBuilder.buildNumberedSeriesPub(new Publication<>());
		boolean success = instance.writeResult(pub);
		assertTrue("should be able to write a valid publication", success);
		instance.end();
		String output = new String(target.toByteArray(), PubsConstantsHelper.DEFAULT_ENCODING);
		assertNotNull(output);
		assertTrue(0 < output.length());
		assertWellFormed(output);
		String expected = harmonizeXml(testOneNumberedSeriesXml);
		String actual = harmonizeXml(output);
		assertEquals(expected, actual);
	}

	/**
	 * Test that contributors of unknown type are omitted
	 */
	@Test
	public void testOmitUnknownContributorType() throws IOException {
		//make a new contributor with an unknown type
		int unknownContributorTypeId = -999;
		ContributorType unknownContributorType = new ContributorType();
		unknownContributorType.setId(unknownContributorTypeId);

		PublicationContributor<?> strangePublicationContributor = new PublicationContributor<>();
		strangePublicationContributor.setContributorType(unknownContributorType);

		Publication<?> pub = CrossrefTestPubBuilder.buildNumberedSeriesPub(new Publication<>());

		//add the contributor of unknown type to the publication
		Collection<PublicationContributor<?>> contributors = pub.getContributors();
		contributors.add(strangePublicationContributor);
		pub.setContributors(contributors);

		boolean success = instance.writeResult(pub);
		assertTrue("should be able to write a valid publication, even if one of its contributors is of an unknown type", success);

		instance.end();
		String output = new String(target.toByteArray(), PubsConstantsHelper.DEFAULT_ENCODING);
		assertNotNull(output);
		assertTrue(0 < output.length());
		assertWellFormed(output);
		String expected = harmonizeXml(testOneNumberedSeriesXml);
		String actual = harmonizeXml(output);
		assertEquals(expected, actual);
	}

}