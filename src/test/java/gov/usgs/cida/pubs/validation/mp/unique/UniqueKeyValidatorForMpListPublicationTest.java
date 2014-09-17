package gov.usgs.cida.pubs.validation.mp.unique;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

import org.junit.Test;

public class UniqueKeyValidatorForMpListPublicationTest extends BaseValidatorTest {

	UniqueKeyValidatorForMpListPublication validator;

	@Test
	public void isValidTest() {
		validator = new UniqueKeyValidatorForMpListPublication();
		MpListPublication mpListPublication = new MpListPublication();
		MpPublication mpPublication = new MpPublication();
		MpList mpList = new MpList();

		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(mpListPublication, null));
		
		assertTrue(validator.isValid(mpListPublication, context));
		
		mpListPublication.setMpList(mpList);
		assertTrue(validator.isValid(mpListPublication, context));
		
		mpListPublication.setMpPublication(mpPublication);
		assertTrue(validator.isValid(mpListPublication, context));

		mpPublication.setId(1);
		assertTrue(validator.isValid(mpListPublication, context));

		mpList.setId(1);
		assertTrue(validator.isValid(mpListPublication, context));
		
		mpListPublication.setId(1);
		mpList.setId(320);
		mpPublication.setId(2);
		assertTrue(validator.isValid(mpListPublication, context));
		
		mpListPublication.setId("");
		assertFalse(validator.isValid(mpListPublication, context));

		mpListPublication.setId(-1);
		assertFalse(validator.isValid(mpListPublication, context));

		mpListPublication.setId(2);
		assertFalse(validator.isValid(mpListPublication, context));
	}

}
