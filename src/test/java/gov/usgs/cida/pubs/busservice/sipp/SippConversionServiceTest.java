package gov.usgs.cida.pubs.busservice.sipp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.OutsideContributorHelper;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtypeHelper;
import gov.usgs.cida.pubs.domain.PublicationTypeHelper;
import gov.usgs.cida.pubs.domain.PublishingServiceCenter;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.domain.UsgsContributorHelper;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.domain.sipp.Author;
import gov.usgs.cida.pubs.domain.sipp.InformationProduct;
import gov.usgs.cida.pubs.domain.sipp.InformationProductHelper;
import gov.usgs.cida.pubs.domain.sipp.NoteHelper;
import gov.usgs.cida.pubs.domain.sipp.SippContributorHelper;

public class SippConversionServiceTest extends BaseTest {

	protected SippConversionService sippConversionService;

	@BeforeEach
	public void setUp() {
		sippConversionService = new SippConversionService();
	}

	@Test
	public void buildMpPublicationTest() throws Exception {
		MpPublication mpPublication = sippConversionService.buildMpPublication(InformationProductHelper.TEST_ONE, 987654);

		assertTestOne(mpPublication, 987654);
	}

	@Test
	public void buildContributorTest() {
		Contributor<?> contributor = sippConversionService.buildContributor(SippContributorHelper.USGS_CONTRIBUTOR);
		assertTrue(contributor instanceof UsgsContributor);
		UsgsContributorHelper.assertJaneDoe((UsgsContributor) contributor);

		contributor = sippConversionService.buildContributor(SippContributorHelper.EXISTING_OUTSIDE_CONTRIBUTOR);
		assertTrue(contributor instanceof OutsideContributor);
		OutsideContributorHelper.assertJaneMDoe((OutsideContributor) contributor);
	}

	@Test
	public void buildContributorTypeTest() {
		Author author = new Author();
		assertEquals(2, sippConversionService.buildContributorType(author).getId().intValue());

		author.setContributorRole("1");
		assertEquals(1, sippConversionService.buildContributorType(author).getId().intValue());

		author.setContributorRole("13");
		assertEquals(2, sippConversionService.buildContributorType(author).getId().intValue());
	}


	@Test
	public void BuildNotesTest() {
		InformationProduct informationProduct = new InformationProduct();

		assertNull(sippConversionService.buildNotes(informationProduct));

		informationProduct.setProductSummary("productSummary");

		assertEquals("productSummary\n\t", sippConversionService.buildNotes(informationProduct));

		informationProduct.setNotes(List.of(NoteHelper.ONE, NoteHelper.TWO, NoteHelper.THREE));

		assertEquals("productSummary\n\tnote1|note2|" , sippConversionService.buildNotes(informationProduct));

		informationProduct.setProductSummary(null);
		assertEquals("note1|note2|" , sippConversionService.buildNotes(informationProduct));
	}

	@Test
	public void buildObjectPropertiesTest1() {
		MpPublication mpPublication = sippConversionService
				.buildObjectProperties(InformationProductHelper.TEST_ONE, null);
		assertTestOneBase(mpPublication, null);
	}

	@Test
	public void buildObjectPropertiesTest2() {
		MpPublication mpPublication = sippConversionService
				.buildObjectProperties(InformationProductHelper.TEST_TWO, 987654321);
		assertEquals(987654321, mpPublication.getId().intValue());
		assertEquals("indexID", mpPublication.getIndexId());
		assertEquals(PublicationTypeHelper.FOUR, mpPublication.getPublicationType());
		assertNull(mpPublication.getPublicationSubtype());
		assertNull(mpPublication.getSeriesTitle());
		assertEquals("usgsSeriesNumber", mpPublication.getSeriesNumber());
		assertEquals("usgsSeriesLetter", mpPublication.getChapter());
		assertEquals("finalTitle", mpPublication.getTitle());
		assertEquals("abstractText", mpPublication.getDocAbstract());
		assertEquals("English", mpPublication.getLanguage());
		assertEquals("nonUSGSPublisher", mpPublication.getPublisher());
		assertNull(mpPublication.getPublisherLocation());
		assertEquals("digitalObjectIdentifier", mpPublication.getDoi());
		assertEquals("cooperators", mpPublication.getCollaboration());
		assertEquals("citation", mpPublication.getUsgsCitation());
		assertEquals("physicalDescription", mpPublication.getProductDescription());
		assertEquals("pageRange", mpPublication.getStartPage());
		assertEquals("ipNumber", mpPublication.getIpdsId());
		assertEquals("task", mpPublication.getIpdsReviewProcessState());
		assertEquals("journalTitle", mpPublication.getLargerWorkTitle());
		assertEquals(String.valueOf(LocalDate.now().getYear()), mpPublication.getPublicationYear());
		assertEquals("volume", mpPublication.getVolume());
		assertEquals("issue", mpPublication.getIssue());
		assertEquals("editionNumber", mpPublication.getEdition());
		assertEquals("Madison PSC", mpPublication.getPublishingServiceCenter().getText());
	}

