package gov.usgs.cida.pubs.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.annotation.SecurityTestExecutionListeners;
import org.springframework.security.test.context.support.WithMockUser;
import static org.mockito.Mockito.when;
import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.TestOAuth;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;

@SecurityTestExecutionListeners
public class PubsUtilitiesTest extends BaseTest {

	public static final String ID_NOT_MATCH_VALIDATION_JSON = "\"validationErrors\":[{\"field\":\"id\",\"level\":\"FATAL\",\"message\":\"The id in the URL does not match the id in the request.\",\"value\":\"30\"}]";

	@Mock
	ConfigurationService configurationService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(configurationService.getAuthorizedAuthorities()).thenReturn(new String[] {TestOAuth.AUTHORIZED_AUTHORITY, TestOAuth.SPN_AUTHORITY, "silly", "willy"});
		when(configurationService.getSpnAuthorities()).thenReturn(new String[] {TestOAuth.SPN_AUTHORITY, "silly"});
	}

	@Test
	public void isUsgsNumberedSeriesTest() {
		assertFalse(PubsUtilities.isUsgsNumberedSeries(null));
		PublicationSubtype pubSubtype = new PublicationSubtype();
		assertFalse(PubsUtilities.isUsgsNumberedSeries(pubSubtype));
		pubSubtype.setId(1);
		assertFalse(PubsUtilities.isUsgsNumberedSeries(pubSubtype));
		pubSubtype.setId(PublicationSubtype.USGS_UNNUMBERED_SERIES);
		assertFalse(PubsUtilities.isUsgsNumberedSeries(pubSubtype));
		pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		assertTrue(PubsUtilities.isUsgsNumberedSeries(pubSubtype));
	}

	@Test
	public void isUsgsUnnumberedSeriesTest() {
		assertFalse(PubsUtilities.isUsgsUnnumberedSeries(null));
		PublicationSubtype pubSubtype = new PublicationSubtype();
		assertFalse(PubsUtilities.isUsgsUnnumberedSeries(pubSubtype));
		pubSubtype.setId(1);
		assertFalse(PubsUtilities.isUsgsUnnumberedSeries(pubSubtype));
		pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		assertFalse(PubsUtilities.isUsgsUnnumberedSeries(pubSubtype));
		pubSubtype.setId(PublicationSubtype.USGS_UNNUMBERED_SERIES);
		assertTrue(PubsUtilities.isUsgsUnnumberedSeries(pubSubtype));
	}

	@Test
	public void isPublicationTypeArticleTest() {
		assertFalse(PubsUtilities.isPublicationTypeArticle(null));
		PublicationType pubType = new PublicationType();
		assertFalse(PubsUtilities.isPublicationTypeArticle(pubType));
		pubType.setId(1);
		assertFalse(PubsUtilities.isPublicationTypeArticle(pubType));
		pubType.setId(PublicationType.REPORT);
		assertFalse(PubsUtilities.isPublicationTypeArticle(pubType));
		pubType.setId(PublicationType.ARTICLE);
		assertTrue(PubsUtilities.isPublicationTypeArticle(pubType));
	}

	@Test
	public void isSpnProductionTest() {
		assertFalse(PubsUtilities.isSpnProduction(null));
		assertFalse(PubsUtilities.isSpnProduction(""));
		assertFalse(PubsUtilities.isSpnProduction(" "));
		assertFalse(PubsUtilities.isSpnProduction("xyz"));
		assertTrue(PubsUtilities.isSpnProduction(ProcessType.SPN_PRODUCTION.getIpdsValue()));
		assertFalse(PubsUtilities.isSpnProduction(ProcessType.SPN_PRODUCTION.getIpdsValueEncoded()));
	}

	@Test
	public void isPublicationTypeUSGSDataReleaseTest() {
		assertFalse(PubsUtilities.isPublicationTypeUSGSDataRelease(null));
		PublicationSubtype pubSubtype = new PublicationSubtype();
		assertFalse(PubsUtilities.isPublicationTypeUSGSDataRelease(pubSubtype));
		pubSubtype.setId(1);
		assertFalse(PubsUtilities.isPublicationTypeUSGSDataRelease(pubSubtype));
		pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		assertFalse(PubsUtilities.isPublicationTypeUSGSDataRelease(pubSubtype));
		pubSubtype.setId(PublicationSubtype.USGS_DATA_RELEASE);
		assertTrue(PubsUtilities.isPublicationTypeUSGSDataRelease(pubSubtype));
	}

	@Test
	public void isPublicationTypeUSGSWebsiteTest() {
		assertFalse(PubsUtilities.isPublicationTypeUSGSWebsite(null));
		PublicationSubtype pubSubtype = new PublicationSubtype();
		assertFalse(PubsUtilities.isPublicationTypeUSGSWebsite(pubSubtype));
		pubSubtype.setId(1);
		assertFalse(PubsUtilities.isPublicationTypeUSGSWebsite(pubSubtype));
		pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		assertFalse(PubsUtilities.isPublicationTypeUSGSWebsite(pubSubtype));
		pubSubtype.setId(PublicationSubtype.USGS_DATA_RELEASE);
		assertFalse(PubsUtilities.isPublicationTypeUSGSWebsite(pubSubtype));
		pubSubtype.setId(PublicationSubtype.USGS_WEBSITE);
		assertTrue(PubsUtilities.isPublicationTypeUSGSWebsite(pubSubtype));
	}

	@Test
	public void getUsernameTest_noAuthentication() {
		assertEquals("Not Authenticated", PubsConstants.ANONYMOUS_USER, PubsUtilities.getUsername());
	}

	@Test
	@WithMockUser(username=TestOAuth.AUTHENTICATED_USER)
	public void getUsernameTest_authenticated() {
		assertEquals("Is Authenticated", TestOAuth.AUTHENTICATED_USER, PubsUtilities.getUsername());
	}

	@Test
	public void buildErrorMsgTest() {
		Object[] messageArguments = Arrays.asList(new String[]{"abc", "def"}).toArray();
		assertNull(PubsUtilities.buildErrorMsg(null, null));
		assertEquals("", PubsUtilities.buildErrorMsg("", null));
		assertEquals("{0} is already in use on Prod Id {1}.", PubsUtilities.buildErrorMsg("{publication.indexid.duplicate}", null));
		assertEquals("abc is already in use on Prod Id def.", PubsUtilities.buildErrorMsg("{publication.indexid.duplicate}", messageArguments));
		assertEquals("is {0} from {1}", PubsUtilities.buildErrorMsg("is {0} from {1}", null));
		assertEquals("is abc from def", PubsUtilities.buildErrorMsg("is {0} from {1}", messageArguments));
		assertEquals("abc from def not", PubsUtilities.buildErrorMsg("{0} from {1} not", messageArguments));
	}

	@Test
	public void isIntegerTest() {
		assertFalse(PubsUtilities.isInteger(null));
		assertFalse(PubsUtilities.isInteger(""));
		assertFalse(PubsUtilities.isInteger("  "));
		assertFalse(PubsUtilities.isInteger("abc123"));
		assertTrue(PubsUtilities.isInteger("123"));
	}

	@Test
	public void parseIntegerTest() {
		assertNull(PubsUtilities.parseInteger(null));
		assertNull(PubsUtilities.parseInteger(""));
		assertNull(PubsUtilities.parseInteger("  "));
		assertNull(PubsUtilities.parseInteger("abc123"));
		assertEquals(123, PubsUtilities.parseInteger("123").intValue());
	}

	@Test
	public void isSpnUserTest_noAuthentication() {
		assertFalse(PubsUtilities.isSpnUser(configurationService));
	}

	@Test
	@WithMockUser(username=TestOAuth.AUTHENTICATED_USER)
	public void isSpnUserTest_noAuthorities() {
		assertFalse(PubsUtilities.isSpnUser(configurationService));
	}

	@Test
	@WithMockUser(username=TestOAuth.SPN_USER, authorities={TestOAuth.SPN_AUTHORITY})
	public void isSpnUserTest() {
		assertTrue(PubsUtilities.isSpnUser(configurationService));
	}

	@Test
	@WithMockUser(username=TestOAuth.AUTHORIZED_USER,authorities={TestOAuth.SPN_AUTHORITY, TestOAuth.AUTHORIZED_AUTHORITY})
	public void isSpnUserTest_plus() {
		assertTrue(PubsUtilities.isSpnUser(configurationService));
	}

	@Test
	@WithMockUser(username=TestOAuth.AUTHORIZED_USER, authorities={TestOAuth.AUTHORIZED_AUTHORITY})
	public void isSpnUserTest_pubs() {
		assertFalse(PubsUtilities.isSpnUser(configurationService));
	}

	@Test
	public void isSpnOnlyTest_noAuthentication() {
		assertFalse(PubsUtilities.isSpnOnly(configurationService));
	}

	@Test
	@WithMockUser(username=TestOAuth.AUTHENTICATED_USER)
	public void isSpnOnlyTest_noAuthorities() {
		assertFalse(PubsUtilities.isSpnOnly(configurationService));
	}

	@Test
	@WithMockUser(username=TestOAuth.SPN_USER, authorities={TestOAuth.SPN_AUTHORITY})
	public void isSpnOnlyTest() {
		assertTrue(PubsUtilities.isSpnOnly(configurationService));
	}

	@Test
	@WithMockUser(username=TestOAuth.AUTHORIZED_USER,authorities={TestOAuth.SPN_AUTHORITY, TestOAuth.AUTHORIZED_AUTHORITY})
	public void isSpnOnlyTest_plus() {
		assertFalse(PubsUtilities.isSpnOnly(configurationService));
	}

	@Test
	@WithMockUser(username=TestOAuth.AUTHORIZED_USER, authorities={TestOAuth.AUTHORIZED_AUTHORITY})
	public void isSpnOnlyTest_pubs() {
		assertFalse(PubsUtilities.isSpnOnly(configurationService));
	}
}
