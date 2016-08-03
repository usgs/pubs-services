package gov.usgs.cida.pubs.busservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.CorporateContributor;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.utility.PubsEMailer;

@Ignore
//TODO
public class CrossRefBusServiceTest extends BaseSpringTest {
	
	private static final String testPagesXml = "<pages><first_page>52</first_page><last_page>56</last_page></pages>";
	private static final String testPagesXmlEscaped = "<pages><first_page>52&lt;&gt;</first_page><last_page>56&lt;&gt;</last_page></pages>";

	@Autowired
	@Qualifier("ofr20131259Xml")
	private String ofr20131259Xml;
	@Autowired
	@Qualifier("testUnNumberedSeriesXml")
	private String testUnNumberedSeriesXml;

	@Autowired
	public String warehouseEndpoint;

	@Autowired
	protected String crossRefProtocol;
	@Autowired
	protected String crossRefHost;
	@Autowired
	protected String crossRefUrl;
	@Autowired
	protected Integer crossRefPort;
	@Autowired
	protected String crossRefUser;
	@Autowired
	protected String crossRefPwd;
	@Autowired
	@Qualifier("numberedSeriesXml")
	protected String numberedSeriesXml;
	@Autowired
	@Qualifier("unNumberedSeriesXml")
	protected String unNumberedSeriesXml;
	@Autowired
	@Qualifier("personNameXml")
	protected String personNameXml;
	@Autowired
	@Qualifier("organizationNameXml")
	protected String organizationNameXml;
	@Autowired
	@Qualifier("pagesXml")
	protected String pagesXml;
	@Autowired
	@Qualifier("crossRefDepositorEmail")
	protected String depositorEmail;
	@Mock
	protected PubsEMailer pubsEMailer;

	private CrossRefBusService busService;
	
	@Before
	public void initTest() throws Exception {
		MockitoAnnotations.initMocks(this);
		busService = new CrossRefBusService(crossRefProtocol, crossRefHost, crossRefUrl, crossRefPort, crossRefUser,
				crossRefPwd, numberedSeriesXml, unNumberedSeriesXml, organizationNameXml, personNameXml,
				pagesXml, depositorEmail, pubsEMailer, warehouseEndpoint);
	}

	@Test
	public void replacePlaceHolderTest() {
		assertEquals("", busService.replacePlaceHolder(null, null, null));
		assertEquals("", busService.replacePlaceHolder(null, "f", "q"));
		assertEquals("", busService.replacePlaceHolder(null, null, "q"));
		assertEquals("", busService.replacePlaceHolder(null, "f", null));
		assertEquals("abc", busService.replacePlaceHolder("abc", null, null));
		assertEquals("abc", busService.replacePlaceHolder("abc", null, "q"));
		assertEquals("abc", busService.replacePlaceHolder("abc", "f", null));
		assertEquals("abc", busService.replacePlaceHolder("abc", "f", "q"));
		assertEquals("abq", busService.replacePlaceHolder("abc", "c", "q"));
		assertEquals("ab", busService.replacePlaceHolder("abc", "c", null));
		//We don't escape at this level...
		assertEquals("a&b<>q", busService.replacePlaceHolder("a&b<>c", "c", "q"));
	}

	@Test
	public void buildXmlNPETest() {
		assertNull(busService.buildXml(null, null));
		assertNull(busService.buildXml(new MpPublication(), null));
		assertNull(busService.buildXml(new MpPublication(), "abc"));
	}

	@Test
	public void buildXmlNumberedSeriesTest() {
		MpPublication pub = buildNumberedSeriesPub();
		assertTrue(busService.buildXml(pub, "http://pubs.usgs.gov/of/2013/1259/").matches(".*[\\\\/]numbered[0-9]+\\.xml"));
	}

	@Test
	public void buildXmlUnNumberedSeriesTest() {
		MpPublication pub = buildUnNumberedSeriesPub();
		assertTrue(busService.buildXml(pub, "http://pubs.usgs.gov/of/2013/1259/").matches(".*[\\\\/]unnumbered[0-9]+\\.xml"));
	}