	@Test
	public void buildObjectPropertiesTest3() {
		MpPublication mpPublication = sippConversionService
				.buildObjectProperties(InformationProductHelper.TEST_THREE, 987654321);
		assertEquals(987654321, mpPublication.getId().intValue());
		assertEquals("indexID", mpPublication.getIndexId());
		assertEquals(PublicationTypeHelper.TWO, mpPublication.getPublicationType());
		assertEquals(PublicationSubtypeHelper.TEN, mpPublication.getPublicationSubtype());
		assertEquals("usgsSeriesNumber", mpPublication.getSeriesNumber());
		assertEquals("usgsSeriesLetter", mpPublication.getChapter());
		assertEquals("finalTitle", mpPublication.getTitle());
		assertEquals("abstractText", mpPublication.getDocAbstract());
		assertEquals("English", mpPublication.getLanguage());
		assertEquals("nonUSGSPublisher", mpPublication.getPublisher());
		assertNull(mpPublication.getPublisherLocation());
		assertEquals("digitalObjectIdentifier", mpPublication.getDoi());
		assertEquals("cooperators", mpPublication.getCollaboration());
		assertEquals("citation", mpPublication.getUsgsCitation());
		assertEquals("physicalDescription", mpPublication.getProductDescription());
		assertEquals("pageRange", mpPublication.getStartPage());
		assertEquals("ipNumber", mpPublication.getIpdsId());
		assertEquals("task", mpPublication.getIpdsReviewProcessState());
		assertNull(mpPublication.getLargerWorkTitle());
		assertEquals(String.valueOf(LocalDate.now().getYear()), mpPublication.getPublicationYear());
		assertEquals("volume", mpPublication.getVolume());
		assertEquals("issue", mpPublication.getIssue());
		assertEquals("editionNumber", mpPublication.getEdition());
		assertEquals("Madison PSC", mpPublication.getPublishingServiceCenter().getText());
	}

	@Test
	public void buildOutsideContributorTest() {
		OutsideContributor contributor = sippConversionService.buildOutsideContributor(SippContributorHelper.EXISTING_OUTSIDE_CONTRIBUTOR);
		OutsideContributorHelper.assertJaneMDoe(contributor);

		contributor = sippConversionService.buildOutsideContributor(SippContributorHelper.NEW_OUTSIDE_CONTRIBUTOR_NO_AFFILIATION);
		OutsideContributorHelper.assertJaneQODoul(contributor);
	}

	@Test
	public void buildPublicationContributorTest() {
		MpPublicationContributor mpPublicationContributor = sippConversionService.buildPublicationContributor(SippContributorHelper.EXISTING_OUTSIDE_CONTRIBUTOR);
		assertNotNull(mpPublicationContributor);

		assertTrue(mpPublicationContributor.getContributor() instanceof OutsideContributor);
		OutsideContributorHelper.assertJaneMDoe((OutsideContributor) mpPublicationContributor.getContributor());

		assertNotNull( mpPublicationContributor.getContributorType());
		assertEquals(ContributorType.AUTHORS, mpPublicationContributor.getContributorType().getId());

		assertEquals(1, mpPublicationContributor.getRank().intValue());
	}

