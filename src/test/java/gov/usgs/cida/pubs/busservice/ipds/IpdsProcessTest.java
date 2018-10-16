package gov.usgs.cida.pubs.busservice.ipds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.xml.sax.SAXException;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.dao.intfc.IPublicationDao;
import gov.usgs.cida.pubs.dao.intfc.IPwPublicationDao;
import gov.usgs.cida.pubs.dao.ipds.IpdsMessageLogDao;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;
import gov.usgs.cida.pubs.domain.ipds.PublicationMap;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.validation.ValidatorResult;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={PwPublication.class, IpdsMessageLog.class})
public class IpdsProcessTest extends BaseTest {

	@MockBean
	protected ICrossRefBusService crossRefBusService;
	@MockBean
	protected IpdsBinding binder;
	@MockBean
	protected IpdsWsRequester requester;
	@MockBean
	protected IMpPublicationBusService pubBusService;
	@MockBean(name="pwPublicationDao")
	protected IPwPublicationDao pwPublicationDao;
	@MockBean(name="publicationDao")
	protected IPublicationDao publicationDao;
	@MockBean
	protected PlatformTransactionManager transactionManager;
	@MockBean
	protected IpdsMessageLogDao ipdsMessageLogDao;

	protected IpdsProcess ipdsProcess;
	protected List<MpPublication> emptyList = new ArrayList<>();
	protected PublicationSeries pubSeries;
	protected PublicationSubtype pubSubtype;
	protected PublicationType pubType;
	protected MpPublication existingMpPub9;
	protected MpPublication existingMpPub11;
	protected PwPublication existingPwPub9;
	protected PwPublication existingPwPub11;
	protected IpdsMessageLog ipdsMessageLog;

	public static final String TEST_IPDS_CONTEXT = "content9"; 

