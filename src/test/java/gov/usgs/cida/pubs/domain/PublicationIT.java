package gov.usgs.cida.pubs.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.google.common.collect.ImmutableMap;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.dao.ContributorTypeDao;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublicationLink;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, ContributorType.class, ContributorTypeDao.class})
public class PublicationIT extends BaseIT {
	private static final Logger LOG = LoggerFactory.getLogger(PublicationIT.class);

	@Test
	public void testMappingContributors() {
		MpPublication pub = new MpPublication();
		assertNull(pub.getContributorsToMap());

		Collection<PublicationContributor<?>> cl = new ArrayList<>();
		pub.setContributors(cl);
		assertNull(pub.getContributorsToMap());

		PublicationContributor<?> author = new MpPublicationContributor();
		cl.add(author);
		//Not really an author yet...
		Map<String, List<PublicationContributor<?>>> hm = pub.getContributorsToMap();
		assertTrue(hm.containsKey("unknown"));
		author.setContributorType(new ContributorType());
		hm = pub.getContributorsToMap();
		assertTrue(hm.containsKey("unknown"));

		ContributorType act = new ContributorType();
		act.setId(ContributorType.AUTHORS);
		author.setContributorType(act);
		//Now I am by ID
		hm = pub.getContributorsToMap();
		assertTrue(hm.containsKey("authors"));
		act.setText("Authors");
		//Now I am by name
		hm = pub.getContributorsToMap();
		assertTrue(hm.containsKey("authors"));

		//make sure we add more keys to the hm
		cl.add(new MpPublicationContributor());
		ContributorType bad = new ContributorType();
		bad.setId(-1);
		PublicationContributor<?> badc = new MpPublicationContributor();
		badc.setContributorType(bad);
		cl.add(badc);
		hm = pub.getContributorsToMap();
		assertTrue(hm.containsKey("authors"));
		assertEquals(1, hm.get("authors").size());
		assertTrue(hm.containsKey("unknown"));
		assertEquals(2, hm.get("unknown").size());
	}