	@Test
	public void buildPublicationContributorsTest() {
		Author author1 = new Author();
		author1.setAuthorNameText("One, Author");
		author1.setContributorRole("1");
		author1.setRank("5");
		Author author2 = new Author();
		author2.setAuthorNameText("Two, Author");
		author2.setContributorRole("1");
		author2.setRank("5");

		Author editor1 = new Author();
		editor1.setAuthorNameText("One, Editor");
		editor1.setContributorRole("2");
		editor1.setRank("6");
		Author editor2 = new Author();
		editor2.setAuthorNameText("Two, Editor");
		editor2.setContributorRole("2");
		editor2.setRank("6");

		InformationProduct informationProduct = new InformationProduct();
		informationProduct.setAuthors(List.of(editor1, author2, author1, editor2));

		Collection<PublicationContributor<?>> contributors = sippConversionService.buildPublicationContributors(informationProduct);

		assertEquals(4, contributors.size());

		PublicationContributor<?>[] contributorList = contributors.toArray(new PublicationContributor<?>[4]);

		assertTrue(contributorList[0].getContributor() instanceof PersonContributor<?>);
		PersonContributor<?> zero = (PersonContributor<?>) contributorList[0].getContributor();
		assertEquals("Two", zero.getFamily());
		assertEquals("Author", zero.getGiven());
		assertEquals(ContributorType.AUTHORS, contributorList[0].getContributorType().getId());
		assertEquals(1, contributorList[0].getRank().intValue());

		assertTrue(contributorList[1].getContributor() instanceof PersonContributor<?>);
		PersonContributor<?> one = (PersonContributor<?>) contributorList[1].getContributor();
		assertEquals("One", one.getFamily());
		assertEquals("Author", one.getGiven());
		assertEquals(ContributorType.AUTHORS, contributorList[1].getContributorType().getId());
		assertEquals(2, contributorList[1].getRank().intValue());

		assertTrue(contributorList[2].getContributor() instanceof PersonContributor<?>);
		PersonContributor<?> two = (PersonContributor<?>) contributorList[2].getContributor();
		assertEquals("One", two.getFamily());
		assertEquals("Editor", two.getGiven());
		assertEquals(ContributorType.EDITORS, contributorList[2].getContributorType().getId());
		assertEquals(1, contributorList[2].getRank().intValue());

		assertTrue(contributorList[3].getContributor() instanceof PersonContributor<?>);
		PersonContributor<?> three = (PersonContributor<?>) contributorList[3].getContributor();
		assertEquals("Two", three.getFamily());
		assertEquals("Editor", three.getGiven());
		assertEquals(ContributorType.EDITORS, contributorList[3].getContributorType().getId());
		assertEquals(2, contributorList[3].getRank().intValue());
	}

	@Test
	public void buildPublicationCostCentersTest() {
		assertNull(sippConversionService.buildPublicationCostCenters(null));

		Collection<PublicationCostCenter<?>> costCenters = sippConversionService.buildPublicationCostCenters("PSC");
		assertEquals(1, costCenters.size());
		assertEquals("PSC", costCenters.toArray(new PublicationCostCenter[1])[0].getCostCenter().getText());
	}

	@Test
	public void buildPublicationLinksTest() {
		assertNull(sippConversionService.buildPublicationLinks(null));

		Collection<PublicationLink<?>> links = sippConversionService.buildPublicationLinks("https://water.usgs.gov");
		assertEquals(1, links.size());
		PublicationLink<?> link = links.toArray(new PublicationLink[1])[0];
		assertEquals("https://water.usgs.gov", link.getUrl());
		assertEquals(LinkType.INDEX_PAGE, link.getLinkType().getId());
	}

	@Test
	public void buildPublishingServiceCenterTest() {
		assertNull(sippConversionService.buildPublishingServiceCenter(null));

		PublishingServiceCenter publishingServiceCenter = sippConversionService.buildPublishingServiceCenter("PSC");
		assertNotNull(publishingServiceCenter);
		assertEquals("PSC", publishingServiceCenter.getText());
	}

	@Test
	public void buildRankTest() {
		assertEquals(1, sippConversionService.buildRank(null).intValue());

		assertEquals(1, sippConversionService.buildRank("1").intValue());

		assertEquals(13, sippConversionService.buildRank("13").intValue());
	}

