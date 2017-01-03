package gov.usgs.cida.pubs.validation.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
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
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;
import gov.usgs.cida.pubs.validation.ValidatorResult;
import gov.usgs.cida.pubs.validation.mp.unique.UniqueKeyValidatorForMpListPublicationTest;

//The Dao mocking works because the getDao() methods are all static and JAVA/Spring don't redo them 
//for each reference. This does mean that we need to let Spring know that the context is now dirty...
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class MpListPublicationValidationTest extends BaseValidatorTest {

	public static final String DUPLICATE_TEXT = new ValidatorResult("", "Duplicates found", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_MP_PUBLICATION = new ValidatorResult("publicationId", NO_PARENT_MSG, SeverityLevel.FATAL, null).toString();
	public static final String INVALID_MP_LIST = new ValidatorResult("mpList", NO_PARENT_MSG, SeverityLevel.FATAL, null).toString();

	@Autowired
	public Validator validator;

	@Mock
	protected IMpDao<MpListPublication> mpListPublicationDao;
	@Mock
	protected IDao<MpList> mpListDao;
	@Mock
	protected IMpPublicationDao mpPublicationDao;
	
	private MpListPublication mpListPublication;
	private MpList mpList;
	private MpPublication mpPublication;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		mpPublication = new MpPublication();
		mpPublication.setId(1);
		mpPublication.setMpPublicationDao(mpPublicationDao);
		mpList = new MpList();
		mpList.setId(1);
		mpList.setMpListDao(mpListDao);
		mpListPublication = new MpListPublication();
		mpListPublication.setMpListPublicationDao(mpListPublicationDao);
		mpListPublication.setMpList(mpList);
		mpListPublication.setMpPublication(mpPublication);

		when(mpListPublicationDao.getByMap(anyMap())).thenReturn(UniqueKeyValidatorForMpListPublicationTest.buildList());
		when(mpPublicationDao.getById(any(Integer.class))).thenReturn(null);
		when(mpListDao.getById(any(Integer.class))).thenReturn(null);
	}

	@Test
	public void wiringTest() {
		mpListPublication.setValidationErrors(validator.validate(mpListPublication));
		assertFalse(mpListPublication.getValidationErrors().isEmpty());
		assertEquals(3, mpListPublication.getValidationErrors().getValidationErrors().size());
		assertValidationResults(mpListPublication.getValidationErrors().getValidationErrors(),
				//From UniqueKeyValidatorForMpListPublication
				DUPLICATE_TEXT,
				//From ParentExistsValidatorForMpListPublication
				INVALID_MP_PUBLICATION,
				INVALID_MP_LIST
				);
	}

}
