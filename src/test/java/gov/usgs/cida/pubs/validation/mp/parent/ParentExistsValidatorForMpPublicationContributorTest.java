package gov.usgs.cida.pubs.validation.mp.parent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.dao.intfc.IMpDao;
import gov.usgs.cida.pubs.dao.intfc.IMpPublicationDao;
import gov.usgs.cida.pubs.dao.intfc.IPublicationDao;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={LocalValidatorFactoryBean.class, MpPublicationContributor.class,
			Contributor.class, ContributorType.class, MpPublication.class})
public class ParentExistsValidatorForMpPublicationContributorTest extends BaseValidatorTest {

	protected ParentExistsValidatorForMpPublicationContributor validator;
	protected PublicationContributor<?> mpPubContributor;
	protected MpPublication mpPublication;
	protected Contributor<?> contributor;
	protected ContributorType contributorType;

	@MockBean(name="mpPublicationDao")
	protected IMpPublicationDao mpPublicationDao;
	@MockBean(name="publicationDao")
	protected IPublicationDao publicationDao;
	@MockBean(name="contributorDao")
	protected IDao<Contributor<?>> contributorDao;
	@MockBean(name="contributorTypeDao")
	protected IDao<ContributorType> contributorTypeDao;
	@MockBean(name="mpPublicationContributorDao")
	protected IMpDao<MpPublicationContributor> mpPublicationContributorDao;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		super.setUp();
		validator = new ParentExistsValidatorForMpPublicationContributor();
		mpPubContributor = new MpPublicationContributor();
		mpPublication = new MpPublication();
		contributor = new Contributor<>();
		contributorType = new ContributorType();

		reset(mpPublicationDao, contributorDao, contributorTypeDao);
	}

	@Test
	public void isValidNPETest() {
		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(mpPubContributor, null));

		assertFalse(validator.isValid(mpPubContributor, context));

		mpPubContributor.setContributor(contributor);
		assertFalse(validator.isValid(mpPubContributor, context));

		mpPubContributor.setContributorType(contributorType);
		assertFalse(validator.isValid(mpPubContributor, context));
	}

	@Test
	public void isValidTrueTest() {
		when(mpPublicationDao.getById(any(Integer.class))).thenReturn(new MpPublication());
		when(contributorDao.getById(any(Integer.class))).thenReturn(new Contributor<>());
		when(contributorTypeDao.getById(any(Integer.class))).thenReturn(new ContributorType());
		mpPubContributor.setContributor(contributor);
		mpPubContributor.setContributorType(contributorType);

		//works with all set
		mpPubContributor.setPublicationId(1);
		contributor.setId(1);
		contributorType.setId(1);
		assertTrue(validator.isValid(mpPubContributor, context));
		verify(mpPublicationDao).getById(any(Integer.class));
		verify(contributorDao).getById(any(Integer.class));
		verify(contributorTypeDao).getById(any(Integer.class));

		//works with contributor & contributorType set
		mpPubContributor.setPublicationId(null);
		contributor.setId(1);
		contributorType.setId(1);
		assertTrue(validator.isValid(mpPubContributor, context));
		verify(mpPublicationDao).getById(any(Integer.class));
		verify(contributorDao, times(2)).getById(any(Integer.class));
		verify(contributorTypeDao, times(2)).getById(any(Integer.class));
	}

	@Test
	public void isValidFalseTest() {
		when(mpPublicationDao.getById(any(Integer.class))).thenReturn(null);
		when(contributorDao.getById(any(Integer.class))).thenReturn(null);
		when(contributorTypeDao.getById(any(Integer.class))).thenReturn(null);
		mpPubContributor.setContributor(contributor);
		mpPubContributor.setContributorType(contributorType);

		//works with all set
		mpPubContributor.setPublicationId(1);
		contributor.setId(1);
		contributorType.setId(1);
		assertFalse(validator.isValid(mpPubContributor, context));
		verify(mpPublicationDao).getById(any(Integer.class));
		verify(contributorDao).getById(any(Integer.class));
		verify(contributorTypeDao).getById(any(Integer.class));

		//works with mpPubContributor set
		mpPubContributor.setPublicationId(11);
		contributor.setId("");
		contributorType.setId("");
		assertFalse(validator.isValid(mpPubContributor, context));
		verify(mpPublicationDao, times(2)).getById(any(Integer.class));
		verify(contributorDao).getById(any(Integer.class));
		verify(contributorTypeDao).getById(any(Integer.class));

		//works with contributor set
		mpPubContributor.setPublicationId(null);
		contributor.setId(11);
		contributorType.setId("");
		assertFalse(validator.isValid(mpPubContributor, context));
		verify(mpPublicationDao, times(2)).getById(any(Integer.class));
		verify(contributorDao, times(2)).getById(any(Integer.class));
		verify(contributorTypeDao).getById(any(Integer.class));

		//works with contributorType set
		mpPubContributor.setPublicationId(null);
		contributor.setId("");
		contributorType.setId(11);
		assertFalse(validator.isValid(mpPubContributor, context));
		verify(mpPublicationDao, times(2)).getById(any(Integer.class));
		verify(contributorDao, times(2)).getById(any(Integer.class));
		verify(contributorTypeDao, times(2)).getById(any(Integer.class));
	}

}
