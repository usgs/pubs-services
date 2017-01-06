package gov.usgs.cida.pubs.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.dao.intfc.IMpDao;
import gov.usgs.cida.pubs.dao.intfc.IMpPublicationDao;
import gov.usgs.cida.pubs.domain.LinkFileType;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;

//The Dao mocking works because the getDao() methods are all static and JAVA/Spring don't redo them 
//for each reference. This does mean that we need to let Spring know that the context is now dirty...
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class PublicationLinkValidationTest extends BaseValidatorTest {
	@Autowired
	public Validator validator;

	public static final String INVALID_PUBLICATION = new ValidatorResult("publicationId", NO_PARENT_MSG, SeverityLevel.FATAL, null).toString();
	public static final String INVALID_LINK_TYPE = new ValidatorResult("linkType", NO_PARENT_MSG, SeverityLevel.FATAL, null).toString();
	public static final String INVALID_LINK_FILE_TYPE = new ValidatorResult("linkFileType", NO_PARENT_MSG, SeverityLevel.FATAL, null).toString();
	public static final String NOT_NULL_LINK_TYPE = new ValidatorResult("linkType", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();
	public static final String NOT_NULL_URL = new ValidatorResult("url", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();
	public static final String INVALID_TEXT_LENGTH = new ValidatorResult("text", LENGTH_0_TO_XXX_MSG + "4000", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_SIZE_LENGTH = new ValidatorResult("size", LENGTH_0_TO_XXX_MSG + "100", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_DESCRIPTION_LENGTH = new ValidatorResult("description", LENGTH_0_TO_XXX_MSG + "4000", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_URL_FOMAT = new ValidatorResult("url", URL_FORMAT_MSG, SeverityLevel.FATAL, null).toString();

	@Mock
	protected IMpDao<MpPublicationLink> pubLinkDao;
	@Mock
	protected IMpPublicationDao pubDao;
	@Mock
	protected IDao<LinkType> linkTypeDao;
	@Mock
	protected IDao<LinkFileType> linkFileTypeDao;

	private MpPublication pub;
	private LinkType linkType;
	private LinkFileType linkFileType;

	//Using MpPublicationLink because it works easier (all validations are the same via PublicationLink...)
	private MpPublicationLink pubLink;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		pubLink = new MpPublicationLink();
		linkType = new LinkType();
		linkType.setLinkTypeDao(linkTypeDao);
		linkType.setId(1);
		linkFileType = new LinkFileType();
		linkFileType.setLinkFileTypeDao(linkFileTypeDao);
		linkFileType.setId(1);
		pub = new MpPublication();
		pub.setMpPublicationDao(pubDao);
		pub.setId(1);
		pubLink.setUrl("http://noway.com");
	}

	@Test
	public void wiringTest() {
		when(pubDao.getById(any(Integer.class))).thenReturn(null);
		when(linkTypeDao.getById(any(Integer.class))).thenReturn(null);
		when(linkFileTypeDao.getById(any(Integer.class))).thenReturn(null);
		pubLink.setLinkType(linkType);
		pubLink.setLinkFileType(linkFileType);
		pubLink.setPublicationId(1);

		pubLink.setValidationErrors(validator.validate(pubLink));
		assertFalse(pubLink.getValidationErrors().isEmpty());
		assertEquals(3, pubLink.getValidationErrors().getValidationErrors().size());
		assertValidationResults(pubLink.getValidationErrors().getValidationErrors(),
				//From ParentExistsValidatorForMpPublicationLink
				INVALID_PUBLICATION,
				INVALID_LINK_TYPE,
				INVALID_LINK_FILE_TYPE
				);
	}

	@Test
	public void notNullTest() {
		pubLink.setLinkType(null);
		pubLink.setUrl(null);
		pubLink.setValidationErrors(validator.validate(pubLink));
		assertFalse(pubLink.getValidationErrors().isEmpty());
		assertEquals(2, pubLink.getValidationErrors().getValidationErrors().size());
		assertValidationResults(pubLink.getValidationErrors().getValidationErrors(),
				//From PublicationLink
				NOT_NULL_LINK_TYPE,
				NOT_NULL_URL
				);
	}

	@Test
	public void maxLengthTest() {
		when(linkTypeDao.getById(any(Integer.class))).thenReturn(new LinkType());
		pubLink.setLinkType(linkType);
		pubLink.setText(StringUtils.repeat('X', 4001));
		pubLink.setSize(StringUtils.repeat('X', 101));
		pubLink.setDescription(StringUtils.repeat('X', 4001));
		pubLink.setUrl("X");
		pubLink.setValidationErrors(validator.validate(pubLink));
		assertFalse(pubLink.getValidationErrors().isEmpty());
		assertEquals(4, pubLink.getValidationErrors().getValidationErrors().size());
		assertValidationResults(pubLink.getValidationErrors().getValidationErrors(),
				//From PublicationLink
				INVALID_TEXT_LENGTH,
				INVALID_SIZE_LENGTH,
				INVALID_DESCRIPTION_LENGTH,
				INVALID_URL_FOMAT
				);

		pubLink.setText(StringUtils.repeat('X', 4000));
		pubLink.setSize(StringUtils.repeat('X', 100));
		pubLink.setDescription(StringUtils.repeat('X', 4000));
		pubLink.setUrl("http://noway.com");
		pubLink.setValidationErrors(validator.validate(pubLink));
		assertTrue(pubLink.getValidationErrors().isEmpty());
	}

}
