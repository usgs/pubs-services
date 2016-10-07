package gov.usgs.cida.pubs.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.when;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.dao.intfc.IMpDao;
import gov.usgs.cida.pubs.dao.intfc.IMpPublicationDao;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.validation.mp.unique.UniqueKeyValidatorForMpPublicationContributorTest;

//The Dao mocking works because the getDao() methods are all static and JAVA/Spring don't redo them 
//for each reference. This does mean that we need to let Spring know that the context is now dirty...
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class PublicationContributorValidationTest extends BaseValidatorTest {
	@Autowired
	public Validator validator;

	public static final String DUPLICATE_CONTRIBUTOR = new ValidatorResult("", "1 is already in use on Prod Id 1.", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_PUBLICATION = new ValidatorResult("publicationId", NO_PARENT_MSG, SeverityLevel.FATAL, null).toString();
	public static final String INVALID_CONTRIBUTOR = new ValidatorResult("contributor", NO_PARENT_MSG, SeverityLevel.FATAL, null).toString();
	public static final String INVALID_CONTRIBUTOR_TYPE = new ValidatorResult("contributorType", NO_PARENT_MSG, SeverityLevel.FATAL, null).toString();
	public static final String NOT_NULL_CONTRIBUTOR = new ValidatorResult("contributor", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();

	@Mock
	protected IDao<Contributor<?>> contributorDao;
	@Mock
	protected IDao<ContributorType> contributorTypeDao;
	@Mock
	protected IMpDao<MpPublicationContributor> pubContributorDao;
	@Mock
	protected IMpPublicationDao pubDao;

	//Using OutsideContributor  because it works easier (all validations are the same via PersonContributor...)
	private OutsideContributor contributor;
	private ContributorType contributorType;
	private MpPublication pub;

	//Using MpPublicationContributor because it works easier (all validations are the same via PublicationContributor...)
	private MpPublicationContributor pubContributor;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		pubContributor = new MpPublicationContributor();
		contributor = new OutsideContributor();
		contributor.setContributorDao(contributorDao);
		contributor.setId(1);
		contributorType = new ContributorType();
		contributorType.setContributorTypeDao(contributorTypeDao);
		contributorType.setId(1);
		pub = new MpPublication();
		pub.setMpPublicationDao(pubDao);
		pub.setId(1);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void wiringTest() {
		when(pubContributorDao.getByMap(anyMap())).thenReturn(UniqueKeyValidatorForMpPublicationContributorTest.buildList());
		when(contributorDao.getById(any(Integer.class))).thenReturn(null);
		when(contributorTypeDao.getById(any(Integer.class))).thenReturn(null);
		when(pubDao.getById(any(Integer.class))).thenReturn(null);
		pubContributor.setContributorType(contributorType);
		pubContributor.setContributor(contributor);
		pubContributor.setPublicationId(1);

		pubContributor.setValidationErrors(validator.validate(pubContributor));
		assertFalse(pubContributor.getValidationErrors().isEmpty());
		assertEquals(4, pubContributor.getValidationErrors().getValidationErrors().size());
		assertValidationResults(pubContributor.getValidationErrors().getValidationErrors(),
				//From UniqueKeyValidatorForMpPublicationContributor
				DUPLICATE_CONTRIBUTOR,
				//From ParentExistsValidatorForMpPublicationContributor
				INVALID_PUBLICATION,
				INVALID_CONTRIBUTOR,
				INVALID_CONTRIBUTOR_TYPE
				);
	}

	@Test
	public void notNullTest() {
		pubContributor.setContributor(null);
		pubContributor.setValidationErrors(validator.validate(pubContributor));
		assertFalse(pubContributor.getValidationErrors().isEmpty());
		assertEquals(1, pubContributor.getValidationErrors().getValidationErrors().size());
		assertValidationResults(pubContributor.getValidationErrors().getValidationErrors(),
				//From PublicationContributor
				NOT_NULL_CONTRIBUTOR
				);
	}

}