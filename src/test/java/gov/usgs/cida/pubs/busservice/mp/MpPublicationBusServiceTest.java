package gov.usgs.cida.pubs.busservice.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.busservice.intfc.IListBusService;
import gov.usgs.cida.pubs.dao.BaseSpringDaoTest;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDaoTest;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDaoTest;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.utility.PubsUtilitiesTest;
import gov.usgs.cida.pubs.validation.ValidationResults;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

public class MpPublicationBusServiceTest extends BaseSpringDaoTest {

    public static final List<String> IGNORE_PROPERTIES = Arrays.asList("validationErrors", "valErrors", "costCenters", "authors", "editors", "links",
            "doi", "indexId");

	public Integer lockTimeoutHours = 1;

	@Autowired
    public Validator validator;

    @Mock
    private ICrossRefBusService crossRefBusService;

    @Autowired
    public IListBusService<PublicationCostCenter<MpPublicationCostCenter>> ccBusService;

    @Autowired
    public IListBusService<PublicationLink<MpPublicationLink>> linkBusService;

    @Autowired
    public IListBusService<PublicationContributor<MpPublicationContributor>> contributorBusService;

    private MpPublicationBusService busService;

    @Before
    public void initTest() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        busService = new MpPublicationBusService(validator, lockTimeoutHours, crossRefBusService, ccBusService, linkBusService, contributorBusService);
    }

    @Test
    public void getObjectTest() {
        busService.getObject(null);
        assertNull(busService.getObject(-1));
        assertNotNull(busService.getObject(1));
        MpPublication mpPub = busService.getObject(1);
        MpPublicationDaoTest.assertMpPub1(mpPub, PubsConstants.ANONYMOUS_USER);
        MpPublicationDaoTest.assertMpPub1Children(mpPub);
    }

    @Test
    public void getObjectsTest() {
        busService.getObjects(null);
        busService.getObjects(new HashMap<String, Object>());

        Map<String, Object> filters = new HashMap<>();
        filters.put("id", new int[] {-1});
        Collection<MpPublication> pubs = busService.getObjects(filters);
        assertNotNull(pubs);
        assertEquals(0, pubs.size());

        filters.put("id", new int[] {1});
        pubs = busService.getObjects(filters);
        assertNotNull(pubs);
        assertEquals(1, pubs.size());
    }

    @Test
    public void createObjectTest() {
        //TODO both a good create and a create w/validation errors.
        //public MpPublication createObject(MpPublication object)
        busService.createObject(null);

        MpPublication pub = busService.createObject(new MpPublication());
        assertNotNull(pub.getId());
    }

    @Test
    public void updateObjectTest() {
        busService.updateObject(null);
        busService.updateObject(new MpPublication());

        MpPublication pub = MpPublicationDaoTest.updatePubProperties(MpPublicationDaoTest.addAPub(MpPublication.getDao().getNewProdId()));
        MpPublication after = busService.updateObject(MpPublicationDaoTest.updatePubProperties(pub));
        assertDaoTestResults(MpPublication.class, pub, after, IGNORE_PROPERTIES, true, true);
        assertEquals(pub.getId().toString(), after.getIndexId());
        assertNull("Doi gets nulled out because the pub should no longer have one.", after.getDoi());

        pub = MpPublicationDaoTest.updatePubProperties(MpPublicationDaoTest.addAPub(MpPublication.getDao().getNewProdId()));
        MpPublication mid = MpPublicationDaoTest.updatePubProperties(pub);
        mid.setIpdsId("12345678901234567890");
        after = busService.updateObject(mid);
        assertDaoTestResults(MpPublication.class, pub, after, IGNORE_PROPERTIES, true, true);
        assertEquals(3, after.getValidationErrors().getValidationErrors().size());
    }

    @Test
    public void deleteObjectTest() {
        //TODO a delete w/validation errors.
        //public ValidationResults deleteObject(MpPublication object)
        busService.deleteObject(null);
        busService.deleteObject(-1);
        
        busService.deleteObject(2);
        assertMpPublicationDeleted(2);
    }

    @Test
    public void preProcessingTest() {
        //TODO - more than just the basic doi testing
        busService.publicationPreProcessing(null);
        busService.publicationPreProcessing(new MpPublication());

        MpPublication inPublication = new MpPublication();
        PublicationType pubType = new PublicationType();
        pubType.setId(PublicationType.REPORT);
        inPublication.setPublicationType(pubType);
        PublicationSubtype pubSubtype = new PublicationSubtype();
        pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
        inPublication.setPublicationSubtype(pubSubtype);
        PublicationSeries pubSeries = new PublicationSeries();
        pubSeries.setId(PublicationSeries.SIR);
        inPublication.setSeriesTitle(pubSeries);
        inPublication.setSeriesNumber("nu-m,be r");
        MpPublication outPublication = busService.publicationPreProcessing(inPublication);
        assertNotNull(outPublication);
        assertNotNull(outPublication.getId());
        assertEquals("sirnumber", outPublication.getIndexId());
        assertEquals(PubsConstants.DOI_PREFIX + "/" + outPublication.getIndexId(), outPublication.getDoi());

        inPublication = new MpPublication();
        inPublication.setSeriesNumber("nu-m,be r");
        inPublication.setId(123);
        outPublication = busService.publicationPreProcessing(inPublication);
        assertNotNull(outPublication);
        assertNotNull(outPublication.getId());
        assertEquals("123", outPublication.getIndexId());
        assertNull(outPublication.getDoi());

        inPublication = new MpPublication();
        pubSeries.setId(508);
        inPublication.setSeriesTitle(pubSeries);
        outPublication = busService.publicationPreProcessing(inPublication);
        assertNotNull(outPublication);
        assertNotNull(outPublication.getId());
        assertEquals(outPublication.getId().toString(), outPublication.getIndexId());
        assertNull(outPublication.getDoi());

        inPublication = new MpPublication();
        pubSubtype.setId(PublicationSubtype.USGS_UNNUMBERED_SERIES);
        inPublication.setPublicationType(pubType);
        inPublication.setPublicationSubtype(pubSubtype);
        inPublication.setSeriesTitle(pubSeries);
        outPublication = busService.publicationPreProcessing(inPublication);
        assertNotNull(outPublication);
        assertNotNull(outPublication.getId());
        assertEquals(outPublication.getId().toString(), outPublication.getIndexId());
        assertEquals(PubsConstants.DOI_PREFIX + "/" + outPublication.getIndexId(), outPublication.getDoi());

    }

    @Test
    public void getUsgsNumberedSeriesIndexId() {
        assertNull(busService.getUsgsNumberedSeriesIndexId(null));
        assertNull(busService.getUsgsNumberedSeriesIndexId(new MpPublication()));
        MpPublication pub = new MpPublication();
        PublicationSeries pubSeries = new PublicationSeries();
        pub.setSeriesTitle(pubSeries);
        assertNull(busService.getUsgsNumberedSeriesIndexId(pub));
        pubSeries.setId(-1);
        assertNull(busService.getUsgsNumberedSeriesIndexId(pub));
        pubSeries.setId(330);
        assertNull(busService.getUsgsNumberedSeriesIndexId(pub));
        pub.setSeriesNumber( "1- 2-3,4,5 6");
        assertEquals("ofr123456", busService.getUsgsNumberedSeriesIndexId(pub));
        pub.setChapter("abc");
        pub.setSubchapterNumber("123");
        assertEquals("ofr123456ABC123", busService.getUsgsNumberedSeriesIndexId(pub));
    }

    @Test
    public void doiNameTest() {
        assertNull(MpPublicationBusService.getDoiName(null));
        assertNull(MpPublicationBusService.getDoiName(""));
        assertEquals(PubsConstants.DOI_PREFIX + "/abc", MpPublicationBusService.getDoiName("abc"));
    }

    @Test
    public void publicationPostProcessingTest() {
        assertNull(busService.publicationPostProcessing(null));
        MpPublication pub = busService.publicationPostProcessing(new MpPublication());
        assertNull(pub);

        //Check CostCenters merged
        pub = busService.getObject(1);
        Collection<PublicationCostCenter<?>> costCenters = pub.getCostCenters();
        costCenters.remove(costCenters.toArray()[0]);
        MpPublicationCostCenter cc = new MpPublicationCostCenter();
        cc.setCostCenter((CostCenter) CostCenter.getDao().getById(4));
        costCenters.add(cc);
        pub = busService.publicationPostProcessing(pub);
        assertEquals(2, pub.getCostCenters().size());
        boolean gotCc2 = false;
        boolean gotCc4 = false;
        for (Object i : pub.getCostCenters().toArray()) {
        	if (i instanceof MpPublicationCostCenter) {
        		if (2 == ((MpPublicationCostCenter) i).getCostCenter().getId()) {
        			gotCc2 = true;
        		} else if (4 == ((MpPublicationCostCenter) i).getCostCenter().getId()) {
            		gotCc4 = true;
            	}
        	}
        }
        assertTrue(gotCc2);
        assertTrue(gotCc4);

        //Check Links merged
        pub = busService.getObject(1);
        Collection<PublicationLink<?>> links = pub.getLinks();
        links.remove(links.toArray()[0]);
        MpPublicationLink link = new MpPublicationLink();
        link.setPublicationId(1);
        link.setRank(3);
        link.setDescription("new merge");
        links.add(link);
        pub = busService.publicationPostProcessing(pub);
        assertEquals(2, pub.getLinks().size());
        boolean gotLink1 = false;
        boolean gotLink2 = false;
        boolean gotLink3 = false;
        for (PublicationLink<?> added : pub.getLinks()) {
            assertEquals(1, added.getPublicationId().intValue());
            if (1 == added.getRank()) {
            	gotLink1 = true;
            } else if (2 == added.getRank()) {
            	gotLink2 = true;
            } else if (3 == added.getRank()) {
            	gotLink3 = true;
            }
        }
        assertFalse(gotLink1);
        assertTrue(gotLink2);
        assertTrue(gotLink3);

        //Check Authors merged
        pub = busService.getObject(1);
        Collection<PublicationContributor<?>> authors = pub.getAuthors();
        authors.remove(authors.toArray()[0]);
        MpPublicationContributor author = new MpPublicationContributor();
        author.setPublicationId(1);
        author.setContributorType(ContributorType.getDao().getById(ContributorType.AUTHORS));
        author.setContributor(Contributor.getDao().getById(3));
        author.setRank(80);
        authors.add(author);
        pub = busService.publicationPostProcessing(pub);
        assertEquals(2, pub.getAuthors().size());
        boolean gotAuth1 = false;
        boolean gotAuth2 = false;
        boolean gotAuth3 = false;
        for (PublicationContributor<?> added : pub.getAuthors()) {
            assertEquals(1, added.getPublicationId().intValue());
            if (1 == added.getContributor().getId()) {
            	gotAuth1 = true;
            } else if (2 == added.getContributor().getId()) {
            	gotAuth2 = true;
            } else if (3 == added.getContributor().getId()) {
            	gotAuth3 = true;
            }
        }
        assertFalse(gotAuth1);
        assertTrue(gotAuth2);
        assertTrue(gotAuth3);

        //Check Editors merged
        pub = busService.getObject(1);
        Collection<PublicationContributor<?>> editors = pub.getEditors();
        //Get rid of ID 2 - which comes out first due to the ranks on the editors.
        editors.remove(editors.toArray()[0]);
        MpPublicationContributor editor = new MpPublicationContributor();
        editor.setPublicationId(1);
        editor.setContributorType(ContributorType.getDao().getById(ContributorType.EDITORS));
        editor.setContributor(Contributor.getDao().getById(3));
        editor.setRank(80);
        editors.add(editor);
        pub = busService.publicationPostProcessing(pub);
        assertEquals(2, pub.getEditors().size());
        boolean gotEditor1 = false;
        boolean gotEditor2 = false;
        boolean gotEditor3 = false;
        for (PublicationContributor<?> added : pub.getEditors()) {
            assertEquals(1, added.getPublicationId().intValue());
            if (1 == added.getContributor().getId()) {
            	gotEditor1 = true;
            } else if (2 == added.getContributor().getId()) {
            	gotEditor2 = true;
            } else if (3 == added.getContributor().getId()) {
            	gotEditor3 = true;
            }
        }
        assertTrue(gotEditor1);
        assertFalse(gotEditor2);
        assertTrue(gotEditor3);

    }

    @Test
    public void beginPublicationEditTest() {
        //This one should change nothing
        busService.beginPublicationEdit(2);
        Publication<?> mpPub2after = MpPublication.getDao().getById(2);
        MpPublicationDaoTest.assertMpPub2(mpPub2after, PubsConstants.ANONYMOUS_USER);
        MpPublicationDaoTest.assertMpPub2Children(mpPub2after);

        //This one is in PW, not MP and should be moved
        MpPublication mpPub4before = MpPublication.getDao().getById(4);
        assertNull(mpPub4before);
        busService.beginPublicationEdit(4);
        MpPublication mpPub4after = MpPublication.getDao().getById(4);
        PwPublicationDaoTest.assertPwPub4(mpPub4after);
        PwPublicationDaoTest.assertPwPub4Children(mpPub4after);
        assertEquals(PubsConstants.ANONYMOUS_USER, mpPub4after.getLockUsername());
    }

    @Test
    public void checkLocksTest() {
        //nulls = OK (assume this is an add)
    	assertNull(busService.checkAvailability(null));
        
    	//No lockedUsername = OK
    	assertNull(busService.checkAvailability(3));
    	
    	//Not expired = not OK
    	assertEquals("drsteini", busService.checkAvailability(1).getValue());
    	
    	//Same user = OK
    	PubsUtilitiesTest.buildTestAuthentication("drsteini", null);
    	assertNull(busService.checkAvailability(1));

    	//Expired = OK (We are testing by setting the timeout to 0 and -1 for these test)
//TODO make less brittle and re-activate
//    	PubsUtilitiesTest.clearTestAuthentication();
//        busService = new MpPublicationBusService(validator, 0, crossRefBusService, ccBusService, linkBusService, contributorBusService);
//    	assertNull(busService.checkAvailability(1));
//        busService = new MpPublicationBusService(validator, -1, crossRefBusService, ccBusService, linkBusService, contributorBusService);
//    	assertNull(busService.checkAvailability(1));
    }

    @Test
    public void releaseLocksUserTest() {
    	MpPublication mpPub = MpPublicationDaoTest.addAPub(MpPublication.getDao().getNewProdId());
    	busService.releaseLocksUser(PubsConstants.ANONYMOUS_USER);
    	mpPub = MpPublication.getDao().getById(mpPub.getId());
    	assertNull(mpPub.getLockUsername());
    	
    	//this one was also anonymous
    	mpPub = MpPublication.getDao().getById(2);
    	assertNull(mpPub.getLockUsername());
    	
    	//this should still be locked
    	mpPub = MpPublication.getDao().getById(1);
    	assertEquals("drsteini", mpPub.getLockUsername());

    	busService.releaseLocksUser("drsteini");
    	mpPub = MpPublication.getDao().getById(1);
    	assertNull(mpPub.getLockUsername());
    }
    
    @Test
    public void releaseLocksPubTest() {
    	MpPublication mpPub = MpPublicationDaoTest.addAPub(MpPublication.getDao().getNewProdId());
    	busService.releaseLocksPub(mpPub.getId());
    	mpPub = MpPublication.getDao().getById(mpPub.getId());
    	assertNull(mpPub.getLockUsername());
    	
    	mpPub = MpPublication.getDao().getById(2);
    	assertEquals(PubsConstants.ANONYMOUS_USER,mpPub.getLockUsername());
    	
    	mpPub = MpPublication.getDao().getById(1);
    	assertEquals("drsteini", mpPub.getLockUsername());

    	busService.releaseLocksPub(1);
    	mpPub = MpPublication.getDao().getById(1);
    	assertNull(mpPub.getLockUsername());
    }

	@Test
    public void setListTest() {
    	busService.setList(null);
    	MpPublication mpPub = new MpPublication();
    	busService.setList(mpPub);
    	mpPub.setId(1);
    	busService.setList(mpPub);
    	
    	mpPub = MpPublicationDaoTest.addAPub(MpPublication.getDao().getNewProdId());
    	busService.setList(mpPub);
    	Map<String, Object> filters = new HashMap<>();
    	filters.put("publicationId", mpPub.getId());
    	List<MpListPublication> lists = MpListPublication.getDao().getByMap(filters);
    	assertEquals(1, lists.size());
    	testLists(lists, false, false, true, false);
    	
    	mpPub.setIpdsReviewProcessState(null);
    	busService.setList(mpPub);
    	lists = MpListPublication.getDao().getByMap(filters);
    	assertEquals(2, lists.size());
    	testLists(lists, false, false, true, true);

    	PublicationType pubType = new PublicationType();
    	pubType.setId(PublicationType.ARTICLE);
    	mpPub.setPublicationType(pubType);
    	busService.setList(mpPub);
    	lists = MpListPublication.getDao().getByMap(filters);
    	assertEquals(3, lists.size());
    	testLists(lists, true, false, true, true);

    	pubType.setId(4);
    	mpPub.setPublicationSubtype(null);
    	busService.setList(mpPub);
    	lists = MpListPublication.getDao().getByMap(filters);
    	assertEquals(4, lists.size());
    	testLists(lists, true, true, true, true);

    	mpPub.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
    	busService.setList(mpPub);
    	lists = MpListPublication.getDao().getByMap(filters);
    	assertEquals(4, lists.size());
    	testLists(lists, true, true, true, true);
	}
	
	public void testLists(List<MpListPublication> lists, boolean expect_journal, boolean expect_other,
			boolean expect_pending, boolean expect_numbered) {
		boolean got_journal = false;  //IPDS_JOURNAL_ARTICLES = "3";
		boolean got_other = false;    //IPDS_OTHER_PUBS = "4";
		boolean got_pending = false;  // PENDING_USGS_SERIES = "9";
		boolean got_numbered = false; // IPDS_USGS_NUMBERED_SERIES = "275";

    	for (MpListPublication list : lists) {
    		if (MpList.IPDS_JOURNAL_ARTICLES.contentEquals(list.getMpList().getId().toString())) {
    			got_journal = true;
    		} else 	if (MpList.IPDS_OTHER_PUBS.contentEquals(list.getMpList().getId().toString())) {
    			got_other = true;
    		} else 	if (MpList.PENDING_USGS_SERIES.contentEquals(list.getMpList().getId().toString())) {
    			got_pending = true;
    		} else 	if (MpList.IPDS_USGS_NUMBERED_SERIES.contentEquals(list.getMpList().getId().toString())) {
    			got_numbered = true;
    		}
    	}
		assertEquals(expect_journal, got_journal);
		assertEquals(expect_other, got_other);
		assertEquals(expect_pending, got_pending);
		assertEquals(expect_numbered, got_numbered);
	}

    @Test
    public void defaultThumbnailTest() {
    	busService.defaultThumbnail(null);
    	MpPublication mpPub = new MpPublication();
    	busService.defaultThumbnail(mpPub);
    	
    	mpPub = MpPublicationDaoTest.addAPub(MpPublication.getDao().getNewProdId());
    	busService.defaultThumbnail(mpPub);
    	Map<String, Object> filters = new HashMap<>();
    	filters.put("publicationId", mpPub.getId());
    	List<MpPublicationLink> links = MpPublicationLink.getDao().getByMap(filters);
    	assertEquals(1, links.size());
    	MpPublicationLink link = links.get(0);
    	assertEquals(LinkType.THUMBNAIL, link.getLinkType().getId());
    	assertEquals(MpPublicationLink.USGS_THUMBNAIL, link.getUrl());
    	
    	mpPub = MpPublicationDaoTest.addAPub(MpPublication.getDao().getNewProdId());
    	PublicationSubtype pubSubtype = new PublicationSubtype();
    	pubSubtype.setId(1);
    	mpPub.setPublicationSubtype(pubSubtype);
    	busService.defaultThumbnail(mpPub);
    	filters.clear();
    	filters.put("publicationId", mpPub.getId());
    	links = MpPublicationLink.getDao().getByMap(filters);
    	assertEquals(1, links.size());
    	link = links.get(0);
    	assertEquals(LinkType.THUMBNAIL, link.getLinkType().getId());
    	assertEquals(MpPublicationLink.EXTERNAL_THUMBNAIL, link.getUrl());
    	
    	link.setUrl("something else");
    	MpPublicationLink.getDao().update(link);
    	busService.defaultThumbnail(mpPub);
    	links = MpPublicationLink.getDao().getByMap(filters);
    	assertEquals(1, links.size());
    	link = links.get(0);
    	assertEquals(LinkType.THUMBNAIL, link.getLinkType().getId());
    	assertEquals("something else", link.getUrl());
    }

    @Test
    public void publishTest() {
    	assertTrue(busService.publish(null).isEmpty());
    	assertEquals("Field:Publication - Message:Publication does not exist. - Level:fatal - Value:-1\nValidator Results: 1 result(s)\n",
    			busService.publish(-1).toString());

    	ValidationResults valRes = busService.publish(2);
    	assertTrue(valRes.isEmpty());
    	Publication<?> pub = PwPublication.getDao().getById(2);
    	MpPublicationDaoTest.assertPwPub2(pub);
    	assertEquals(1, pub.getAuthors().size());
    	assertEquals(1, pub.getEditors().size());
    	//Link count is one more than in the dataset.xml because a default thumbnail is added by the service.
    	assertEquals(2, pub.getLinks().size());
    	assertEquals(1, pub.getCostCenters().size());

    	assertMpPublicationDeleted(2);
    }
    
    public void assertMpPublicationDeleted(Integer id) {
    	assertNull(MpPublication.getDao().getById(id));
    	Map<String, Object> filters = new HashMap<>();
    	filters.put("publicationId", id);
    	assertTrue(MpPublicationLink.getDao().getByMap(filters).isEmpty());
    	assertTrue(MpPublicationCostCenter.getDao().getByMap(filters).isEmpty());
    	assertTrue(MpPublicationContributor.getDao().getByMap(filters).isEmpty());
    	assertTrue(MpListPublication.getDao().getByMap(filters).isEmpty());
    }

}
