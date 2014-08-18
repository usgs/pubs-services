package gov.usgs.cida.pubs.busservice.ipds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.dao.ContributorDaoTest;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class IpdsBindingTest extends BaseSpringTest {

    @Autowired
    public String contributorsXml;

    @Autowired
    public String usgsContributorXml;

    @Autowired
    public String newOutsideContributorXml;

    @Autowired
    public String existingOutsideContributorXml;

    @Mock
    private IpdsWsRequester ipdsWsRequester;

    @InjectMocks
    @Autowired
    public IpdsBinding binding;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
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
        when(ipdsWsRequester.getContributor(Matchers.anyString())).thenReturn(usgsContributorXml);
        Document d = binding.makeDocument("<root><d:CostCenterId>1</d:CostCenterId></root>");
        Contributor<?> contributor = binding.createUsgsContributor(d.getDocumentElement(), "123");
        assertNotNull(contributor);
        assertNotNull(contributor.getId());
        assertTrue(contributor instanceof UsgsContributor);
        assertUsgsContributorXml((UsgsContributor) contributor);
        assertEquals("1", ((UsgsContributor) contributor).getAffiliation().getId().toString());
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
        when(ipdsWsRequester.getContributor(Matchers.anyString())).thenReturn(usgsContributorXml);
        Document d = binding.makeDocument("<root><d:CostCenterId>1</d:CostCenterId></root>");
        Contributor<?> contributor = binding.getOrCreateUsgsContributor(d.getDocumentElement(), "3");
        ContributorDaoTest.assertContributor1(contributor);

        contributor = binding.getOrCreateUsgsContributor(d.getDocumentElement(), "123");
        assertNotNull(contributor);
        assertTrue(contributor instanceof UsgsContributor);
        assertNotNull(contributor.getId());
        assertUsgsContributorXml((UsgsContributor) contributor);
        assertEquals("1", ((UsgsContributor) contributor).getAffiliation().getId().toString());
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
        for (Iterator<MpPublicationContributor> fixedIter = fixed.iterator(); fixedIter.hasNext();) {
            MpPublicationContributor test = fixedIter.next();
            assertNull(test.getRank());
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
        assertNull(editorA.getRank());
        MpPublicationContributor editorB = (MpPublicationContributor) contributors.toArray()[3];
        assertEquals(1, editorB.getContributor().getId().intValue());
        assertEquals(ContributorType.EDITORS, editorB.getContributorType().getId());
        assertNull(editorB.getRank());
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
        assertEquals("182", contributor.getAffiliation().getId().toString());
    }

}