	@Test
	public void buildBaseXmlNPETest() {
		assertEquals("", busService.buildBaseXml(null, null, null));
		assertEquals("", busService.buildBaseXml(new MpPublication(), null, null));
		assertEquals("", busService.buildBaseXml(new MpPublication(), "", null));
		assertEquals("", busService.buildBaseXml(new MpPublication(), "xyz", null));
		assertEquals("", busService.buildBaseXml(new MpPublication(), "xyz", ""));
		assertEquals("abc", busService.buildBaseXml(new MpPublication(), "xyz", "abc"));
		assertEquals("", busService.buildBaseXml(null, "xyz", "abc"));
		assertEquals("", busService.buildBaseXml(null, null, "abc"));
		assertEquals("", busService.buildBaseXml(null, "xyz", null));
		assertEquals("", busService.buildBaseXml(new MpPublication(), null, "abc"));
	}


	@Test
	public void buildBaseXmlEscapeTest() {
		CrossRefBusService busService2 = new CrossRefBusService(crossRefProtocol, crossRefHost, crossRefUrl, crossRefPort, crossRefUser,
				crossRefPwd, numberedSeriesXml, unNumberedSeriesXml, organizationNameXml, personNameXml,
				pagesXml, "drsteini@usgs.gov<>", pubsEMailer, warehouseEndpoint);
		String templateXml = "<root><de>" + CrossRefBusService.DEPOSITOR_EMAIL_REPLACE + "</de><yr>"
				+ CrossRefBusService.DISSEMINATION_YEAR_REPLACE + "</yr><ti>"
				+ CrossRefBusService.TITLE_REPLACE + "</ti><doi>"
				+ CrossRefBusService.DOI_NAME_REPLACE + "</doi><i>"
				+ CrossRefBusService.INDEX_PAGE_REPLACE + "</i><sna>"
				+ CrossRefBusService.SERIES_NAME_REPLACE + "</sna><issn>"
				+ CrossRefBusService.ONLINE_ISSN_REPLACE + "</issn><snbr>"
				+ CrossRefBusService.SERIES_NUMBER_REPLACE + "</snbr><root>";
		String resultXml = "<root><de>drsteini@usgs.gov&lt;&gt;</de><yr>yr&lt;&gt;</yr><ti>title&lt;&gt;</ti><doi>"
						+ "doi&lt;</doi><i>http://pubs.usgs.gov/&lt;&gt;</i><sna>&lt;sname&lt;</sna><issn>"
						+ "&gt;issn&lt;</issn><snbr>&lt;snbr&gt;</snbr><root>";
		MpPublication pub = new MpPublication();
		pub.setPublicationYear("yr<>");
		pub.setTitle("title<>");
		pub.setDoi("doi<");
		pub.setSeriesNumber("<snbr>");
		PublicationSeries ps = new PublicationSeries();
		ps.setText("<sname<");
		ps.setOnlineIssn(">issn<");
		pub.setSeriesTitle(ps);
		
		String xml = busService2.buildBaseXml(pub, "http://pubs.usgs.gov/<>", templateXml);
		assertNotNull(xml);
		assertEquals(harmonizeXml(resultXml), harmonizeXml(xml));
	}
	
	
	@Test
	public void buildBaseXmlNumberedSeriesTest() {
		MpPublication pub = buildNumberedSeriesPub();
		String xml = busService.buildBaseXml(pub, "http://pubs.usgs.gov/of/2013/1259/", numberedSeriesXml);
		assertNotNull(xml);
		assertFalse(xml.contains(CrossRefBusService.DOI_BATCH_ID_REPLACE));
		assertFalse(xml.contains(CrossRefBusService.SUBMISSION_TIMESTAMP_REPLACE));
		assertFalse(xml.contains(CrossRefBusService.DEPOSITOR_EMAIL_REPLACE));
		assertFalse(xml.contains(CrossRefBusService.SERIES_NAME_REPLACE));
		assertFalse(xml.contains(CrossRefBusService.ONLINE_ISSN_REPLACE));
		assertFalse(xml.contains(CrossRefBusService.DISSEMINATION_YEAR_REPLACE));
		assertFalse(xml.contains(CrossRefBusService.CONTRIBUTORS_REPLACE));
		assertFalse(xml.contains(CrossRefBusService.TITLE_REPLACE));
		assertFalse(xml.contains(CrossRefBusService.SERIES_NUMBER_REPLACE));
		assertFalse(xml.contains(CrossRefBusService.PAGES_REPLACE));
		assertFalse(xml.contains(CrossRefBusService.DOI_NAME_REPLACE));
		assertFalse(xml.contains(CrossRefBusService.INDEX_PAGE_REPLACE));
		String modCompare = replaceDoiBatchId(ofr20131259Xml, xml);
		modCompare = replaceTimestamp(modCompare, xml);
		modCompare = modCompare.replace("pubs_tech_group@usgs.gov", depositorEmail);
		assertEquals(harmonizeXml(modCompare), harmonizeXml(xml));
	}

