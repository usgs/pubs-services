package gov.usgs.cida.pubs.validation.mp.parent;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.mp.MpListDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDao;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={MpPublication.class, Publication.class, MpList.class})
public class ParentExistsValidatorForMpListPublicationTest extends BaseValidatorTest {

	protected ParentExistsValidatorForMpListPublication validator;
	protected MpListPublication mpListPublication;
	protected MpPublication mpPublication;
	protected MpList mpList;

	@MockBean(name="mpPublicationDao")
	protected MpPublicationDao mpPublicationDao;
	@MockBean(name="publicationDao")
	protected PublicationDao publicationDao;
	@MockBean(name="mpListDao")
	protected MpListDao mpListDao;

	@BeforeEach
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		buildContext();
		validator = new ParentExistsValidatorForMpListPublication();
		mpListPublication = new MpListPublication();
		mpPublication = new MpPublication();
		mpList = new MpList();

		reset(mpPublicationDao, mpListDao);
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
