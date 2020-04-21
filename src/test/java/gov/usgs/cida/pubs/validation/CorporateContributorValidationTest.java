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
import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.CorporateContributor;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={LocalValidatorFactoryBean.class, Contributor.class, CorporateContributor.class})
public class CorporateContributorValidationTest extends BaseValidatorTest {

	public static final String NOT_NULL_ORGANIZATION = new ValidatorResult("organization", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();
	public static final String NOT_NULL_USGS = new ValidatorResult("usgs", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();
	public static final String NOT_NULL_CORPORATION = new ValidatorResult("corporation", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();
	public static final String INVALID_ORGANIZATION_LENGTH = new ValidatorResult("organization", LENGTH_1_TO_XXX_MSG + "400", SeverityLevel.FATAL, null).toString();

	@Autowired
	public Validator validator;

	@MockBean(name="contributorDao")
	protected IDao<Contributor<?>> contributorDao;
	@MockBean(name="corporateContributorDao")
	protected IDao<Contributor<?>> corporateContributorDao;

	private CorporateContributor contributor;

	@BeforeEach
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		buildContext();
		contributor = new CorporateContributor();

		reset(contributorDao, corporateContributorDao);
	}

	@Test
	public void notNullTest() {
		contributor.setOrganization(null);
		//voodoo follows
		try {
			Class<?> c = contributor.getClass();
			Field corporation = c.getSuperclass().getDeclaredField("corporation");
			corporation.setAccessible(true);
			corporation.set(contributor, null);
			Field usgs = c.getSuperclass().getDeclaredField("usgs");
			usgs.setAccessible(true);
			usgs.set(contributor, null);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		//end voodoo

		contributor.setValidationErrors(validator.validate(contributor));
		assertFalse(contributor.isValid());
		assertEquals(3, contributor.getValidationErrors().getValidationErrors().size());
		assertValidationResults(contributor.getValidationErrors().getValidationErrors(),
				//From CorporateContributor
				NOT_NULL_ORGANIZATION,
				//From Contributor
				NOT_NULL_USGS,
				NOT_NULL_CORPORATION
				);
	}

	@Test
	public void minLengthTest() {
		when(contributorDao.getByMap(anyMap())).thenReturn(new ArrayList<>());
		contributor.setOrganization("");

		contributor.setValidationErrors(validator.validate(contributor));
		assertFalse(contributor.isValid());
		assertEquals(1, contributor.getValidationErrors().getValidationErrors().size());
		assertValidationResults(contributor.getValidationErrors().getValidationErrors(),
				//From CorporateContributor
				INVALID_ORGANIZATION_LENGTH
				);

		contributor.setOrganization("a");
		contributor.setValidationErrors(validator.validate(contributor));
		assertTrue(contributor.isValid());
	}

	@Test
	public void maxLengthTest() {
		when(contributorDao.getByMap(anyMap())).thenReturn(new ArrayList<>());

		contributor.setOrganization(StringUtils.repeat('X', 401));
		contributor.setValidationErrors(validator.validate(contributor));
		assertFalse(contributor.isValid());
		assertEquals(1, contributor.getValidationErrors().getValidationErrors().size());
		assertValidationResults(contributor.getValidationErrors().getValidationErrors(),
				//From CorporateContributor
				INVALID_ORGANIZATION_LENGTH
				);

		contributor.setOrganization(StringUtils.repeat('X', 400));
		contributor.setValidationErrors(validator.validate(contributor));
		assertTrue(contributor.isValid());
	}

}
