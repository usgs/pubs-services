package gov.usgs.cida.pubs.domain.pw;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationTest;
import gov.usgs.cida.pubs.json.View;

import org.json.JSONObject;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class PwPublicationTest extends BaseSpringTest {

	@Test
	public void serializePWTest() {
		PwPublication pub = buildAPub(1);
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
			String result = mapper.writerWithView(View.PW.class).writeValueAsString(pub);

			String expected = getCompareFile("pwPublication/serialized.json");
			assertThat(new JSONObject(result), sameJSONObjectAs(new JSONObject(expected)));
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}

	public static PwPublication buildAPub(final Integer pubId) {
		PwPublication pub = (PwPublication) PublicationTest.buildAPub(new PwPublication(), pubId);
		pub.setScienceBaseUri("http://test.sciencebase.org");
		Chorus chorus = new Chorus();
		chorus.setDoi("myDoi");
		chorus.setUrl("http://chorus.url.org");
		chorus.setPublisher("CHORUS Publisher");
		chorus.setAuthors("CHORUS Author 1, CHORUS AUTHOR2");
		chorus.setJournalName("CHORUS journal name");
		chorus.setPublicationDate("published 14Jun2016");
		chorus.setAuditedOn("audit 14Jun2016");
		chorus.setPblcllyAccessDate("avail 14Jun2016");
		pub.setChorus(chorus);
		return pub;
	}

	public static void assertPwPub4Children(Publication<?> pub) {
		assertEquals(2, pub.getContributors().size());
		assertEquals(1, pub.getCostCenters().size());
		assertEquals(1, pub.getLinks().size());
	}

	public static void assertPwPub4(Publication<?> pub) {
		assertEquals(4, pub.getId().intValue());
		assertEquals("4", pub.getIndexId());
		assertEquals("2014-07-22T17:09:24", pub.getDisplayToPublicDate().toString());
		assertEquals(5, pub.getPublicationType().getId().intValue());
		assertEquals(18, pub.getPublicationSubtype().getId().intValue());
		assertEquals(332, pub.getSeriesTitle().getId().intValue());
		assertEquals("series number", pub.getSeriesNumber());
		assertEquals("subseries title", pub.getSubseriesTitle());
		assertEquals("chapter", pub.getChapter());
		assertEquals("subchapter", pub.getSubchapterNumber());
		assertEquals("display title", pub.getDisplayTitle());
		assertEquals("title", pub.getTitle());
		assertEquals("abstract", pub.getDocAbstract());
		assertEquals("language", pub.getLanguage());
		assertEquals("publisher", pub.getPublisher());
		assertEquals("publisher loc", pub.getPublisherLocation());
		assertEquals("doi", pub.getDoi());
		assertEquals("issn", pub.getIssn());
		assertEquals("isbn", pub.getIsbn());
		assertEquals("collaborator", pub.getCollaboration());
		assertEquals("usgs citation", pub.getUsgsCitation());
		assertEquals("product description", pub.getProductDescription());
		assertEquals("start", pub.getStartPage());
		assertEquals("end", pub.getEndPage());
		assertEquals("12", pub.getNumberOfPages());
		assertEquals("N", pub.getOnlineOnly());
		assertEquals("Y", pub.getAdditionalOnlineFiles());
		assertEquals("2014-07-22", pub.getTemporalStart().toString());
		assertEquals("2014-07-23", pub.getTemporalEnd().toString());
		assertEquals("2014", pub.getPublicationYear());
		assertFalse(pub.isNoYear());
		assertEquals("notes", pub.getNotes());
		assertEquals("ipds_id", pub.getIpdsId());
		assertEquals("100", pub.getScale());
		assertEquals("EPSG:3857", pub.getProjection());
		assertEquals("NAD83", pub.getDatum());
		assertEquals("USA", pub.getCountry());
		assertEquals("WI", pub.getState());
		assertEquals("DANE", pub.getCounty());
		assertEquals("MIDDLETON", pub.getCity());
		assertEquals("On the moon", pub.getOtherGeospatial());
		assertEquals(GEOGRAPHIC_EXTENTS, pub.getGeographicExtents());
		assertEquals("contact for the pub4", pub.getContact());
		assertEquals("edition4", pub.getEdition());
		assertEquals("comments on this4", pub.getComments());
		assertEquals("contents, table of4", pub.getTableOfContents());
		assertEquals(5, pub.getPublishingServiceCenter().getId().intValue());
		assertEquals("2001-01-01", pub.getPublishedDate().toString());
		assertEquals(5, pub.getIsPartOf().getId().intValue());
		assertEquals(6, pub.getSupersededBy().getId().intValue());
		assertEquals("2004-04-04", pub.getRevisedDate().toString());
		if (pub instanceof PwPublication) {
			PwPublication pwPub = (PwPublication) pub;
			assertEquals("http://sciencebase.org", pwPub.getScienceBaseUri());
			assertEquals("http://doi.org", pwPub.getChorus().getDoi());
			assertEquals("http://dx.doi.org/10.1002/ece3.1813", pwPub.getChorus().getUrl());
			assertEquals("Wiley-Blackwell", pwPub.getChorus().getPublisher());
			assertEquals("Beerens James M., Frederick Peter C., Noonburg Erik G., Gawlik Dale E.", pwPub.getChorus().getAuthors());
			assertEquals("Ecology and Evolution", pwPub.getChorus().getJournalName());
			assertEquals("11/19/2015", pwPub.getChorus().getPublicationDate());
			assertEquals("11/21/2015", pwPub.getChorus().getAuditedOn());
			assertEquals("11/19/2015", pwPub.getChorus().getPblcllyAccessDate());
		}
	}
}