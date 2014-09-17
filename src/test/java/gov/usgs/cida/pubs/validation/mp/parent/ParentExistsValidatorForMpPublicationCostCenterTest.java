package gov.usgs.cida.pubs.validation.mp.parent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

import org.junit.Test;

public class ParentExistsValidatorForMpPublicationCostCenterTest extends BaseValidatorTest {

	ParentExistsValidatorForMpPublicationCostCenter validator;

	@Test
	public void isValidTest() {
		validator = new ParentExistsValidatorForMpPublicationCostCenter();
		PublicationCostCenter<MpPublicationCostCenter> mpPubCostCenter = new MpPublicationCostCenter();
		CostCenter costCenter = new CostCenter();

		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(mpPubCostCenter, null));
		
		assertTrue(validator.isValid(mpPubCostCenter, context));
		
		mpPubCostCenter.setCostCenter(costCenter);
		assertTrue(validator.isValid(mpPubCostCenter, context));
		
		mpPubCostCenter.setPublicationId(1);
		assertTrue(validator.isValid(mpPubCostCenter, context));

		mpPubCostCenter.setPublicationId(-1);
		assertFalse(validator.isValid(mpPubCostCenter, context));
		
		mpPubCostCenter.setPublicationId("");
		costCenter.setId(1);
		assertTrue(validator.isValid(mpPubCostCenter, context));
		
		costCenter.setId(-1);
		assertFalse(validator.isValid(mpPubCostCenter, context));
		
		mpPubCostCenter.setPublicationId(-1);
		assertFalse(validator.isValid(mpPubCostCenter, context));
	}

}
