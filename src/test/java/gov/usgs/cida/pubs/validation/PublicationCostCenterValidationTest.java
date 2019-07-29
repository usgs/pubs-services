package gov.usgs.cida.pubs.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.dao.intfc.IMpDao;
import gov.usgs.cida.pubs.dao.intfc.IMpPublicationDao;
import gov.usgs.cida.pubs.dao.intfc.IPublicationDao;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.validation.mp.unique.UniqueKeyValidatorForMpPublicationCostCenterTest;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={LocalValidatorFactoryBean.class, MpPublicationCostCenter.class, CostCenter.class,
			MpPublication.class, Publication.class})
public class PublicationCostCenterValidationTest extends BaseValidatorTest {
	@Autowired
	public Validator validator;

	public static final String DUPLICATE_COST_CENTER = new ValidatorResult("", "1 is already in use on Prod Id 1.", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_PUBLICATION = new ValidatorResult("publicationId", NO_PARENT_MSG, SeverityLevel.FATAL, null).toString();
	public static final String INVALID_COST_CENTER = new ValidatorResult("costCenter", NO_PARENT_MSG, SeverityLevel.FATAL, null).toString();
	public static final String NOT_NULL_COST_CENTER = new ValidatorResult("costCenter", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();

	@MockBean(name="mpPublicationCostCenterDao")
	protected IMpDao<MpPublicationCostCenter> pubCostCenterDao;
	@MockBean(name="mpPublicationDao")
	protected IMpPublicationDao pubDao;
	@MockBean(name="publicationDao")
	protected IPublicationDao publicationDao;
	@MockBean(name="costCenterDao")
	protected IDao<CostCenter> costCenterDao;
	@MockBean(name="affiliationDao")
	protected IDao<CostCenter> affiliationDao;

	private MpPublication pub;
	private CostCenter costCenter;

	//Using MpPublicationCostCenter because it works easier (all validations are the same via PublicationCostCenter...)
	private MpPublicationCostCenter pubCostCenter;

	@Before
	@Override
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		super.setUp();
		pubCostCenter = new MpPublicationCostCenter();
		costCenter = new CostCenter();
		costCenter.setId(1);
		pub = new MpPublication();
		pub.setId(1);

		reset(pubCostCenterDao, pubDao, publicationDao, costCenterDao, affiliationDao);
	}

	@Test
	public void wiringTest() {
		when(pubCostCenterDao.getByMap(anyMap())).thenReturn(UniqueKeyValidatorForMpPublicationCostCenterTest.buildList());
		when(pubDao.getById(any(Integer.class))).thenReturn(null);
		pubCostCenter.setCostCenter(costCenter);
		pubCostCenter.setPublicationId(1);

		pubCostCenter.setValidationErrors(validator.validate(pubCostCenter));
		assertFalse(pubCostCenter.isValid());
		assertEquals(3, pubCostCenter.getValidationErrors().getValidationErrors().size());
		assertValidationResults(pubCostCenter.getValidationErrors().getValidationErrors(),
				//From UniqueKeyValidatorForMpPublicationCostCenter
				DUPLICATE_COST_CENTER,
				//From ParentExistsValidatorForMpPublicationCostCenter
				INVALID_PUBLICATION,
				INVALID_COST_CENTER
				);
	}

	@Test
	public void notNullTest() {
		pubCostCenter.setCostCenter(null);
		pubCostCenter.setValidationErrors(validator.validate(pubCostCenter));
		assertFalse(pubCostCenter.isValid());
		assertEquals(1, pubCostCenter.getValidationErrors().getValidationErrors().size());
		assertValidationResults(pubCostCenter.getValidationErrors().getValidationErrors(),
				//From PublicationCostCenter
				NOT_NULL_COST_CENTER
				);
	}

}