	@Test
	public void testGetMappingContributorsSorted() {
		Publication<?> pub = new Publication<>();

		ContributorType authorType = new ContributorType();
		authorType.setId(ContributorType.AUTHORS);

		ContributorType editorType = new ContributorType();
		editorType.setId(ContributorType.EDITORS);

		PublicationContributor<?> firstAuthor = new PublicationContributor<>();
		firstAuthor.setContributorType(authorType);
		firstAuthor.setRank(1);

		PublicationContributor<?> secondAuthor = new PublicationContributor<>();
		secondAuthor.setContributorType(authorType);
		secondAuthor.setRank(2);

		PublicationContributor<?> firstEditor = new PublicationContributor<>();
		firstEditor.setContributorType(editorType);
		firstEditor.setRank(3);

		PublicationContributor<?> secondEditor = new PublicationContributor<>();
		secondEditor.setContributorType(editorType);
		secondEditor.setRank(4);

		List<PublicationContributor<?>> contributors = Arrays.asList(
			//purposefully not in order
			secondEditor,
			firstAuthor,
			firstEditor,
			secondAuthor

		);
		Map<String, List<PublicationContributor<?>>> expected = ImmutableMap.of(
			"authors", Arrays.asList(firstAuthor, secondAuthor),
			"editors", Arrays.asList(firstEditor, secondEditor)
		);
		//Test a non-exhaustive set of permutations (exhaustive is too slow)
		for(int i = 0; i < 3; i++){
			Collections.rotate(contributors, 1);
			LOG.debug("a contributors test permutation: " + contributors.toString());
			pub.setContributors(contributors);
			Map<String, List<PublicationContributor<?>>> actual = pub.getContributorsToMap();
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testSetMappingContributors() {
		MpPublication pub = new MpPublication();
		pub.setContributorsFromMap(null);
		assertTrue(pub.getContributors().isEmpty());

		Map<String, List<PublicationContributor<?>>> cm = new HashMap<>();
		pub.setContributorsFromMap(cm);
		assertTrue(pub.getContributors().isEmpty());

		cm.put("unknown", null);
		pub.setContributorsFromMap(cm);
		assertTrue(pub.getContributors().isEmpty());

		List<PublicationContributor<?>> cl = new ArrayList<>();
		cm.put("unknown", cl);
		pub.setContributorsFromMap(cm);
		assertTrue(pub.getContributors().isEmpty());

		cl.add(new MpPublicationContributor());
		cl.add(new MpPublicationContributor());
		cl.add(new MpPublicationContributor());
		cm.put("authors", cl);
		pub.setContributorsFromMap(cm);
		//3 from unknown & 3 from authors...
		assertEquals(6, pub.getContributors().size());
	}

	public static Publication<?> buildAPub(Publication<?> newPub, final Integer pubId) {
		newPub.setIndexId("indexid" + pubId);
		newPub.setDisplayToPublicDate(LocalDateTime.of(2012, 8, 23, 11, 29, 46));

		CostCenter costCenter = new CostCenter();
		costCenter.setId(2);
		costCenter.setText("Affiliation Cost Center 2");
		PublicationCostCenter<?> pcc = new PublicationCostCenter<>();
		pcc.setId(1);
		pcc.setCostCenter(costCenter);
		Collection<PublicationCostCenter<?>> costCenters = new ArrayList<>();
		costCenters.add(pcc);
		newPub.setCostCenters(costCenters);

		PublicationType pubType = new PublicationType();
		pubType.setId(PublicationType.REPORT);
		pubType.setText("Report");
		newPub.setPublicationType(pubType);

		PublicationSubtype pubSubtype = new PublicationSubtype();
		pubSubtype.setId(5);
		pubSubtype.setText("USGS Numbered Series");
		newPub.setPublicationSubtype(pubSubtype);

		PublicationSeries pubSeries = new PublicationSeries();
		pubSeries.setId(PublicationSeries.SIR);
		pubSeries.setText("Scientific Investigations Report");
		newPub.setSeriesTitle(pubSeries);

		newPub.setSeriesNumber("Series Number");
		newPub.setSubseriesTitle("subseries");
		newPub.setChapter("chapter");
		newPub.setSubchapterNumber("subchapter");
		newPub.setDisplayTitle("Display Title");
		newPub.setTitle("Title");
		newPub.setDocAbstract("Abstract Text");
		newPub.setLanguage("Language");
		newPub.setPublisher("Publisher");
		newPub.setPublisherLocation("Publisher Location");
		newPub.setDoi("doiname");
		newPub.setIssn("inIssn");
		newPub.setIsbn("inIsbn");
		newPub.setCollaboration("collaboration");
		newPub.setUsgsCitation("usgscitation");
		newPub.setProductDescription("Prod Description");
		newPub.setStartPage("inStartPage");
		newPub.setEndPage("inEndPage");
		newPub.setNumberOfPages("5");
		newPub.setOnlineOnly("O");
		newPub.setAdditionalOnlineFiles("A");
		newPub.setTemporalStart(LocalDate.of(2010,10,10));
		newPub.setTemporalEnd(LocalDate.of(2012,12,12));
		newPub.setNotes("notes");
		newPub.setIpdsId("ipds_id" + pubId);
		newPub.setIpdsReviewProcessState(ProcessType.DISSEMINATION.getIpdsValue());
		newPub.setIpdsInternalId("12");
		newPub.setId(pubId);
		newPub.setPublicationYear("2001");
		newPub.setNoYear(false);
		newPub.setLargerWorkTitle("Larger Work Title");

		PublicationType largerWorkType = new PublicationType();
		largerWorkType.setId(PublicationType.ARTICLE);
		largerWorkType.setText("Article");
		newPub.setLargerWorkType(largerWorkType);

		newPub.setConferenceDate("a new free form date");
		newPub.setConferenceTitle("A title");
		newPub.setConferenceLocation("a conference location");

		PublicationSubtype largerWorkSubype = new PublicationSubtype();
		largerWorkSubype.setId(23);
		largerWorkSubype.setText("Database-spatial");
		newPub.setLargerWorkSubtype(largerWorkSubype);

		newPub.setScale("100");
		newPub.setProjection("EPSG:3857");
		newPub.setDatum("NAD83");
		newPub.setCountry("USA");
		newPub.setState("WI");
		newPub.setCounty("DANE");
		newPub.setCity("MIDDLETON");
		newPub.setOtherGeospatial("On the moon");
		newPub.setGeographicExtents(GEOGRAPHIC_EXTENTS);
		newPub.setVolume("VOL12");
		newPub.setIssue("ISIV");
		newPub.setContact("My Contact Info");
		newPub.setEdition("Edition X");
		newPub.setComments("just a little comment");
		newPub.setTableOfContents("tbl contents");
		PublishingServiceCenter publishingServiceCenter = new PublishingServiceCenter();
		publishingServiceCenter.setId(6);
		newPub.setPublishingServiceCenter(publishingServiceCenter);
		newPub.setPublishedDate(LocalDate.of(2002,2,2));
		Publication<?> po = new PwPublication();
		po.setId(1);
		po.setIndexId("ABC1");
		po.setPublicationYear("2015");
		po.setNoYear(false);
		po.setTitle("A Master Title");
		newPub.setIsPartOf(po);
		Publication<?> sb = new PwPublication();
		sb.setId(2);
		sb.setIndexId("XYZ2");
		sb.setPublicationYear(null);
		sb.setNoYear(true);
		sb.setTitle("A Super Title");
		newPub.setSupersededBy(sb);
		newPub.setRevisedDate(LocalDate.of(2003,3,3));
		newPub.setInsertDate(LocalDateTime.of(2015, 12, 31, 12, 1, 1));
		newPub.setInsertUsername("inInsertUsername");
		newPub.setUpdateDate(LocalDateTime.of(2015, 12, 31, 12, 1, 2));
		newPub.setUpdateUsername("inUpdateUsername");
		newPub.setPublished(true);
		newPub.setSourceDatabase("mypubs");

		LinkType linkType = new LinkType();
		linkType.setId(LinkType.PUBLICATION_XML);
		PwPublicationLink pwl = new PwPublicationLink();
		pwl.setLinkType(linkType);
		pwl.setUrl("http://usgs.gov/12354");
		newPub.setLinks(List.of(pwl));

		newPub.setContributors(PublicationContributorHelper.getContributors());

		return newPub;
	}

	
}
