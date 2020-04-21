package gov.usgs.cida.pubs.validation.mp.parent;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import gov.usgs.cida.pubs.dao.CostCenterDao;
import gov.usgs.cida.pubs.dao.intfc.IPublicationDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDao;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={MpPublication.class, Publication.class, CostCenter.class, Affiliation.class})
public class ParentExistsValidatorForMpPublicationCostCenterTest extends BaseValidatorTest {

	protected ParentExistsValidatorForMpPublicationCostCenter validator;
	protected MpPublication mpPublication;
	protected PublicationCostCenter<?> mpPubCostCenter;
	protected CostCenter costCenter;

	@MockBean(name="mpPublicationDao")
	protected MpPublicationDao mpPublicationDao;
	@MockBean(name="publicationDao")
	protected IPublicationDao publicationDao;
	@MockBean(name="costCenterDao")
	protected CostCenterDao costCenterDao;
	@MockBean(name="affiliationDao")
	protected CostCenterDao affiliationDao;

	@BeforeEach
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		buildContext();
		validator = new ParentExistsValidatorForMpPublicationCostCenter();
		mpPublication = new MpPublication();
		mpPubCostCenter = new PublicationCostCenter<>();
		costCenter = new CostCenter();

		reset(mpPublicationDao, publicationDao, costCenterDao, affiliationDao);
	}

	@Test
	public void npeTests() {
		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(mpPubCostCenter, null));

		assertFalse(validator.isValid(mpPubCostCenter, context));

		mpPubCostCenter.setCostCenter(costCenter);
		assertFalse(validator.isValid(mpPubCostCenter, context));
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

		//works with just mpPubCostCenter set
		mpPubCostCenter.setPublicationId("");
		costCenter.setId(1);
		assertTrue(validator.isValid(mpPubCostCenter, context));
		verify(mpPublicationDao).getById(any(Integer.class));
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
