package gov.usgs.cida.pubs.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.dao.intfc.IPublicationDao;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.validation.constraint.DeleteChecks;
import gov.usgs.cida.pubs.validation.unique.UniqueKeyValidatorForPublicationSeriesTest;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={LocalValidatorFactoryBean.class, PublicationSubtype.class, PublicationSeries.class,
			Publication.class})
public class PublicationSeriesValidationTest extends BaseValidatorTest {
	@Autowired
	public Validator validator;

	public static final String DUPLICATE_TEXT = new ValidatorResult("text", "Name TextX is already used by the Series with ID 1.", SeverityLevel.FATAL, null).toString();
	public static final String DUPLICATE_CODE = new ValidatorResult("code", "Code CodeX is already used by the Series with ID 1.", SeverityLevel.FATAL, null).toString();
	public static final String DUPLICATE_SERIES_DOI_NAME = new ValidatorResult("seriesDoiName", "DOI Name DOINameX is already used by the Series with ID 1.", SeverityLevel.FATAL, null).toString();
	public static final String DUPLICATE_PRINT_ISSN = new ValidatorResult("printIssn", "Print ISSN PrintX is already used by the Series with ID 1.", SeverityLevel.FATAL, null).toString();
	public static final String DUPLICATE_ONLINE_ISSN = new ValidatorResult("onlineIssn", "Online ISSN On-lineX is already used by the Series with ID 1.", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_PUBLICATION_SUBTYPE = new ValidatorResult("id", NO_PARENT_MSG, SeverityLevel.FATAL, null).toString();
	public static final String NOT_NULL_PUBLICATION_SUBTYPE = new ValidatorResult("publicationSubtype", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();
	public static final String INVALID_TEXT_LENGTH = new ValidatorResult("text", LENGTH_1_TO_XXX_MSG + "250", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_CODE_LENGTH = new ValidatorResult("code", LENGTH_0_TO_XXX_MSG + "7", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_SERIES_DOI_NAME_LENGTH = new ValidatorResult("seriesDoiName", LENGTH_0_TO_XXX_MSG + "2000", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_ONLINE_ISSN_LENGTH = new ValidatorResult("onlineIssn", LENGTH_0_TO_XXX_MSG + "9", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_PRINT_ISSN_LENGTH = new ValidatorResult("printIssn", LENGTH_0_TO_XXX_MSG + "9", SeverityLevel.FATAL, null).toString();

	@MockBean(name="publicationSeriesDao")
	protected IDao<PublicationSeries> pubSeriesDao;
	@MockBean(name="publicationSubtypeDao")
	protected IDao<PublicationSubtype> publicationSubtypeDao;
	@MockBean(name="publicationDao")
	protected IPublicationDao pubDao;

	private PublicationSeries pubSeries;
	private PublicationSubtype publicationSubtype;

	@Before
	@Override
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		super.setUp();
		publicationSubtype = new PublicationSubtype();
		publicationSubtype.setId(1);
		pubSeries = new PublicationSeries();
		pubSeries.setPublicationSubtype(publicationSubtype);
		pubSeries.setText("abc");

		reset(publicationSubtypeDao, pubSeriesDao);
	}

	@Test
	public void wiringTest() {
		when(publicationSubtypeDao.getById(any(Integer.class))).thenReturn(null);
		when(pubSeriesDao.uniqueCheck(any(PublicationSeries.class))).thenReturn(UniqueKeyValidatorForPublicationSeriesTest.allDup());
		pubSeries.setText("TextX");
		pubSeries.setCode("CodeX");
		pubSeries.setSeriesDoiName("DOINameX");
		pubSeries.setPrintIssn("PrintX");
		pubSeries.setOnlineIssn("On-lineX");

		pubSeries.setValidationErrors(validator.validate(pubSeries));
		assertFalse(pubSeries.isValid());
		assertEquals(6, pubSeries.getValidationErrors().getValidationErrors().size());
		assertValidationResults(pubSeries.getValidationErrors().getValidationErrors(),
				//From ParentExistsValidatorForPublicationSeries
				INVALID_PUBLICATION_SUBTYPE,
				//From UniqueKeyValidatorForPublicationSeries
				DUPLICATE_TEXT,
				DUPLICATE_CODE,
				DUPLICATE_SERIES_DOI_NAME,
				DUPLICATE_PRINT_ISSN,
				DUPLICATE_ONLINE_ISSN
				);
	}

	@Test
	public void notNullTest() {
		pubSeries.setPublicationSubtype(null);;
		pubSeries.setText(null);
		pubSeries.setValidationErrors(validator.validate(pubSeries));
		assertFalse(pubSeries.isValid());
		assertEquals(2, pubSeries.getValidationErrors().getValidationErrors().size());
		assertValidationResults(pubSeries.getValidationErrors().getValidationErrors(),
				//From PublicationSeries
				NOT_NULL_PUBLICATION_SUBTYPE,
				NOT_NULL_TEXT
				);
	}

	@Test
	public void minLengthTest() {
		when(publicationSubtypeDao.getById(any(Integer.class))).thenReturn(new PublicationSubtype());
		pubSeries.setText("");
		pubSeries.setValidationErrors(validator.validate(pubSeries));
		assertFalse(pubSeries.isValid());
		assertEquals(1, pubSeries.getValidationErrors().getValidationErrors().size());
		assertValidationResults(pubSeries.getValidationErrors().getValidationErrors(),
				//From PublicationSeries
				INVALID_TEXT_LENGTH
				);

		pubSeries.setText("A");
		pubSeries.setValidationErrors(validator.validate(pubSeries));
		assertTrue(pubSeries.isValid());
	}

	@Test
	public void maxLengthTest() {
		when(publicationSubtypeDao.getById(any(Integer.class))).thenReturn(new PublicationSubtype());
		pubSeries.setText(StringUtils.repeat('X', 251));
		pubSeries.setCode(StringUtils.repeat('X', 8));
		pubSeries.setSeriesDoiName(StringUtils.repeat('X', 2001));
		pubSeries.setOnlineIssn(StringUtils.repeat('X', 10));
		pubSeries.setPrintIssn(StringUtils.repeat('X', 10));
		pubSeries.setValidationErrors(validator.validate(pubSeries));
		assertFalse(pubSeries.isValid());
		assertEquals(5, pubSeries.getValidationErrors().getValidationErrors().size());
		assertValidationResults(pubSeries.getValidationErrors().getValidationErrors(),
				//From PublicationSeries
				INVALID_TEXT_LENGTH,
				INVALID_CODE_LENGTH,
				INVALID_SERIES_DOI_NAME_LENGTH,
				INVALID_ONLINE_ISSN_LENGTH,
				INVALID_PRINT_ISSN_LENGTH
				);

		pubSeries.setText(StringUtils.repeat('X', 250));
		pubSeries.setCode(StringUtils.repeat('X', 7));
		pubSeries.setSeriesDoiName(StringUtils.repeat('X', 2000));
		pubSeries.setOnlineIssn(StringUtils.repeat('X', 9));
		pubSeries.setPrintIssn(StringUtils.repeat('X', 9));
		pubSeries.setValidationErrors(validator.validate(pubSeries));
		assertTrue(pubSeries.isValid());
	}

	@Test
	public void deleteTest() {
		when(pubDao.getObjectCount(anyMap())).thenReturn(5);
		pubSeries.setValidationErrors(validator.validate(pubSeries, DeleteChecks.class));
		assertFalse(pubSeries.isValid());
		assertEquals(1, pubSeries.getValidationErrors().getValidationErrors().size());
		assertValidationResults(pubSeries.getValidationErrors().getValidationErrors(),
				//From NoChildrenValidatorForPublicationSeries
				MAY_NOT_DELETE_ID
				);
	}

}