	private MpPublication buildNumberedSeriesPub() {
		MpPublication pub = new MpPublication();
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
		UsgsContributor contributor = new UsgsContributor();
		contributor.setFamily("familyNameAuthor");
		ContributorType contributorTypeAuthor = new ContributorType();
		contributorTypeAuthor.setText("Authors");
		PublicationContributor<?> pubContributor = new MpPublicationContributor();
		pubContributor.setContributor(contributor);
		pubContributor.setContributorType(contributorTypeAuthor);
		contributors.add(pubContributor);
		pub.setContributors(contributors);

		pub.setStartPage("52");
		pub.setEndPage("56");
		
		Collection<PublicationLink<?>> links = new ArrayList<>();
		PublicationLink<?> link = new MpPublicationLink();
		LinkType linkType = new LinkType();
		linkType.setId(LinkType.INDEX_PAGE);
		link.setLinkType(linkType);
		link.setUrl("http://pubs.usgs.gov/of/2013/1259/");
		links.add(link);
		pub.setLinks(links);

		return pub;
	}

	@Test
	public void buildBaseXmlUnNumberedSeriesTest() {
		MpPublication pub = buildUnNumberedSeriesPub();
		String xml = busService.buildBaseXml(pub, "http://pubs.usgs.gov/of/2013/1259/", unNumberedSeriesXml);
		assertNotNull(xml);
		assertFalse(xml.contains(CrossRefBusService.DOI_BATCH_ID_REPLACE));
		assertFalse(xml.contains(CrossRefBusService.SUBMISSION_TIMESTAMP_REPLACE));
		assertFalse(xml.contains(CrossRefBusService.DEPOSITOR_EMAIL_REPLACE));
		assertFalse(xml.contains(CrossRefBusService.SERIES_NAME_REPLACE));
		assertFalse(xml.contains(CrossRefBusService.ONLINE_ISSN_REPLACE));
		assertFalse(xml.contains(CrossRefBusService.DISSEMINATION_YEAR_REPLACE));
		assertFalse(xml.contains(CrossRefBusService.CONTRIBUTORS_REPLACE));
		assertFalse(xml.contains(CrossRefBusService.TITLE_REPLACE));
		assertFalse(xml.contains(CrossRefBusService.SERIES_NUMBER_REPLACE));
		assertFalse(xml.contains(CrossRefBusService.PAGES_REPLACE));
		assertFalse(xml.contains(CrossRefBusService.DOI_NAME_REPLACE));
		assertFalse(xml.contains(CrossRefBusService.INDEX_PAGE_REPLACE));
		String modCompare = replaceDoiBatchId(testUnNumberedSeriesXml, xml);
		modCompare = replaceTimestamp(modCompare, xml);
		modCompare = modCompare.replace("pubs_tech_group@usgs.gov", depositorEmail);
		assertEquals(harmonizeXml(modCompare), harmonizeXml(xml));
	}

	protected MpPublication buildUnNumberedSeriesPub() {
		MpPublication pub = new MpPublication();
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
		return pub;
	}

