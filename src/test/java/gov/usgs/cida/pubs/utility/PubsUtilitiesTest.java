package gov.usgs.cida.pubs.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.webservice.security.PubsRoles;

public class PubsUtilitiesTest extends BaseTest {

	public static final String ID_NOT_MATCH_VALIDATION_JSON = "\"validationErrors\":[{\"field\":\"id\",\"level\":\"FATAL\",\"message\":\"The id in the URL does not match the id in the request.\",\"value\":\"30\"}]";

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
	public void getUsernameTest() {
		assertEquals("Not Authenticated", PubsConstants.ANONYMOUS_USER, PubsUtilities.getUsername());

		buildTestAuthentication("dummy");
		assertEquals("Is Authenticated", "dummy", PubsUtilities.getUsername());

		SecurityContextHolder.clearContext();
		assertEquals("Not Authenticated", PubsConstants.ANONYMOUS_USER, PubsUtilities.getUsername());
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
	public void isSpnUserTest() {
		assertFalse(PubsUtilities.isSpnUser());

		PubsUtilitiesTest.buildTestAuthentication("dummy", Arrays.asList(PubsRoles.PUBS_SPN_USER.name()));
		assertTrue(PubsUtilities.isSpnUser());

		PubsUtilitiesTest.buildTestAuthentication("dummy", Arrays.asList(PubsRoles.PUBS_SPN_USER.name(),
				PubsRoles.PUBS_ADMIN.name()));
		assertTrue(PubsUtilities.isSpnUser());

		PubsUtilitiesTest.buildTestAuthentication("dummy", Arrays.asList(PubsRoles.PUBS_SPN_USER.name(),
				PubsRoles.PUBS_CATALOGER_USER.name()));
		assertTrue(PubsUtilities.isSpnUser());

		PubsUtilitiesTest.buildTestAuthentication("dummy", Arrays.asList(PubsRoles.PUBS_SPN_USER.name(),
				PubsRoles.PUBS_ADMIN.name(), PubsRoles.PUBS_CATALOGER_USER.name()));
		assertTrue(PubsUtilities.isSpnUser());

		PubsUtilitiesTest.buildTestAuthentication("dummy", Arrays.asList(PubsRoles.PUBS_ADMIN.name(),
				PubsRoles.PUBS_CATALOGER_USER.name()));
		assertFalse(PubsUtilities.isSpnUser());

		PubsUtilitiesTest.buildTestAuthentication("dummy", Arrays.asList(PubsRoles.PUBS_ADMIN.name()));
		assertFalse(PubsUtilities.isSpnUser());

		PubsUtilitiesTest.buildTestAuthentication("dummy", Arrays.asList(PubsRoles.PUBS_CATALOGER_USER.name()));
		assertFalse(PubsUtilities.isSpnUser());
	}

	@Test
	public void isSpnOnlyTest() {
		assertFalse(PubsUtilities.isSpnOnly());

		PubsUtilitiesTest.buildTestAuthentication("dummy", Arrays.asList(PubsRoles.PUBS_SPN_USER.name()));
		assertTrue(PubsUtilities.isSpnOnly());

		PubsUtilitiesTest.buildTestAuthentication("dummy", Arrays.asList(PubsRoles.PUBS_SPN_USER.name(),
				PubsRoles.PUBS_ADMIN.name()));
		assertFalse(PubsUtilities.isSpnOnly());

		PubsUtilitiesTest.buildTestAuthentication("dummy", Arrays.asList(PubsRoles.PUBS_SPN_USER.name(),
				PubsRoles.PUBS_CATALOGER_USER.name()));
		assertFalse(PubsUtilities.isSpnOnly());

		PubsUtilitiesTest.buildTestAuthentication("dummy", Arrays.asList(PubsRoles.PUBS_SPN_USER.name(),
				PubsRoles.PUBS_ADMIN.name(), PubsRoles.PUBS_CATALOGER_USER.name()));
		assertFalse(PubsUtilities.isSpnOnly());

		PubsUtilitiesTest.buildTestAuthentication("dummy", Arrays.asList(PubsRoles.PUBS_ADMIN.name(),
				PubsRoles.PUBS_CATALOGER_USER.name()));
		assertFalse(PubsUtilities.isSpnOnly());

		PubsUtilitiesTest.buildTestAuthentication("dummy", Arrays.asList(PubsRoles.PUBS_ADMIN.name()));
		assertFalse(PubsUtilities.isSpnOnly());

		PubsUtilitiesTest.buildTestAuthentication("dummy", Arrays.asList(PubsRoles.PUBS_CATALOGER_USER.name()));
		assertFalse(PubsUtilities.isSpnOnly());
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

}
