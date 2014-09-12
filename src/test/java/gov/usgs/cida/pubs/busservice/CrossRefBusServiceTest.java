package gov.usgs.cida.pubs.busservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class CrossRefBusServiceTest extends BaseSpringTest {
	
	private static final String testPagesXml ="<pages><first_page>52</first_page><last_page>56</last_page></pages>";

	@Autowired
	@Qualifier("ofr20131259Xml")
	private String ofr20131259Xml;
	@Autowired
	@Qualifier("testUnNumberedSeriesXml")
	private String testUnNumberedSeriesXml;

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
	@Mock
    protected PubsEMailer pubsEMailer;

    private CrossRefBusService busService;

    @Before
    public void initTest() throws Exception {
        MockitoAnnotations.initMocks(this);
        busService = new CrossRefBusService(crossRefProtocol, crossRefHost, crossRefUrl, crossRefPort, crossRefUser,
        		crossRefPwd, numberedSeriesXml, unNumberedSeriesXml, organizationNameXml, personNameXml,
        		pagesXml, pubsEMailer);
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
	public void buildBaseXmlNumberedSeriesTest() {
		MpPublication pub = buildNumberedSeriesPub();
		String xml = busService.buildBaseXml(pub, "http://pubs.usgs.gov/of/2013/1259/", numberedSeriesXml);
		assertNotNull(xml);
		assertFalse(xml.contains("{doi_batch_id}"));
		assertFalse(xml.contains("{submission_timestamp}"));
		assertFalse(xml.contains("{series_name}"));
		assertFalse(xml.contains("{online_issn}"));
		assertFalse(xml.contains("{dissemination_month}"));
		assertFalse(xml.contains("{dissemination_day}"));
		assertFalse(xml.contains("{dissemination_year}"));
		assertFalse(xml.contains("{contributers}"));
		assertFalse(xml.contains("{title}"));
		assertFalse(xml.contains("{series_number}"));
		assertFalse(xml.contains("{pages}"));
		assertFalse(xml.contains("{doi_name}"));
		assertFalse(xml.contains("{index_page}"));
		String modCompare = replaceDoiBatchId(ofr20131259Xml, xml);
		modCompare = replaceTimestamp(modCompare, xml);
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

		Collection<PublicationContributor<?>> authors = new ArrayList<>();
		UsgsContributor contributor = new UsgsContributor();
		contributor.setFamily("familyNameAuthor");
		ContributorType contributorTypeAuthor = new ContributorType();
		contributorTypeAuthor.setText("Authors");
		PublicationContributor<?> pubContributor = new MpPublicationContributor();
		pubContributor.setContributor(contributor);
		pubContributor.setContributorType(contributorTypeAuthor);
		authors.add(pubContributor);
		pub.setAuthors(authors);

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
		assertFalse(xml.contains("{doi_batch_id}"));
		assertFalse(xml.contains("{submission_timestamp}"));
		assertFalse(xml.contains("{series_name}"));
		assertFalse(xml.contains("{online_issn}"));
		assertFalse(xml.contains("{dissemination_month}"));
		assertFalse(xml.contains("{dissemination_day}"));
		assertFalse(xml.contains("{dissemination_year}"));
		assertFalse(xml.contains("{contributers}"));
		assertFalse(xml.contains("{title}"));
		assertFalse(xml.contains("{series_number}"));
		assertFalse(xml.contains("{pages}"));
		assertFalse(xml.contains("{doi_name}"));
		assertFalse(xml.contains("{index_page}"));
		String modCompare = replaceDoiBatchId(testUnNumberedSeriesXml, xml);
		modCompare = replaceTimestamp(modCompare, xml);
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

		Collection<PublicationContributor<?>> editors = new ArrayList<>();
		pub.setEditors(editors);
		Collection<PublicationContributor<?>> authors = new ArrayList<>();
		pub.setAuthors(authors);
		assertEquals("", harmonizeXml(busService.getContributors(pub)));
		
		ContributorType contributorTypeEditor = new ContributorType();
		contributorTypeEditor.setText("Editors");
		UsgsContributor contributor1 = new UsgsContributor();
		contributor1.setFamily("familyNameEditor");
		PublicationContributor<?> pubContributor1 = new MpPublicationContributor();
		pubContributor1.setContributor(contributor1);
		pubContributor1.setContributorType(contributorTypeEditor);
		editors.add(pubContributor1);
		CorporateContributor contributor2 = new CorporateContributor();
		contributor2.setOrganization("orgNameEditor");
		PublicationContributor<?> pubContributor2 = new MpPublicationContributor();
		pubContributor2.setContributor(contributor2);
		pubContributor2.setContributorType(contributorTypeEditor);
		editors.add(pubContributor2);
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
		authors.add(pubContributor4);
		OutsideContributor contributor3 = new OutsideContributor();
		contributor3.setFamily("familyNameAuthor");
		PublicationContributor<?> pubContributor3 = new MpPublicationContributor();
		pubContributor3.setContributor(contributor3);
		pubContributor3.setContributorType(contributorTypeAuthor);
		authors.add(pubContributor3);
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
		
	}

	@Test
	public void getIndexPageTest() {
		assertEquals("", busService.getIndexPage(null));

		MpPublication pub = new MpPublication();
		assertEquals("", busService.getIndexPage(pub));
		
		Collection<PublicationLink<?>> links = new ArrayList<>();
		pub.setLinks(links);
		assertEquals("", busService.getIndexPage(pub));

		PublicationLink<?> link = new MpPublicationLink();
		links.add(link);
		assertEquals("", busService.getIndexPage(pub));

		link.setUrl("xyz");
		assertEquals("", busService.getIndexPage(pub));

		LinkType linkType = new LinkType();
		linkType.setId(LinkType.THUMBNAIL);
		link.setLinkType(linkType);;
		assertEquals("", busService.getIndexPage(pub));

		PublicationLink<?> link2 = new MpPublicationLink();
		LinkType linkType2 = new LinkType();
		linkType2.setId(LinkType.INDEX_PAGE);
		link2.setLinkType(linkType2);
		link2.setUrl("http://pubs.usgs.gov/of/2013/1259/");
		links.add(link2);
		assertEquals("http://pubs.usgs.gov/of/2013/1259/", busService.getIndexPage(pub));
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
