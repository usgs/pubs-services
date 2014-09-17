package gov.usgs.cida.pubs.busservice.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.busservice.intfc.IListBusService;
import gov.usgs.cida.pubs.dao.BaseSpringDaoTest;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDaoTest;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDaoTest;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.utility.PubsUtilitiesTest;

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
        MpPublicationDaoTest.assertMpPub1(mpPub);
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
        assertEquals(3, after.getValErrors().size());
    }

    @Test
    public void deleteObjectTest() {
        //TODO both a good delete and a delete w/validation errors.
        //public ValidationResults deleteObject(MpPublication object)
        busService.deleteObject(null);
        busService.deleteObject(new MpPublication());
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
        assertEquals(MpPublicationBusService.DOI_PREFIX + "/" + outPublication.getIndexId(), outPublication.getDoi());

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
        assertEquals(MpPublicationBusService.DOI_PREFIX + "/" + outPublication.getIndexId(), outPublication.getDoi());

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
        assertEquals(MpPublicationBusService.DOI_PREFIX + "/abc", MpPublicationBusService.getDoiName("abc"));
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
        MpPublication mpPub2after = MpPublication.getDao().getById(2);
        MpPublicationDaoTest.assertMpPub2(mpPub2after);
        MpPublicationDaoTest.assertMpPub2Children(mpPub2after);

        //This one is in PW, not MP and should be moved
        MpPublication mpPub4before = MpPublication.getDao().getById(4);
        assertNull(mpPub4before);
        busService.beginPublicationEdit(4);
        MpPublication mpPub4after = MpPublication.getDao().getById(4);
        PwPublicationDaoTest.assertPwPub4(mpPub4after);
        PwPublicationDaoTest.assertPwPub4Children(mpPub4after);
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
    	assertNull("drsteini", busService.checkAvailability(1));

    	//Expired = OK (We are testing by setting the timeout to 0 and -1 for these test)
    	PubsUtilitiesTest.clearTestAuthentication();
        busService = new MpPublicationBusService(validator, 0, crossRefBusService, ccBusService, linkBusService, contributorBusService);
    	assertNull("drsteini", busService.checkAvailability(1));
        busService = new MpPublicationBusService(validator, -1, crossRefBusService, ccBusService, linkBusService, contributorBusService);
    	assertNull("drsteini", busService.checkAvailability(1));
    }

    //TODO@Test
    public void setListTest() {
//    	void setList(MpPublication inPublication) {
//    	    if (null != inPublication.getIpdsId() 
//    	            && null == PwPublication.getDao().getById(inPublication.getId()) ) {
//    	        MpListPublication newListEntry = new MpListPublication();
//    	        newListEntry.setMpPublication(inPublication);
//    	        if (null != inPublication.getPublicationType()
//    	        		&& PublicationType.ARTICLE.equals(inPublication.getPublicationType().getId())) {
//    	            newListEntry.setMpList(MpList.getDao().getById(MpList.IPDS_JOURNAL_ARTICLES));
//    	        } else {
//    	            if (isUsgsNumberedSeries(inPublication.getPublicationSubtype())) {
//    	                if (null != inPublication.getIpdsReviewProcessState() &&
//    	                        ProcessType.SPN_PRODUCTION.getIpdsValue().contentEquals(inPublication.getIpdsReviewProcessState())) {
//    	                    newListEntry.setMpList(MpList.getDao().getById(MpList.PENDING_USGS_SERIES));
//    	                } else {
//    	                    newListEntry.setMpList(MpList.getDao().getById(MpList.IPDS_USGS_NUMBERED_SERIES));
//    	                }
//    	            } else {
//    	                if (null != inPublication.getIpdsReviewProcessState() &&
//    	                        ProcessType.SPN_PRODUCTION.getIpdsValue().contentEquals(inPublication.getIpdsReviewProcessState())) {
//    	                    newListEntry.setMpList(MpList.getDao().getById(MpList.PENDING_USGS_SERIES));
//    	                } else {
//    	                    newListEntry.setMpList(MpList.getDao().getById(MpList.IPDS_OTHER_PUBS));
//    	                }
//    	            }
//    	        }
//    	
//    	        //Check for existing list entry
//    	        Map<String, Object> params = new HashMap<>();
//    	        params.put("publicationId", newListEntry.getMpPublication().getId());
//    	        params.put("mpListId", newListEntry.getMpList().getId());
//    	        List<MpListPublication> listEntries = MpListPublication.getDao().getByMap(params);
//    	        if (0 == listEntries.size()) {
//    	            MpListPublication.getDao().add(newListEntry);
//    	        } else {
//    	            MpListPublication.getDao().update(newListEntry);
//    	        }
//    	    }
    	fail("you need to code this");
    }

    //TODO@Test
    public void defaultThumbnailTest() {
//		void defaultThumbnail(final MpPublication mpPub)
//        Map<String, Object> filters = new HashMap<String, Object>();
//        filters.put("linkTypeId", LinkType.THUMBNAIL);
//        filters.put("publicationId", mpPub.getId());
//        List<MpPublicationLink> thumbnails = MpPublicationLink.getDao().getByMap(filters);
//        if (0 == thumbnails.size()) {
//        	MpPublicationLink thumbnail = new MpPublicationLink();
//            thumbnail.setPublicationId(mpPub.getId());
//            thumbnail.setLinkType(LinkType.getDao().getById(LinkType.THUMBNAIL.toString()));
//            if (null != mpPub.getPublicationSubtype() &&
//                    (PublicationSubtype.USGS_NUMBERED_SERIES.equals(mpPub.getPublicationSubtype())
//                            || PublicationSubtype.USGS_UNNUMBERED_SERIES.equals(mpPub.getPublicationSubtype()))) {
//                thumbnail.setUrl(MpPublicationLink.USGS_THUMBNAIL);
//            } else {
//                thumbnail.setUrl(MpPublicationLink.EXTERNAL_THUMBNAIL);
//            }
//            MpPublicationLink.getDao().add(thumbnail);
//        }

    	fail("you need to code this");
    }

    //TODO@Test
    public void publishTest() {
    	fail("you need to code this");
    }

}
