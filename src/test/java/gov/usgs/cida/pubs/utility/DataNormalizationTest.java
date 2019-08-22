package gov.usgs.cida.pubs.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.annotation.SecurityTestExecutionListeners;
import static org.mockito.Mockito.when;
import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.TestOAuth;

@SecurityTestExecutionListeners
public class DataNormalizationTest extends BaseTest {
    private static final String orcidNull = null;
    private static final String orcidPrefixNoNumber = "http://orcid/";
    private static final String orcidGoodNumber = "0000-0002-1824-0097";
    private static final String orcidBadNumber = "ojae-hjrg-aag2-0020";
    private static final String orcidPrefixBadNumber = orcidPrefixNoNumber + orcidBadNumber;
    private static final String orcidBadPrefixNoNumber = "http://notorcid.org";
    private static final String orcidBadPrefixBadNumber = "http://gro.dicro" + "1234-5678-9101-112K";

	@Mock
	ConfigurationService configurationService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(configurationService.getAuthorizedAuthorities()).thenReturn(new String[] {TestOAuth.AUTHORIZED_AUTHORITY, TestOAuth.SPN_AUTHORITY, "silly", "willy"});
		when(configurationService.getSpnAuthorities()).thenReturn(new String[] {TestOAuth.SPN_AUTHORITY, "silly"});
	}

	@Test
    public void testNormalizeOrcid() {

            assertNull(DataNormalization.normalizeOrcid(orcidNull));
            assertNull(DataNormalization.normalizeOrcid(orcidPrefixNoNumber));
            assertNull(DataNormalization.normalizeOrcid(orcidBadNumber));
            assertNull(DataNormalization.normalizeOrcid(orcidPrefixBadNumber));
            assertNull(DataNormalization.normalizeOrcid(orcidBadPrefixNoNumber));
            assertNull(DataNormalization.normalizeOrcid(orcidBadPrefixBadNumber));

            assertEquals(orcidGoodNumber, DataNormalization.normalizeOrcid(orcidGoodNumber));
            assertEquals(orcidGoodNumber, DataNormalization.normalizeOrcid(orcidPrefixNoNumber + orcidGoodNumber));
            assertEquals("0000-0000-0000-0000", DataNormalization.normalizeOrcid("http://orcid.org/0000-0000-0000-0000"));
            assertEquals("0000-0000-0000-000X", DataNormalization.normalizeOrcid("http://orcid.org/0000-0000-0000-000X"));

            assertEquals("0000-0000-0000-0000", DataNormalization.normalizeOrcid("https://orcid.org/0000-0000-0000-0000"));
            assertEquals("0000-0000-0000-000X", DataNormalization.normalizeOrcid("https://orcid.org/0000-0000-0000-000X"));
    }

	@Test
    public void testDeormalizeOrcid() {
            assertNull(DataNormalization.denormalizeOrcid(orcidNull));
            assertNull(DataNormalization.denormalizeOrcid(orcidPrefixNoNumber));
            assertNull(DataNormalization.denormalizeOrcid(orcidBadNumber));
            assertNull(DataNormalization.denormalizeOrcid(orcidPrefixBadNumber));
            assertNull(DataNormalization.denormalizeOrcid(orcidBadPrefixNoNumber));
            assertNull(DataNormalization.denormalizeOrcid(orcidBadPrefixBadNumber));

            String orcidPrefix = DataNormalization.ORCID_PREFIX;

            assertEquals(orcidPrefix + orcidGoodNumber, DataNormalization.denormalizeOrcid(orcidGoodNumber));
            assertEquals(orcidPrefix + orcidGoodNumber, DataNormalization.denormalizeOrcid(orcidPrefixNoNumber + orcidGoodNumber));

            assertEquals(orcidPrefix + "0000-0000-0000-0000", DataNormalization.denormalizeOrcid("0000-0000-0000-0000"));
            assertEquals(orcidPrefix + "0000-0000-0000-000X", DataNormalization.denormalizeOrcid("0000-0000-0000-000X"));

            assertEquals(orcidPrefix + "0000-0000-0000-0000", DataNormalization.denormalizeOrcid("http://orcid.org/0000-0000-0000-0000"));
            assertEquals(orcidPrefix + "0000-0000-0000-000X", DataNormalization.denormalizeOrcid("http://orcid.org/0000-0000-0000-000X"));

            assertEquals(orcidPrefix + "0000-0000-0000-0000", DataNormalization.denormalizeOrcid("https://orcid.org/0000-0000-0000-0000"));
            assertEquals(orcidPrefix + "0000-0000-0000-000X", DataNormalization.denormalizeOrcid("https://orcid.org/0000-0000-0000-000X"));
	}

}