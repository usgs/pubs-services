package gov.usgs.cida.pubs.busservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.busservice.intfc.IPublicationBusService;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.CorporateContributor;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.transform.TransformerFactory;
import gov.usgs.cida.pubs.utility.PubsEMailer;
import gov.usgs.cida.pubs.validation.xml.XMLValidator;

public class CrossRefBusServiceTest extends BaseSpringTest {

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
	@Qualifier("crossRefDepositorEmail")
	protected String depositorEmail;
	@Autowired
	protected String crossRefSchemaUrl;
	@Mock
	protected PubsEMailer pubsEMailer;
	@Mock
	IPublicationBusService pubBusService;
	@Mock
	TransformerFactory transformerFactory;
	@Mock
	XMLValidator xmlValidator;
	
	private CrossRefBusService busService;
	
	@Before
	public void initTest() throws Exception {
		MockitoAnnotations.initMocks(this);
		busService = new CrossRefBusService(
			crossRefProtocol,
			crossRefHost,
			crossRefUrl,
			crossRefPort,
			crossRefUser,
			crossRefPwd,
			crossRefSchemaUrl,
			pubsEMailer,
			transformerFactory,
			xmlValidator
		);
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
	public void submitCrossRefTest() {
		MpPublication pub = buildNumberedSeriesPub();
		busService.submitCrossRef(pub);
	}
	
	
	@Test
	public void getIndexIdMessageForNullPub() {
		assertEquals("", busService.getIndexIdMessage(null));
	}
	
	@Test
	public void getIndexIdMessageForPubWithoutIndexId() {
		Publication<?> pub = new Publication<>();
		assertEquals("", busService.getIndexIdMessage(pub));
	}
	
	@Test
	public void getIndexIdForPubWithIndexId() {
		String indexId = "greatPubIndexId07";
		Publication<?> pub = new Publication<>();
		pub.setIndexId(indexId);
		assertTrue(busService.getIndexIdMessage(pub).contains(indexId));
	}
	
}
