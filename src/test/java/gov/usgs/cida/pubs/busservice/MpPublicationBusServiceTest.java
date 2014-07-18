package gov.usgs.cida.pubs.busservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.BaseSpringTest;
//import gov.usgs.cida.pubs.domain.MpLinkDim;
//import gov.usgs.cida.pubs.domain.MpList;
//import gov.usgs.cida.pubs.domain.MpListPubsRel;
import gov.usgs.cida.pubs.domain.MpPublication;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
//import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.PublicationType;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

public class MpPublicationBusServiceTest extends BaseSpringTest {

    private class BusService extends MpPublicationBusService {
        public MpPublication publicationPreProcessing(final MpPublication inPublication) {
            return super.publicationPreProcessing(inPublication);
        }
        public MpPublication publicationPostProcessing(final MpPublication inPublication) {
            return super.publicationPostProcessing(inPublication);
        }
    }
    private BusService busService = new BusService();

    @Test
    public void getObjectTest() {
        assertNull(busService.getObject(-1));
        assertNotNull(busService.getObject(1));
    }

    @Test
    public void getObjectsTest() {
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
    }

    @Test
    public void updateObjectTest() {
        //TODO both a good update and an update w/validation errors.
        //public MpPublication updateObject(MpPublication object)
    }

    @Test
    public void deleteObjectTest() {
        //TODO both a good delete and a delete w/validation errors.
        //public ValidationResults deleteObject(MpPublication object)
    }

    @Test
    public void preProcessingTest() {
        //TODO - more than just the basic doiName testing
        MpPublication inPublication = new MpPublication();
        PublicationType pubType = new PublicationType();
        pubType.setId(18);
        inPublication.setPublicationType(pubType);
        PublicationSubtype pubSubtype = new PublicationSubtype();
        pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
        inPublication.setPublicationSubtype(pubSubtype);
        PublicationSeries pubSeries = new PublicationSeries();
        pubSeries.setId(334);
        inPublication.setPublicationSeries(pubSeries);
        inPublication.setSeriesNumber("nu-m,be r");
        MpPublication outPublication = busService.publicationPreProcessing(inPublication);
        assertNotNull(outPublication);
        assertNotNull(outPublication.getId());
        assertEquals("sirnumber", outPublication.getIndexId());
        assertEquals(MpPublicationBusService.CROSS_REF + "/" + outPublication.getIndexId(), outPublication.getDoiName());

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
        assertEquals(MpPublicationBusService.CROSS_REF + "/" + outPublication.getIndexId(), outPublication.getDoiName());

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
        assertEquals(MpPublicationBusService.CROSS_REF + "/abc", MpPublicationBusService.getDoiName("abc"));
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
        MpPublication pub = new MpPublication();
        pub.setId(MpPublication.getDao().getNewProdId());
        MpPublication.getDao().add(pub);
        busService.publicationPostProcessing(pub);

//        pub.setIpdsId("IPDS-" + pub.getId());
//        pub.setPublicationTypeId(PublicationType.USGS_NUMBERED_SERIES);
        busService.publicationPostProcessing(pub);

//        pub.setPublicationTypeId(PublicationType.USGS_UNNUMBERED_SERIES);
        busService.publicationPostProcessing(pub);
    }

    //public ValidationResults publish(final Integer prodId)
    //private void defaultThumbnail(final MpPublication mpPub)
    //private boolean checkForEmptyMpPublication(final MpPublication mpPub)

}
