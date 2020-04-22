package gov.usgs.cida.pubs.validation.mp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;
import gov.usgs.cida.pubs.validation.ValidatorResult;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={LocalValidatorFactoryBean.class})
public class MpListValidationTest extends BaseValidatorTest {
	public static final String INVALID_TEXT_LENGTH = new ValidatorResult("text", LENGTH_0_TO_XXX_MSG + "500", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_DESCRIPTION_LENGTH = new ValidatorResult("description", LENGTH_0_TO_XXX_MSG + "2000", SeverityLevel.FATAL, null).toString();

	@Autowired
	public Validator validator;

	private MpList mpList;

	@BeforeEach
	public void setUp() throws Exception {
		buildContext();
		mpList = new MpList();
	}

	@Test
	public void maxLengthTest() {
		mpList.setText(StringUtils.repeat('X', 501));
		mpList.setDescription(StringUtils.repeat('X', 2001));
		mpList.setValidationErrors(validator.validate(mpList));
		assertFalse(mpList.isValid());
		assertEquals(2, mpList.getValidationErrors().getValidationErrors().size());
		assertValidationResults(mpList.getValidationErrors().getValidationErrors(),
				//From MpList
				INVALID_TEXT_LENGTH,
				INVALID_DESCRIPTION_LENGTH
				);

		mpList.setText(StringUtils.repeat('X', 500));
		mpList.setDescription(StringUtils.repeat('X', 2000));
		mpList.setValidationErrors(validator.validate(mpList));
		assertTrue(mpList.isValid());
	}

}
