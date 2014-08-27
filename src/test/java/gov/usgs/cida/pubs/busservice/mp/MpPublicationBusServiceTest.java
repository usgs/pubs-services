package gov.usgs.cida.pubs.busservice.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.busservice.intfc.IListBusService;
import gov.usgs.cida.pubs.dao.BaseSpringDaoTest;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDaoTest;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;

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

    @Autowired
    public Validator validator;

    @Mock
    private ICrossRefBusService crossRefBusService;

    @Autowired
    public IListBusService<PublicationCostCenter<MpPublicationCostCenter>> ccBusService;

    @Autowired
    public IListBusService<PublicationLink<MpPublicationLink>> linkBusService;

    private MpPublicationBusService busService;

    @Before
    public void initTest() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        busService = new MpPublicationBusService(validator, crossRefBusService, ccBusService, linkBusService);
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
    public void isUsgsNumberedSeriesTest() {
        assertFalse(busService.isUsgsNumberedSeries(null));
        PublicationSubtype pubSubtype = new PublicationSubtype();
        assertFalse(busService.isUsgsNumberedSeries(pubSubtype));
        pubSubtype.setId(1);
        assertFalse(busService.isUsgsNumberedSeries(pubSubtype));
        pubSubtype.setId(PublicationSubtype.USGS_UNNUMBERED_SERIES);
        assertFalse(busService.isUsgsNumberedSeries(pubSubtype));
        pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
        assertTrue(busService.isUsgsNumberedSeries(pubSubtype));
    }

    @Test
    public void isUsgsUnnumberedSeriesTest() {
        assertFalse(busService.isUsgsUnnumberedSeries(null));
        PublicationSubtype pubSubtype = new PublicationSubtype();
        assertFalse(busService.isUsgsUnnumberedSeries(pubSubtype));
        pubSubtype.setId(1);
        assertFalse(busService.isUsgsUnnumberedSeries(pubSubtype));
        pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
        assertFalse(busService.isUsgsUnnumberedSeries(pubSubtype));
        pubSubtype.setId(PublicationSubtype.USGS_UNNUMBERED_SERIES);
        assertTrue(busService.isUsgsUnnumberedSeries(pubSubtype));
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
            if (1 == link.getRank()) {
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

        //        //TODO
//        //Check Authors merged
//        pub = busService.getObject(1);
//        Collection<PublicationCostCenter<?>> costCenters = pub.getCostCenters();
//        costCenters.remove(costCenters.toArray()[0]);
//        MpPublicationCostCenter cc = new MpPublicationCostCenter();
//        cc.setCostCenter((CostCenter) CostCenter.getDao().getById(4));
//        costCenters.add(cc);
//        pub = busService.publicationPostProcessing(pub);
//        assertEquals(2, pub.getCostCenters().size());
//        boolean gotCc2 = false;
//        boolean gotCc4 = false;
//        for (Object i : pub.getCostCenters().toArray()) {
//        	if (i instanceof MpPublicationCostCenter) {
//        		if (2 == ((MpPublicationCostCenter) i).getCostCenter().getId()) {
//        			gotCc2 = true;
//        		} else if (4 == ((MpPublicationCostCenter) i).getCostCenter().getId()) {
//            		gotCc4 = true;
//            	}
//        	}
//        }
//        assertTrue(gotCc2);
//        assertTrue(gotCc4);
//
//        //TODO
//        //Check Editors merged
//        pub = busService.getObject(1);
//        Collection<PublicationCostCenter<?>> costCenters = pub.getCostCenters();
//        costCenters.remove(costCenters.toArray()[0]);
//        MpPublicationCostCenter cc = new MpPublicationCostCenter();
//        cc.setCostCenter((CostCenter) CostCenter.getDao().getById(4));
//        costCenters.add(cc);
//        pub = busService.publicationPostProcessing(pub);
//        assertEquals(2, pub.getCostCenters().size());
//        boolean gotCc2 = false;
//        boolean gotCc4 = false;
//        for (Object i : pub.getCostCenters().toArray()) {
//        	if (i instanceof MpPublicationCostCenter) {
//        		if (2 == ((MpPublicationCostCenter) i).getCostCenter().getId()) {
//        			gotCc2 = true;
//        		} else if (4 == ((MpPublicationCostCenter) i).getCostCenter().getId()) {
//            		gotCc4 = true;
//            	}
//        	}
//        }
//        assertTrue(gotCc2);
//        assertTrue(gotCc4);
//
    }

}
