package gov.usgs.cida.pubs.validation.mp.parent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import gov.usgs.cida.pubs.dao.ContributorDao;
import gov.usgs.cida.pubs.dao.ContributorTypeDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDao;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

//The Dao mocking works because the getDao() methods are all static and JAVA/Spring don't redo them 
//for each reference. This does mean that we nned to let Spring know that the context is now dirty...
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class ParentExistsValidatorForMpPublicationContributorTest extends BaseValidatorTest {

	protected ParentExistsValidatorForMpPublicationContributor validator;
	protected PublicationContributor<?> mpPubContributor;
	protected MpPublication mpPublication;
	protected Contributor<?> contributor;
	protected ContributorType contributorType;

	@Mock
	protected MpPublicationDao mpPublicationDao;
	@Mock
	protected ContributorDao contributorDao;
	@Mock
	protected ContributorTypeDao contributorTypeDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		validator = new ParentExistsValidatorForMpPublicationContributor();
		mpPubContributor = new MpPublicationContributor();
		mpPublication = new MpPublication();
		mpPublication.setMpPublicationDao(mpPublicationDao);
		contributor = new Contributor<>();
		contributor.setContributorDao(contributorDao);
		contributorType = new ContributorType();
		contributorType.setContributorTypeDao(contributorTypeDao);
	}

	@Test
	public void isValidNPETest() {
		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(mpPubContributor, null));

		assertTrue(validator.isValid(mpPubContributor, context));

		mpPubContributor.setContributor(contributor);
		assertTrue(validator.isValid(mpPubContributor, context));

		mpPubContributor.setContributorType(contributorType);
		assertTrue(validator.isValid(mpPubContributor, context));
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

		//works with mpPubContributor set
		mpPubContributor.setPublicationId(1);
		contributor.setId("");
		contributorType.setId("");
		assertTrue(validator.isValid(mpPubContributor, context));
		verify(mpPublicationDao, times(2)).getById(any(Integer.class));
		verify(contributorDao).getById(any(Integer.class));
		verify(contributorTypeDao).getById(any(Integer.class));

		//works with contributor set
		mpPubContributor.setPublicationId(null);
		contributor.setId(1);
		contributorType.setId("");
		assertTrue(validator.isValid(mpPubContributor, context));
		verify(mpPublicationDao, times(2)).getById(any(Integer.class));
		verify(contributorDao, times(2)).getById(any(Integer.class));
		verify(contributorTypeDao).getById(any(Integer.class));

		//works with contributorType set
		mpPubContributor.setPublicationId(null);
		contributor.setId("");
		contributorType.setId(1);
		assertTrue(validator.isValid(mpPubContributor, context));
		verify(mpPublicationDao, times(2)).getById(any(Integer.class));
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
