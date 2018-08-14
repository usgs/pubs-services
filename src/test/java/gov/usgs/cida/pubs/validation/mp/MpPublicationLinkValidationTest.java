package gov.usgs.cida.pubs.validation.mp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;
import gov.usgs.cida.pubs.validation.ValidatorResult;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={LocalValidatorFactoryBean.class})
public class MpPublicationLinkValidationTest extends BaseValidatorTest {

	public static final String NOT_NULL_LINK_TYPE = new ValidatorResult("linkType", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();
	public static final String NOT_NULL_URL = new ValidatorResult("url", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();
	public static final String BAD_URL = new ValidatorResult("url", BAD_URL_MSG, SeverityLevel.FATAL, null).toString();
	public static final String INVALID_DESCRIPTION_LENGTH = new ValidatorResult("description", LENGTH_0_TO_XXX_MSG + "4000", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_SIZE_LENGTH = new ValidatorResult("size", LENGTH_0_TO_XXX_MSG + "100", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_TEXT_LENGTH = new ValidatorResult("text", LENGTH_0_TO_XXX_MSG + "4000", SeverityLevel.FATAL, null).toString();

	@Autowired
	public Validator validator;
	protected MpPublicationLink pubLink;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		pubLink = new MpPublicationLink();
		pubLink.setUrl("http://usgs.gov");
		pubLink.setLinkType(new LinkType());
	}

	@Test
	public void notNullFalseTest() {
		pubLink.setUrl(null);
		pubLink.setLinkType(null);

		pubLink.setValidationErrors(validator.validate(pubLink));
		assertFalse(pubLink.isValid());
		assertValidationResults(pubLink.getValidationErrors().getValidationErrors(),
				//From PublicationLink
				NOT_NULL_LINK_TYPE,
				NOT_NULL_URL
				);
	}

	@Test
	public void notNullTrueTest() {
		pubLink.setValidationErrors(validator.validate(pubLink));
		assertTrue(pubLink.isValid());
	}

	@Test
	public void badUrlTest() {
		pubLink.setUrl("GP");
		pubLink.setLinkType(new LinkType());
		pubLink.setValidationErrors(validator.validate(pubLink));
		assertFalse(pubLink.isValid());
		assertValidationResults(pubLink.getValidationErrors().getValidationErrors(),
				//From PublicationLink
				BAD_URL
				);
	}

	@Test
	public void tooLongTest() {
		pubLink.setText(StringUtils.repeat('X', 4001));
		pubLink.setDescription(StringUtils.repeat('Y', 4001));
		pubLink.setSize(StringUtils.repeat('Z', 101));
		pubLink.setValidationErrors(validator.validate(pubLink));
		assertFalse(pubLink.isValid());
		assertValidationResults(pubLink.getValidationErrors().getValidationErrors(),
				//From PublicationLink
				INVALID_DESCRIPTION_LENGTH,
				INVALID_SIZE_LENGTH,
				INVALID_TEXT_LENGTH
				);
	}

	@Test
	public void maxLengthTest() {
		pubLink.setText(StringUtils.repeat('X', 4000));
		pubLink.setDescription(StringUtils.repeat('Y', 4000));
		pubLink.setSize(StringUtils.repeat('Z', 100));
		pubLink.setValidationErrors(validator.validate(pubLink));
		assertTrue(pubLink.isValid());
	}

}
