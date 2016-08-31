package gov.usgs.cida.pubs.validation.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;
import gov.usgs.cida.pubs.validation.ValidatorResult;

//The Dao mocking works because the getDao() methods are all static and JAVA/Spring don't redo them 
//for each reference. This does mean that we need to let Spring know that the context is now dirty...
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class MpListValidationTest extends BaseValidatorTest {
	public static final String INVALID_TEXT_LENGTH = new ValidatorResult("text", LENGTH_0_TO_XXX_MSG + "500", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_DESCRIPTION_LENGTH = new ValidatorResult("description", LENGTH_0_TO_XXX_MSG + "2000", SeverityLevel.FATAL, null).toString();

	@Autowired
	public Validator validator;

	private MpList mpList;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		mpList = new MpList();
	}

	@Test
	public void maxLengthTest() {
		mpList.setText(StringUtils.repeat('X', 501));
		mpList.setDescription(StringUtils.repeat('X', 2001));
		mpList.setValidationErrors(validator.validate(mpList));
		assertFalse(mpList.getValidationErrors().isEmpty());
		assertEquals(2, mpList.getValidationErrors().getValidationErrors().size());
		assertValidationResults(mpList.getValidationErrors().getValidationErrors(),
				//From MpList
				INVALID_TEXT_LENGTH,
				INVALID_DESCRIPTION_LENGTH
				);

		mpList.setText(StringUtils.repeat('X', 500));
		mpList.setDescription(StringUtils.repeat('X', 2000));
		mpList.setValidationErrors(validator.validate(mpList));
		assertTrue(mpList.getValidationErrors().isEmpty());
	}

}
