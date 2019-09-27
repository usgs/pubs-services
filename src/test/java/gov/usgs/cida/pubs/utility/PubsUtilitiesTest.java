package gov.usgs.cida.pubs.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.annotation.SecurityTestExecutionListeners;
import org.springframework.security.test.context.support.WithMockUser;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.security.UserDetailTestService;

@SecurityTestExecutionListeners
public class PubsUtilitiesTest extends BaseTest {

	public static final String ID_NOT_MATCH_VALIDATION_JSON = "\"validationErrors\":[{\"field\":\"id\",\"level\":\"FATAL\",\"message\":\"The id in the URL does not match the id in the request.\",\"value\":\"30\"}]";

	public static final String SPN_AUTHORITY = "SPN_AUTHORITY";
	public static final String AUTHORIZED_AUTHORITY = "PUBS_AUTHORITY";

	@Mock
	private ConfigurationService configurationService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(configurationService.getAuthorizedAuthorities()).thenReturn(new String[] {AUTHORIZED_AUTHORITY, SPN_AUTHORITY, "silly", "willy"});
		when(configurationService.getSpnAuthorities()).thenReturn(new String[] {SPN_AUTHORITY, "silly"});
	}

	@Test
	public void isUsgsNumberedSeriesTest() {
		assertFalse(PubsUtils.isUsgsNumberedSeries(null));
		PublicationSubtype pubSubtype = new PublicationSubtype();
		assertFalse(PubsUtils.isUsgsNumberedSeries(pubSubtype));
		pubSubtype.setId(1);
		assertFalse(PubsUtils.isUsgsNumberedSeries(pubSubtype));
		pubSubtype.setId(PublicationSubtype.USGS_UNNUMBERED_SERIES);
		assertFalse(PubsUtils.isUsgsNumberedSeries(pubSubtype));
		pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		assertTrue(PubsUtils.isUsgsNumberedSeries(pubSubtype));
	}

	@Test
	public void isUsgsUnnumberedSeriesTest() {
		assertFalse(PubsUtils.isUsgsUnnumberedSeries(null));
		PublicationSubtype pubSubtype = new PublicationSubtype();
		assertFalse(PubsUtils.isUsgsUnnumberedSeries(pubSubtype));
		pubSubtype.setId(1);
		assertFalse(PubsUtils.isUsgsUnnumberedSeries(pubSubtype));
		pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		assertFalse(PubsUtils.isUsgsUnnumberedSeries(pubSubtype));
		pubSubtype.setId(PublicationSubtype.USGS_UNNUMBERED_SERIES);
		assertTrue(PubsUtils.isUsgsUnnumberedSeries(pubSubtype));
	}

	@Test
	public void isPublicationTypeArticleTest() {
		assertFalse(PubsUtils.isPublicationTypeArticle(null));
		PublicationType pubType = new PublicationType();
		assertFalse(PubsUtils.isPublicationTypeArticle(pubType));
		pubType.setId(1);
		assertFalse(PubsUtils.isPublicationTypeArticle(pubType));
		pubType.setId(PublicationType.REPORT);
		assertFalse(PubsUtils.isPublicationTypeArticle(pubType));
		pubType.setId(PublicationType.ARTICLE);
		assertTrue(PubsUtils.isPublicationTypeArticle(pubType));
	}

	@Test
	public void isSpnProductionTest() {
		assertFalse(PubsUtils.isSpnProduction(null));
		assertFalse(PubsUtils.isSpnProduction(""));
		assertFalse(PubsUtils.isSpnProduction(" "));
		assertFalse(PubsUtils.isSpnProduction("xyz"));
		assertTrue(PubsUtils.isSpnProduction(ProcessType.SPN_PRODUCTION.getIpdsValue()));
		assertFalse(PubsUtils.isSpnProduction(ProcessType.SPN_PRODUCTION.getIpdsValueEncoded()));
	}

	@Test
	public void isPublicationTypeUSGSDataReleaseTest() {
		assertFalse(PubsUtils.isPublicationTypeUSGSDataRelease(null));
		PublicationSubtype pubSubtype = new PublicationSubtype();
		assertFalse(PubsUtils.isPublicationTypeUSGSDataRelease(pubSubtype));
		pubSubtype.setId(1);
		assertFalse(PubsUtils.isPublicationTypeUSGSDataRelease(pubSubtype));
		pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		assertFalse(PubsUtils.isPublicationTypeUSGSDataRelease(pubSubtype));
		pubSubtype.setId(PublicationSubtype.USGS_DATA_RELEASE);
		assertTrue(PubsUtils.isPublicationTypeUSGSDataRelease(pubSubtype));
	}

	@Test
	public void isPublicationTypeUSGSWebsiteTest() {
		assertFalse(PubsUtils.isPublicationTypeUSGSWebsite(null));
		PublicationSubtype pubSubtype = new PublicationSubtype();
		assertFalse(PubsUtils.isPublicationTypeUSGSWebsite(pubSubtype));
		pubSubtype.setId(1);
		assertFalse(PubsUtils.isPublicationTypeUSGSWebsite(pubSubtype));
		pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		assertFalse(PubsUtils.isPublicationTypeUSGSWebsite(pubSubtype));
		pubSubtype.setId(PublicationSubtype.USGS_DATA_RELEASE);
		assertFalse(PubsUtils.isPublicationTypeUSGSWebsite(pubSubtype));
		pubSubtype.setId(PublicationSubtype.USGS_WEBSITE);
		assertTrue(PubsUtils.isPublicationTypeUSGSWebsite(pubSubtype));
	}

	@Test
	public void getUsernameTest_noAuthentication() {
		assertEquals("Not Authenticated", PubsConstantsHelper.ANONYMOUS_USER, PubsUtils.getUsername());
	}

	@Test
	@WithMockUser(username=UserDetailTestService.AUTHENTICATED_USER)
	public void getUsernameTest_authenticated() {
		assertEquals("Is Authenticated", UserDetailTestService.AUTHENTICATED_USER, PubsUtils.getUsername());
	}

	@Test
	public void buildErrorMsgTest() {
		Object[] messageArguments = new String[]{"abc", "def"};
		assertNull(PubsUtils.buildErrorMsg(null, null));
		assertEquals("", PubsUtils.buildErrorMsg("", null));
		assertEquals("{0} is already in use on Prod Id {1}.", PubsUtils.buildErrorMsg("{publication.indexid.duplicate}", null));
		assertEquals("abc is already in use on Prod Id def.", PubsUtils.buildErrorMsg("{publication.indexid.duplicate}", messageArguments));
		assertEquals("is {0} from {1}", PubsUtils.buildErrorMsg("is {0} from {1}", null));
		assertEquals("is abc from def", PubsUtils.buildErrorMsg("is {0} from {1}", messageArguments));
		assertEquals("abc from def not", PubsUtils.buildErrorMsg("{0} from {1} not", messageArguments));
	}

	@Test
	public void isIntegerTest() {
		assertFalse(PubsUtils.isInteger(null));
		assertFalse(PubsUtils.isInteger(""));
		assertFalse(PubsUtils.isInteger("  "));
		assertFalse(PubsUtils.isInteger("abc123"));
		assertTrue(PubsUtils.isInteger("123"));
	}

	@Test
	public void parseIntegerTest() {
		assertNull(PubsUtils.parseInteger(null));
		assertNull(PubsUtils.parseInteger(""));
		assertNull(PubsUtils.parseInteger("  "));
		assertNull(PubsUtils.parseInteger("abc123"));
		assertEquals(123, PubsUtils.parseInteger("123").intValue());
	}

	@Test
	public void isSpnUserTest_noAuthentication() {
		assertFalse(PubsUtils.isSpnUser(configurationService));
	}

	@Test
	@WithMockUser(username=UserDetailTestService.AUTHENTICATED_USER)
	public void isSpnUserTest_noAuthorities() {
		assertFalse(PubsUtils.isSpnUser(configurationService));
	}

	@Test
	@WithMockUser(username=UserDetailTestService.SPN_USER, authorities={SPN_AUTHORITY})
	public void isSpnUserTest() {
		assertTrue(PubsUtils.isSpnUser(configurationService));
	}

	@Test
	@WithMockUser(username=UserDetailTestService.AUTHORIZED_USER,authorities={SPN_AUTHORITY, AUTHORIZED_AUTHORITY})
	public void isSpnUserTest_plus() {
		assertTrue(PubsUtils.isSpnUser(configurationService));
	}

	@Test
	@WithMockUser(username=UserDetailTestService.AUTHORIZED_USER, authorities={AUTHORIZED_AUTHORITY})
	public void isSpnUserTest_pubs() {
		assertFalse(PubsUtils.isSpnUser(configurationService));
	}

	@Test
	public void isSpnOnlyTest_noAuthentication() {
		assertFalse(PubsUtils.isSpnOnly(configurationService));
	}

	@Test
	@WithMockUser(username=UserDetailTestService.AUTHENTICATED_USER)
	public void isSpnOnlyTest_noAuthorities() {
		assertFalse(PubsUtils.isSpnOnly(configurationService));
	}

	@Test
	@WithMockUser(username=UserDetailTestService.SPN_USER, authorities={SPN_AUTHORITY})
	public void isSpnOnlyTest() {
		assertTrue(PubsUtils.isSpnOnly(configurationService));
	}

	@Test
	@WithMockUser(username=UserDetailTestService.AUTHORIZED_USER,authorities={SPN_AUTHORITY, AUTHORIZED_AUTHORITY})
	public void isSpnOnlyTest_plus() {
		assertFalse(PubsUtils.isSpnOnly(configurationService));
	}

	@Test
	@WithMockUser(username=UserDetailTestService.AUTHORIZED_USER, authorities={AUTHORIZED_AUTHORITY})
	public void isSpnOnlyTest_pubs() {
		assertFalse(PubsUtils.isSpnOnly(configurationService));
	}
}
