package gov.usgs.cida.pubs.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import gov.usgs.cida.pubs.domain.LinkFileType;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={LocalValidatorFactoryBean.class, LinkType.class, LinkFileType.class, MpPublication.class,
			Publication.class})
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

	@MockBean(name="mpPublicationLinkDao")
	protected IMpDao<MpPublicationLink> pubLinkDao;
	@MockBean(name="mpPublicationDao")
	protected IMpPublicationDao pubDao;
	@MockBean(name="linkTypeDao")
	protected IDao<LinkType> linkTypeDao;
	@MockBean(name="linkFileTypeDao")
	protected IDao<LinkFileType> linkFileTypeDao;
	@MockBean(name="publicationDao")
	protected IPublicationDao publicationDao;

	private MpPublication pub;
	private LinkType linkType;
	private LinkFileType linkFileType;

	//Using MpPublicationLink because it works easier (all validations are the same via PublicationLink...)
	private MpPublicationLink pubLink;

	@BeforeEach
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		buildContext();
		pubLink = new MpPublicationLink();
		linkType = new LinkType();
		linkType.setId(1);
		linkFileType = new LinkFileType();
		linkFileType.setId(1);
		pub = new MpPublication();
		pub.setId(1);
		pubLink.setUrl("http://noway.com");

		reset(pubLinkDao, pubDao, linkTypeDao, linkFileTypeDao, publicationDao);
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
		assertFalse(pubLink.isValid());
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
		assertFalse(pubLink.isValid());
		assertEquals(3, pubLink.getValidationErrors().getValidationErrors().size());
		assertValidationResults(pubLink.getValidationErrors().getValidationErrors(),
				//From PublicationLink
				NOT_NULL_LINK_TYPE,
				NOT_NULL_URL,
				//From ParentExistsValidatorForMpPublicationLink
				INVALID_LINK_TYPE
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
		assertFalse(pubLink.isValid());
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
		assertTrue(pubLink.isValid());
	}

}
