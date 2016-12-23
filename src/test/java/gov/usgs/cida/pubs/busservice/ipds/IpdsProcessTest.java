package gov.usgs.cida.pubs.busservice.ipds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.dao.intfc.IPwPublicationDao;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublication;

//The Dao mocking works because the getDao() methods are all static and JAVA/Spring don't redo them 
//for each reference. This does mean that we need to let Spring know that the context is now dirty...
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class IpdsProcessTest extends BaseSpringTest {

	@Mock
	protected ICrossRefBusService crossRefBusService;
	@Mock
	protected IpdsBinding binder;
	@Mock
	protected IpdsWsRequester requester;
	@Mock
	protected IMpPublicationBusService pubBusService;
	@Mock
	protected IPwPublicationDao publicationDao;
	@Mock
	protected IDao<PublicationSeries> pubSeriestDao;

	protected IpdsProcess ipdsProcess;
	protected List<MpPublication> emptyList = new ArrayList<>();
	protected PublicationSeries pubSeries;
	protected MpPublication existingMpPub9;
	protected MpPublication existingMpPub11;
	protected PwPublication existingPwPub9;
	protected PwPublication existingPwPub11;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		ipdsProcess = new IpdsProcess(crossRefBusService, binder, requester, pubBusService);
		pubSeries = new PublicationSeries();
		pubSeries.setPublicationSeriesDao(pubSeriestDao);
		existingMpPub9 = new MpPublication();
		existingMpPub9.setId(9);
		existingMpPub11 = new MpPublication();
		existingMpPub11.setId(11);

		existingPwPub9 = new PwPublication();
		existingPwPub9.setId(9);
		existingPwPub11 = new PwPublication();
		existingPwPub11.setId(11);

		existingPwPub9.setPwPublicationDao(publicationDao);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getFromMpTest() {
		when(pubBusService.getObjects(anyMapOf(String.class, Object.class))).thenReturn(null, null, null, Arrays.asList(existingMpPub9,existingMpPub11), null, Arrays.asList(existingMpPub11,existingMpPub9), emptyList, emptyList);
		MpPublication newPub = new MpPublication();
		newPub.setIpdsId("IPDS_123");

		//This time we don't find it by IPDS ID and we do not attempt by index ID
		assertNull(ipdsProcess.getFromMp(newPub));
		verify(pubBusService).getObjects(anyMapOf(String.class, Object.class));

		//This time we don't find it by either ID
		PublicationSubtype psub = new PublicationSubtype();
		psub.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		newPub.setPublicationSubtype(psub);
		newPub.setSeriesTitle(pubSeries);
		newPub.setSeriesNumber("456");
		assertNull(ipdsProcess.getFromMp(newPub));
		verify(pubBusService, times(3)).getObjects(anyMapOf(String.class, Object.class));

		//This time is by IPDS ID
		assertEquals(9, ipdsProcess.getFromMp(newPub).getId().intValue());
		verify(pubBusService, times(4)).getObjects(anyMapOf(String.class, Object.class));

		//This time is by Index ID
		assertEquals(11, ipdsProcess.getFromMp(newPub).getId().intValue());
		verify(pubBusService, times(6)).getObjects(anyMapOf(String.class, Object.class));

		//Again not found with either - empty lists
		assertNull(ipdsProcess.getFromMp(newPub));
		verify(pubBusService, times(8)).getObjects(anyMapOf(String.class, Object.class));
	}

	@Test
	public void getFromPwTest() {
		when(publicationDao.getByIpdsId(anyString())).thenReturn(null, null, existingPwPub9, null);
		when(publicationDao.getByIndexId(anyString())).thenReturn(null, existingPwPub11);
		MpPublication newPub = new MpPublication();
		newPub.setIpdsId("IPDS_123");

		//This time we don't find it by IPDS ID and we do not attempt by index ID
		assertNull(ipdsProcess.getFromPw(newPub));
		verify(publicationDao).getByIpdsId(anyString());
		verify(publicationDao, never()).getByIndexId(anyString());

		//This time we don't find it by either ID
		PublicationSubtype psub = new PublicationSubtype();
		psub.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		newPub.setPublicationSubtype(psub);
		newPub.setSeriesTitle(pubSeries);
		newPub.setSeriesNumber("456");
		assertNull(ipdsProcess.getFromPw(newPub));
		verify(publicationDao, times(2)).getByIpdsId(anyString());
		verify(publicationDao).getByIndexId(anyString());

		//This time is by IPDS ID
		assertEquals(9, ipdsProcess.getFromPw(newPub).getId().intValue());
		verify(publicationDao, times(3)).getByIpdsId(anyString());
		verify(publicationDao).getByIndexId(anyString());

		//This time is by Index ID
		assertEquals(11, ipdsProcess.getFromPw(newPub).getId().intValue());
		verify(publicationDao, times(4)).getByIpdsId(anyString());
		verify(publicationDao, times(2)).getByIndexId(anyString());
	}

	@Test 
	public void okToProcessTest() {
		assertFalse(ipdsProcess.okToProcess(null, null, null, null));
		assertFalse(ipdsProcess.okToProcess(ProcessType.DISSEMINATION, null, null, null));
		assertFalse(ipdsProcess.okToProcess(null, new MpPublication(), null, null));
		assertFalse(ipdsProcess.okToProcess(null, null, new MpPublication(), null));
		assertFalse(ipdsProcess.okToProcess(null, new MpPublication(), new MpPublication(), null));
		assertFalse(ipdsProcess.okToProcess(ProcessType.DISSEMINATION, new MpPublication(), new MpPublication(), null));

		//DISEMMINATION tests
		//Do not process USGS numbered series without an actual series.
		MpPublication pub = new MpPublication();
		PublicationType pubType = new PublicationType();
		pub.setPublicationType(pubType);
		assertTrue(ipdsProcess.okToProcess(ProcessType.DISSEMINATION, pub, new MpPublication(), null));
		PublicationSubtype pubSubtype = new PublicationSubtype();
		pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		pub.setPublicationSubtype(pubSubtype);
		assertFalse(ipdsProcess.okToProcess(ProcessType.DISSEMINATION, pub, new MpPublication(), null));
		pub.setSeriesTitle(new PublicationSeries());
		assertTrue(ipdsProcess.okToProcess(ProcessType.DISSEMINATION, pub, new MpPublication(), null));
		pub.setPublicationSubtype(new PublicationSubtype());
		assertTrue(ipdsProcess.okToProcess(ProcessType.DISSEMINATION, pub, new MpPublication(), null));
		assertFalse(ipdsProcess.okToProcess(ProcessType.DISSEMINATION, pub, new MpPublication(), new PwPublication()));

		//It is ok to process a publication already in our system if has no review state or
		//was in the SPN Production state. (Or if it is not already in our system).
		MpPublication existingPub = new MpPublication();
		assertTrue(ipdsProcess.okToProcess(ProcessType.DISSEMINATION, pub, existingPub, null));
		existingPub.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
		assertTrue(ipdsProcess.okToProcess(ProcessType.DISSEMINATION, pub, existingPub, null));
		assertFalse(ipdsProcess.okToProcess(ProcessType.DISSEMINATION, pub, existingPub, new PwPublication()));

		//Do not process if already in our system (with a Dissemination state).
		existingPub.setIpdsReviewProcessState(ProcessType.DISSEMINATION.getIpdsValue());
		assertFalse(ipdsProcess.okToProcess(ProcessType.DISSEMINATION, pub, existingPub, null));

		//SPN_PRODUCTION tests
		//Skip if we have already assigned a DOI (shouldn't happen as we are querying for null DOI publications)
		pub = new MpPublication();
		pub.setPublicationType(pubType);
		pub.setPublicationSubtype(pubSubtype);
		pub.setDoi("something");
		assertFalse(ipdsProcess.okToProcess(ProcessType.SPN_PRODUCTION, pub, new MpPublication(), null));

		//Skip if not in SPN Production (shouldn't happen as we are querying SPN Production only)
		pub.setDoi(null);
		assertFalse(ipdsProcess.okToProcess(ProcessType.SPN_PRODUCTION, pub, new MpPublication(), null));
		pub.setIpdsReviewProcessState("garbage");
		assertFalse(ipdsProcess.okToProcess(ProcessType.SPN_PRODUCTION, pub, new MpPublication(), null));

		//Process USGS numbered series
		pub.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
		assertTrue(ipdsProcess.okToProcess(ProcessType.SPN_PRODUCTION, pub, new MpPublication(), null));
		//unless in the warehouse
		assertTrue(ipdsProcess.okToProcess(ProcessType.SPN_PRODUCTION, pub, new MpPublication(), new PwPublication()));
		pub.setPublicationSubtype(null);
		assertFalse(ipdsProcess.okToProcess(ProcessType.SPN_PRODUCTION, pub, new MpPublication(), null));
		pub.setPublicationSubtype(new PublicationSubtype());
		assertFalse(ipdsProcess.okToProcess(ProcessType.SPN_PRODUCTION, pub, new MpPublication(), null));
	}

	@Test
	public void okToProcessDisseminationTest() {
		assertTrue(ipdsProcess.okToProcessDissemination(new MpPublication(), null, null));
		assertFalse(ipdsProcess.okToProcessDissemination(null, new MpPublication(), null));
		assertTrue(ipdsProcess.okToProcessDissemination(new MpPublication(), new MpPublication(), null));
		assertFalse(ipdsProcess.okToProcessDissemination(new MpPublication(), new MpPublication(), new PwPublication()));

		//Do not process USGS numbered series without an actual series.
		MpPublication pub = new MpPublication();
		PublicationType pubType = new PublicationType();
		pub.setPublicationType(pubType);
		assertTrue(ipdsProcess.okToProcessDissemination(pub, new MpPublication(), null));
		PublicationSubtype pubSubtype = new PublicationSubtype();
		pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		pub.setPublicationSubtype(pubSubtype);
		assertFalse(ipdsProcess.okToProcessDissemination(pub, new MpPublication(), null));
		pub.setSeriesTitle(new PublicationSeries());
		assertTrue(ipdsProcess.okToProcessDissemination(pub, new MpPublication(), null));
		pub.setPublicationSubtype(new PublicationSubtype());
		assertTrue(ipdsProcess.okToProcessDissemination(pub, new MpPublication(), null));

		//It is ok to process a publication already in MyPubs if has no review state or
		//was in the SPN Production state. (Or if it is not already in MyPubs).
		MpPublication existingPub = new MpPublication();
		assertTrue(ipdsProcess.okToProcessDissemination(pub, existingPub, null));
		existingPub.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
		assertTrue(ipdsProcess.okToProcessDissemination(pub, existingPub, null));

		//Do not process if already in MyPubs (with a Dissemination state).
		existingPub.setIpdsReviewProcessState(ProcessType.DISSEMINATION.getIpdsValue());
		assertFalse(ipdsProcess.okToProcessDissemination(pub, existingPub, null));

		//Do not process if already in Pubs Warehouse.
		existingPub.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
		assertFalse(ipdsProcess.okToProcessDissemination(pub, existingPub, new PwPublication()));
	}

	@Test
	public void okToProcessSpnProductionTest() {
		assertFalse(ipdsProcess.okToProcessSpnProduction(null));
		assertFalse(ipdsProcess.okToProcessSpnProduction(new MpPublication()));

		//Skip if we have already assigned a DOI (shouldn't happen as we are querying for null DOI publications)
		PublicationType pubType = new PublicationType();
		PublicationSubtype pubSubtype = new PublicationSubtype();
		pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		MpPublication pub = new MpPublication();
		pub.setPublicationType(pubType);
		pub.setPublicationSubtype(pubSubtype);
		pub.setDoi("something");
		assertFalse(ipdsProcess.okToProcessSpnProduction(pub));

		//Skip if not in SPN Production (shouldn't happen as we are querying SPN Production only)
		pub.setDoi(null);
		assertFalse(ipdsProcess.okToProcessSpnProduction(pub));
		pub.setIpdsReviewProcessState("garbage");
		assertFalse(ipdsProcess.okToProcessSpnProduction(pub));

		//Process USGS numbered series
		pub.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
		assertTrue(ipdsProcess.okToProcessSpnProduction(pub));
		pub.setPublicationSubtype(null);
		assertFalse(ipdsProcess.okToProcessSpnProduction(pub));
		pub.setPublicationSubtype(new PublicationSubtype());
		assertFalse(ipdsProcess.okToProcessSpnProduction(pub));
	}

//	@Test
//	public void processLogTest() {
////		for (int i=2; i<32; i++) {
//			LOG.info(ipdsProcess.processLog(ProcessType.SPN_PRODUCTION, 1));
////		}
////		for (int i=58; i<99; i++) {
////			LOG.info(ipdsProcess.processLog(ProcessType.DISSEMINATION, i));
////		}
//	}

}