	@Test
	public void buildSeriesTitleTest() {
		assertNull(sippConversionService.buildSeriesTitle(null, null));
		assertNull(sippConversionService.buildSeriesTitle(PublicationSubtypeHelper.TEN, null));
		assertNull(sippConversionService.buildSeriesTitle(null, "abc"));

		PublicationSeries publicationSeries = sippConversionService.buildSeriesTitle(PublicationSubtypeHelper.TEN, "def");
		assertEquals(PublicationSubtypeHelper.TEN, publicationSeries.getPublicationSubtype());
		assertEquals("def", publicationSeries.getText());
	}

	@Test
	public void buildTitleTest() {
		assertNull(sippConversionService.buildTitle(null, null));
		assertEquals("abc", sippConversionService.buildTitle("abc", "def"));
		assertEquals("def", sippConversionService.buildTitle(null, "def"));
	}

	@Test
	public void buildUsgsContributorTest() {
		UsgsContributor contributor = sippConversionService.buildUsgsContributor(SippContributorHelper.USGS_CONTRIBUTOR);
		UsgsContributorHelper.assertJaneDoe(contributor);

		contributor = sippConversionService.buildUsgsContributor(SippContributorHelper.USGS_CONTRIBUTOR_NO_COST_CENTER);
		UsgsContributorHelper.assertJaneNDoe(contributor);
	}

	@Test
	public void cleanseUsgsSeriesNumberTest() {
		assertNull(sippConversionService.cleanseUsgsSeriesNumber(null));
		assertNull(sippConversionService.cleanseUsgsSeriesNumber("."));
		assertEquals("123.", sippConversionService.cleanseUsgsSeriesNumber("123."));
	}

	@Test
	public void fixRanksTest() {
		List<MpPublicationContributor> contributors = new ArrayList<>();
		MpPublicationContributor contributorA = new MpPublicationContributor();
		contributorA.setId(1);
		contributorA.setRank(1);
		contributors.add(contributorA);
		MpPublicationContributor contributorB = new MpPublicationContributor();
		contributorB.setId(2);
		contributorB.setRank(2);
		contributors.add(contributorB);
		Collection<MpPublicationContributor> fixed = sippConversionService.fixRanks(contributors);
		assertEquals(2, fixed.size());
		for (Iterator<MpPublicationContributor> fixedIter = fixed.iterator(); fixedIter.hasNext();) {
			MpPublicationContributor test = fixedIter.next();
			if (1 == test.getId()) {
				assertEquals(1, test.getRank().intValue());
			} else {
				assertEquals(2, test.getRank().intValue());
			}
		}

		MpPublicationContributor contributorC = new MpPublicationContributor();
		contributorC.setId(3);
		contributorC.setRank(1);
		contributors.add(contributorC);
		fixed = sippConversionService.fixRanks(contributors);
		assertEquals(3, fixed.size());
		for (int i=0; i<fixed.size(); i++) {
			assertEquals(i+1, ((MpPublicationContributor) fixed.toArray()[i]).getRank().intValue());
		}
	}

