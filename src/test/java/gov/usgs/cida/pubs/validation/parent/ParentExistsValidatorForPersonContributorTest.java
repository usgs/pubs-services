package gov.usgs.cida.pubs.validation.parent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import gov.usgs.cida.pubs.dao.CostCenterDao;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={Affiliation.class, CostCenter.class})
public class ParentExistsValidatorForPersonContributorTest extends BaseValidatorTest {

	protected ParentExistsValidatorForPersonContributor validator;
	protected PersonContributor<? extends PersonContributor<?>> personContributor;
	protected CostCenter affiliation;
	protected Set<Affiliation<? extends Affiliation<?>>> affiliations;

	@MockBean(name="affiliationDao")
	protected CostCenterDao affiliationDao;
	@MockBean(name="costCenterDao")
	protected CostCenterDao costCenterDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		validator = new ParentExistsValidatorForPersonContributor();
		personContributor = new UsgsContributor();
		affiliation = new CostCenter();
		affiliations = new HashSet<Affiliation<? extends Affiliation<?>>>();
		affiliations.add(affiliation);

		reset(affiliationDao, costCenterDao);
	}

	@Test
	public void isValidNPETest() {
		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(personContributor, null));

		assertTrue(validator.isValid(personContributor, context));

		personContributor.setAffiliations(affiliations);
		assertTrue(validator.isValid(personContributor, context));
	}

	@Test
	public void isValidTrueTest() {
		when(affiliationDao.getById(any(Integer.class))).thenReturn(new CostCenter());
		personContributor.setAffiliations(affiliations);

		affiliation.setId(1);
		assertTrue(validator.isValid(personContributor, context));
		verify(affiliationDao).getById(any(Integer.class));
	}

	@Test
	public void isValidFalseTest() {
		when(affiliationDao.getById(any(Integer.class))).thenReturn(null);
		personContributor.setAffiliations(affiliations);

		affiliation.setId(-1);
		assertFalse(validator.isValid(personContributor, context));
		verify(affiliationDao).getById(any(Integer.class));
	}
}