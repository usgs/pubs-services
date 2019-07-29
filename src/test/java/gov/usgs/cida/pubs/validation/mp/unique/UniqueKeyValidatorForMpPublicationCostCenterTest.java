package gov.usgs.cida.pubs.validation.mp.unique;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import gov.usgs.cida.pubs.dao.mp.MpPublicationCostCenterDao;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={MpPublicationCostCenter.class})
public class UniqueKeyValidatorForMpPublicationCostCenterTest extends BaseValidatorTest {

	protected UniqueKeyValidatorForMpPublicationCostCenter validator;
	protected MpPublicationCostCenter mpPubCostCenter;
	protected CostCenter costCenter;

	@MockBean(name="mpPublicationCostCenterDao")
	protected MpPublicationCostCenterDao mpPublicationCostCenterDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		validator = new UniqueKeyValidatorForMpPublicationCostCenter();
		mpPubCostCenter = new MpPublicationCostCenter();
		mpPubCostCenter.setMpPublicationCostCenterDao(mpPublicationCostCenterDao);
		costCenter = new CostCenter();

		reset(mpPublicationCostCenterDao);
	}

	@Test
	public void isValidNPETest() {
		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(mpPubCostCenter, null));

		assertTrue(validator.isValid(mpPubCostCenter, context));

		mpPubCostCenter.setCostCenter(costCenter);
		assertTrue(validator.isValid(mpPubCostCenter, context));
		costCenter.setId(1);
		assertTrue(validator.isValid(mpPubCostCenter, context));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void isValidTrueTest() {
		when(mpPublicationCostCenterDao.getByMap(anyMap())).thenReturn(new ArrayList<>(), buildList(), new ArrayList<>());
		mpPubCostCenter.setId(1);
		mpPubCostCenter.setCostCenter(costCenter);
		mpPubCostCenter.setPublicationId(1);
		costCenter.setId(1);

		//Works with empty list returned
		assertTrue(validator.isValid(mpPubCostCenter, context));
		verify(mpPublicationCostCenterDao).getByMap(anyMap());

		//Works with a list returned (pub assigned to same costCenter)
		assertTrue(validator.isValid(mpPubCostCenter, context));
		verify(mpPublicationCostCenterDao, times(2)).getByMap(anyMap());

		//Works with add and no list returned (mpPubCostCenter.getid() is null)
		mpPubCostCenter.setId("");
		assertTrue(validator.isValid(mpPubCostCenter, context));
		verify(mpPublicationCostCenterDao, times(3)).getByMap(anyMap());
	}

	@Test
	public void isValidFalseTest() {
		when(mpPublicationCostCenterDao.getByMap(anyMap())).thenReturn(buildList());
		mpPubCostCenter.setCostCenter(costCenter);
		mpPubCostCenter.setPublicationId(1);
		costCenter.setId(2);

		//Works with add (mpPubCostCenter.getid() is null)
		assertFalse(validator.isValid(mpPubCostCenter, context));
		verify(mpPublicationCostCenterDao).getByMap(anyMap());

		//Works with a list returned (pub assigned to different costCenter)
		mpPubCostCenter.setId(2);
		assertFalse(validator.isValid(mpPubCostCenter, context));
		verify(mpPublicationCostCenterDao, times(2)).getByMap(anyMap());
	}

	public static List<MpPublicationCostCenter> buildList() {
		List<MpPublicationCostCenter> rtn = new ArrayList<>();
		MpPublicationCostCenter mpcc = new MpPublicationCostCenter();
		mpcc.setId(1);
		mpcc.setCostCenter(new CostCenter());
		mpcc.getCostCenter().setId(1);
		rtn.add(mpcc);
		return rtn;
	}
}
