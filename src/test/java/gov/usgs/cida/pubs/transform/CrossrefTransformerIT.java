package gov.usgs.cida.pubs.transform;

import static org.junit.Assert.assertEquals;
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
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import freemarker.template.Configuration;
import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.busservice.PublicationBusService;
import gov.usgs.cida.pubs.dao.ContributorTypeDao;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.springinit.DbTestConfig;
import gov.usgs.cida.pubs.springinit.TestSpringConfig;

@ContextConfiguration(classes = FreeMarkerAutoConfiguration.class)
@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, ConfigurationService.class, TestSpringConfig.class,
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
	@Qualifier("testOneUnNumberedSeriesPubXmlMin")
	private String testOneUnNumberedSeriesXmlMin;

	@Autowired
	@Qualifier("testOneNumberedSeriesPubXml")
	private String testOneNumberedSeriesXml;

	@Autowired
	@Qualifier("testOneNumberedSeriesPubXmlMin")
	private String testOneNumberedSeriesXmlMin;

	private CrossrefTransformer instance;
	private ByteArrayOutputStream target;
	private static DocumentBuilder docBuilder; 

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
		instance = new CrossrefTransformer(this.target, templateConfig, configurationService) {
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
		CrossrefTransformer myInstance = new CrossrefTransformer(this.target, templateConfig, configurationService);
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
		CrossrefTransformer myInstance = new CrossrefTransformer(this.target, templateConfig, configurationService);
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
		instance.writeResult(pub);
		instance.end();

		String output = new String(target.toByteArray(), PubsConstantsHelper.DEFAULT_ENCODING);
	
		assertNotNull(output);
		assertWellFormed(output);
	
		String expected = harmonizeXml(updateWarehouseEndpoint(testOneUnNumberedSeriesXmlMin));
		String actual = harmonizeXml(output);
		assertEquals(expected, actual);
	}

	@Test
	public void testPubWithoutSeriesTitle() throws IOException, UnsupportedEncodingException {
		Publication<?> pub = CrossrefTestPubBuilder.buildUnNumberedSeriesPub(new Publication<>());
		pub.setSeriesTitle(null);

		instance.writeResult(pub);
		instance.end();
		String output = new String(target.toByteArray(), PubsConstantsHelper.DEFAULT_ENCODING);

		assertNotNull(output);
		assertTrue(0 < output.length());
		assertWellFormed(output);

		String expected = harmonizeXml(updateWarehouseEndpoint(testOneUnNumberedSeriesXmlMin));
		String actual = harmonizeXml(output);
		assertEquals(expected, actual);
	}

	@Test
	public void testPubWithoutSeriesNumber() throws IOException, UnsupportedEncodingException {
		Publication<?> pub = CrossrefTestPubBuilder.buildNumberedSeriesPub(new Publication<>());
		pub.setSeriesNumber(null);

		instance.writeResult(pub);
		instance.end();
		String output = new String(target.toByteArray(), PubsConstantsHelper.DEFAULT_ENCODING);

		assertNotNull(output);
		assertTrue(0 < output.length());
		assertWellFormed(output);

		String expected = harmonizeXml(updateWarehouseEndpoint(testOneNumberedSeriesXmlMin));
		expected = expected.replace("{reasonForMinimalCrossRef}",
									"Minimal doi record shown due to publication missing series number.");
		String actual = harmonizeXml(output);
		assertEquals(expected, actual);
	}

	@Test
	public void testPubWithoutIssn() throws IOException, UnsupportedEncodingException {
		Publication<?> pub = CrossrefTestPubBuilder.buildNumberedSeriesPub(new Publication<>());
		pub.getSeriesTitle().setOnlineIssn(null);

		instance.writeResult(pub);
		instance.end();
		String output = new String(target.toByteArray(), PubsConstantsHelper.DEFAULT_ENCODING);

		assertNotNull(output);
		assertTrue(0 < output.length());
		assertWellFormed(output);

		String expected = harmonizeXml(updateWarehouseEndpoint(testOneNumberedSeriesXmlMin));
		String actual = harmonizeXml(output);
		assertEquals(expected, actual);
	}

	@Test
	public void testPubWithoutPublicationYear() throws IOException, UnsupportedEncodingException {
		Publication<?> pub = CrossrefTestPubBuilder.buildUnNumberedSeriesPub(new Publication<>());
		pub.setPublicationYear(null);

		instance.writeResult(pub);
		instance.end();
		String output = new String(target.toByteArray(), PubsConstantsHelper.DEFAULT_ENCODING);

		assertNotNull(output);
		assertTrue(0 < output.length());
		assertWellFormed(output);

		String expected = harmonizeXml(updateWarehouseEndpoint(testOneUnNumberedSeriesXml));
		String comment = instance.wrapInComment("Excluded Problematic Publication (indexId: unnumbered doi: 10.3133/ofr20131259)");
		expected = expected.replaceAll("<body>.*</body>", "<body>" + comment + "</body>").replace("\n","");
		String actual = harmonizeXml(output);
		assertEquals(expected, actual);
	}

	/**
	 * Test one pub
	 */
	@Test
	public void testOneUnNumberedSeriesPub() throws IOException {
		Publication<?> pub = CrossrefTestPubBuilder.buildUnNumberedSeriesPub(new Publication<>());
		instance.writeResult(pub);
		instance.end();
		String output = new String(target.toByteArray(), PubsConstantsHelper.DEFAULT_ENCODING);
		assertNotNull(output);
		assertTrue(0 < output.length());
		assertWellFormed(output);
		String expected = harmonizeXml(updateWarehouseEndpoint(testOneUnNumberedSeriesXml));
		String actual = harmonizeXml(output);
		assertEquals(expected, actual);
	}

	/**
	 * Test one pub
	 */
	@Test
	public void testOneNumberedSeriesPub() throws IOException {
		Publication<?> pub = CrossrefTestPubBuilder.buildNumberedSeriesPub(new Publication<>());
		instance.writeResult(pub);
		instance.end();
		String output = new String(target.toByteArray(), PubsConstantsHelper.DEFAULT_ENCODING);
		assertNotNull(output);
		assertTrue(0 < output.length());
		assertWellFormed(output);
		String expected = harmonizeXml(updateWarehouseEndpoint(testOneNumberedSeriesXml));
		String actual = harmonizeXml(output);
		assertEquals(expected, actual);
	}

	/**
	 * Test publication containing a data release.
	 */
	@Test
	public void testPubWithDataRelease() throws IOException {
		Publication<?> pub = CrossrefTestPubBuilder.buildNumberedSeriesPub(new Publication<>());
		PublicationLink<?> link = CrossrefTestPubBuilder.buildDataReleaseLink();
		pub.getLinks().add(link);

		instance.writeResult(pub);
		instance.end();
		String output = new String(target.toByteArray(), PubsConstantsHelper.DEFAULT_ENCODING);
		assertNotNull(output);
		assertTrue(0 < output.length());
		assertWellFormed(output);

		String expected = harmonizeXml(updateWarehouseEndpoint(testOneNumberedSeriesXml));
		String dataReleaseDesc = link.getDescription() + link.getHelpText();
		String doi = "10.5066/P944FPA4";
		String dataReleaseXml = getDataReleaseXml(dataReleaseDesc, doi);
		expected = expected.replace("<doi_data>", dataReleaseXml + "<doi_data>");

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

		instance.writeResult(pub);
		instance.end();
		String output = new String(target.toByteArray(), PubsConstantsHelper.DEFAULT_ENCODING);
		assertNotNull(output);
		assertTrue(0 < output.length());
		assertWellFormed(output);
		String expected = harmonizeXml(updateWarehouseEndpoint(testOneNumberedSeriesXml));
		String actual = harmonizeXml(output);
		assertEquals(expected, actual);
	}

	private String updateWarehouseEndpoint(String xml) {
		String updated = xml;
		updated = updated.replace("{WarehouseEndpoint}", configurationService.getWarehouseEndpoint());
		return updated;
	}

	private String getDataReleaseXml(String desc, String doi) {
		String format = "<program xmlns=\"http://www.crossref.org/relations.xsd\"><related_item>"
				+ "<description>%s</description><inter_work_relation relationship-type=\"isSupplementedBy\" "
				+ "identifier-type=\"doi\">%s</inter_work_relation></related_item></program>";
		String xml = String.format(format, desc, doi);
		xml = harmonizeXml(xml);
		return xml;
	}

}
