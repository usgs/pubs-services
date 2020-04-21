package gov.usgs.cida.pubs.validation.nochildren;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import gov.usgs.cida.pubs.dao.PersonContributorDao;
import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={PersonContributor.class, Contributor.class})
public class NoChildrenValidatorForAffiliationTest extends BaseValidatorTest {

	protected NoChildrenValidatorForAffiliation validator;
	protected CostCenter affiliation;
	protected PersonContributor<?> personContributor;

	@MockBean(name="contributorDao")
	protected IDao<Contributor<?>> contributorDao;
	@MockBean(name="personContributorDao")
	protected PersonContributorDao personContributorDao;

	@BeforeEach
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		buildContext();
		validator = new NoChildrenValidatorForAffiliation();
		personContributor = new PersonContributor<>();
		affiliation = new CostCenter();

		reset(contributorDao, personContributorDao);
	}

	@Test
	public void isValidNPETest() {
		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(affiliation, null));
	}

	@Test
	public void isValidTrueTest() {
		when(personContributorDao.getObjectCount(anyMap())).thenReturn(0);
		assertTrue(validator.isValid(affiliation, context));
	}

	@Test
	public void isValidFalseTest() {
		when(personContributorDao.getObjectCount(anyMap())).thenReturn(10);
		assertFalse(validator.isValid(affiliation, context));
	}
}