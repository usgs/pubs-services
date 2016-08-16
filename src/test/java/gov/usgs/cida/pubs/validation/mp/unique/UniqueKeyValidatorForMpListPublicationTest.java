package gov.usgs.cida.pubs.validation.mp.unique;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import gov.usgs.cida.pubs.dao.mp.MpListPublicationDao;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

//The Dao mocking works because the getDao() methods are all static and JAVA/Spring don't redo them 
//for each reference. This does mean that we need to let Spring know that the context is now dirty...
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class UniqueKeyValidatorForMpListPublicationTest extends BaseValidatorTest {

	protected UniqueKeyValidatorForMpListPublication validator;
	protected MpListPublication mpListPublication;
	protected MpPublication mpPublication;
	protected MpList mpList;

	@Mock
	protected MpListPublicationDao mpListPublicationDao;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		validator = new UniqueKeyValidatorForMpListPublication();
		mpListPublication = new MpListPublication();
		mpListPublication.setMpListPublicationDao(mpListPublicationDao);
		mpPublication = new MpPublication();
		mpList = new MpList();
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

		mpPublication.setId(1);
		mpList.setId("");
		assertTrue(validator.isValid(mpListPublication, context));

		mpPublication.setId("");
		mpList.setId(1);
		assertTrue(validator.isValid(mpListPublication, context));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void isValidTrueTest() {
		when(mpListPublicationDao.getByMap(anyMapOf(String.class, Object.class))).thenReturn(new ArrayList<>(), buildList(), new ArrayList<>());
		mpListPublication.setMpList(mpList);
		mpListPublication.setMpPublication(mpPublication);
		mpListPublication.setId(1);
		mpPublication.setId(1);
		mpList.setId(1);

		//Works with empty list returned
		assertTrue(validator.isValid(mpListPublication, context));
		verify(mpListPublicationDao).getByMap(anyMapOf(String.class, Object.class));

		//Works with a list returned (pub assigned to same list)
		assertTrue(validator.isValid(mpListPublication, context));
		verify(mpListPublicationDao, times(2)).getByMap(anyMapOf(String.class, Object.class));

		//Works with add and no list returned (MpListPublication.getid() is null)
		mpListPublication.setId("");
		assertTrue(validator.isValid(mpListPublication, context));
		verify(mpListPublicationDao, times(3)).getByMap(anyMapOf(String.class, Object.class));
	}

	@Test
	public void isValidFalseTest() {
		when(mpListPublicationDao.getByMap(anyMapOf(String.class, Object.class))).thenReturn(buildList());
		mpListPublication.setMpList(mpList);
		mpListPublication.setMpPublication(mpPublication);
		mpPublication.setId(1);
		mpList.setId(2);

		//Works with add (MpListPublication.getid() is null)
		assertFalse(validator.isValid(mpListPublication, context));
		verify(mpListPublicationDao).getByMap(anyMapOf(String.class, Object.class));

		//Works with a list returned (pub assigned to different list)
		mpListPublication.setId(2);
		assertFalse(validator.isValid(mpListPublication, context));
		verify(mpListPublicationDao, times(2)).getByMap(anyMapOf(String.class, Object.class));
	}

	public static List<MpListPublication> buildList() {
		List<MpListPublication> rtn = new ArrayList<>();
		MpListPublication mlp = new MpListPublication();
		mlp.setId(1);
		mlp.setMpList(new MpList());
		mlp.getMpList().setId(1);
		mlp.setMpPublication(new MpPublication());
		mlp.getMpPublication().setId(1);
		rtn.add(mlp);
		return rtn;
	}
}
