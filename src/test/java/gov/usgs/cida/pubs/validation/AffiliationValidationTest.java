package gov.usgs.cida.pubs.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.dao.PersonContributorDao;
import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.validation.constraint.DeleteChecks;
import gov.usgs.cida.pubs.validation.unique.UniqueKeyValidatorForAffiliationTest;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={LocalValidatorFactoryBean.class, CostCenter.class, UsgsContributor.class,
			PersonContributor.class, Contributor.class})
public class AffiliationValidationTest extends BaseValidatorTest {

	public static final String DUPLICATE_TEXT = new ValidatorResult("text", "Affiliation \"abc\" is already in use: id 1.", SeverityLevel.FATAL, null).toString();
	public static final String NOT_NULL_USGS = new ValidatorResult("usgs", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();
	public static final String NOT_NULL_ACTIVE = new ValidatorResult("active", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();
	public static final String INVALID_TEXT_LENGTH = new ValidatorResult("text", LENGTH_1_TO_XXX_MSG + "500", SeverityLevel.FATAL, null).toString();

	@Autowired
	public Validator validator;

	@MockBean(name="affiliationDao")
	protected IDao<CostCenter> affiliationDao;
	@MockBean(name="costCenterDao")
	protected IDao<CostCenter> costCenterDao;
	@MockBean(name="personContributorDao")
	protected PersonContributorDao personContributorDao;
	@MockBean(name="contributorDao")
	protected IDao<Contributor<?>> contributorDao;

	protected UsgsContributor contributor;

	//Using CostCenter because it works easier (all validations are the same via Affiliation...)
	private CostCenter affiliation;

	@BeforeEach
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		buildContext();
		affiliation = new CostCenter();
		affiliation.setText("abc");
		contributor = new UsgsContributor();

		reset(affiliationDao, costCenterDao, personContributorDao, contributorDao);

		when(affiliationDao.getByMap(anyMap())).thenReturn(UniqueKeyValidatorForAffiliationTest.buildCostCenterList());
	}

	@Test
	public void uniqueKeyTest() {
		affiliation.setValidationErrors(validator.validate(affiliation));
		assertFalse(affiliation.isValid());
		assertEquals(1, affiliation.getValidationErrors().getValidationErrors().size());
		assertValidationResults(affiliation.getValidationErrors().getValidationErrors(),
				//From UniqueKeyValidatorForAffiliation
				DUPLICATE_TEXT
				);
	}

	@Test
	public void deleteTest() {
		when(personContributorDao.getObjectCount(anyMap())).thenReturn(3);
		affiliation.setValidationErrors(validator.validate(affiliation, DeleteChecks.class));
		assertFalse(affiliation.isValid());
		assertEquals(1, affiliation.getValidationErrors().getValidationErrors().size());
		assertValidationResults(affiliation.getValidationErrors().getValidationErrors(),
				//From NoChildrenValidatorForAffiliation
				MAY_NOT_DELETE_ID
				);
	}

	@Test
	public void notNullTest() {
		affiliation.setText(null);
		//voodoo follows
		try {
			Class<?> c = affiliation.getClass();
			Field active = c.getSuperclass().getDeclaredField("active");
			active.setAccessible(true);
			active.set(affiliation, null);
			Field usgs = c.getSuperclass().getDeclaredField("usgs");
			usgs.setAccessible(true);
			usgs.set(affiliation, null);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		//end voodoo

		affiliation.setValidationErrors(validator.validate(affiliation));
		assertFalse(affiliation.isValid());
		assertEquals(3, affiliation.getValidationErrors().getValidationErrors().size());
		assertValidationResults(affiliation.getValidationErrors().getValidationErrors(),
				//From Affiliation
				NOT_NULL_TEXT,
				NOT_NULL_USGS,
				NOT_NULL_ACTIVE
				);
	}

	@Test
	public void minLengthTest() {
		when(affiliationDao.getByMap(anyMap())).thenReturn(new ArrayList<>());
		affiliation.setText("");

		affiliation.setValidationErrors(validator.validate(affiliation));
		assertFalse(affiliation.isValid());
		assertEquals(1, affiliation.getValidationErrors().getValidationErrors().size());
		assertValidationResults(affiliation.getValidationErrors().getValidationErrors(),
				//From Affiliation
				INVALID_TEXT_LENGTH
				);

		affiliation.setText("a");
		affiliation.setValidationErrors(validator.validate(affiliation));
		assertTrue(affiliation.isValid());
	}

	@Test
	public void maxLengthTest() {
		when(affiliationDao.getByMap(anyMap())).thenReturn(new ArrayList<>());

		affiliation.setText(StringUtils.repeat('X', 501));
		affiliation.setValidationErrors(validator.validate(affiliation));
		assertFalse(affiliation.isValid());
		assertEquals(1, affiliation.getValidationErrors().getValidationErrors().size());
		assertValidationResults(affiliation.getValidationErrors().getValidationErrors(),
				//From Affiliation
				INVALID_TEXT_LENGTH
				);

		affiliation.setText(StringUtils.repeat('X', 500));
		affiliation.setValidationErrors(validator.validate(affiliation));
		assertTrue(affiliation.isValid());
	}

}
