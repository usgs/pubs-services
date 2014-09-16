package gov.usgs.cida.pubs.validation.mp.unique;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

import org.junit.Test;

public class UniqueKeyValidatorForMpPublicationTest extends BaseValidatorTest {
	
	UniqueKeyValidatorForMpPublication validator;

	@Test
	public void isValidTest() {
		validator = new UniqueKeyValidatorForMpPublication();
		Publication<MpPublication> mpPub = new MpPublication();

		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(mpPub, null));
		
		assertTrue(validator.isValid(mpPub, context));
		
		mpPub.setIndexId("-9999");
		assertTrue(validator.isValid(mpPub, context));
		
		mpPub.setId(1);
		mpPub.setIndexId("sir20145083");
		assertTrue(validator.isValid(mpPub, context));

		mpPub.setId("-9999");
		assertFalse(validator.isValid(mpPub, context));
		
		mpPub.setId(2);
		mpPub.setIndexId("");
		mpPub.setIpdsId("ipdsid");
		assertTrue(validator.isValid(mpPub, context));
	
		mpPub.setId("-9999");
		assertFalse(validator.isValid(mpPub, context));
	}
	
}
