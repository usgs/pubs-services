package gov.usgs.cida.pubs.validation.mp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import javax.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.dao.intfc.IMpDao;
import gov.usgs.cida.pubs.dao.intfc.IMpPublicationDao;
import gov.usgs.cida.pubs.dao.intfc.IPublicationDao;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;
import gov.usgs.cida.pubs.validation.ValidatorResult;
import gov.usgs.cida.pubs.validation.mp.unique.UniqueKeyValidatorForMpListPublicationTest;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={LocalValidatorFactoryBean.class, MpPublication.class, Publication.class, MpList.class,
			MpListPublication.class})
public class MpListPublicationValidationTest extends BaseValidatorTest {

	public static final String DUPLICATE_TEXT = new ValidatorResult("", "Duplicates found", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_MP_PUBLICATION = new ValidatorResult("publicationId", NO_PARENT_MSG, SeverityLevel.FATAL, null).toString();
	public static final String INVALID_MP_LIST = new ValidatorResult("mpList", NO_PARENT_MSG, SeverityLevel.FATAL, null).toString();

	@Autowired
	public Validator validator;

	@MockBean(name="mpListPublicationDao")
	protected IMpDao<MpListPublication> mpListPublicationDao;
	@MockBean(name="mpListDao")
	protected IDao<MpList> mpListDao;
	@MockBean(name="mpPublicationDao")
	protected IMpPublicationDao mpPublicationDao;
	@MockBean(name="publicationDao")
	protected IPublicationDao publicationDao;

	private MpListPublication mpListPublication;
	private MpList mpList;
	private MpPublication mpPublication;

	@BeforeEach
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		buildContext();
		mpPublication = new MpPublication();
		mpPublication.setId(1);
		mpList = new MpList();
		mpList.setId(1);
		mpListPublication = new MpListPublication();
		mpListPublication.setMpList(mpList);
		mpListPublication.setMpPublication(mpPublication);

		reset(mpListPublicationDao, mpListDao, mpPublicationDao, publicationDao);

		when(mpListPublicationDao.getByMap(anyMap())).thenReturn(UniqueKeyValidatorForMpListPublicationTest.buildList());
		when(mpPublicationDao.getById(any(Integer.class))).thenReturn(null);
		when(mpListDao.getById(any(Integer.class))).thenReturn(null);
	}

	@Test
	public void wiringTest() {
		mpListPublication.setValidationErrors(validator.validate(mpListPublication));
		assertFalse(mpListPublication.isValid());
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
