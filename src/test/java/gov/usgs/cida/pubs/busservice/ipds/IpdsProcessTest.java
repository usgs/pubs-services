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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.xml.sax.SAXException;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.PubMap;
import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
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
	protected PlatformTransactionManager transactionManager;
	@Mock
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

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		ipdsProcess = new IpdsProcess(crossRefBusService, binder, requester, pubBusService, transactionManager);
		pubType = new PublicationType();
		pubType.setId(1);
		pubType.setText("Test Type");
		pubSubtype = new PublicationSubtype();
		pubSubtype.setId(2);
		pubSubtype.setText("Test Subtype");
		pubSeries = new PublicationSeries();
		pubSeries.setText("Test Series");
		existingMpPub9 = new MpPublication();
		existingMpPub9.setId(9);
		existingMpPub9.setPublicationType(pubType);
		existingMpPub9.setPublicationSubtype(pubSubtype);
		existingMpPub9.setSeriesTitle(pubSeries);
		existingMpPub9.setIpdsReviewProcessState("Test State");
		existingMpPub9.setDoi("Test Doi");
		existingMpPub11 = new MpPublication();
		existingMpPub11.setId(11);

		existingPwPub9 = new PwPublication();
		existingPwPub9.setId(9);
		existingPwPub11 = new PwPublication();
		existingPwPub11.setId(11);

		existingPwPub9.setPwPublicationDao(publicationDao);
		IpdsMessageLog ipdsMessageLog = new IpdsMessageLog();
		ipdsMessageLog.setIpdsMessageLogDao(ipdsMessageLogDao);
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
		when(publicationDao.getByIpdsId(anyString())).thenReturn(null, null, existingPwPub9, null);
		when(publicationDao.getByIndexId(anyString())).thenReturn(null, existingPwPub11);
		when(pubBusService.getUsgsNumberedSeriesIndexId(any(MpPublication.class))).thenReturn("sir1234a");
		MpPublication newPub = new MpPublication();
		newPub.setIpdsId("IPDS_123");

		//Don't NPE
		assertNull(ipdsProcess.getFromPw(null));

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
		when(publicationDao.getByIpdsId(null)).thenReturn(null);
		when(publicationDao.getByIndexId(null)).thenReturn(null);
		when(publicationDao.getByIpdsId("IPDS-1")).thenReturn(new PwPublication());

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

		//Wrong process type
		assertFalse(ipdsProcess.okToProcess(ProcessType.COST_CENTER, newMpPub, new MpPublication()));

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
		when(publicationDao.getByIpdsId(null)).thenReturn(null);
		when(publicationDao.getByIndexId(null)).thenReturn(null);
		when(publicationDao.getByIpdsId("IPDS-1")).thenReturn(new PwPublication());

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
		when(requester.updateIpdsDoi(any(MpPublication.class))).thenReturn(null, "ERROR", "Cool");
		MpPublication pub = new MpPublication();
		IpdsProcess.setErrors(0);
		IpdsProcess.setStringBuilder(new StringBuilder(""));
		IpdsProcess.setAdditions(0);

		//updateIpdsDoi returned null
		ipdsProcess.updateIpdsWithDoi(pub);
		verify(requester).updateIpdsDoi(any(MpPublication.class));
		assertEquals(1, IpdsProcess.getErrors().intValue());
		assertEquals("\n\tnull", IpdsProcess.getStringBuilder().toString());

		//updateIpdsDoi returned ERROR & added to stringBuilder
		ipdsProcess.updateIpdsWithDoi(pub);
		verify(requester, times(2)).updateIpdsDoi(any(MpPublication.class));
		assertEquals(2, IpdsProcess.getErrors().intValue());
		assertEquals("\n\tnull\n\tERROR", IpdsProcess.getStringBuilder().toString());

		//updateIpdsDoi returned Cool - errors not changed and added to stringBuilder
		ipdsProcess.updateIpdsWithDoi(pub);
		verify(requester, times(3)).updateIpdsDoi(any(MpPublication.class));
		assertEquals(2, IpdsProcess.getErrors().intValue());
		assertEquals("\n\tnull\n\tERROR\n\tCool", IpdsProcess.getStringBuilder().toString());
	}

	@Test
	public void getNotesTest() throws SAXException, IOException {
		when(requester.getNotes(null)).thenReturn("<xml></xml>");
		when(binder.bindNotes(anyString(), anySet())).thenReturn(getPublicationMapEmpty(), getPublicationMapNullNotes(), getPublicationMapZeroLengthNotes(), getPublicationMapActualNotes());
		when(requester.getNotes("IPDS-101")).thenReturn("<xml>1</xml>");
		when(binder.bindNotes(eq("<xml>1</xml>"), anySet())).thenThrow(new RuntimeException("oops!!"));
		MpPublication pub = new MpPublication();
		IpdsProcess.setErrors(0);
		IpdsProcess.setStringBuilder(new StringBuilder(""));
		IpdsProcess.setAdditions(0);

		//nulls everywhere
		assertEquals("", ipdsProcess.getNotes(pub));
		verify(requester).getNotes(null);
		verify(binder).bindNotes(anyString(), anySet());

		//null from ipds
		assertEquals("", ipdsProcess.getNotes(pub));
		verify(requester, times(2)).getNotes(null);
		verify(binder, times(2)).bindNotes(anyString(), anySet());

		//empty from ipds
		assertEquals("", ipdsProcess.getNotes(pub));
		verify(requester, times(3)).getNotes(null);
		verify(binder, times(3)).bindNotes(anyString(), anySet());

		//real stuff from ipds
		assertEquals("Wow, we have this!", ipdsProcess.getNotes(pub));
		verify(requester, times(4)).getNotes(null);
		verify(binder, times(4)).bindNotes(anyString(), anySet());

		//real stuff from ipds & data in pub
		pub.setNotes("Plus me!");
		assertEquals("Plus me!\n\tWow, we have this!", ipdsProcess.getNotes(pub));
		verify(requester, times(5)).getNotes(null);
		verify(binder, times(5)).bindNotes(anyString(), anySet());

		//problems in binder
		pub.setIpdsId("IPDS-101");
		assertEquals("Plus me!", ipdsProcess.getNotes(pub));
		assertEquals(1, IpdsProcess.getErrors().intValue());
		assertEquals("\n\tTrouble getting comment: oops!!", IpdsProcess.getStringBuilder().toString());
		verify(requester).getNotes("IPDS-101");
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
		IpdsProcess.setErrors(0);
		IpdsProcess.setStringBuilder(new StringBuilder(""));
		IpdsProcess.setAdditions(0);

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
		when(requester.getContributors(null)).thenReturn(null);
		when(binder.bindContributors(null)).thenThrow(new RuntimeException("oops!!")).thenReturn(contribs);
		IpdsProcess.setErrors(0);
		IpdsProcess.setStringBuilder(new StringBuilder(""));
		IpdsProcess.setAdditions(0);
		MpPublication mpPub = new MpPublication();

		//Trouble in binder
		assertNull(ipdsProcess.getContributors(mpPub));
		assertEquals("\n\tTrouble getting authors/editors: oops!!", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		verify(requester).getContributors(null);
		verify(binder).bindContributors(null);

		//A-OK
		assertEquals(contribs, ipdsProcess.getContributors(mpPub));
		assertEquals("\n\tTrouble getting authors/editors: oops!!", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		verify(requester, times(2)).getContributors(null);
		verify(binder, times(2)).bindContributors(null);
	}

	@Test
	public void processPublicationFailuresAndDeleteTest() {
		MpPublication newMpPub = new MpPublication();
		when(pubBusService.deleteObject(null)).thenReturn(null);
		when(pubBusService.createObject(newMpPub)).thenReturn(getInvalidPub(), newMpPub);
		when(requester.updateIpdsDoi(any(MpPublication.class))).thenReturn("");
		IpdsProcess.setErrors(0);
		IpdsProcess.setStringBuilder(new StringBuilder(""));
		IpdsProcess.setAdditions(0);

		//Failed validation
		ipdsProcess.processPublication(ProcessType.DISSEMINATION, null, newMpPub, null);
		assertEquals("\n\tTrouble getting comment: null\nERROR: Failed validation.\n\tField:inField - Message:inMessage - Level:FATAL - Value:inValue\n\tValidator Results: 1 result(s)\n\t", IpdsProcess.getStringBuilder().toString());
		assertEquals(2, IpdsProcess.getErrors().intValue());
		verify(pubBusService, never()).deleteObject(null);
		verify(pubBusService).createObject(newMpPub);
		verify(requester, never()).updateIpdsDoi(any(MpPublication.class));
		verify(crossRefBusService, never()).submitCrossRef(any(MpPublication.class));

		//What is this process type?
		IpdsProcess.setErrors(0);
		IpdsProcess.setStringBuilder(new StringBuilder(""));
		IpdsProcess.setAdditions(0);
		ipdsProcess.processPublication(ProcessType.COST_CENTER, null, newMpPub, null);
		assertEquals("\n\tTrouble getting comment: null\n\tAdded to MyPubs as ProdId: null", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		assertEquals(1, IpdsProcess.getAdditions().intValue());
		verify(pubBusService, never()).deleteObject(null);
		verify(pubBusService, times(2)).createObject(newMpPub);
		verify(requester, never()).updateIpdsDoi(any(MpPublication.class));
		verify(crossRefBusService, never()).submitCrossRef(any(MpPublication.class));

		//Delete
		IpdsProcess.setErrors(0);
		IpdsProcess.setStringBuilder(new StringBuilder(""));
		IpdsProcess.setAdditions(0);
		ipdsProcess.processPublication(ProcessType.COST_CENTER, null, newMpPub, newMpPub);
		assertEquals("\n\tTrouble getting comment: null\n\tAdded to MyPubs as ProdId: null", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		assertEquals(1, IpdsProcess.getAdditions().intValue());
		verify(pubBusService).deleteObject(null);
		verify(pubBusService, times(3)).createObject(newMpPub);
		verify(requester, never()).updateIpdsDoi(any(MpPublication.class));
		verify(crossRefBusService, never()).submitCrossRef(any(MpPublication.class));
	}

	@Test
	public void processPublicationSpnTest() {
		MpPublication newMpPub = new MpPublication();
		when(pubBusService.deleteObject(null)).thenReturn(null);
		when(pubBusService.createObject(newMpPub)).thenReturn(newMpPub);
		when(requester.updateIpdsDoi(any(MpPublication.class))).thenReturn("");
		IpdsProcess.setErrors(0);
		IpdsProcess.setStringBuilder(new StringBuilder(""));
		IpdsProcess.setAdditions(0);

		ipdsProcess.processPublication(ProcessType.SPN_PRODUCTION, null, newMpPub, null);
		assertEquals("\n\tTrouble getting comment: null\n\tAdded to MyPubs as ProdId: null\n\t", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		assertEquals(1, IpdsProcess.getAdditions().intValue());
		verify(pubBusService, never()).deleteObject(null);
		verify(pubBusService).createObject(newMpPub);
		verify(requester).updateIpdsDoi(any(MpPublication.class));
		verify(crossRefBusService, never()).submitCrossRef(any(MpPublication.class));
	}

	@Test
	public void processPublicationDisseminationTest() {
		MpPublication newMpPub = new MpPublication();
		when(pubBusService.deleteObject(null)).thenReturn(null);
		when(pubBusService.createObject(newMpPub)).thenReturn(newMpPub);
		when(requester.updateIpdsDoi(any(MpPublication.class))).thenReturn("");
		IpdsProcess.setErrors(0);
		IpdsProcess.setStringBuilder(new StringBuilder(""));
		IpdsProcess.setAdditions(0);

		//Not USGS Numbered or UnNumbered Series
		ipdsProcess.processPublication(ProcessType.DISSEMINATION, null, newMpPub, null);
		assertEquals("\n\tTrouble getting comment: null\n\tAdded to MyPubs as ProdId: null", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		assertEquals(1, IpdsProcess.getAdditions().intValue());
		verify(pubBusService, never()).deleteObject(null);
		verify(pubBusService).createObject(newMpPub);
		verify(requester, never()).updateIpdsDoi(any(MpPublication.class));
		verify(crossRefBusService, never()).submitCrossRef(any(MpPublication.class));

		//Is USGS Numbered, null doi
		IpdsProcess.setErrors(0);
		IpdsProcess.setStringBuilder(new StringBuilder(""));
		IpdsProcess.setAdditions(0);
		PublicationSubtype numbered = new PublicationSubtype();
		numbered.setId(5);
		newMpPub.setPublicationSubtype(numbered);
		ipdsProcess.processPublication(ProcessType.DISSEMINATION, null, newMpPub, null);
		assertEquals("\n\tTrouble getting comment: null\n\tAdded to MyPubs as ProdId: null", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		assertEquals(1, IpdsProcess.getAdditions().intValue());
		verify(pubBusService, never()).deleteObject(null);
		verify(pubBusService, times(2)).createObject(newMpPub);
		verify(requester, never()).updateIpdsDoi(any(MpPublication.class));
		verify(crossRefBusService, never()).submitCrossRef(any(MpPublication.class));

		//Is USGS Numbered, emptyString doi
		IpdsProcess.setErrors(0);
		IpdsProcess.setStringBuilder(new StringBuilder(""));
		IpdsProcess.setAdditions(0);
		newMpPub.setDoi("");
		ipdsProcess.processPublication(ProcessType.DISSEMINATION, null, newMpPub, null);
		assertEquals("\n\tTrouble getting comment: null\n\tAdded to MyPubs as ProdId: null", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		assertEquals(1, IpdsProcess.getAdditions().intValue());
		verify(pubBusService, never()).deleteObject(null);
		verify(pubBusService, times(3)).createObject(newMpPub);
		verify(requester, never()).updateIpdsDoi(any(MpPublication.class));
		verify(crossRefBusService, never()).submitCrossRef(any(MpPublication.class));

		//Is USGS Numbered, "real" doi
		IpdsProcess.setErrors(0);
		IpdsProcess.setStringBuilder(new StringBuilder(""));
		IpdsProcess.setAdditions(0);
		newMpPub.setDoi("http:\\doi.gov");
		ipdsProcess.processPublication(ProcessType.DISSEMINATION, null, newMpPub, null);
		assertEquals("\n\tTrouble getting comment: null\n\tAdded to MyPubs as ProdId: null", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		assertEquals(1, IpdsProcess.getAdditions().intValue());
		verify(pubBusService, never()).deleteObject(null);
		verify(pubBusService, times(4)).createObject(newMpPub);
		verify(requester, never()).updateIpdsDoi(any(MpPublication.class));
		verify(crossRefBusService).submitCrossRef(any(MpPublication.class));

		//Is USGS UnNumbered, null doi
		IpdsProcess.setErrors(0);
		IpdsProcess.setStringBuilder(new StringBuilder(""));
		IpdsProcess.setAdditions(0);
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
		verify(requester, never()).updateIpdsDoi(any(MpPublication.class));
		verify(crossRefBusService).submitCrossRef(any(MpPublication.class));

		//Is USGS UnNumbered, emptyString doi
		IpdsProcess.setErrors(0);
		IpdsProcess.setStringBuilder(new StringBuilder(""));
		IpdsProcess.setAdditions(0);
		newMpPub.setDoi("");
		ipdsProcess.processPublication(ProcessType.DISSEMINATION, null, newMpPub, null);
		assertEquals("\n\tTrouble getting comment: null\n\tAdded to MyPubs as ProdId: null", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		assertEquals(1, IpdsProcess.getAdditions().intValue());
		verify(pubBusService, never()).deleteObject(null);
		verify(pubBusService, times(6)).createObject(newMpPub);
		verify(requester, never()).updateIpdsDoi(any(MpPublication.class));
		verify(crossRefBusService).submitCrossRef(any(MpPublication.class));

		//Is USGS UnNumbered, "real" doi
		IpdsProcess.setErrors(0);
		IpdsProcess.setStringBuilder(new StringBuilder(""));
		IpdsProcess.setAdditions(0);
		newMpPub.setDoi("http:\\doi.gov");
		ipdsProcess.processPublication(ProcessType.DISSEMINATION, null, newMpPub, null);
		assertEquals("\n\tTrouble getting comment: null\n\tAdded to MyPubs as ProdId: null", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		assertEquals(1, IpdsProcess.getAdditions().intValue());
		verify(pubBusService, never()).deleteObject(null);
		verify(pubBusService, times(7)).createObject(newMpPub);
		verify(requester, never()).updateIpdsDoi(any(MpPublication.class));
		verify(crossRefBusService, times(2)).submitCrossRef(any(MpPublication.class));	}

	protected MpPublication getInvalidPub() {
		MpPublication rtn = new MpPublication();
		ValidatorResult vr = new ValidatorResult("inField", "inMessage",SeverityLevel.FATAL, "inValue");
		rtn.addValidatorResult(vr);
		return rtn;
	}

	@Test
	public void processIpdsPublicationTest() throws SAXException, IOException {
		PubMap pm = new PubMap();
		pm.put(IpdsMessageLog.IPNUMBER, "IP-123");
		when(binder.bindPublication(any(PubMap.class))).thenReturn(existingMpPub9);
		when(pubBusService.getObjects(anyMap())).thenThrow(new RuntimeException("test")).thenReturn(null);
		when(pubBusService.createObject(existingMpPub9)).thenReturn(existingMpPub9);
		when(requester.getNotes(null)).thenReturn("<xml></xml>");
		when(binder.bindNotes(anyString(), anySet())).thenReturn(getPublicationMapEmpty());
		when(transactionManager.getTransaction(any(TransactionDefinition.class))).thenReturn(new DefaultTransactionStatus(null, false, false, false, false, null));
		IpdsProcess.setErrors(0);
		IpdsProcess.setStringBuilder(new StringBuilder(""));
		IpdsProcess.setAdditions(0);

		//Error condition - should rollback
		ipdsProcess.processIpdsPublication(ProcessType.SPN_PRODUCTION, pm);
		assertEquals("IP-123:\n\tERROR: Trouble processing pub: test\n\n", IpdsProcess.getStringBuilder().toString());
		assertEquals(1, IpdsProcess.getErrors().intValue());
		assertEquals(0, IpdsProcess.getAdditions().intValue());
		verify(transactionManager).getTransaction(any(TransactionDefinition.class));
		verify(transactionManager).rollback(any(TransactionStatus.class));
		verify(transactionManager, never()).commit(any(TransactionStatus.class));

		//Good, but not processed - should commit
		IpdsProcess.setErrors(0);
		IpdsProcess.setStringBuilder(new StringBuilder(""));
		IpdsProcess.setAdditions(0);
		ipdsProcess.processIpdsPublication(ProcessType.COST_CENTER, pm);
		assertEquals("IP-123:\n\tIPDS record not processed (COST_CENTER)- Publication Type: Test Type PublicationSubtype: Test Subtype Series: Test Series Process State: Test State DOI: Test Doi\n\n",
				IpdsProcess.getStringBuilder().toString());
		assertEquals(0, IpdsProcess.getErrors().intValue());
		assertEquals(0, IpdsProcess.getAdditions().intValue());
		verify(transactionManager, times(2)).getTransaction(any(TransactionDefinition.class));
		verify(transactionManager).rollback(any(TransactionStatus.class));
		verify(transactionManager).commit(any(TransactionStatus.class));

		//Good, processed, and should commit
		IpdsProcess.setErrors(0);
		IpdsProcess.setStringBuilder(new StringBuilder(""));
		IpdsProcess.setAdditions(0);
		ipdsProcess.processIpdsPublication(ProcessType.DISSEMINATION, pm);
		assertEquals("IP-123:\n\tAdded to MyPubs as ProdId: 9\n\n", IpdsProcess.getStringBuilder().toString());
		assertEquals(0, IpdsProcess.getErrors().intValue());
		assertEquals(1, IpdsProcess.getAdditions().intValue());
		verify(transactionManager, times(3)).getTransaction(any(TransactionDefinition.class));
		verify(transactionManager).rollback(any(TransactionStatus.class));
		verify(transactionManager, times(2)).commit(any(TransactionStatus.class));
	}

	@Test
	public void processLogTest() {
		String expectedMsg = "Summary:\n\tTotal Entries: 2\n\tPublications Added: 0\n\tErrors Encountered: 2\n\nnull:\n\tERROR: Trouble processing pub: test\n\nnull:\n\tERROR: Trouble processing pub: test\n\n";
		when(binder.bindPublication(any(PubMap.class))).thenThrow(new RuntimeException("test"));
		when(ipdsMessageLogDao.getFromIpds(1)).thenReturn(getPubMapList());
		assertEquals(expectedMsg, ipdsProcess.processLog(ProcessType.COST_CENTER, 1));

		//Should be the same message as the ThreadLocals are reset at start of method
		assertEquals(expectedMsg, ipdsProcess.processLog(ProcessType.COST_CENTER, 1));
	}

	protected List<PubMap> getPubMapList() {
		List<PubMap> rtn = new ArrayList<>();
		rtn.add(new PubMap());
		rtn.add(new PubMap());
		return rtn;
	}
}
