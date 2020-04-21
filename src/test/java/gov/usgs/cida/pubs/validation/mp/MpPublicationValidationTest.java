package gov.usgs.cida.pubs.validation.mp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import javax.validation.Validator;
import javax.validation.groups.Default;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.PublicationSeriesDao;
import gov.usgs.cida.pubs.dao.PublicationSubtypeDao;
import gov.usgs.cida.pubs.dao.PublicationTypeDao;
import gov.usgs.cida.pubs.dao.PublishingServiceCenterDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDao;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.PublishingServiceCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;
import gov.usgs.cida.pubs.validation.ValidatorResult;
import gov.usgs.cida.pubs.validation.constraint.DeleteChecks;
import gov.usgs.cida.pubs.validation.constraint.PublishChecks;
import gov.usgs.cida.pubs.validation.mp.unique.UniqueKeyValidatorForMpPublicationTest;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={LocalValidatorFactoryBean.class, PublicationType.class, PublicationSubtype.class,
			PublicationSeries.class, PublishingServiceCenter.class, MpPublication.class,
			Publication.class})
public class MpPublicationValidationTest extends BaseValidatorTest {

	public static final String NOT_NULL_DISPLAY_TO_PUBLIC_DATE = new ValidatorResult("displayToPublicDate", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();
	public static final String NOT_NULL_INDEX_ID = new ValidatorResult("indexId", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();
	public static final String NOT_NULL_PUBLICATION_TYPE = new ValidatorResult("publicationType", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();
	public static final String NOT_NULL_TITLE = new ValidatorResult("title", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();
	public static final String INVALID_ADD_ONLINE_LENGTH = new ValidatorResult("additionalOnlineFiles", REGEX_MSG + "[YN]\"", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_CITY_LENGTH = new ValidatorResult("city", LENGTH_0_TO_XXX_MSG + "500", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_COLLABORATION_LENGTH = new ValidatorResult("collaboration", LENGTH_0_TO_XXX_MSG + "4000", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_COMMENTS_LENGTH = new ValidatorResult("comments", LENGTH_0_TO_XXX_MSG + "4000", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_CONFERENCE_DATE_LENGTH = new ValidatorResult("conferenceDate", LENGTH_0_TO_XXX_MSG + "255", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_CONFERENCE_LOCATION_LENGTH = new ValidatorResult("conferenceLocation", LENGTH_0_TO_XXX_MSG + "255", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_CONFERENCE_TITLE_LENGTH = new ValidatorResult("conferenceTitle", LENGTH_0_TO_XXX_MSG + "2000", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_CONTACT_LENGTH = new ValidatorResult("contact", LENGTH_0_TO_XXX_MSG + "4000", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_COUNTY_LENGTH = new ValidatorResult("county", LENGTH_0_TO_XXX_MSG + "500", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_COUNTRY_LENGTH = new ValidatorResult("country", LENGTH_0_TO_XXX_MSG + "500", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_DATUM_LENGTH = new ValidatorResult("datum", LENGTH_0_TO_XXX_MSG + "500", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_DOI_LENGTH = new ValidatorResult("doi", LENGTH_0_TO_XXX_MSG + "2000", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_EDITION_LENGTH = new ValidatorResult("edition", LENGTH_0_TO_XXX_MSG + "4000", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_END_PAGE_LENGTH = new ValidatorResult("endPage", LENGTH_0_TO_XXX_MSG + "20", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_INDEX_ID_LENGTH = new ValidatorResult("indexId", LENGTH_1_TO_XXX_MSG + "100", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_IPDS_ID_LENGTH = new ValidatorResult("ipdsId",LENGTH_0_TO_XXX_MSG + "15", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_IPDS_INTERNAL_ID_LENGTH = new ValidatorResult("ipdsInternalId", REGEX_MSG + "^ *\\d*$\"", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_ISBN_LENGTH = new ValidatorResult("isbn", LENGTH_0_TO_XXX_MSG + "30", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_ISSN_LENGTH = new ValidatorResult("issn", LENGTH_0_TO_XXX_MSG + "20", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_ISSUE_LENGTH = new ValidatorResult("issue", LENGTH_0_TO_XXX_MSG + "20", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_LANGUAGE_LENGTH = new ValidatorResult("language", LENGTH_0_TO_XXX_MSG + "70", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_LARGER_WORK_TITLE_LENGTH = new ValidatorResult("largerWorkTitle", LENGTH_0_TO_XXX_MSG + "2000", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_NUMBER_PAGES_LENGTH = new ValidatorResult("numberOfPages", REGEX_MSG + "^ *\\d*$\"", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_ONLINE_ONLY_LENGTH = new ValidatorResult("onlineOnly", REGEX_MSG + "[YN]\"", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_OTHER_GEOSPATIAL_LENGTH = new ValidatorResult("otherGeospatial", LENGTH_0_TO_XXX_MSG + "500", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_PRODUCT_DESCRIPTION_LENGTH = new ValidatorResult("productDescription", LENGTH_0_TO_XXX_MSG + "4000", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_PROJECTION_LENGTH = new ValidatorResult("projection", LENGTH_0_TO_XXX_MSG + "500", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_PUBLISHER_LENGTH = new ValidatorResult("publisher", LENGTH_0_TO_XXX_MSG + "255", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_PUBLISHER_LOCATION_LENGTH = new ValidatorResult("publisherLocation", LENGTH_0_TO_XXX_MSG + "255", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_SCALE_LENGTH = new ValidatorResult("scale", REGEX_MSG + "^ *\\d*$\"", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_SERIES_NUMER_LENGTH = new ValidatorResult("seriesNumber", LENGTH_0_TO_XXX_MSG + "100", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_START_PAGE_LENGTH = new ValidatorResult("startPage", LENGTH_0_TO_XXX_MSG + "20", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_STATE_LENGTH = new ValidatorResult("state", LENGTH_0_TO_XXX_MSG + "500", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_SUBSERIES_TITLE_LENGTH = new ValidatorResult("subseriesTitle", LENGTH_0_TO_XXX_MSG + "255", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_DISPLAY_TITLE_LENGTH = new ValidatorResult("displayTitle", LENGTH_1_TO_XXX_MSG + "2000", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_TITLE_LENGTH = new ValidatorResult("title", LENGTH_1_TO_XXX_MSG + "2000", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_USGS_CITATION_LENGTH = new ValidatorResult("usgsCitation", LENGTH_0_TO_XXX_MSG + "4000", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_VOLUME_LENGTH = new ValidatorResult("volume", LENGTH_0_TO_XXX_MSG + "50", SeverityLevel.FATAL, null).toString();

	public static final String INVALID_PUBLICATION_TYPE = new ValidatorResult("type", NO_PARENT_MSG, SeverityLevel.FATAL, null).toString();
	public static final String INVALID_PUBLICATION_SUBTYPE = new ValidatorResult("genre", NO_PARENT_MSG, SeverityLevel.FATAL, null).toString();
	public static final String INVALID_SERIES_TITLE = new ValidatorResult("seriesTitle", NO_PARENT_MSG, SeverityLevel.FATAL, null).toString();
	public static final String INVALID_LARGER_WORK_TYPE = new ValidatorResult("largerWorkType", NO_PARENT_MSG, SeverityLevel.FATAL, null).toString();
	public static final String INVALID_IS_PART_OF = new ValidatorResult("isPartOf", NO_PARENT_MSG, SeverityLevel.FATAL, null).toString();
	public static final String INVALID_SUPERCEDED_BY = new ValidatorResult("supersededBy", NO_PARENT_MSG, SeverityLevel.FATAL, null).toString();
	public static final String INVALID_PUBLISHING_SERVICE_CENTER = new ValidatorResult("publishingServiceCenter", NO_PARENT_MSG, SeverityLevel.FATAL, null).toString();

	public static final String DUPLICATE_IPDS_ID = new ValidatorResult("ipdsId", "ipdsId IPDS-1 is already in use on Prod Id 1.", SeverityLevel.FATAL, null).toString();
	public static final String DUPLICATE_INDEX_ID = new ValidatorResult("indexId", "indexId 123 is already in use on Prod Id 1.", SeverityLevel.FATAL, null).toString();

	public static final String NOT_NULL_CONTRIBUTOR = new ValidatorResult("contributors[0].contributor", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();
	public static final String INVALID_CONTRIBUTOR = new ValidatorResult("contributors[0].contributor", NO_PARENT_MSG, SeverityLevel.FATAL, null).toString();
	public static final String INVALID_CONTRIBUTOR_TYPE = new ValidatorResult("contributors[0].contributorType", NO_PARENT_MSG, SeverityLevel.FATAL, null).toString();

	public static final String NOT_NULL_COST_CENTER = new ValidatorResult("costCenters[0].costCenter", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();
	public static final String INVALID_COST_CENTER = new ValidatorResult("costCenters[0].costCenter", NO_PARENT_MSG, SeverityLevel.FATAL, null).toString();

	public static final String NOT_NULL_LINK_URL = new ValidatorResult("links[0].url", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();
	public static final String NOT_NULL_LINK_TYPE = new ValidatorResult("links[0].linkType", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();
	public static final String INVALID_LINK_TYPE = new ValidatorResult("links[0].linkType", NO_PARENT_MSG, SeverityLevel.FATAL, null).toString();


	public static final String USGS_NUMBERED_SERIES_NEED_SERIES_TITLE = new ValidatorResult("", "USGS Numbered Series must have a Series Title.", SeverityLevel.FATAL, null).toString();

	@Autowired
	public Validator validator;

	protected MpPublication mpPub;
	protected PublicationType pubType;
	protected PublicationSubtype pubSubtype;
	protected PublicationSeries pubSeries;
	protected PublicationType largerWorkType;
	protected Publication<?> po;
	protected Publication<?> sb;
	protected PublishingServiceCenter psc;

	@MockBean(name="publicationTypeDao")
	protected PublicationTypeDao publicationTypeDao;
	@MockBean(name="publicationSubtypeDao")
	protected PublicationSubtypeDao publicationSubtypeDao;
	@MockBean(name="publicationSeriesDao")
	protected PublicationSeriesDao publicationSeriesDao;
	@MockBean(name="publicationDao")
	protected PublicationDao publicationDao;
	@MockBean(name="mpPublicationDao")
	protected MpPublicationDao mpPublicationDao;
	@MockBean(name="publishingServiceCenterDao")
	protected PublishingServiceCenterDao publishingServiceCenterDao;

	@BeforeEach
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		buildContext();
		pubType = new PublicationType();
		pubType.setId(1);
		largerWorkType = new PublicationType();
		largerWorkType.setId(1);

		pubSubtype = new PublicationSubtype();
		pubSubtype.setId(5);

		pubSeries = new PublicationSeries();
		pubSeries.setId(1);

		psc = new PublishingServiceCenter();
		psc.setId(1);

		mpPub = new MpPublication();
		mpPub.setPublicationType(pubType);
		mpPub.setIndexId("123");
		mpPub.setTitle("abc");

		po = new MpPublication();
		po.setId(1);
		sb = new MpPublication();
		sb.setId(1);

		reset(publicationTypeDao, publicationSubtypeDao, publicationSeriesDao, publicationDao, publishingServiceCenterDao, mpPublicationDao);

		when(publicationDao.getById(any(Integer.class))).thenReturn(null);
		when(publicationDao.validateByMap(anyMap())).thenReturn(UniqueKeyValidatorForMpPublicationTest.buildList());
		when(publicationTypeDao.getById(any(Integer.class))).thenReturn(null);
		when(publicationSubtypeDao.getById(any(Integer.class))).thenReturn(null);
		when(publicationSeriesDao.getById(any(Integer.class))).thenReturn(null);
		when(publishingServiceCenterDao.getById(any(Integer.class))).thenReturn(null);
	}


	@Test
	public void wiringTest() {
		mpPub.setPublicationSubtype(pubSubtype);
		mpPub.setLargerWorkType(largerWorkType);
		mpPub.setIsPartOf(po);
		mpPub.setSupersededBy(sb);
		mpPub.setPublishingServiceCenter(psc);
		mpPub.setIpdsId("IPDS-1");
		mpPub.setSeriesTitle(pubSeries);
		mpPub.setContributors(getInvalidPublicationContributors());
		mpPub.setCostCenters(getInvalidPublicationCostCenter());
		mpPub.setLinks(getInvalidPublicationLink());

		mpPub.setValidationErrors(validator.validate(mpPub));
		assertFalse(mpPub.isValid());
		assertEquals(17, mpPub.getValidationErrors().getValidationErrors().size());
		assertValidationResults(mpPub.getValidationErrors().getValidationErrors(),
				//From ParentExistsValidatorForMpPublication
				INVALID_PUBLICATION_TYPE,
				INVALID_PUBLICATION_SUBTYPE,
				INVALID_SERIES_TITLE,
				INVALID_LARGER_WORK_TYPE,
				INVALID_IS_PART_OF,
				INVALID_SUPERCEDED_BY,
				INVALID_PUBLISHING_SERVICE_CENTER,
				//From UniqueKeyValidatorForMpPublication
				DUPLICATE_IPDS_ID,
				DUPLICATE_INDEX_ID,
				//From PublicationContributor
				NOT_NULL_CONTRIBUTOR,
				INVALID_CONTRIBUTOR,
				INVALID_CONTRIBUTOR_TYPE,
				//From PublicationCostCenter
				NOT_NULL_COST_CENTER,
				INVALID_COST_CENTER,
				//From PublicationLink
				NOT_NULL_LINK_URL,
				NOT_NULL_LINK_TYPE,
				INVALID_LINK_TYPE
				);
	}

	@Test
	public void notNullTest() {
		mpPub.setPublicationType(null);
		mpPub.setIndexId(null);
		mpPub.setTitle(null);

		mpPub.setValidationErrors(validator.validate(mpPub, Default.class, PublishChecks.class));
		assertFalse(mpPub.isValid());
		assertEquals(4, mpPub.getValidationErrors().getValidationErrors().size());
		assertValidationResults(mpPub.getValidationErrors().getValidationErrors(),
				//From Publication
				NOT_NULL_DISPLAY_TO_PUBLIC_DATE,
				NOT_NULL_INDEX_ID,
				NOT_NULL_PUBLICATION_TYPE,
				NOT_NULL_TITLE
				);
	}

	@Test
	public void crossPropertyTest() {
		when(publicationDao.validateByMap(anyMap())).thenReturn(new ArrayList<>());
		when(publicationTypeDao.getById(any(Integer.class))).thenReturn(new PublicationType());
		when(publicationSubtypeDao.getById(any(Integer.class))).thenReturn(new PublicationSubtype());
		mpPub.setPublicationSubtype(pubSubtype);
		mpPub.setSeriesTitle(null);

		mpPub.setValidationErrors(validator.validate(mpPub));
		assertFalse(mpPub.isValid());
		assertEquals(1, mpPub.getValidationErrors().getValidationErrors().size());
		assertValidationResults(mpPub.getValidationErrors().getValidationErrors(),
				//From CrossPropertyValidatorForMpPublication
				USGS_NUMBERED_SERIES_NEED_SERIES_TITLE
				);
	}

	@Test
	public void minLengthTest() {
		when(publicationDao.validateByMap(anyMap())).thenReturn(new ArrayList<>());
		when(publicationTypeDao.getById(any(Integer.class))).thenReturn(new PublicationType());
		mpPub.setIndexId("");

		mpPub.setValidationErrors(validator.validate(mpPub));
		assertFalse(mpPub.isValid());
		assertEquals(1, mpPub.getValidationErrors().getValidationErrors().size());
		assertValidationResults(mpPub.getValidationErrors().getValidationErrors(),
				//From Publication
				INVALID_INDEX_ID_LENGTH
				);

		mpPub.setIndexId("a");

		mpPub.setValidationErrors(validator.validate(mpPub));
		assertTrue(mpPub.isValid());
	}

	@Test
	public void maxLengthAndRegexTest() {
		when(publicationDao.validateByMap(anyMap())).thenReturn(new ArrayList<>());
		when(publicationTypeDao.getById(any(Integer.class))).thenReturn(new PublicationType());

		mpPub.setIndexId(StringUtils.repeat('X', 101));
		mpPub.setIpdsId(StringUtils.repeat('X', 16));
		mpPub.setSeriesNumber(StringUtils.repeat('X', 101));
		mpPub.setSubseriesTitle(StringUtils.repeat('X', 256));
		mpPub.setChapter(StringUtils.repeat('X', 101));
		mpPub.setSubchapterNumber(StringUtils.repeat('X', 101));
                mpPub.setDisplayTitle(StringUtils.repeat('X', 2001));
		mpPub.setTitle(StringUtils.repeat('X', 2001));
		mpPub.setLargerWorkTitle(StringUtils.repeat('X', 2001));
		mpPub.setConferenceTitle(StringUtils.repeat('X', 2001));
		mpPub.setConferenceDate(StringUtils.repeat('X', 256));
		mpPub.setConferenceLocation(StringUtils.repeat('X', 256));
		mpPub.setLanguage(StringUtils.repeat('X', 71));
		mpPub.setPublisher(StringUtils.repeat('X', 256));
		mpPub.setPublisherLocation(StringUtils.repeat('X', 256));
		mpPub.setDoi(StringUtils.repeat('X', 2001));
		mpPub.setIssn(StringUtils.repeat('X', 21));
		mpPub.setIsbn(StringUtils.repeat('X', 31));
		mpPub.setCollaboration(StringUtils.repeat('X', 4001));
		mpPub.setUsgsCitation(StringUtils.repeat('X', 4001));
		mpPub.setProductDescription(StringUtils.repeat('X', 4001));
		mpPub.setStartPage(StringUtils.repeat('X', 21));
		mpPub.setEndPage(StringUtils.repeat('X', 21));
		mpPub.setNumberOfPages(StringUtils.repeat('X', 21));
		mpPub.setOnlineOnly("Q");
		mpPub.setAdditionalOnlineFiles("Q");
		mpPub.setIpdsInternalId(StringUtils.repeat('X', 21));
		mpPub.setScale(StringUtils.repeat('X', 21));
		mpPub.setProjection(StringUtils.repeat('X', 501));
		mpPub.setDatum(StringUtils.repeat('X', 501));
		mpPub.setCountry(StringUtils.repeat('X', 501));
		mpPub.setState(StringUtils.repeat('X', 501));
		mpPub.setCounty(StringUtils.repeat('X', 501));
		mpPub.setCity(StringUtils.repeat('X', 501));
		mpPub.setOtherGeospatial(StringUtils.repeat('X', 501));
		mpPub.setVolume(StringUtils.repeat('X', 51));
		mpPub.setIssue(StringUtils.repeat('X', 21));
		mpPub.setEdition(StringUtils.repeat('X', 4001));
		mpPub.setComments(StringUtils.repeat('X', 4001));
		mpPub.setContact(StringUtils.repeat('X', 4001));

		mpPub.setValidationErrors(validator.validate(mpPub));
		assertFalse(mpPub.isValid());
		assertEquals(38, mpPub.getValidationErrors().getValidationErrors().size());
		assertValidationResults(mpPub.getValidationErrors().getValidationErrors(),
				//From Publication
				INVALID_ADD_ONLINE_LENGTH,
				INVALID_CITY_LENGTH,
				INVALID_COLLABORATION_LENGTH,
				INVALID_COMMENTS_LENGTH,
				INVALID_CONFERENCE_DATE_LENGTH,
				INVALID_CONFERENCE_LOCATION_LENGTH,
				INVALID_CONFERENCE_TITLE_LENGTH,
				INVALID_CONTACT_LENGTH,
				INVALID_COUNTY_LENGTH,
				INVALID_COUNTRY_LENGTH,
				INVALID_DATUM_LENGTH,
				INVALID_DOI_LENGTH,
				INVALID_EDITION_LENGTH,
				INVALID_END_PAGE_LENGTH,
				INVALID_INDEX_ID_LENGTH,
				INVALID_IPDS_ID_LENGTH,
				INVALID_IPDS_INTERNAL_ID_LENGTH,
				INVALID_ISBN_LENGTH,
				INVALID_ISSN_LENGTH,
				INVALID_ISSUE_LENGTH,
				INVALID_LANGUAGE_LENGTH,
				INVALID_LARGER_WORK_TITLE_LENGTH,
				INVALID_NUMBER_PAGES_LENGTH,
				INVALID_ONLINE_ONLY_LENGTH,
				INVALID_OTHER_GEOSPATIAL_LENGTH,
				INVALID_PRODUCT_DESCRIPTION_LENGTH,
				INVALID_PROJECTION_LENGTH,
				INVALID_PUBLISHER_LENGTH,
				INVALID_PUBLISHER_LOCATION_LENGTH,
				INVALID_SCALE_LENGTH,
				INVALID_SERIES_NUMER_LENGTH,
				INVALID_START_PAGE_LENGTH,
				INVALID_STATE_LENGTH,
				INVALID_SUBSERIES_TITLE_LENGTH,
                                INVALID_DISPLAY_TITLE_LENGTH,
				INVALID_TITLE_LENGTH,
				INVALID_USGS_CITATION_LENGTH,
				INVALID_VOLUME_LENGTH
				);
	}

	@Test
	public void maxLengthAndRegexGoodTest() {
		when(publicationDao.validateByMap(anyMap())).thenReturn(new ArrayList<>());
		when(publicationTypeDao.getById(any(Integer.class))).thenReturn(new PublicationType());

		mpPub.setIndexId(StringUtils.repeat('X', 100));
		mpPub.setIpdsId(StringUtils.repeat('X', 15));
		mpPub.setSeriesNumber(StringUtils.repeat('X', 100));
		mpPub.setSubseriesTitle(StringUtils.repeat('X', 255));
		mpPub.setChapter(StringUtils.repeat('X', 100));
		mpPub.setSubchapterNumber(StringUtils.repeat('X', 100));
                mpPub.setDisplayTitle(StringUtils.repeat('X', 2000));
		mpPub.setTitle(StringUtils.repeat('X', 2000));
		mpPub.setLargerWorkTitle(StringUtils.repeat('X', 2000));
		mpPub.setConferenceTitle(StringUtils.repeat('X', 2000));
		mpPub.setConferenceDate(StringUtils.repeat('X', 255));
		mpPub.setConferenceLocation(StringUtils.repeat('X', 255));
		mpPub.setLanguage(StringUtils.repeat('X', 70));
		mpPub.setPublisher(StringUtils.repeat('X', 255));
		mpPub.setPublisherLocation(StringUtils.repeat('X', 255));
		mpPub.setDoi(StringUtils.repeat('X', 2000));
		mpPub.setIssn(StringUtils.repeat('X', 20));
		mpPub.setIsbn(StringUtils.repeat('X', 30));
		mpPub.setCollaboration(StringUtils.repeat('X', 4000));
		mpPub.setUsgsCitation(StringUtils.repeat('X', 4000));
		mpPub.setProductDescription(StringUtils.repeat('X', 4000));
		mpPub.setStartPage(StringUtils.repeat('X', 20));
		mpPub.setEndPage(StringUtils.repeat('X', 20));
		mpPub.setNumberOfPages(" 20");
		mpPub.setOnlineOnly("Y");
		mpPub.setAdditionalOnlineFiles("Y");
		mpPub.setIpdsInternalId(" 20");
		mpPub.setScale(" 20");
		mpPub.setProjection(StringUtils.repeat('X', 500));
		mpPub.setDatum(StringUtils.repeat('X', 500));
		mpPub.setCountry(StringUtils.repeat('X', 500));
		mpPub.setState(StringUtils.repeat('X', 500));
		mpPub.setCounty(StringUtils.repeat('X', 500));
		mpPub.setCity(StringUtils.repeat('X', 500));
		mpPub.setOtherGeospatial(StringUtils.repeat('X', 500));
		mpPub.setVolume(StringUtils.repeat('X', 50));
		mpPub.setIssue(StringUtils.repeat('X', 20));
		mpPub.setEdition(StringUtils.repeat('X', 4000));
		mpPub.setComments(StringUtils.repeat('X', 4000));
		mpPub.setContact(StringUtils.repeat('X', 4000));

		mpPub.setValidationErrors(validator.validate(mpPub));
		assertTrue(mpPub.isValid());
	};

	@Test
	public void deleteTest() {
		when(publicationDao.validateByMap(anyMap())).thenReturn(new ArrayList<>());
		when(publicationTypeDao.getById(any(Integer.class))).thenReturn(new PublicationType());

		mpPub.setValidationErrors(validator.validate(mpPub, DeleteChecks.class));
		assertTrue(mpPub.isValid());
	}

	public static Collection<PublicationContributor<?>> getInvalidPublicationContributors() {
		Collection<PublicationContributor<?>> pcs = new ArrayList<>();
		PublicationContributor<?> pc = new MpPublicationContributor();
		pcs.add(pc);
		return pcs;
	}

	public static Collection<PublicationCostCenter<?>> getInvalidPublicationCostCenter() {
		Collection<PublicationCostCenter<?>> pccs = new ArrayList<>();
		PublicationCostCenter<?> pcc = new MpPublicationCostCenter();
		pccs.add(pcc);
		return pccs;
	}

	public static Collection<PublicationLink<?>> getInvalidPublicationLink() {
		Collection<PublicationLink<?>> pls = new ArrayList<>();
		PublicationLink<?> pl = new MpPublicationLink();
		pls.add(pl);
		return pls;
	}
}
