package gov.usgs.cida.pubs.validation.unique;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.OutsideAffiliation;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={MockAffiliationDao.class})
public class UniqueKeyValidatorForAffiliationTest extends BaseValidatorTest {
	@MockBean
	@Qualifier("affiliationDao")
	protected MockAffiliationDao mockAffiliationDao;

	protected UniqueKeyValidatorForAffiliation validator;
	protected CostCenter affiliation;
	protected OutsideAffiliation outsideAffiliation;

	@BeforeEach
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setUp() throws Exception {
		buildContext();
		validator = new UniqueKeyValidatorForAffiliation();
		affiliation = new CostCenter();
		outsideAffiliation = new OutsideAffiliation();

		mockAffiliationDao = new MockAffiliationDao();
		new Affiliation().setAffiliationDao(mockAffiliationDao);
	}

	@Test
	public void isValidNPETest() {
		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(affiliation, null));
		assertTrue(validator.isValid(affiliation, context));
	}

	@Test
	public void isValidAddTest() {
		affiliation.setText("affiliation");
		assertTrue(validator.isValid(affiliation, context));
		assertEquals(1, mockAffiliationDao.getGetByMapCount());
	}

	@Test
	public void isValidAddFailCostCenterTest() {
		mockAffiliationDao.setAffialiationList(buildCostCenterAffilationList());
		affiliation.setText("affiliation");
		assertFalse(validator.isValid(affiliation, context));
		assertEquals(1, mockAffiliationDao.getGetByMapCount());
	}

	@Test
	public void isValidAddFailOutsideAffiliationTest() {
		mockAffiliationDao.setAffialiationList(buildOutsideAffialitionList());
		affiliation.setText("affiliation");
		assertFalse(validator.isValid(affiliation, context));
		assertEquals(1, mockAffiliationDao.getGetByMapCount());
	}

	@Test
	public void isValidNoMatchTest() {
		affiliation.setText("affiliation");
		assertTrue(validator.isValid(affiliation, context));
		assertEquals(1, mockAffiliationDao.getGetByMapCount());
	}

	@Test
	public void isValidNoMatchOutsideAffiliationbTest() {
		outsideAffiliation.setText("affiliation");
		assertTrue(validator.isValid(outsideAffiliation, context));
		assertEquals(1, mockAffiliationDao.getGetByMapCount());
	}

	@Test
	public void isValidMatchTest() {
		mockAffiliationDao.setAffialiationList(buildCostCenterAffilationList());
		affiliation.setId(1);
		affiliation.setText("affiliation");
		assertTrue(validator.isValid(affiliation, context));
		assertEquals(1, mockAffiliationDao.getGetByMapCount());
	}

	@Test
	public void isValidMatchOutSideAffiliationTest() {
		mockAffiliationDao.setAffialiationList(buildOutsideAffialitionList());
		outsideAffiliation.setId(2);
		outsideAffiliation.setText("affiliation");
		assertTrue(validator.isValid(outsideAffiliation, context));
		assertEquals(1, mockAffiliationDao.getGetByMapCount());
	}

	@Test
	public void isValidFalseCostCenterTest() {
		mockAffiliationDao.setAffialiationList(buildCostCenterAffilationList());
		affiliation.setText("affiliation");
		assertFalse(validator.isValid(affiliation, context));
		assertEquals(1, mockAffiliationDao.getGetByMapCount());
	}

	@Test
	public void isValidFalseOutsideAffiliationTest() {
		mockAffiliationDao.setAffialiationList(buildOutsideAffialitionList());
		outsideAffiliation.setText("affiliation");
		assertFalse(validator.isValid(outsideAffiliation, context));
		assertEquals(1, mockAffiliationDao.getGetByMapCount());
	}

	public static List<Affiliation<?>> buildCostCenterAffilationList() {
		List<Affiliation<?>> rtn = new ArrayList<>();
		CostCenter affiliation = new CostCenter();
		affiliation.setId(1);
		affiliation.setText("affiliation");
		rtn.add(affiliation);

		return rtn;
	}

	public static List<CostCenter> buildCostCenterList() {
		List<CostCenter> rtn = new ArrayList<>();
		CostCenter affiliation = new CostCenter();
		affiliation.setId(1);
		affiliation.setText("affiliation");
		rtn.add(affiliation);

		return rtn;
	}

	public static List<Affiliation<?>> buildOutsideAffialitionList() {
		List<Affiliation<?>> rtn = new ArrayList<>();

		OutsideAffiliation outsideAffialiation = new OutsideAffiliation();
		outsideAffialiation.setId(2);
		outsideAffialiation.setText("affiliation");
		rtn.add(outsideAffialiation);

		return rtn;
	}

}
