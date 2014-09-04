package gov.usgs.cida.pubs.busservice.ipds;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import gov.usgs.cida.pubs.PubMap;
import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.dao.AffiliationDaoTest;
import gov.usgs.cida.pubs.dao.BaseSpringDaoTest;
import gov.usgs.cida.pubs.dao.ContributorDaoTest;
import gov.usgs.cida.pubs.dao.ipds.IpdsMessageLogDaoTest;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.OutsideAffiliation;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;
import gov.usgs.cida.pubs.domain.ipds.PublicationMap;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class IpdsBindingTest extends BaseSpringDaoTest {

    @Autowired
    public String contributorsXml;

    @Autowired
    public String usgsContributorXml;

    @Autowired
    public String newOutsideContributorXml;

    @Autowired
    public String existingOutsideContributorXml;

    @Autowired
    public String costCenterXml;

    @Autowired
    public String notesXml;

    @Autowired
    @Qualifier("personContributorBusService")
    public IBusService<PersonContributor<?>> contributorBusService;

    @Mock
    private IpdsWsRequester ipdsWsRequester;

    public IpdsBinding binding;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        binding = new IpdsBinding(ipdsWsRequester, contributorBusService);
    }

    @Test
    public void makeDocumentTest() throws SAXException, IOException {
        assertNull(binding.makeDocument(null));
        assertNull(binding.makeDocument(""));
        try {
            binding.makeDocument("<root>");
        } catch (Exception e) {
            assertTrue(e instanceof SAXParseException);
        }
        Document doc = binding.makeDocument("<root/>");
        assertNotNull(doc);
        assertEquals(1, doc.getElementsByTagName("root").getLength());
    }

    @Test
    public void bindNotesTest() throws SAXException, IOException {
        PublicationMap map = binding.bindNotes(null, null);
        assertEquals(0, map.getFields().size());

        map = binding.bindNotes("", null);
        assertEquals(0, map.getFields().size());

        map = binding.bindNotes(notesXml, null);
        assertEquals(0, map.getFields().size());

        Set<String> tags = new HashSet<>();
        map = binding.bindNotes(notesXml, tags);
        assertEquals(0, map.getFields().size());

        tags.add("d:NoteComment");
        map = binding.bindNotes(notesXml, tags);
        assertEquals(1, map.getFields().size());
        assertEquals("d:NoteComment", map.getFields().get(0));
        assertEquals("M10-0272|", map.get("d:NoteComment"));

        tags.clear();
        tags.add("com");
        map = binding.bindNotes("<root><com>hi</com><com>dave</com><com/></root>", tags);
        assertEquals(1, map.getFields().size());
        assertEquals("com", map.getFields().get(0));
        assertEquals("hi|dave||", map.get("com"));

        tags.clear();
        tags.add("com");
        map = binding.bindNotes("<root><com/></root>", tags);
        assertEquals(0, map.getFields().size());

        tags.clear();
        tags.add("dog");
        map = binding.bindNotes("<root><com/></root>", tags);
        assertEquals(0, map.getFields().size());
    }

    @Test
    public void bindPublishedURLTest() {
        PubMap pubMap = new PubMap();
        assertNull(binding.bindPublishedURL(null));
        assertNull(binding.bindPublishedURL(pubMap));
        pubMap.put(IpdsMessageLog.PUBLISHEDURL, null);
        assertNull(binding.bindPublishedURL(pubMap));
        pubMap.put(IpdsMessageLog.PUBLISHEDURL, "");
        assertNull(binding.bindPublishedURL(pubMap));
        pubMap.put(IpdsMessageLog.PUBLISHEDURL, ",yada ,yada, yada");
        assertNull(binding.bindPublishedURL(pubMap));
        pubMap.put(IpdsMessageLog.PUBLISHEDURL, "http://dave.com/this/url, Howdy!");
        Collection<PublicationLink<?>> links = binding.bindPublishedURL(pubMap);
        assertNotNull(links);
        assertEquals(1, links.size());
        PublicationLink<?> link = (PublicationLink<?>) links.toArray()[0];
        assertEquals("http://dave.com/this/url", link.getUrl());
        assertEquals(LinkType.INDEX_PAGE, link.getLinkType().getId());
    }

    @Test
    public void bindContributorTest() throws SAXException, IOException, ParserConfigurationException {
        UsgsContributor person = binding.bindContributor(usgsContributorXml);
        assertUsgsContributorXml(person);
    }

    @Test
    public void getFirstNodeTextTest() throws SAXException, IOException {
        Document d = binding.makeDocument("<root/>");
        assertNull(binding.getFirstNodeText(null, null));
        assertNull(binding.getFirstNodeText(d.getDocumentElement(), null));
        assertNull(binding.getFirstNodeText(null, ""));

        d = binding.makeDocument("<root><one>oneText</one><two>twoTextA</two><two>twoTextB</two></root>");
        assertEquals("oneText", binding.getFirstNodeText(d.getDocumentElement(), "one"));
        assertEquals("twoTextA", binding.getFirstNodeText(d.getDocumentElement(), "two"));
        assertNull(binding.getFirstNodeText(d.getDocumentElement(), "three"));
   }

    @Test
    public void createUsgsContributorTest() throws SAXException, IOException {
        when(ipdsWsRequester.getContributor(anyString())).thenReturn(usgsContributorXml);
        Document d = binding.makeDocument("<root><d:CostCenterId>1</d:CostCenterId></root>");
        Contributor<?> contributor = binding.createUsgsContributor(d.getDocumentElement(), "123");
        assertNotNull(contributor);
        assertNotNull(contributor.getId());
        assertTrue(contributor instanceof UsgsContributor);
        assertUsgsContributorXml((UsgsContributor) contributor);
        assertEquals("4", ((UsgsContributor) contributor).getAffiliation().getId().toString());
    }

    @Test
    public void createNonUsgsContributor() throws SAXException, IOException {
        Document d = binding.makeDocument(newOutsideContributorXml);
        Contributor<?> contributor = binding.createNonUsgsContributor(d.getDocumentElement(), "Jane", "ODoe");
        assertNotNull(contributor);
        assertTrue(contributor instanceof OutsideContributor);
        assertNotNull(contributor.getId());
        assertNewOutsideContributorXml((OutsideContributor) contributor);
    }

    @Test
    public void getOrCreateUsgsContributorTest() throws SAXException, IOException {
        when(ipdsWsRequester.getContributor(anyString())).thenReturn(usgsContributorXml);
        Document d = binding.makeDocument("<root><d:CostCenterId>1</d:CostCenterId></root>");
        Contributor<?> contributor = binding.getOrCreateUsgsContributor(d.getDocumentElement(), "3");
        ContributorDaoTest.assertContributor1(contributor);

        contributor = binding.getOrCreateUsgsContributor(d.getDocumentElement(), "123");
        assertNotNull(contributor);
        assertTrue(contributor instanceof UsgsContributor);
        assertNotNull(contributor.getId());
        assertUsgsContributorXml((UsgsContributor) contributor);
        assertEquals("4", ((UsgsContributor) contributor).getAffiliation().getId().toString());
    }

    @Test
    public void getOrCreateNonUsgsContributorTest() throws SAXException, IOException {
        Document d = binding.makeDocument(existingOutsideContributorXml);
        Contributor<?> contributor = binding.getOrCreateNonUsgsContributor(d.getDocumentElement());
        ContributorDaoTest.assertContributor3(contributor);

        d = binding.makeDocument(newOutsideContributorXml);
        contributor = binding.getOrCreateNonUsgsContributor(d.getDocumentElement());
        assertNotNull(contributor);
        assertTrue(contributor instanceof OutsideContributor);
        assertNotNull(contributor.getId());
        assertNewOutsideContributorXml((OutsideContributor) contributor);
    }

    @Test
    public void getOrCreateNonUsgsAffiliationTest() {
        //Get
        Affiliation<?> affiliation = binding.getOrCreateNonUsgsAffiliation("Outside Affiliation 1");
        AffiliationDaoTest.assertAffiliation5(affiliation);

        //New
        affiliation = binding.getOrCreateNonUsgsAffiliation("Outside Test");
        assertNewOutside(affiliation);
    }

    @Test
    public void createNonUsgsAffiliationTest() {
        Affiliation<?> affiliation = binding.getOrCreateNonUsgsAffiliation("Outside Test");
        assertNewOutside(affiliation);
    }

    protected void assertNewOutside(Affiliation<?> affiliation) {
        assertTrue(affiliation instanceof OutsideAffiliation);
        assertNotNull(affiliation.getId());
        assertEquals("Outside Test", affiliation.getName());
        assertTrue(affiliation.isActive());
        assertFalse(affiliation.isUsgs());
    }

    @Test
    public void getOrCreateCostCenterTest() throws SAXException, IOException {
        when(ipdsWsRequester.getCostCenter(anyString(), anyString())).thenReturn(costCenterXml);
        Affiliation<?> affiliation = binding.getOrCreateCostCenter("4");
        AffiliationDaoTest.assertAffiliation1(affiliation);

        String ipdsId = String.valueOf(randomPositiveInt());
        affiliation = binding.getOrCreateCostCenter(ipdsId);
        assertNewUsgs(affiliation, ipdsId);
    }

    @Test
    public void getOrCreateCostCenterCCTest() throws SAXException, IOException {
        when(ipdsWsRequester.getCostCenter(anyString(), anyString())).thenReturn(costCenterXml);
        PubMap pubMap = new PubMap();
        pubMap.put(IpdsMessageLog.COSTCENTERID, "4");
        Affiliation<?> affiliation = binding.getOrCreateCostCenter(pubMap);
        AffiliationDaoTest.assertAffiliation1(affiliation);

        String ipdsId = String.valueOf(randomPositiveInt());
        pubMap.put(IpdsMessageLog.COSTCENTERID, ipdsId);
        affiliation = binding.getOrCreateCostCenter(pubMap);
        assertNewUsgs(affiliation, ipdsId);
    }

    @Test
    public void getOrCreateUsgsAffiliationTest() throws SAXException, IOException {
        when(ipdsWsRequester.getCostCenter(anyString(), anyString())).thenReturn(costCenterXml);
        Document d = binding.makeDocument("<root><d:CostCenterId>4</d:CostCenterId></root>");
        Affiliation<?> affiliation = binding.getOrCreateUsgsAffiliation(d.getDocumentElement());
        AffiliationDaoTest.assertAffiliation1(affiliation);

        String ipdsId = String.valueOf(randomPositiveInt());
        d = binding.makeDocument("<root><d:CostCenterId>" + ipdsId + "</d:CostCenterId></root>");
        affiliation = binding.getOrCreateUsgsAffiliation(d.getDocumentElement());
        assertNewUsgs(affiliation, ipdsId);
    }

    @Test
    public void createUsgsAffiliationTest() throws SAXException, IOException {
        when(ipdsWsRequester.getCostCenter(anyString(), anyString())).thenReturn(costCenterXml);
        String ipdsId = String.valueOf(randomPositiveInt());
        Affiliation<?> affiliation = binding.createUsgsAffiliation(ipdsId);
        assertNewUsgs(affiliation, ipdsId);
    }

    protected void assertNewUsgs(Affiliation<?> affiliation, String ipdsId) {
        assertTrue(affiliation instanceof CostCenter);
        assertNotNull(affiliation.getId());
        assertEquals("CostCenter Test", affiliation.getName());
        assertTrue(affiliation.isActive());
        assertTrue(affiliation.isUsgs());
        assertEquals(ipdsId, ((CostCenter) affiliation).getIpdsId().toString());
    }

    @Test
    public void fixRanksTest() {
        List<MpPublicationContributor> contributors = new ArrayList<>();
        MpPublicationContributor contributorA = new MpPublicationContributor();
        contributorA.setId(1);
        contributorA.setRank(1);
        contributors.add(contributorA);
        MpPublicationContributor contributorB = new MpPublicationContributor();
        contributorB.setId(2);
        contributorB.setRank(2);
        contributors.add(contributorB);
        Collection<MpPublicationContributor> fixed = binding.fixRanks(contributors);
        assertEquals(2, fixed.size());
        for (Iterator<MpPublicationContributor> fixedIter = fixed.iterator(); fixedIter.hasNext();) {
            MpPublicationContributor test = fixedIter.next();
            if (1 == test.getId()) {
                assertEquals(1, test.getRank().intValue());
            } else {
                assertEquals(2, test.getRank().intValue());
            }
        }

        MpPublicationContributor contributorC = new MpPublicationContributor();
        contributorC.setId(3);
        contributorC.setRank(1);
        contributors.add(contributorC);
        fixed = binding.fixRanks(contributors);
        assertEquals(3, fixed.size());
        for (int i=0; i<fixed.size(); i++) {
            assertEquals(i+1, ((MpPublicationContributor) fixed.toArray()[i]).getRank().intValue());
        }
    }

    @Test
    public void bindContributorsTest() throws SAXException, IOException {
        Collection<MpPublicationContributor> contributors = binding.bindContributors(contributorsXml);
        assertEquals(4, contributors.size());
        MpPublicationContributor authorA = (MpPublicationContributor) contributors.toArray()[0];
        assertEquals(1, authorA.getContributor().getId().intValue());
        assertEquals(ContributorType.AUTHORS, authorA.getContributorType().getId());
        assertEquals(2, authorA.getRank().intValue());
        MpPublicationContributor authorB = (MpPublicationContributor) contributors.toArray()[1];
        assertEquals(3, authorB.getContributor().getId().intValue());
        assertEquals(ContributorType.AUTHORS, authorB.getContributorType().getId());
        assertEquals(3, authorB.getRank().intValue());
        MpPublicationContributor editorA = (MpPublicationContributor) contributors.toArray()[2];
        assertEquals(3, editorA.getContributor().getId().intValue());
        assertEquals(ContributorType.EDITORS, editorA.getContributorType().getId());
        assertEquals(1, editorA.getRank().intValue());
        MpPublicationContributor editorB = (MpPublicationContributor) contributors.toArray()[3];
        assertEquals(1, editorB.getContributor().getId().intValue());
        assertEquals(ContributorType.EDITORS, editorB.getContributorType().getId());
        assertEquals(2, editorB.getRank().intValue());
    }

    @Test
    public void getStringValueTest() {
        PubMap pubMap = new PubMap();
        assertNull(binding.getStringValue(null, null));
        assertNull(binding.getStringValue(pubMap, null));
        assertNull(binding.getStringValue(pubMap, "xx"));
        assertNull(binding.getStringValue(pubMap, "xx"));
        assertNull(binding.getStringValue(null, "xx"));

        pubMap.put("xxx", "  owiytuiwruto   ");
        assertEquals("owiytuiwruto", binding.getStringValue(pubMap, "xxx"));

        pubMap.put("xxx", "");
        assertNull(binding.getStringValue(pubMap, "xxx"));
    }

    @Test
    public void getPublicationSeriesTest() {
        PubMap pubMap = new PubMap();
        PublicationSubtype subtype = new PublicationSubtype();
        assertNull(binding.getPublicationSeries(null, null));
        assertNull(binding.getPublicationSeries(subtype, null));
        assertNull(binding.getPublicationSeries(subtype, pubMap));
        assertNull(binding.getPublicationSeries(null, pubMap));

        subtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
        assertNull(binding.getPublicationSeries(subtype, pubMap));

        pubMap.put(IpdsMessageLog.USGSSERIESVALUE, "");
        assertNull(binding.getPublicationSeries(subtype, pubMap));

        pubMap.put(IpdsMessageLog.USGSSERIESVALUE, "Coal Map");
        assertEquals(309, binding.getPublicationSeries(subtype, pubMap).getId().intValue());
    }

    @Test
    public void bindPublicationTest() {
        PubMap pubMap = new PubMap();
        assertNull(binding.bindPublication(null));
        assertNull(binding.bindPublication(pubMap));

        pubMap = IpdsMessageLogDaoTest.createPubMap1();
        MpPublication pub1 = binding.bindPublication(pubMap);
        assertPub1(pub1);

        pubMap = IpdsMessageLogDaoTest.createPubMap2();
        MpPublication pub2 = binding.bindPublication(pubMap);
        assertPub2(pub2);

        pubMap = IpdsMessageLogDaoTest.createPubMap3();
        MpPublication pub3 = binding.bindPublication(pubMap);
        assertPub3(pub3);

        pubMap = IpdsMessageLogDaoTest.createPubMap4();
        MpPublication pub4 = binding.bindPublication(pubMap);
        assertPub4(pub4);
    }

    protected void assertUsgsContributorXml(UsgsContributor person) {
        assertEquals("Jane", person.getFamily());
        assertEquals("Doe", person.getGiven());
        assertEquals("jmdoe@usgs.gov", person.getEmail());
    }

    protected void assertNewOutsideContributorXml(OutsideContributor contributor) {
        assertEquals("Jane", contributor.getFamily());
        assertEquals("ODoe", contributor.getGiven());
        assertNull(contributor.getEmail());
        assertEquals("7", contributor.getAffiliation().getId().toString());
    }

    protected void assertPub1(MpPublication pub) {
        assertPubCommon(pub);

        assertEquals(18, pub.getPublicationType().getId().intValue());
        assertEquals(5, pub.getPublicationSubtype().getId().intValue());
        assertEquals(330, pub.getSeriesTitle().getId().intValue());

        assertEquals("12.1", pub.getSeriesNumber());
        assertEquals("a", pub.getChapter());
        assertEquals("My Final Title", pub.getTitle());

        assertEquals("My Abstract", pub.getDocAbstract());
        assertEquals("U.S. Geological Survey", pub.getPublisher());
        assertEquals("Reston VA", pub.getPublisherLocation());

        assertEquals("doi", pub.getDoi());
        assertEquals("isbn234", pub.getIsbn());
        assertEquals("I really want to cooperate", pub.getCollaboration());

        assertEquals("A short citation", pub.getUsgsCitation());
        assertEquals("physical desc", pub.getProductDescription());
        assertEquals("pages 1-5", pub.getStartPage());

        assertEquals("what a summary", pub.getNotes());
        assertEquals("IP1234", pub.getIpdsId());
        assertEquals(ProcessType.SPN_PRODUCTION.getIpdsValue(), pub.getIpdsReviewProcessState());

        assertEquals("453228", pub.getIpdsInternalId());
        assertEquals("A Journal", pub.getLargerWorkTitle());
        assertEquals("2014", pub.getPublicationYear());
    }

    protected void assertPub2(MpPublication pub) {
        assertPubCommon(pub);

        assertNull(pub.getPublicationType());
        assertNull(pub.getPublicationSubtype());
        assertNull(pub.getSeriesTitle());

        assertNull(pub.getSeriesNumber());
        assertEquals("a", pub.getChapter());
        assertEquals("My Working Title", pub.getTitle());

        assertEquals("My Abstract", pub.getDocAbstract());
        assertEquals("Not one of those USGS Publishers", pub.getPublisher());
        assertNull(pub.getPublisherLocation());

        assertEquals("doi", pub.getDoi());
        assertEquals("isbn234", pub.getIsbn());
        assertEquals("I really want to cooperate", pub.getCollaboration());

        assertEquals("A short citation", pub.getUsgsCitation());
        assertEquals("physical desc", pub.getProductDescription());
        assertEquals("pages 1-5", pub.getStartPage());

        assertEquals("what a summary", pub.getNotes());
        assertEquals("IP1234", pub.getIpdsId());
        assertEquals(ProcessType.SPN_PRODUCTION.getIpdsValue(), pub.getIpdsReviewProcessState());

        assertEquals("453228", pub.getIpdsInternalId());
        assertEquals("A Journal Title", pub.getLargerWorkTitle());
        assertEquals("1994", pub.getPublicationYear());
    }

    protected void assertPub3(MpPublication pub) {
        assertPubCommon(pub);

        assertEquals(2, pub.getPublicationType().getId().intValue());
        assertNull(pub.getPublicationSubtype());
        assertNull(pub.getSeriesTitle());

        assertNull(pub.getSeriesNumber());
        assertEquals("a", pub.getChapter());
        assertEquals("My Final Title", pub.getTitle());

        assertEquals("My Abstract", pub.getDocAbstract());
        assertEquals("U.S. Geological Survey", pub.getPublisher());
        assertEquals("Reston VA", pub.getPublisherLocation());

        assertEquals("doi", pub.getDoi());
        assertEquals("isbn234", pub.getIsbn());
        assertEquals("I really want to cooperate", pub.getCollaboration());

        assertEquals("A short citation", pub.getUsgsCitation());
        assertEquals("physical desc", pub.getProductDescription());
        assertEquals("pages 1-5", pub.getStartPage());

        assertEquals("what a summary", pub.getNotes());
        assertEquals("IP1234", pub.getIpdsId());
        assertEquals(ProcessType.SPN_PRODUCTION.getIpdsValue(), pub.getIpdsReviewProcessState());

        assertEquals("453228", pub.getIpdsInternalId());
        //TODO pub.setLargerWorkTitle(getStringValue(inPub, IpdsMessageLog.JOURNALTITLE));
        assertEquals("1857", pub.getPublicationYear());
    }

    protected void assertPub4(MpPublication pub) {
        assertPubCommon(pub);

        assertEquals(21, pub.getPublicationType().getId().intValue());
        assertEquals(28, pub.getPublicationSubtype().getId().intValue());
        assertNull(pub.getSeriesTitle());

        assertNull(pub.getSeriesNumber());
        assertEquals("a", pub.getChapter());
        assertEquals("My Final Title", pub.getTitle());

        assertEquals("My Abstract", pub.getDocAbstract());
        assertEquals("Not one of those USGS Publishers", pub.getPublisher());
        assertNull(pub.getPublisherLocation());

        assertEquals("doi", pub.getDoi());
        assertEquals("isbn234", pub.getIsbn());
        assertEquals("I really want to cooperate", pub.getCollaboration());

        assertEquals("A short citation", pub.getUsgsCitation());
        assertEquals("physical desc", pub.getProductDescription());
        assertEquals("pages 1-5", pub.getStartPage());

        assertEquals("what a summary", pub.getNotes());
        assertEquals("IP1234", pub.getIpdsId());
        assertEquals(ProcessType.SPN_PRODUCTION.getIpdsValue(), pub.getIpdsReviewProcessState());

        assertEquals("453228", pub.getIpdsInternalId());
        assertEquals("A Journal", pub.getLargerWorkTitle());
        assertEquals("2014", pub.getPublicationYear());
    }

    protected void assertPubCommon(MpPublication pub) {
        assertNotNull(pub);

        assertNull(pub.getId());
        assertNull(pub.getIndexId());
        assertNull(pub.getDisplayToPublicDate());

        assertNull(pub.getSubseriesTitle());
        assertNull(pub.getSubchapterNumber());
        assertEquals("English", pub.getLanguage());

        assertNull(pub.getIssn());
        assertNull(pub.getContact());
        assertNull(pub.getEndPage());

        assertNull(pub.getNumberOfPages());
        assertNull(pub.getOnlineOnly());
        assertNull(pub.getAdditionalOnlineFiles());

        assertNull(pub.getTemporalStart());
        assertNull(pub.getTemporalEnd());
        assertNull(pub.getLargerWorkType());

        assertNull(pub.getConferenceTitle());
        assertNull(pub.getConferenceDate());
        assertNull(pub.getConferenceLocation());

        assertNull(pub.getAuthors());
        assertNull(pub.getEditors());
        assertNull(pub.getCostCenters());

        assertNull(pub.getLinks());
    }

}
