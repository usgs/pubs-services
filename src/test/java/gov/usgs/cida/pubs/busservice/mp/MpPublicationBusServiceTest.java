package gov.usgs.cida.pubs.busservice.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.busservice.intfc.IListBusService;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDaoTest;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MpPublicationBusServiceTest extends BaseSpringTest {

    public static final List<String> IGNORE_PROPERTIES = Arrays.asList("validationErrors", "valErrors", "costCenters", "authors", "editors", "links",
            "doiName", "indexId");

    @Autowired
    public Validator validator;

    @Autowired
    public IListBusService<PublicationCostCenter<MpPublicationCostCenter>> ccBusService;

    private class BusService extends MpPublicationBusService {
        public BusService(Validator validator, IListBusService<PublicationCostCenter<MpPublicationCostCenter>> ccBusService) {
            this.validator = validator;
            this.costCenterBusService = ccBusService;
        }
    }
    private BusService busService;

    @Before
    public void initTest() {
        busService = new BusService(validator, ccBusService);
    }

    @Test
    public void getObjectTest() {
        busService.getObject(null);
        assertNull(busService.getObject(-1));
        assertNotNull(busService.getObject(1));
    }

    @Test
    public void getObjectsTest() {
        busService.getObjects(null);
        busService.getObjects(new HashMap<String, Object>());

        Map<String, Object> filters = new HashMap<>();
        filters.put("id", -1);
        Collection<MpPublication> pubs = busService.getObjects(filters);
        assertNotNull(pubs);
        assertEquals(0, pubs.size());

        filters.put("id", 1);
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
        //TODO both a good update and an update w/validation errors.
        //public MpPublication updateObject(MpPublication object)
        busService.updateObject(null);
        busService.updateObject(new MpPublication());

        MpPublication pub = MpPublicationDaoTest.updatePubProperties(MpPublicationDaoTest.addAPub(MpPublication.getDao().getNewProdId()));
        MpPublication after = busService.updateObject(MpPublicationDaoTest.updatePubProperties(MpPublication.getDao().getById(pub.getId())));
        assertDaoTestResults(MpPublication.class, pub, after, IGNORE_PROPERTIES, true, true);
        assertEquals(pub.getId().toString(), after.getIndexId());
        assertNull("DoiName gets nulled out because the pub should no longer have one.", after.getDoiName());
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
        //TODO - more than just the basic doiName testing
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
        inPublication.setPublicationSeries(pubSeries);
        inPublication.setSeriesNumber("nu-m,be r");
        MpPublication outPublication = busService.publicationPreProcessing(inPublication);
        assertNotNull(outPublication);
        assertNotNull(outPublication.getId());
        assertEquals("sirnumber", outPublication.getIndexId());
        assertEquals(MpPublicationBusService.DOI_PREFIX + "/" + outPublication.getIndexId(), outPublication.getDoiName());

        inPublication = new MpPublication();
        inPublication.setSeriesNumber("nu-m,be r");
        inPublication.setId(123);
        outPublication = busService.publicationPreProcessing(inPublication);
        assertNotNull(outPublication);
        assertNotNull(outPublication.getId());
        assertEquals("123", outPublication.getIndexId());
        assertNull(outPublication.getDoiName());

        inPublication = new MpPublication();
        pubSeries.setId(508);
        inPublication.setPublicationSeries(pubSeries);
        outPublication = busService.publicationPreProcessing(inPublication);
        assertNotNull(outPublication);
        assertNotNull(outPublication.getId());
        assertEquals(outPublication.getId().toString(), outPublication.getIndexId());
        assertNull(outPublication.getDoiName());

        inPublication = new MpPublication();
        pubSubtype.setId(PublicationSubtype.USGS_UNNUMBERED_SERIES);
        inPublication.setPublicationType(pubType);
        inPublication.setPublicationSubtype(pubSubtype);
        inPublication.setPublicationSeries(pubSeries);
        outPublication = busService.publicationPreProcessing(inPublication);
        assertNotNull(outPublication);
        assertNotNull(outPublication.getId());
        assertEquals(outPublication.getId().toString(), outPublication.getIndexId());
        assertEquals(MpPublicationBusService.DOI_PREFIX + "/" + outPublication.getIndexId(), outPublication.getDoiName());

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
        PublicationSeries pubSeries = new PublicationSeries();
        assertNull(busService.getUsgsNumberedSeriesIndexId(null, null));
        assertNull(busService.getUsgsNumberedSeriesIndexId(pubSeries, null));
        assertNull(busService.getUsgsNumberedSeriesIndexId(null, "123"));
        pubSeries.setId(-1);
        assertNull(busService.getUsgsNumberedSeriesIndexId(pubSeries, "123"));
        pubSeries.setId(508);
        assertNull(busService.getUsgsNumberedSeriesIndexId(pubSeries, "123"));
        pubSeries.setId(330);
        assertEquals("ofr123456", busService.getUsgsNumberedSeriesIndexId(pubSeries, "1- 2-3,4,5 6"));
    }

    @Test
    public void doiNameTest() {
        assertNull(MpPublicationBusService.getDoiName(null));
        assertNull(MpPublicationBusService.getDoiName(""));
        assertEquals(MpPublicationBusService.DOI_PREFIX + "/abc", MpPublicationBusService.getDoiName("abc"));
    }

    @Test
    public void publicationPostProcessingNumberedTest() {
        MpPublication pub = new MpPublication();
        pub.setId(MpPublication.getDao().getNewProdId());
//        pub.setPublicationTypeId(PublicationType.USGS_NUMBERED_SERIES);
//        pub.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
//        pub.setIpdsId("IPDSX-" + pub.getId());
        pub.setDoiName("test");
        MpPublication.getDao().add(pub);
        busService.publicationPostProcessing(pub);

//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("prodId", pub.getId());
//        params.put("listId", MpList.PENDING_USGS_SERIES);
//        List<MpListPubsRel> listEntries = MpListPubsRel.getDao().getByMap(params);
//        assertEquals(1, listEntries.size());
//
//        Map<String, Object> filters = new HashMap<String, Object>();
//        filters.put("prodId", pub.getId());
//        filters.put("doiLink", MpLinkDim.DOI_LINK_SITE);
//        List<MpLinkDim> doiLinks = MpLinkDim.getDao().getByMap(filters);
//        assertEquals(0, doiLinks.size());


        pub = new MpPublication();
        pub.setId(MpPublication.getDao().getNewProdId());
//        pub.setPublicationTypeId(PublicationType.USGS_NUMBERED_SERIES);
//        pub.setIpdsReviewProcessState(ProcessType.DISSEMINATION.getIpdsValue());
//        pub.setIpdsId("IPDSX-" + pub.getId());
        pub.setDoiName("test");
        MpPublication.getDao().add(pub);
        busService.publicationPostProcessing(pub);

//        params = new HashMap<String, Object>();
//        params.put("prodId", pub.getId());
//        params.put("listId", MpList.PENDING_USGS_SERIES);
//        listEntries = MpListPubsRel.getDao().getByMap(params);
//        assertEquals(0, listEntries.size());
//
//        filters = new HashMap<String, Object>();
//        filters.put("prodId", pub.getId());
//        filters.put("doiLink", MpLinkDim.DOI_LINK_SITE);
//        doiLinks = MpLinkDim.getDao().getByMap(filters);
//        assertEquals(0, doiLinks.size());
    }

    @Test
    public void publicationPostProcessingUnNumberedTest() {
        MpPublication pub = new MpPublication();
        pub.setId(MpPublication.getDao().getNewProdId());
//        pub.setPublicationTypeId(PublicationType.USGS_UNNUMBERED_SERIES);
//        pub.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
//        pub.setIpdsId("IPDSX-" + pub.getId());
        pub.setDoiName("test");
        MpPublication.getDao().add(pub);
        busService.publicationPostProcessing(pub);

//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("prodId", pub.getId());
//        params.put("listId", MpList.PENDING_USGS_SERIES);
//        List<MpListPubsRel> listEntries = MpListPubsRel.getDao().getByMap(params);
//        assertEquals(1, listEntries.size());
//
//        Map<String, Object> filters = new HashMap<String, Object>();
//        filters.put("prodId", pub.getId());
//        filters.put("doiLink", MpLinkDim.DOI_LINK_SITE);
//        List<MpLinkDim> doiLinks = MpLinkDim.getDao().getByMap(filters);
//        assertEquals(0, doiLinks.size());


        pub = new MpPublication();
        pub.setId(MpPublication.getDao().getNewProdId());
//        pub.setPublicationTypeId(PublicationType.USGS_UNNUMBERED_SERIES);
//        pub.setIpdsReviewProcessState(ProcessType.DISSEMINATION.getIpdsValue());
//        pub.setIpdsId("IPDSX-" + pub.getId());
        pub.setDoiName("test");
        MpPublication.getDao().add(pub);
        busService.publicationPostProcessing(pub);

//        params = new HashMap<String, Object>();
//        params.put("prodId", pub.getId());
//        params.put("listId", MpList.PENDING_USGS_SERIES);
//        listEntries = MpListPubsRel.getDao().getByMap(params);
//        assertEquals(0, listEntries.size());
//
//        filters = new HashMap<String, Object>();
//        filters.put("prodId", pub.getId());
//        filters.put("doiLink", MpLinkDim.DOI_LINK_SITE);
//        doiLinks = MpLinkDim.getDao().getByMap(filters);
//        assertEquals(0, doiLinks.size());

    }

    @Test
    public void publicationPostProcessingNPETest() {
        //No real asserts - just don't NPE.
        busService.publicationPostProcessing(null);
        busService.publicationPostProcessing(new MpPublication());
    }

    //public ValidationResults publish(final Integer prodId)
    //private void defaultThumbnail(final MpPublication mpPub)

}
