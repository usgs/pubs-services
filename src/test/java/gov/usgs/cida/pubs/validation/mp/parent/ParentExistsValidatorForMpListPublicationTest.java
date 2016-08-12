package gov.usgs.cida.pubs.validation.mp.parent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import gov.usgs.cida.pubs.dao.mp.MpListDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDao;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

//The Dao mocking works because the getDao() methods are all static and JAVA/Spring don't redo them 
//for each reference. This does mean that we need to let Spring know that the context is now dirty...
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class ParentExistsValidatorForMpListPublicationTest extends BaseValidatorTest {

	protected ParentExistsValidatorForMpListPublication validator;
	protected MpListPublication mpListPublication;
	protected MpPublication mpPublication;
	protected MpList mpList;

	@Mock
	protected MpPublicationDao mpPublicationDao;
	@Mock
	protected MpListDao mpListDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		validator = new ParentExistsValidatorForMpListPublication();
		mpListPublication = new MpListPublication();
		mpPublication = new MpPublication();
		mpPublication.setMpPublicationDao(mpPublicationDao);
		mpList = new MpList();
		mpList.setMpListDao(mpListDao);
	}

	@Test
	public void isValidNPETest() {
		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(mpListPublication, null));

		assertTrue(validator.isValid(mpListPublication, context));

		mpListPublication.setMpList(mpList);
		assertTrue(validator.isValid(mpListPublication, context));

		mpListPublication.setMpPublication(mpPublication);
		assertTrue(validator.isValid(mpListPublication, context));
	}

	@Test
	public void isValidTrueTest() {
		when(mpPublicationDao.getById(any(Integer.class))).thenReturn(new MpPublication());
		when(mpListDao.getById(any(Integer.class))).thenReturn(new MpList());
		mpListPublication.setMpList(mpList);
		mpListPublication.setMpPublication(mpPublication);

		//works with both set
		mpPublication.setId(1);
		mpList.setId(1);
		assertTrue(validator.isValid(mpListPublication, context));
		verify(mpPublicationDao).getById(any(Integer.class));
		verify(mpListDao).getById(any(Integer.class));

		//works with just mpPublication set
		mpPublication.setId(1);
		mpList.setId("");
		assertTrue(validator.isValid(mpListPublication, context));
		verify(mpPublicationDao, times(2)).getById(any(Integer.class));
		verify(mpListDao).getById(any(Integer.class));

		//works with just mpList set
		mpPublication.setId("");
		mpList.setId(1);
		assertTrue(validator.isValid(mpListPublication, context));
		verify(mpPublicationDao, times(2)).getById(any(Integer.class));
		verify(mpListDao, times(2)).getById(any(Integer.class));
	}

	@Test
	public void isValidFalseTest() {
		when(mpPublicationDao.getById(any(Integer.class))).thenReturn(null);
		when(mpListDao.getById(any(Integer.class))).thenReturn(null);
		mpListPublication.setMpList(mpList);
		mpListPublication.setMpPublication(mpPublication);

		//works with both set
		mpPublication.setId(-1);
		mpList.setId(-1);
		assertFalse(validator.isValid(mpListPublication, context));
		verify(mpPublicationDao).getById(any(Integer.class));
		verify(mpListDao).getById(any(Integer.class));

		//works with just mpPublication set
		mpPublication.setId(-1);
		mpList.setId("");
		assertFalse(validator.isValid(mpListPublication, context));
		verify(mpPublicationDao, times(2)).getById(any(Integer.class));
		verify(mpListDao).getById(any(Integer.class));

		//works with just mpList set
		mpPublication.setId("");
		mpList.setId(-1);
		assertFalse(validator.isValid(mpListPublication, context));
		verify(mpPublicationDao, times(2)).getById(any(Integer.class));
		verify(mpListDao, times(2)).getById(any(Integer.class));
	}

}