	@Test
	public void getContributorsTest() {
		assertEquals("", busService.getContributors(null));
		
		MpPublication pub = new MpPublication();
		assertEquals("", busService.getContributors(pub));

		Collection<PublicationContributor<?>> contributors = new ArrayList<>();
		pub.setContributors(contributors);
		assertEquals("", harmonizeXml(busService.getContributors(pub)));
		
		ContributorType contributorTypeEditor = new ContributorType();
		contributorTypeEditor.setText("Editors");
		UsgsContributor contributor1 = new UsgsContributor();
		contributor1.setFamily("familyNameEditor");
		PublicationContributor<?> pubContributor1 = new MpPublicationContributor();
		pubContributor1.setContributor(contributor1);
		pubContributor1.setContributorType(contributorTypeEditor);
		contributors.add(pubContributor1);
		CorporateContributor contributor2 = new CorporateContributor();
		contributor2.setOrganization("orgNameEditor");
		PublicationContributor<?> pubContributor2 = new MpPublicationContributor();
		pubContributor2.setContributor(contributor2);
		pubContributor2.setContributorType(contributorTypeEditor);
		contributors.add(pubContributor2);
		assertEquals("<person_name sequence=\"first\" contributor_role=\"editor\"><surname>familyNameEditor</surname></person_name>"
				+ "<organization sequence=\"additional\" contributor_role=\"editor\">orgNameEditor</organization>",
				harmonizeXml(busService.getContributors(pub)));
		
		ContributorType contributorTypeAuthor = new ContributorType();
		contributorTypeAuthor.setText("Authors");
		CorporateContributor contributor4 = new CorporateContributor();
		contributor4.setOrganization("orgNameAuthor");
		PublicationContributor<?> pubContributor4 = new MpPublicationContributor();
		pubContributor4.setContributor(contributor4);
		pubContributor4.setContributorType(contributorTypeAuthor);
		contributors.add(pubContributor4);
		OutsideContributor contributor3 = new OutsideContributor();
		contributor3.setFamily("familyNameAuthor");
		PublicationContributor<?> pubContributor3 = new MpPublicationContributor();
		pubContributor3.setContributor(contributor3);
		pubContributor3.setContributorType(contributorTypeAuthor);
		contributors.add(pubContributor3);
		assertEquals("<organization sequence=\"first\" contributor_role=\"author\">orgNameAuthor</organization>"
				+ "<person_name sequence=\"additional\" contributor_role=\"author\"><surname>familyNameAuthor</surname></person_name>"
				+ "<person_name sequence=\"additional\" contributor_role=\"editor\"><surname>familyNameEditor</surname></person_name>"
				+ "<organization sequence=\"additional\" contributor_role=\"editor\">orgNameEditor</organization>",
				harmonizeXml(busService.getContributors(pub)));
	}
	
	@Test
	public void getPagesTest() {
		assertEquals("", busService.getPages(null));
		
		MpPublication pub = new MpPublication();
		assertEquals("", busService.getPages(pub));

		pub.setStartPage("");
		assertEquals("", busService.getPages(pub));
		
		pub.setEndPage("");
		assertEquals("", busService.getPages(pub));
		
		pub.setStartPage("52");
		assertEquals("", busService.getPages(pub));
		
		pub.setEndPage("56");
		assertEquals(testPagesXml, harmonizeXml(busService.getPages(pub)));
		
		//escape for xml
		pub.setStartPage("52<>");
		pub.setEndPage("56<>");
		assertEquals(testPagesXmlEscaped, harmonizeXml(busService.getPages(pub)));
	}

	@Test
	public void getIndexPageTest() {
		assertEquals("", busService.getIndexPage(null));

		MpPublication pub = new MpPublication();
		assertEquals("", busService.getIndexPage(pub));
		
		pub.setIndexId("abcdef123");
		String newUrl = warehouseEndpoint+"/publication/abcdef123";
		assertEquals(newUrl, busService.getIndexPage(pub));
		
		Collection<PublicationLink<?>> links = new ArrayList<>();
		pub.setLinks(links);
		assertEquals(newUrl, busService.getIndexPage(pub));

		PublicationLink<?> link = new MpPublicationLink();
		links.add(link);
		assertEquals(newUrl, busService.getIndexPage(pub));

		link.setUrl("xyz");
		assertEquals(newUrl, busService.getIndexPage(pub));

		LinkType linkType = new LinkType();
		linkType.setId(LinkType.THUMBNAIL);
		link.setLinkType(linkType);;
		assertEquals(newUrl, busService.getIndexPage(pub));

		PublicationLink<?> link2 = new MpPublicationLink();
		LinkType linkType2 = new LinkType();
		linkType2.setId(LinkType.INDEX_PAGE);
		link2.setLinkType(linkType2);
		link2.setUrl("http://pubs.usgs.gov/of/2013/1259/");
		links.add(link2);
		assertEquals("http://pubs.usgs.gov/of/2013/1259/", busService.getIndexPage(pub));
		
		//escape for xml
		link2.setUrl("http://pubs.usgs.gov/of/2013/1259/<>");
		assertEquals("http://pubs.usgs.gov/of/2013/1259/&lt;&gt;", busService.getIndexPage(pub));
	}

	@Test
	public void submitCrossRefTest() {
		MpPublication pub = buildNumberedSeriesPub();
		busService.submitCrossRef(pub);
	}
	