	@Before
	public void setUp() throws Exception {
		ipdsProcess = new IpdsProcess(crossRefBusService, binder, requester, pubBusService, transactionManager);
		resetThreadLocals();
		pubType = buildPublicationType();
		pubSubtype = buildPublicationSubtype();
		pubSeries = buildPublicationSeries();
		existingMpPub9 = buildMpPub9();
		existingMpPub11 = buildMpPub(11);

		existingPwPub9 = buildPwPub(9);
		existingPwPub11 = buildPwPub(11);

		reset(pwPublicationDao, publicationDao, ipdsMessageLogDao, requester, binder, pubBusService);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getFromMpTest() {
		when(pubBusService.getObjects(anyMap())).thenReturn(null, null, null, Arrays.asList(existingMpPub9,existingMpPub11), null, Arrays.asList(existingMpPub11,existingMpPub9), emptyList, emptyList);
		MpPublication newPub = new MpPublication();
		newPub.setIpdsId("IPDS_123");

		//This time we don't find it by IPDS ID and we do not attempt by index ID
		assertNull(ipdsProcess.getFromMp(newPub));
		verify(pubBusService).getObjects(anyMap());

		//This time we don't find it by either ID
		PublicationSubtype psub = new PublicationSubtype();
		psub.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		newPub.setPublicationSubtype(psub);
		newPub.setSeriesTitle(pubSeries);
		newPub.setSeriesNumber("456");
		assertNull(ipdsProcess.getFromMp(newPub));
		verify(pubBusService, times(3)).getObjects(anyMap());

		//This time is by IPDS ID
		assertEquals(9, ipdsProcess.getFromMp(newPub).getId().intValue());
		verify(pubBusService, times(4)).getObjects(anyMap());

		//This time is by Index ID
		assertEquals(11, ipdsProcess.getFromMp(newPub).getId().intValue());
		verify(pubBusService, times(6)).getObjects(anyMap());

		//Again not found with either - empty lists
		assertNull(ipdsProcess.getFromMp(newPub));
		verify(pubBusService, times(8)).getObjects(anyMap());
	}

	@Test
	public void getFromPwTest() {
		when(pwPublicationDao.getByIpdsId(anyString())).thenReturn(null, null, existingPwPub9, null);
		when(pwPublicationDao.getByIndexId(anyString())).thenReturn(null, existingPwPub11);
		when(pubBusService.getUsgsNumberedSeriesIndexId(any(MpPublication.class))).thenReturn("sir1234a");
		MpPublication newPub = new MpPublication();
		newPub.setIpdsId("IPDS_123");

		//Don't NPE
		assertNull(ipdsProcess.getFromPw(null));

		//This time we don't find it by IPDS ID and we do not attempt by index ID
		assertNull(ipdsProcess.getFromPw(newPub));
		verify(pwPublicationDao).getByIpdsId(anyString());
		verify(pwPublicationDao, never()).getByIndexId(anyString());

		//This time we don't find it by either ID
		PublicationSubtype psub = new PublicationSubtype();
		psub.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		newPub.setPublicationSubtype(psub);
		newPub.setSeriesTitle(pubSeries);
		newPub.setSeriesNumber("456");
		assertNull(ipdsProcess.getFromPw(newPub));
		verify(pwPublicationDao, times(2)).getByIpdsId(anyString());
		verify(pwPublicationDao).getByIndexId(anyString());

		//This time is by IPDS ID
		assertEquals(9, ipdsProcess.getFromPw(newPub).getId().intValue());
		verify(pwPublicationDao, times(3)).getByIpdsId(anyString());
		verify(pwPublicationDao).getByIndexId(anyString());

		//This time is by Index ID
		assertEquals(11, ipdsProcess.getFromPw(newPub).getId().intValue());
		verify(pwPublicationDao, times(4)).getByIpdsId(anyString());
		verify(pwPublicationDao, times(2)).getByIndexId(anyString());
	}

	@Test
	public void okToProcessTest() {
		when(pwPublicationDao.getByIpdsId(null)).thenReturn(null);
		when(pwPublicationDao.getByIndexId(null)).thenReturn(null);
		when(pwPublicationDao.getByIpdsId("IPDS-1")).thenReturn(new PwPublication());

		//NPE tests
		assertFalse(ipdsProcess.okToProcess(null, null, null));
		assertFalse(ipdsProcess.okToProcess(ProcessType.DISSEMINATION, null, null));
		assertFalse(ipdsProcess.okToProcess(null, new MpPublication(), null));
		assertFalse(ipdsProcess.okToProcess(null, null, new MpPublication()));
		assertFalse(ipdsProcess.okToProcess(null, new MpPublication(), new MpPublication()));
		assertFalse(ipdsProcess.okToProcess(ProcessType.DISSEMINATION, new MpPublication(), new MpPublication()));

		MpPublication newMpPub = new MpPublication();
		PublicationType pubType = new PublicationType();
		newMpPub.setPublicationType(pubType);

		//Good Dissemination (brand new)
		assertTrue(ipdsProcess.okToProcess(ProcessType.DISSEMINATION, newMpPub, new MpPublication()));
		//Bad Dissemination (in warehouse)
		newMpPub.setIpdsId("IPDS-1");
		assertFalse(ipdsProcess.okToProcess(ProcessType.DISSEMINATION, newMpPub, new MpPublication()));

		//Good SPN Production
		newMpPub = new MpPublication();
		PublicationSubtype pubSubtype = new PublicationSubtype();
		pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		newMpPub.setPublicationType(pubType);
		newMpPub.setPublicationSubtype(pubSubtype);
		newMpPub.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
		assertTrue(ipdsProcess.okToProcess(ProcessType.SPN_PRODUCTION, newMpPub, new MpPublication()));
		//Bad SPN Production
		newMpPub.setIpdsReviewProcessState("garbage");
		assertFalse(ipdsProcess.okToProcess(ProcessType.SPN_PRODUCTION, newMpPub, new MpPublication()));
	}

	@Test
	public void okToProcessDisseminationTest() {
		when(pwPublicationDao.getByIpdsId(null)).thenReturn(null);
		when(pwPublicationDao.getByIndexId(null)).thenReturn(null);
		when(pwPublicationDao.getByIpdsId("IPDS-1")).thenReturn(new PwPublication());

		//Do not process if new data is null
		assertFalse(ipdsProcess.okToProcessDissemination(null, null));
		assertFalse(ipdsProcess.okToProcessDissemination(null, new MpPublication()));

		//Do not process if already in Pubs Warehouse.
		MpPublication newMpPub = new MpPublication();
		newMpPub.setIpdsId("IPDS-1");
		assertFalse(ipdsProcess.okToProcessDissemination(newMpPub, new MpPublication()));

		//Do not process USGS numbered series without an actual series.
		newMpPub = new MpPublication();
		PublicationType pubType = new PublicationType();
		newMpPub.setPublicationType(pubType);
		PublicationSubtype pubSubtype = new PublicationSubtype();
		pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		newMpPub.setPublicationSubtype(pubSubtype);
		assertFalse(ipdsProcess.okToProcessDissemination(newMpPub, null));

		//OK to process USGS Numbered Series when new
		newMpPub.setSeriesTitle(new PublicationSeries());
		assertTrue(ipdsProcess.okToProcessDissemination(newMpPub, null));

		//OK to process USGS Numbered Series in MyPubs if has no review state
		MpPublication existingPub = new MpPublication();
		assertTrue(ipdsProcess.okToProcessDissemination(newMpPub, existingPub));

		//OK to process USGS Numbered Series in MyPubs if in the SPN Production state
		existingPub.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
		assertTrue(ipdsProcess.okToProcessDissemination(newMpPub, existingPub));

		//Do not process USGS Numbered Series if already in MyPubs (with a Dissemination state).
		existingPub.setIpdsReviewProcessState(ProcessType.DISSEMINATION.getIpdsValue());
		assertFalse(ipdsProcess.okToProcessDissemination(newMpPub, existingPub));

		//OK to process other than USGS Numbered Series when new
		newMpPub = new MpPublication();
		assertTrue(ipdsProcess.okToProcessDissemination(newMpPub, null));

		//OK to process other than USGS Numbered Series in MyPubs if has no review state
		existingPub = new MpPublication();
		assertTrue(ipdsProcess.okToProcessDissemination(newMpPub, existingPub));

		//OK to process other than USGS Numbered Series in MyPubs if in the SPN Production state
		existingPub.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
		assertTrue(ipdsProcess.okToProcessDissemination(newMpPub, existingPub));

		//Do not process other than USGS Numbered Series if already in MyPubs (with a Dissemination state).
		existingPub.setIpdsReviewProcessState(ProcessType.DISSEMINATION.getIpdsValue());
		assertFalse(ipdsProcess.okToProcessDissemination(newMpPub, existingPub));
	}

	@Test
	public void okToProcessSpnProductionTest() {
		//Do not process if new data is null
		assertFalse(ipdsProcess.okToProcessSpnProduction(null));

		//Skip if we have already assigned a DOI (shouldn't happen as we are querying for null DOI publications)
		MpPublication newMpPub = new MpPublication();
		newMpPub.setDoi("something");
		assertFalse(ipdsProcess.okToProcessSpnProduction(newMpPub));

		//Skip if not in SPN Production (shouldn't happen as we are querying SPN Production only)
		newMpPub = new MpPublication();
		assertFalse(ipdsProcess.okToProcessSpnProduction(newMpPub));
		newMpPub.setIpdsReviewProcessState("garbage");
		assertFalse(ipdsProcess.okToProcessSpnProduction(newMpPub));

		//Process USGS numbered series in SPN Production
		PublicationType pubType = new PublicationType();
		PublicationSubtype pubSubtype = new PublicationSubtype();
		pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		newMpPub.setPublicationType(pubType);
		newMpPub.setPublicationSubtype(pubSubtype);
		newMpPub.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
		assertTrue(ipdsProcess.okToProcessSpnProduction(newMpPub));

		//Otherwise Skip
		newMpPub.setIpdsReviewProcessState("garbage");
		assertFalse(ipdsProcess.okToProcessSpnProduction(newMpPub));
		newMpPub.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
		newMpPub.setPublicationSubtype(null);
		assertFalse(ipdsProcess.okToProcessSpnProduction(newMpPub));
		newMpPub.setPublicationSubtype(new PublicationSubtype());
		assertFalse(ipdsProcess.okToProcessSpnProduction(newMpPub));
		assertFalse(ipdsProcess.okToProcessSpnProduction(new MpPublication()));
	}

	@Test
	public void updateIpdsWithDoiTest() {
		when(requester.updateIpdsDoi(any(MpPublication.class), anyString())).thenReturn(null, "ERROR", "Cool");
		MpPublication pub = new MpPublication();

		//updateIpdsDoi returned null
		ipdsProcess.updateIpdsWithDoi(pub);
		verify(requester).updateIpdsDoi(any(MpPublication.class), anyString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		assertEquals("\n\tnull", IpdsProcess.getStringBuilder().toString());

		//updateIpdsDoi returned ERROR & added to stringBuilder
		ipdsProcess.updateIpdsWithDoi(pub);
		verify(requester, times(2)).updateIpdsDoi(any(MpPublication.class), anyString());
		assertEquals(2, IpdsProcess.getErrors().intValue());
		assertEquals("\n\tnull\n\tERROR", IpdsProcess.getStringBuilder().toString());

		//updateIpdsDoi returned Cool - errors not changed and added to stringBuilder
		ipdsProcess.updateIpdsWithDoi(pub);
		verify(requester, times(3)).updateIpdsDoi(any(MpPublication.class), anyString());
		assertEquals(2, IpdsProcess.getErrors().intValue());
		assertEquals("\n\tnull\n\tERROR\n\tCool", IpdsProcess.getStringBuilder().toString());
	}

	@Test
	public void getNotesTest() throws SAXException, IOException {
		when(requester.getNotes(null, TEST_IPDS_CONTEXT)).thenReturn("<xml></xml>");
		when(binder.bindNotes(anyString(), anySet())).thenReturn(getPublicationMapEmpty(), getPublicationMapNullNotes(), getPublicationMapZeroLengthNotes(), getPublicationMapActualNotes());
		when(requester.getNotes("IPDS-101", TEST_IPDS_CONTEXT)).thenReturn("<xml>1</xml>");
		when(binder.bindNotes(eq("<xml>1</xml>"), anySet())).thenThrow(new RuntimeException("oops!!"));
		MpPublication pub = new MpPublication();

		//nulls everywhere
		assertEquals("", ipdsProcess.getNotes(pub));
		verify(requester).getNotes(null, TEST_IPDS_CONTEXT);
		verify(binder).bindNotes(anyString(), anySet());

		//null from ipds
		assertEquals("", ipdsProcess.getNotes(pub));
		verify(requester, times(2)).getNotes(null, TEST_IPDS_CONTEXT);
		verify(binder, times(2)).bindNotes(anyString(), anySet());

		//empty from ipds
		assertEquals("", ipdsProcess.getNotes(pub));
		verify(requester, times(3)).getNotes(null, TEST_IPDS_CONTEXT);
		verify(binder, times(3)).bindNotes(anyString(), anySet());

		//real stuff from ipds
		assertEquals("Wow, we have this!", ipdsProcess.getNotes(pub));
		verify(requester, times(4)).getNotes(null, TEST_IPDS_CONTEXT);
		verify(binder, times(4)).bindNotes(anyString(), anySet());

		//real stuff from ipds & data in pub
		pub.setNotes("Plus me!");
		assertEquals("Plus me!\n\tWow, we have this!", ipdsProcess.getNotes(pub));
		verify(requester, times(5)).getNotes(null, TEST_IPDS_CONTEXT);
		verify(binder, times(5)).bindNotes(anyString(), anySet());

		//problems in binder
		pub.setIpdsId("IPDS-101");
		assertEquals("Plus me!", ipdsProcess.getNotes(pub));
		assertEquals(1, IpdsProcess.getErrors().intValue());
		assertEquals("\n\tTrouble getting comment: oops!!", IpdsProcess.getStringBuilder().toString());
		verify(requester).getNotes("IPDS-101", TEST_IPDS_CONTEXT);
		verify(binder).bindNotes(eq("<xml>1</xml>"), anySet());
	}

	protected PublicationMap getPublicationMapEmpty() {
		return new PublicationMap();
	}

	protected PublicationMap getPublicationMapNullNotes() {
		PublicationMap rtn =  new PublicationMap();
		rtn.put("NoteComment", null);
		return rtn;
	}

	protected PublicationMap getPublicationMapZeroLengthNotes() {
		PublicationMap rtn =  new PublicationMap();
		rtn.put("NoteComment", "");
		return rtn;
	}

	protected PublicationMap getPublicationMapActualNotes() {
		PublicationMap rtn =  new PublicationMap();
		rtn.put("NoteComment", "Wow, we have this!");
		return rtn;
	}

	@Test
	public void getCostCentersTest() throws SAXException, IOException {
		when(binder.getOrCreateCostCenter(null)).thenThrow(new RuntimeException("oops!!")).thenReturn(null, getCostCenter());

		//Trouble in binder
		assertNull(ipdsProcess.getCostCenters(null));
		assertEquals("\n\tTrouble getting cost center: oops!!", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		verify(binder).getOrCreateCostCenter(null);

		//Null (no) cost center
		assertNull(ipdsProcess.getCostCenters(null));
		assertEquals("\n\tTrouble getting cost center: oops!!", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		verify(binder, times(2)).getOrCreateCostCenter(null);

		//Found it!
		Collection<PublicationCostCenter<?>> ccs = ipdsProcess.getCostCenters(null);
		assertFalse(ccs.isEmpty());
		assertEquals(12, ((MpPublicationCostCenter) ccs.toArray()[0]).getCostCenter().getId().intValue());
		assertEquals("\n\tTrouble getting cost center: oops!!", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		verify(binder, times(3)).getOrCreateCostCenter(null);
	}

	protected CostCenter getCostCenter() {
		CostCenter rtn = new CostCenter();
		rtn.setId(12);
		return rtn;
	}

	@Test
	public void getContributorsTest() throws SAXException, IOException {
		Collection<PublicationContributor<?>> contribs = new ArrayList<>();
		when(requester.getContributors(null, TEST_IPDS_CONTEXT)).thenReturn(null);
		when(binder.bindContributors(null, TEST_IPDS_CONTEXT)).thenThrow(new RuntimeException("oops!!")).thenReturn(contribs);
		MpPublication mpPub = new MpPublication();

		//Trouble in binder
		assertNull(ipdsProcess.getContributors(mpPub));
		assertEquals("\n\tTrouble getting authors/editors: oops!!", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		verify(requester).getContributors(null, TEST_IPDS_CONTEXT);
		verify(binder).bindContributors(null, TEST_IPDS_CONTEXT);

		//A-OK
		assertEquals(contribs, ipdsProcess.getContributors(mpPub));
		assertEquals("\n\tTrouble getting authors/editors: oops!!", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		verify(requester, times(2)).getContributors(null, TEST_IPDS_CONTEXT);
		verify(binder, times(2)).bindContributors(null, TEST_IPDS_CONTEXT);
	}

	@Test
	public void processPublicationFailuresAndDeleteTest() {
		MpPublication newMpPub = new MpPublication();
		when(pubBusService.deleteObject(null)).thenReturn(null);
		when(pubBusService.createObject(newMpPub)).thenReturn(getInvalidPub(), newMpPub);
		when(requester.updateIpdsDoi(any(MpPublication.class), anyString())).thenReturn("");

		//Failed validation
		ipdsProcess.processPublication(ProcessType.DISSEMINATION, null, newMpPub, null);
		assertEquals("\n\tTrouble getting comment: null\nERROR: Failed validation.\n\tField:inField - Message:inMessage - Level:FATAL - Value:inValue\n\tValidator Results: 1 result(s)\n\t", IpdsProcess.getStringBuilder().toString());
		assertEquals(2, IpdsProcess.getErrors().intValue());
		verify(pubBusService, never()).deleteObject(null);
		verify(pubBusService).createObject(newMpPub);
		verify(requester, never()).updateIpdsDoi(any(MpPublication.class), anyString());
		verify(crossRefBusService, never()).submitCrossRef(any(MpPublication.class));
	}

	@Test
	public void processPublicationSpnTest() {
		MpPublication newMpPub = new MpPublication();
		when(pubBusService.deleteObject(null)).thenReturn(null);
		when(pubBusService.createObject(newMpPub)).thenReturn(newMpPub);
		when(requester.updateIpdsDoi(any(MpPublication.class), anyString())).thenReturn("");

		ipdsProcess.processPublication(ProcessType.SPN_PRODUCTION, null, newMpPub, null);
		assertEquals("\n\tTrouble getting comment: null\n\tAdded to MyPubs as ProdId: null\n\t", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		assertEquals(1, IpdsProcess.getAdditions().intValue());
		verify(pubBusService, never()).deleteObject(null);
		verify(pubBusService).createObject(newMpPub);
		verify(requester).updateIpdsDoi(any(MpPublication.class), anyString());
		verify(crossRefBusService, never()).submitCrossRef(any(MpPublication.class));
	}

	@Test
	public void processPublicationDisseminationTest() {
		MpPublication newMpPub = new MpPublication();
		when(pubBusService.deleteObject(null)).thenReturn(null);
		when(pubBusService.createObject(newMpPub)).thenReturn(newMpPub);
		when(requester.updateIpdsDoi(any(MpPublication.class), anyString())).thenReturn("");

		//Not USGS Numbered or UnNumbered Series
		ipdsProcess.processPublication(ProcessType.DISSEMINATION, null, newMpPub, null);
		assertEquals("\n\tTrouble getting comment: null\n\tAdded to MyPubs as ProdId: null", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		assertEquals(1, IpdsProcess.getAdditions().intValue());
		verify(pubBusService, never()).deleteObject(null);
		verify(pubBusService).createObject(newMpPub);
		verify(requester, never()).updateIpdsDoi(any(MpPublication.class), anyString());
		verify(crossRefBusService, never()).submitCrossRef(any(MpPublication.class));

		//Is USGS Numbered, null doi
		resetThreadLocals();
		PublicationSubtype numbered = new PublicationSubtype();
		numbered.setId(5);
		newMpPub.setPublicationSubtype(numbered);
		ipdsProcess.processPublication(ProcessType.DISSEMINATION, null, newMpPub, null);
		assertEquals("\n\tTrouble getting comment: null\n\tAdded to MyPubs as ProdId: null", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		assertEquals(1, IpdsProcess.getAdditions().intValue());
		verify(pubBusService, never()).deleteObject(null);
		verify(pubBusService, times(2)).createObject(newMpPub);
		verify(requester, never()).updateIpdsDoi(any(MpPublication.class), anyString());
		verify(crossRefBusService, never()).submitCrossRef(any(MpPublication.class));

		//Is USGS Numbered, emptyString doi
		resetThreadLocals();
		newMpPub.setDoi("");
		ipdsProcess.processPublication(ProcessType.DISSEMINATION, null, newMpPub, null);
		assertEquals("\n\tTrouble getting comment: null\n\tAdded to MyPubs as ProdId: null", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		assertEquals(1, IpdsProcess.getAdditions().intValue());
		verify(pubBusService, never()).deleteObject(null);
		verify(pubBusService, times(3)).createObject(newMpPub);
		verify(requester, never()).updateIpdsDoi(any(MpPublication.class), anyString());
		verify(crossRefBusService, never()).submitCrossRef(any(MpPublication.class));

		//Is USGS Numbered, "real" doi
		resetThreadLocals();
		newMpPub.setDoi("http:\\doi.gov");
		ipdsProcess.processPublication(ProcessType.DISSEMINATION, null, newMpPub, null);
		assertEquals("\n\tTrouble getting comment: null\n\tAdded to MyPubs as ProdId: null", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		assertEquals(1, IpdsProcess.getAdditions().intValue());
		verify(pubBusService, never()).deleteObject(null);
		verify(pubBusService, times(4)).createObject(newMpPub);
		verify(requester, never()).updateIpdsDoi(any(MpPublication.class), anyString());
		verify(crossRefBusService).submitCrossRef(any(MpPublication.class));

		//Is USGS UnNumbered, null doi
		resetThreadLocals();
		newMpPub.setDoi(null);
		PublicationSubtype unnumbered = new PublicationSubtype();
		unnumbered.setId(6);
		newMpPub.setPublicationSubtype(unnumbered);
		ipdsProcess.processPublication(ProcessType.DISSEMINATION, null, newMpPub, null);
		assertEquals("\n\tTrouble getting comment: null\n\tAdded to MyPubs as ProdId: null", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		assertEquals(1, IpdsProcess.getAdditions().intValue());
		verify(pubBusService, never()).deleteObject(null);
		verify(pubBusService, times(5)).createObject(newMpPub);
		verify(requester, never()).updateIpdsDoi(any(MpPublication.class), anyString());
		verify(crossRefBusService).submitCrossRef(any(MpPublication.class));

		//Is USGS UnNumbered, emptyString doi
		resetThreadLocals();
		newMpPub.setDoi("");
		ipdsProcess.processPublication(ProcessType.DISSEMINATION, null, newMpPub, null);
		assertEquals("\n\tTrouble getting comment: null\n\tAdded to MyPubs as ProdId: null", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		assertEquals(1, IpdsProcess.getAdditions().intValue());
		verify(pubBusService, never()).deleteObject(null);
		verify(pubBusService, times(6)).createObject(newMpPub);
		verify(requester, never()).updateIpdsDoi(any(MpPublication.class), anyString());
		verify(crossRefBusService).submitCrossRef(any(MpPublication.class));

		//Is USGS UnNumbered, "real" doi
		resetThreadLocals();
		newMpPub.setDoi("http:\\doi.gov");
		ipdsProcess.processPublication(ProcessType.DISSEMINATION, null, newMpPub, null);
		assertEquals("\n\tTrouble getting comment: null\n\tAdded to MyPubs as ProdId: null", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		assertEquals(1, IpdsProcess.getAdditions().intValue());
		verify(pubBusService, never()).deleteObject(null);
		verify(pubBusService, times(7)).createObject(newMpPub);
		verify(requester, never()).updateIpdsDoi(any(MpPublication.class), anyString());
		verify(crossRefBusService, times(2)).submitCrossRef(any(MpPublication.class));	}

	protected MpPublication getInvalidPub() {
		MpPublication rtn = new MpPublication();
		ValidatorResult vr = new ValidatorResult("inField", "inMessage",SeverityLevel.FATAL, "inValue");
		rtn.addValidatorResult(vr);
		return rtn;
	}

	@Test
	@SuppressWarnings("unchecked")
	public void processIpdsPublicationTest() throws SAXException, IOException {
		Map<String, Object> pm = new HashMap<>();
		pm.put(IpdsMessageLog.IPNUMBER, "IP-123");
		when(binder.bindPublication(any(Map.class), anyString())).thenReturn(existingMpPub9);
		when(pubBusService.getObjects(anyMap())).thenThrow(new RuntimeException("test")).thenReturn(null);
		when(pubBusService.createObject(existingMpPub9)).thenReturn(existingMpPub9);
		when(requester.getNotes(null, TEST_IPDS_CONTEXT)).thenReturn("<xml></xml>");
		when(binder.bindNotes(anyString(), anySet())).thenReturn(getPublicationMapEmpty());
		when(transactionManager.getTransaction(any(TransactionDefinition.class))).thenReturn(new DefaultTransactionStatus(null, false, false, false, false, null));

		//Error condition - should rollback
		ipdsProcess.processIpdsPublication(ProcessType.SPN_PRODUCTION, pm);
		assertEquals("IP-123:\n\tERROR: Trouble processing pub: IP-123 - test\n\n", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		assertEquals(0, IpdsProcess.getAdditions().intValue());
		verify(transactionManager).getTransaction(any(TransactionDefinition.class));
		verify(transactionManager).rollback(any(TransactionStatus.class));
		verify(transactionManager, never()).commit(any(TransactionStatus.class));

		//Good, processed, and should commit
		resetThreadLocals();
		ipdsProcess.processIpdsPublication(ProcessType.DISSEMINATION, pm);
		assertEquals("IP-123:\n\tAdded to MyPubs as ProdId: 9\n\n", IpdsProcess.getStringBuilder().toString());
		assertEquals(0, IpdsProcess.getErrors().intValue());
		assertEquals(1, IpdsProcess.getAdditions().intValue());
		verify(transactionManager, times(2)).getTransaction(any(TransactionDefinition.class));
		verify(transactionManager).rollback(any(TransactionStatus.class));
		verify(transactionManager, times(1)).commit(any(TransactionStatus.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void processLogTest() {
		String expectedMsg = "Summary:\n\tTotal Entries: 2\n\tPublications Added: 0\n\tErrors Encountered: 2\n\nnull:\n\tERROR: Trouble processing pub: null - test\n\nnull:\n\tERROR: Trouble processing pub: null - test\n\nLog: 1\n";
		when(binder.bindPublication(any(Map.class), anyString())).thenThrow(new RuntimeException("test"));
		when(ipdsMessageLogDao.getFromIpds(1)).thenReturn(getPubMapList());
		assertEquals(expectedMsg, ipdsProcess.processLog(ProcessType.DISSEMINATION, 1, TEST_IPDS_CONTEXT));

		//Should be the same message as the ThreadLocals are reset at start of method
		assertEquals(expectedMsg, ipdsProcess.processLog(ProcessType.DISSEMINATION, 1, TEST_IPDS_CONTEXT));
	}

	protected List<Map<String, Object>> getPubMapList() {
		List<Map<String, Object>> rtn = new ArrayList<>();
		rtn.add(new HashMap<>());
		rtn.add(new HashMap<>());
		return rtn;
	}

	protected PublicationType buildPublicationType() {
		PublicationType pubType = new PublicationType();
		pubType.setId(1);
		pubType.setText("Test Type");
		return pubType;
	}

	protected PublicationSubtype buildPublicationSubtype() {
		PublicationSubtype pubSubtype = new PublicationSubtype();
		pubSubtype.setId(2);
		pubSubtype.setText("Test Subtype");
		return pubSubtype;
	}

	protected PublicationSeries buildPublicationSeries() {
		PublicationSeries pubSeries = new PublicationSeries();
		pubSeries.setText("Test Series");
		return pubSeries;
	}

	protected MpPublication buildMpPub9() {
		MpPublication mpPub = buildMpPub(9);
		mpPub.setPublicationType(pubType);
		mpPub.setPublicationSubtype(pubSubtype);
		mpPub.setSeriesTitle(pubSeries);
		mpPub.setIpdsReviewProcessState("Test State");
		mpPub.setDoi("Test Doi");
		return mpPub;
	}

	protected MpPublication buildMpPub(Integer id) {
		MpPublication mpPub = new MpPublication();
		mpPub.setId(id);
		mpPub.setIpdsContext(TEST_IPDS_CONTEXT);
		return mpPub;
	}

	protected PwPublication buildPwPub(Integer id) {
		PwPublication pwPub = new PwPublication();
		pwPub.setId(id);
		pwPub.setIpdsContext(TEST_IPDS_CONTEXT);
		return pwPub;
	}

	protected void resetThreadLocals() {
		IpdsProcess.setErrors(0);
		IpdsProcess.setStringBuilder(new StringBuilder(""));
		IpdsProcess.setAdditions(0);
		IpdsProcess.setContext(TEST_IPDS_CONTEXT);
	}

}
