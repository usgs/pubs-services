package gov.usgs.cida.pubs.validation.unique;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

//The Dao mocking works because the getDao() methods are all static and JAVA/Spring don't redo them 
//for each reference. This does mean that we need to let Spring know that the context is now dirty...
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class UniqueKeyValidatorForAffiliationTest extends BaseValidatorTest {
	
	protected UniqueKeyValidatorForAffiliation validator;
	protected CostCenter affiliation;

	@Mock
	protected IDao<CostCenter> affiliationDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		validator = new UniqueKeyValidatorForAffiliation();
		affiliation = new CostCenter();
		affiliation.setAffiliationDao(affiliationDao);
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
		when(affiliationDao.getByMap(anyMap())).thenReturn(new ArrayList<>());
		affiliation.setText("affiliation");
		assertTrue(validator.isValid(affiliation, context));
		verify(affiliationDao, times(1)).getByMap(anyMap());
	}

	@Test
	public void isValidAddFailTest() {
		when(affiliationDao.getByMap(anyMap())).thenReturn(buildList());
		affiliation.setText("affiliation");
		assertFalse(validator.isValid(affiliation, context));
		verify(affiliationDao, times(1)).getByMap(anyMap());
	}

	@Test
	public void isValidNoMatchTest() {
		when(affiliationDao.getByMap(anyMap())).thenReturn(new ArrayList<>());
		affiliation.setId(1);
		affiliation.setText("affiliation");
		assertTrue(validator.isValid(affiliation, context));
		verify(affiliationDao, times(1)).getByMap(anyMap());
	}

	@Test
	public void isValidMatchTest() {
		when(affiliationDao.getByMap(anyMap())).thenReturn(buildList());
		affiliation.setId(1);
		affiliation.setText("affiliation");
		assertTrue(validator.isValid(affiliation, context));
		verify(affiliationDao, times(1)).getByMap(anyMap());
	}

	@Test
	public void isValidFalseTest() {
		when(affiliationDao.getByMap(anyMap())).thenReturn(buildList());
		affiliation.setId(2);
		affiliation.setText("affiliation");
		assertFalse(validator.isValid(affiliation, context));
		verify(affiliationDao, times(1)).getByMap(anyMap());
	}

	public static List<CostCenter> buildList() {
		List<CostCenter> rtn = new ArrayList<>();
		CostCenter affiliation = new CostCenter();
		affiliation.setId(1);
		rtn.add(affiliation);
		return rtn;
	}
}