	@Test
	public void processPersonTest() {
		UsgsContributor contributor = new UsgsContributor();
		contributor.setFamily("familyName");
		ContributorType contributorType = new ContributorType();
		contributorType.setText("Authors");
		PublicationContributor<?> pubContributor = new MpPublicationContributor();
		pubContributor.setContributor(contributor);
		pubContributor.setContributorType(contributorType);
		assertEquals("<person_name sequence=\"first\" contributor_role=\"author\"><surname>familyName</surname></person_name>",
				harmonizeXml(busService.processPerson(pubContributor, CrossRefBusService.FIRST)));

		contributor.setGiven("givenName");
		contributor.setSuffix("sufF");
		assertEquals("<person_name sequence=\"additional\" contributor_role=\"author\"><given_name>givenName</given_name>" 
				+ "<surname>familyName</surname><suffix>sufF</suffix></person_name>",
				harmonizeXml(busService.processPerson(pubContributor, CrossRefBusService.ADDITIONAL)));
		
		contributor.setFamily("");
		assertEquals("<person_name sequence=\"additional\" contributor_role=\"author\"><given_name>givenName</given_name>" 
				+ "<surname></surname><suffix>sufF</suffix></person_name>",
				harmonizeXml(busService.processPerson(pubContributor, CrossRefBusService.ADDITIONAL)));

		//escape for xml
		contributor.setFamily("familyName<>");
		contributor.setGiven("givenName<>");
		contributor.setSuffix("sufF<>");
		assertEquals("<person_name sequence=\"additional\" contributor_role=\"author\"><given_name>givenName&lt;&gt;</given_name>" 
				+ "<surname>familyName&lt;&gt;</surname><suffix>sufF&lt;&gt;</suffix></person_name>",
				harmonizeXml(busService.processPerson(pubContributor, CrossRefBusService.ADDITIONAL)));
	}

	@Test
	public void processCorporationTest() {
		CorporateContributor contributor = new CorporateContributor();
		contributor.setOrganization("orgName");
		ContributorType contributorType = new ContributorType();
		contributorType.setText("Authors");
		PublicationContributor<?> pubContributor = new MpPublicationContributor();
		pubContributor.setContributor(contributor);
		pubContributor.setContributorType(contributorType);
		assertEquals("<organization sequence=\"first\" contributor_role=\"author\">orgName</organization>",
				harmonizeXml(busService.processCorporation(pubContributor, CrossRefBusService.FIRST)));

		contributor.setOrganization("");
		assertEquals("<organization sequence=\"additional\" contributor_role=\"author\"></organization>",
				harmonizeXml(busService.processCorporation(pubContributor, CrossRefBusService.ADDITIONAL)));
		
		//escape for xml
		contributor.setOrganization("orgName<>");
		assertEquals("<organization sequence=\"first\" contributor_role=\"author\">orgName&lt;&gt;</organization>",
				harmonizeXml(busService.processCorporation(pubContributor, CrossRefBusService.FIRST)));
	}
	
	@Test
	public void getContributorTypeTest() {
		assertEquals("", busService.getContributorType(null));
		
		PublicationContributor<?> pubContributor = new MpPublicationContributor();
		assertEquals("", busService.getContributorType(pubContributor));
		
		ContributorType contributorType = new ContributorType();
		pubContributor.setContributorType(contributorType);
		assertEquals("", busService.getContributorType(pubContributor));

		contributorType.setText("Authors");
		assertEquals("author", busService.getContributorType(pubContributor));

		contributorType.setText("Authorss");
		assertEquals("authors", busService.getContributorType(pubContributor));

		contributorType.setText("sAsuthors");
		assertEquals("sasuthor", busService.getContributorType(pubContributor));
		
		//escape for xml
		contributorType.setText("sAsuthors<>");
		assertEquals("sasuthors&lt;&gt;", busService.getContributorType(pubContributor));
	}

	private String replaceDoiBatchId(String referenceXml, String compareXml) {
		String doiBatchId = compareXml.substring(compareXml.indexOf("<doi_batch_id>") + 14, compareXml.indexOf("</doi_batch_id>"));
		return referenceXml.replace("4554651", doiBatchId);
	}
	
	private String replaceTimestamp(String referenceXml, String compareXml) {
		String timestamp = compareXml.substring(compareXml.indexOf("<timestamp>") + 11, compareXml.indexOf("</timestamp>"));
		return referenceXml.replace("6546546132", timestamp);
	}

}
