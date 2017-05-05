package gov.usgs.cida.pubs.transform;

import freemarker.template.Configuration;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.busservice.intfc.IPublicationBusService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationContributorTest;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.PublicationLinkTest;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationTest;
import java.util.ArrayList;
import java.util.Collection;

public class CrossrefTransformerTest extends BaseSpringTest {
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
		instance = new CrossrefTransformer(this.target, templateConfig, "nobody@usgs.gov", publicationBusService){
			/**
			 * Override randomly-generated value so we can compare
			 * consistent values over time
			 * @return 
			 */
			@Override
			protected String getBatchId() {
				return TEST_BATCH_ID;
			}
			
			/**
			 * Override time-dependent value so we can compare
			 * consistent values over time
			 * @return 
			 */
			@Override
			protected String getTimeStamp() {
				return TEST_TIMESTAMP;
			}
		};
	}
	
	@After
	public void tearDown() {
	}
	
	private void assertWellFormed(String xml) {
		String errorMsg = "";
		
		try{
			Document doc = docBuilder.parse(new InputSource(new StringReader(xml)));
		} catch(SAXParseException e){
			errorMsg = e.getMessage();
		} catch (SAXException | IOException ex) {
			throw new RuntimeException(ex);
		}
		//assert there are no error messages
		assertEquals("The XML is not well-formed.", "", errorMsg);
	}
	
	private Publication<?> buildNumberedSeriesPub() {
		Publication<?> pub = PublicationTest.buildAPub(new Publication<>(), 42);
		PublicationSubtype numbered = new PublicationSubtype();
		numbered.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		pub.setPublicationSubtype(numbered);
		PublicationSeries series = new PublicationSeries();
		series.setCode("OFR");
		series.setText("Open-File Report");
		series.setOnlineIssn("2331-1258");
		pub.setIndexId("numbered");
		pub.setSeriesTitle(series);
		pub.setPublicationYear("2013");
		pub.setTitle("Postwildfire debris-flow hazard assessment of the area burned by the 2013 West Fork Fire Complex, southwestern Colorado");
		pub.setSeriesNumber("2013-1259");
		pub.setDoi("10.3133/ofr20131259");

		Collection<PublicationContributor<?>> contributors = new ArrayList<>();
		contributors.add(PublicationContributorTest.buildPersonPublicationAuthor());
		contributors.add(PublicationContributorTest.buildPersonPublicationEditor());
		contributors.add(PublicationContributorTest.buildCorporatePublicationAuthor());
		contributors.add(PublicationContributorTest.buildCorporatePublicationEditor());
		pub.setContributors(contributors);

		pub.setStartPage("52");
		pub.setEndPage("56");
		
		Collection<PublicationLink<?>> links = new ArrayList<>();
		links.add(PublicationLinkTest.buildIndexLink());
		pub.setLinks(links);

		return pub;
	}
	protected Publication<?> buildUnNumberedSeriesPub() {
		Publication<?> pub = PublicationTest.buildAPub(new Publication<>(), 42);
		PublicationSubtype unnumbered = new PublicationSubtype();
		unnumbered.setId(PublicationSubtype.USGS_UNNUMBERED_SERIES);
		pub.setPublicationSubtype(unnumbered);
		PublicationSeries series = new PublicationSeries();
		series.setCode("GIP");
		series.setText("General Information Product");
		pub.setIndexId("unnumbered");
		pub.setSeriesTitle(series);
		pub.setPublicationYear("2013");
		pub.setTitle("Postwildfire debris-flow hazard assessment of the area burned by the 2013 West Fork Fire Complex, southwestern Colorado");
		pub.setDoi("10.3133/ofr20131259");
	
		pub.setStartPage("52");
		pub.setEndPage("56");
		
		
		Collection<PublicationContributor<?>> contributors = new ArrayList<>();
		
		contributors.add(PublicationContributorTest.buildPersonPublicationAuthor());
		contributors.add(PublicationContributorTest.buildPersonPublicationEditor());
		contributors.add(PublicationContributorTest.buildCorporatePublicationAuthor());
		contributors.add(PublicationContributorTest.buildCorporatePublicationEditor());
		
		pub.setContributors(contributors);
		
		Collection<PublicationLink<?>> links = new ArrayList<>();
		links.add(PublicationLinkTest.buildIndexLink());
		pub.setLinks(links);
		
		return pub;
	}
	
	/**
	 * Test of init method, of class CrossrefTransformer.
	 */
	@Test
	public void testNoPubs() throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException {
		instance.end();
		String output = new String(target.toByteArray(), "UTF-8");
		assertNotNull(output);
		assertTrue(0 < output.length());
		assertWellFormed(output);
	}
	
	/**
	 * Test one pub
	 */
	@Test
	public void testOneUnNumberedSeriesPub() throws IOException {
		Publication<?> pub = buildUnNumberedSeriesPub();
		instance.write(pub);
		instance.end();
		String output = new String(target.toByteArray(), "UTF-8");
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
		Publication<?> pub = buildNumberedSeriesPub();
		instance.write(pub);
		instance.end();
		String output = new String(target.toByteArray(), "UTF-8");
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
		
		Publication<?> pub = buildNumberedSeriesPub();
		
		//add the contributor of unknown type to the publication
		Collection<PublicationContributor<?>> contributors = pub.getContributors();
		contributors.add(strangePublicationContributor);
		pub.setContributors(contributors);
		
		instance.write(pub);
		instance.end();
		String output = new String(target.toByteArray(), "UTF-8");
		assertNotNull(output);
		assertTrue(0 < output.length());
		assertWellFormed(output);
		String expected = harmonizeXml(testOneNumberedSeriesXml);
		String actual = harmonizeXml(output);
		assertEquals(expected, actual);
	}
	

}