	protected void assertTestOne(MpPublication mpPublication, Integer prodId) {
		assertTestOneBase(mpPublication, prodId);

		Collection<PublicationCostCenter<?>> costCenters = mpPublication.getCostCenters();
		assertEquals(1, costCenters.size());
		assertEquals("PSC", costCenters.toArray(new PublicationCostCenter[1])[0].getCostCenter().getText());

		Collection<PublicationLink<?>> links = mpPublication.getLinks();
		assertEquals(1, links.size());
		PublicationLink<?> link = links.toArray(new PublicationLink[1])[0];
		assertEquals("https://water.usgs.gov", link.getUrl());
		assertEquals(LinkType.INDEX_PAGE, link.getLinkType().getId());

		Collection<PublicationContributor<?>> contributors = mpPublication.getContributors();

		assertEquals(6, contributors.size());

		PublicationContributor<?>[] contributorList = contributors.toArray(new PublicationContributor<?>[6]);

		assertTrue(contributorList[0].getContributor() instanceof PersonContributor<?>);
		PersonContributor<?> zero = (PersonContributor<?>) contributorList[0].getContributor();
		assertEquals("5outerFamily", zero.getFamily());
		assertEquals("5outerGiven", zero.getGiven());
		assertEquals(ContributorType.AUTHORS, contributorList[0].getContributorType().getId());
		assertEquals(1, contributorList[0].getRank().intValue());

		assertTrue(contributorList[1].getContributor() instanceof PersonContributor<?>);
		PersonContributor<?> one = (PersonContributor<?>) contributorList[1].getContributor();
		assertEquals("1outerFamily", one.getFamily());
		assertEquals("1outerGiven", one.getGiven());
		assertEquals(ContributorType.AUTHORS, contributorList[1].getContributorType().getId());
		assertEquals(2, contributorList[1].getRank().intValue());

		assertTrue(contributorList[2].getContributor() instanceof PersonContributor<?>);
		PersonContributor<?> two = (PersonContributor<?>) contributorList[2].getContributor();
		assertEquals("101outerFamily", two.getFamily());
		assertEquals("101outerGiven", two.getGiven());
		assertEquals(ContributorType.AUTHORS, contributorList[2].getContributorType().getId());
		assertEquals(3, contributorList[2].getRank().intValue());

		assertTrue(contributorList[3].getContributor() instanceof PersonContributor<?>);
		PersonContributor<?> three = (PersonContributor<?>) contributorList[3].getContributor();
		assertEquals("4Family", three.getFamily());
		assertEquals("4Given", three.getGiven());
		assertEquals(ContributorType.EDITORS, contributorList[3].getContributorType().getId());
		assertEquals(1, contributorList[3].getRank().intValue());

		assertTrue(contributorList[4].getContributor() instanceof PersonContributor<?>);
		PersonContributor<?> four = (PersonContributor<?>) contributorList[4].getContributor();
		assertEquals("104Family", four.getFamily());
		assertEquals("104Given", four.getGiven());
		assertEquals(ContributorType.EDITORS, contributorList[4].getContributorType().getId());
		assertEquals(2, contributorList[4].getRank().intValue());

		assertTrue(contributorList[5].getContributor() instanceof PersonContributor<?>);
		PersonContributor<?> five = (PersonContributor<?>) contributorList[5].getContributor();
		assertEquals("6outerFamily", five.getFamily());
		assertEquals("6outerGiven", five.getGiven());
		assertEquals(ContributorType.EDITORS, contributorList[5].getContributorType().getId());
		assertEquals(3, contributorList[5].getRank().intValue());

		assertEquals("productSummary\n\tnote1|note2|" , mpPublication.getNotes());

	}

	protected void assertTestOneBase(MpPublication mpPublication, Integer prodId) {
		if (null == prodId) {
			assertNull(mpPublication.getId());
		} else {
			assertEquals(prodId, mpPublication.getId());
		}
		assertEquals("indexID", mpPublication.getIndexId());
		assertEquals(PublicationTypeHelper.FOUR, mpPublication.getPublicationType());
		assertEquals(PublicationSubtypeHelper.THIRTEEN, mpPublication.getPublicationSubtype());
		assertEquals("usgsSeriesNumber", mpPublication.getSeriesNumber());
		assertEquals("usgsSeriesLetter", mpPublication.getChapter());
		assertEquals("finalTitle", mpPublication.getTitle());
		assertEquals("abstractText", mpPublication.getDocAbstract());
		assertEquals("English", mpPublication.getLanguage());
		assertEquals("U.S. Geological Survey", mpPublication.getPublisher());
		assertEquals("Reston VA", mpPublication.getPublisherLocation());
		assertEquals("digitalObjectIdentifier", mpPublication.getDoi());
		assertEquals("cooperators", mpPublication.getCollaboration());
		assertEquals("citation", mpPublication.getUsgsCitation());
		assertEquals("physicalDescription", mpPublication.getProductDescription());
		assertEquals("pageRange", mpPublication.getStartPage());
		assertEquals("ipNumber", mpPublication.getIpdsId());
		assertEquals("task", mpPublication.getIpdsReviewProcessState());
		assertEquals("journalTitle", mpPublication.getLargerWorkTitle());
		assertEquals(String.valueOf(LocalDate.now().getYear()), mpPublication.getPublicationYear());
		assertEquals("volume", mpPublication.getVolume());
		assertEquals("issue", mpPublication.getIssue());
		assertEquals("editionNumber", mpPublication.getEdition());
		assertEquals("Madison PSC", mpPublication.getPublishingServiceCenter().getText());
	}

}
