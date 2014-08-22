package gov.usgs.cida.pubs.busservice.ipds;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.mp.MpPublication;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

public class IpdsProcessTest extends BaseSpringTest {

    @Autowired
    public ICrossRefBusService crossRefBusService;
    @Autowired
    public IpdsBinding binder;
    @Mock
    public IpdsWsRequester requester;
    @Autowired
    public IMpPublicationBusService pubBusService;

    public IpdsProcess ipdsProcess;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ipdsProcess = new IpdsProcess(crossRefBusService, binder, requester, pubBusService);
    }

    @Test 
    public void okToProcessTest() {
        assertFalse(ipdsProcess.okToProcess(null, null, null));
        assertFalse(ipdsProcess.okToProcess(ProcessType.DISSEMINATION, null, null));
        assertFalse(ipdsProcess.okToProcess(null, new MpPublication(), null));
        assertFalse(ipdsProcess.okToProcess(null, null, new MpPublication()));
        assertFalse(ipdsProcess.okToProcess(null, new MpPublication(), new MpPublication()));
        assertFalse(ipdsProcess.okToProcess(ProcessType.DISSEMINATION, new MpPublication(), new MpPublication()));

        //DISEMMINATION tests
        //Do not process USGS numbered series without an actual series.
        MpPublication pub = new MpPublication();
        PublicationType pubType = new PublicationType();
        pub.setPublicationType(pubType);
        assertTrue(ipdsProcess.okToProcess(ProcessType.DISSEMINATION, pub, new MpPublication()));
        PublicationSubtype pubSubtype = new PublicationSubtype();
        pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
        pub.setPublicationSubtype(pubSubtype);
        assertFalse(ipdsProcess.okToProcess(ProcessType.DISSEMINATION, pub, new MpPublication()));
        pub.setSeriesTitle(new PublicationSeries());
        assertTrue(ipdsProcess.okToProcess(ProcessType.DISSEMINATION, pub, new MpPublication()));
        pub.setPublicationSubtype(new PublicationSubtype());
        assertTrue(ipdsProcess.okToProcess(ProcessType.DISSEMINATION, pub, new MpPublication()));

        //It is ok to process a publication already in our system if has no review state or
        //was in the SPN Production state. (Or if it is not already in our system).
        MpPublication existingPub = new MpPublication();
        assertTrue(ipdsProcess.okToProcess(ProcessType.DISSEMINATION, pub, existingPub));
        existingPub.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
        assertTrue(ipdsProcess.okToProcess(ProcessType.DISSEMINATION, pub, existingPub));

        //Do not process if already in our system (with a Dissemination state).
        existingPub.setIpdsReviewProcessState(ProcessType.DISSEMINATION.getIpdsValue());
        assertFalse(ipdsProcess.okToProcess(ProcessType.DISSEMINATION, pub, existingPub));

        //SPN_PRODUCTION tests
        //Skip if we have already assigned a DOI (shouldn't happen as we are querying for null DOI publications)
        pub = new MpPublication();
        pub.setPublicationType(pubType);
        pub.setPublicationSubtype(pubSubtype);
        pub.setDoi("something");
        assertFalse(ipdsProcess.okToProcess(ProcessType.SPN_PRODUCTION, pub, new MpPublication()));

        //Skip if not in SPN Production (shouldn't happen as we are querying SPN Production only)
        pub.setDoi(null);
        assertFalse(ipdsProcess.okToProcess(ProcessType.SPN_PRODUCTION, pub, new MpPublication()));
        pub.setIpdsReviewProcessState("garbage");
        assertFalse(ipdsProcess.okToProcess(ProcessType.SPN_PRODUCTION, pub, new MpPublication()));

        //Process USGS numbered series
        pub.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
        assertTrue(ipdsProcess.okToProcess(ProcessType.SPN_PRODUCTION, pub, new MpPublication()));
        pub.setPublicationSubtype(null);
        assertFalse(ipdsProcess.okToProcess(ProcessType.SPN_PRODUCTION, pub, new MpPublication()));
        pub.setPublicationSubtype(new PublicationSubtype());
        assertFalse(ipdsProcess.okToProcess(ProcessType.SPN_PRODUCTION, pub, new MpPublication()));
    }
}
