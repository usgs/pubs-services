package gov.usgs.cida.pubs.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import gov.usgs.cida.pubs.BaseTest;

public class DataNormalizationTest extends BaseTest {
	private static final String orcidNull = null;
	private static final String orcidPrefixNoNumber = "http://orcid/";
	private static final String orcidGoodNumber = "0000-0002-1824-0097";
	private static final String orcidBadNumber = "ojae-hjrg-aag2-0020";
	private static final String orcidPrefixBadNumber = orcidPrefixNoNumber + orcidBadNumber;
	private static final String orcidBadPrefixNoNumber = "http://notorcid.org";
	private static final String orcidBadPrefixBadNumber = "http://gro.dicro" + "1234-5678-9101-112K";

	@Test
	public void testNormalizeOrcid() {

		assertNull(DataNormalizationUtils.normalizeOrcid(orcidNull));
		assertEquals(orcidPrefixNoNumber,DataNormalizationUtils.normalizeOrcid(orcidPrefixNoNumber));
		assertEquals(orcidBadNumber, DataNormalizationUtils.normalizeOrcid(orcidBadNumber));
		assertEquals(orcidPrefixBadNumber, DataNormalizationUtils.normalizeOrcid(orcidPrefixBadNumber));
		assertEquals(orcidBadPrefixNoNumber, DataNormalizationUtils.normalizeOrcid(orcidBadPrefixNoNumber));
		assertEquals(orcidBadPrefixBadNumber, DataNormalizationUtils.normalizeOrcid(orcidBadPrefixBadNumber));

		assertEquals(orcidGoodNumber, DataNormalizationUtils.normalizeOrcid(orcidGoodNumber));
		assertEquals(orcidGoodNumber, DataNormalizationUtils.normalizeOrcid(orcidPrefixNoNumber + orcidGoodNumber));
		assertEquals("0000-0000-0000-0000", DataNormalizationUtils.normalizeOrcid("http://orcid.org/0000-0000-0000-0000"));
		assertEquals("0000-0000-0000-000X", DataNormalizationUtils.normalizeOrcid("http://orcid.org/0000-0000-0000-000X"));

		assertEquals("0000-0000-0000-0000", DataNormalizationUtils.normalizeOrcid("https://orcid.org/0000-0000-0000-0000"));
		assertEquals("0000-0000-0000-000X", DataNormalizationUtils.normalizeOrcid("https://orcid.org/0000-0000-0000-000X"));
	}

	@Test
	public void testDeormalizeOrcid() {
		assertNull(DataNormalizationUtils.denormalizeOrcid(orcidNull));
		assertEquals(orcidPrefixNoNumber,DataNormalizationUtils.denormalizeOrcid(orcidPrefixNoNumber));
		assertEquals(orcidBadNumber, DataNormalizationUtils.denormalizeOrcid(orcidBadNumber));
		assertEquals(orcidPrefixBadNumber, DataNormalizationUtils.denormalizeOrcid(orcidPrefixBadNumber));
		assertEquals(orcidBadPrefixNoNumber, DataNormalizationUtils.denormalizeOrcid(orcidBadPrefixNoNumber));
		assertEquals(orcidBadPrefixBadNumber, DataNormalizationUtils.denormalizeOrcid(orcidBadPrefixBadNumber));

		String orcidPrefix = DataNormalizationUtils.ORCID_PREFIX;

		assertEquals(orcidPrefix + orcidGoodNumber, DataNormalizationUtils.denormalizeOrcid(orcidGoodNumber));
		assertEquals(orcidPrefix + orcidGoodNumber, DataNormalizationUtils.denormalizeOrcid(orcidPrefixNoNumber + orcidGoodNumber));

		assertEquals(orcidPrefix + "0000-0000-0000-0000", DataNormalizationUtils.denormalizeOrcid("0000-0000-0000-0000"));
		assertEquals(orcidPrefix + "0000-0000-0000-000X", DataNormalizationUtils.denormalizeOrcid("0000-0000-0000-000X"));

		assertEquals(orcidPrefix + "0000-0000-0000-0000", DataNormalizationUtils.denormalizeOrcid("http://orcid.org/0000-0000-0000-0000"));
		assertEquals(orcidPrefix + "0000-0000-0000-000X", DataNormalizationUtils.denormalizeOrcid("http://orcid.org/0000-0000-0000-000X"));

		assertEquals(orcidPrefix + "0000-0000-0000-0000", DataNormalizationUtils.denormalizeOrcid("https://orcid.org/0000-0000-0000-0000"));
		assertEquals(orcidPrefix + "0000-0000-0000-000X", DataNormalizationUtils.denormalizeOrcid("https://orcid.org/0000-0000-0000-000X"));
	}
}