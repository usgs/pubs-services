package gov.usgs.cida.pubs.validation.mp.parent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import gov.usgs.cida.pubs.dao.CostCenterDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDao;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

//The Dao mocking works because the getDao() methods are all static and JAVA/Spring don't redo them 
//for each reference. This does mean that we need to let Spring know that the context is now dirty...
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class ParentExistsValidatorForMpPublicationCostCenterTest extends BaseValidatorTest {

	protected ParentExistsValidatorForMpPublicationCostCenter validator;
	protected MpPublication mpPublication;
	protected PublicationCostCenter<?> mpPubCostCenter;
	protected CostCenter costCenter;

	@Mock
	protected MpPublicationDao mpPublicationDao;
	@Mock
	protected CostCenterDao costCenterDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		validator = new ParentExistsValidatorForMpPublicationCostCenter();
		mpPublication = new MpPublication();
		mpPublication.setMpPublicationDao(mpPublicationDao);
		mpPubCostCenter = new PublicationCostCenter<>();
		costCenter = new CostCenter();
		costCenter.setCostCenterDao(costCenterDao);
	}

	@Test
	public void isValidNPETest() {
		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(mpPubCostCenter, null));

		assertTrue(validator.isValid(mpPubCostCenter, context));

		mpPubCostCenter.setCostCenter(costCenter);
		assertTrue(validator.isValid(mpPubCostCenter, context));
	}

	@Test
	public void isValidTrueTest() {
		when(mpPublicationDao.getById(any(Integer.class))).thenReturn(new MpPublication());
		when(costCenterDao.getById(any(Integer.class))).thenReturn(new CostCenter());
		mpPubCostCenter.setCostCenter(costCenter);

		//works with both set
		mpPubCostCenter.setPublicationId(1);
		costCenter.setId(1);
		assertTrue(validator.isValid(mpPubCostCenter, context));
		verify(mpPublicationDao).getById(any(Integer.class));
		verify(costCenterDao).getById(any(Integer.class));

		//works with just mpPublication set
		mpPubCostCenter.setPublicationId(1);
		costCenter.setId("");
		assertTrue(validator.isValid(mpPubCostCenter, context));
		verify(mpPublicationDao, times(2)).getById(any(Integer.class));
		verify(costCenterDao).getById(any(Integer.class));

		//works with just mpPubCostCenter set
		mpPubCostCenter.setPublicationId("");
		costCenter.setId(1);
		assertTrue(validator.isValid(mpPubCostCenter, context));
		verify(mpPublicationDao, times(2)).getById(any(Integer.class));
		verify(costCenterDao, times(2)).getById(any(Integer.class));
	}

	@Test
	public void isValidFalseTest() {
		when(mpPublicationDao.getById(any(Integer.class))).thenReturn(null);
		when(costCenterDao.getById(any(Integer.class))).thenReturn(null);
		mpPubCostCenter.setCostCenter(costCenter);

		//works with both set
		mpPubCostCenter.setPublicationId(-1);
		costCenter.setId(-1);
		assertFalse(validator.isValid(mpPubCostCenter, context));
		verify(mpPublicationDao).getById(any(Integer.class));
		verify(costCenterDao).getById(any(Integer.class));

		//works with just mpPublication set
		mpPubCostCenter.setPublicationId(-1);
		costCenter.setId("");
		assertFalse(validator.isValid(mpPubCostCenter, context));
		verify(mpPublicationDao, times(2)).getById(any(Integer.class));
		verify(costCenterDao).getById(any(Integer.class));

		//works with just mpPubCostCenter set
		mpPubCostCenter.setPublicationId("");
		costCenter.setId(-1);
		assertFalse(validator.isValid(mpPubCostCenter, context));
		verify(mpPublicationDao, times(2)).getById(any(Integer.class));
		verify(costCenterDao, times(2)).getById(any(Integer.class));
	}

}
