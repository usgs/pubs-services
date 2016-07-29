package gov.usgs.cida.pubs.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;

public class PublicationLinkValidationTest extends BaseValidatorTest {

	public static final String NOT_NULL_LINK_TYPE = new ValidatorResult("linkType", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();
	public static final String NOT_NULL_URL = new ValidatorResult("url", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();
	public static final String BAD_URL = new ValidatorResult("url", BAD_URL_MSG, SeverityLevel.FATAL, null).toString();
	public static final String DESCRIPTION_TOO_LONG = new ValidatorResult("description", "length must be between 0 and 4000", SeverityLevel.FATAL, null).toString();
	public static final String SIZE_TOO_LONG = new ValidatorResult("size", "length must be between 0 and 100", SeverityLevel.FATAL, null).toString();
	public static final String TEXT_TOO_LONG = new ValidatorResult("text", "length must be between 0 and 4000", SeverityLevel.FATAL, null).toString();

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
		assertFalse(pubLink.getValidationErrors().isEmpty());
		assertValidationResults(pubLink.getValidationErrors().getValidationErrors(), NOT_NULL_LINK_TYPE, NOT_NULL_URL);
	}

	@Test
	public void notNullTrueTest() {
		pubLink.setValidationErrors(validator.validate(pubLink));
		assertTrue(pubLink.getValidationErrors().isEmpty());
	}

	@Test
	public void badUrlTest() {
		pubLink.setUrl("GP");
		pubLink.setLinkType(new LinkType());
		pubLink.setValidationErrors(validator.validate(pubLink));
		assertFalse(pubLink.getValidationErrors().isEmpty());
		assertValidationResults(pubLink.getValidationErrors().getValidationErrors(), BAD_URL);
	}

	@Test
	public void tooLongTest() {
		pubLink.setText(StringUtils.repeat('X', 4001));
		pubLink.setDescription(StringUtils.repeat('Y', 4001));
		pubLink.setSize(StringUtils.repeat('Z', 101));
		pubLink.setValidationErrors(validator.validate(pubLink));
		assertFalse(pubLink.getValidationErrors().isEmpty());
		assertValidationResults(pubLink.getValidationErrors().getValidationErrors(), DESCRIPTION_TOO_LONG, SIZE_TOO_LONG, TEXT_TOO_LONG);
	}

	@Test
	public void maxLengthTest() {
		pubLink.setText(StringUtils.repeat('X', 4000));
		pubLink.setDescription(StringUtils.repeat('Y', 4000));
		pubLink.setSize(StringUtils.repeat('Z', 100));
		pubLink.setValidationErrors(validator.validate(pubLink));
		assertTrue(pubLink.getValidationErrors().isEmpty());
	}

}
