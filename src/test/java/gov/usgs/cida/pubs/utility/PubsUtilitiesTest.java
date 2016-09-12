package gov.usgs.cida.pubs.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.webservice.security.PubsRoles;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;

public class PubsUtilitiesTest extends BaseSpringTest {

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
	public void removeStopWordsTest() {
		assertTrue(PubsUtilities.removeStopWords(null).isEmpty());
		assertTrue(PubsUtilities.removeStopWords("").isEmpty());
		assertTrue(PubsUtilities.removeStopWords("   ").isEmpty());
		assertEquals("red and fox and jumped and over and fence",
				StringUtils.join(PubsUtilities.removeStopWords("The red fox jumped over THE fence or not"), " and "));
		assertEquals("turtles and loggerhead", StringUtils.join(PubsUtilities.removeStopWords("Turtles Loggerhead"), " and "));
		assertTrue(PubsUtilities.removeStopWords("~`!@#$%^&*()_+{}|:\"<>?`-=[]\\;',./").isEmpty());
		assertEquals("1234567890qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnm",
				StringUtils.join(PubsUtilities.removeStopWords("1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM"), " and "));
		assertEquals("a1 and b and c and dd and e and f and g and h and ii and j and k and l and m and n and o and p and q and r and ss and tt and u and v and w and x and y and z"
				+ " and 1 and 2 and 3 and 4 and 5 and 6 and 7 and 8",
				StringUtils.join(PubsUtilities.removeStopWords("a1~b`c!dd@e#f$g%h^ii&j*k(l)m_n+o{p}q|r:ss\"tt<u>v?w`x-y=z[1]2\\3;4'5,6.7/8"), " and "));
		assertEquals("new and analysis and mars and  and special and regions and  and  and findings and second and mepag and special and regions and science and analysis and group and  and sr and sag2", 
				StringUtils.join(PubsUtilities.removeStopWords("A new analysis of Mars \"Special Regions\": findings of the Second MEPAG Special Regions Science Analysis Group (SR-SAG2)"), " and "));
	}

	@Test
	public void escapeReservedWordTest() {
		assertNull(PubsUtilities.escapeReservedWord(null));
		assertEquals("", PubsUtilities.escapeReservedWord(""));
		assertEquals("   ", PubsUtilities.escapeReservedWord("   "));
		assertEquals("red", PubsUtilities.escapeReservedWord("red"));
		assertEquals("{within}", PubsUtilities.escapeReservedWord("within"));
		assertEquals("{\\}", PubsUtilities.escapeReservedWord("\\"));
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
