package gov.usgs.cida.pubs.validation.mp.parent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import gov.usgs.cida.pubs.dao.LinkFileTypeDao;
import gov.usgs.cida.pubs.dao.LinkTypeDao;
import gov.usgs.cida.pubs.dao.intfc.IPublicationDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDao;
import gov.usgs.cida.pubs.domain.LinkFileType;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={MpPublication.class, Publication.class, LinkType.class, LinkFileType.class})
public class ParentExistsValidatorForMpPublicationLinkTest extends BaseValidatorTest {

	protected ParentExistsValidatorForMpPublicationLink validator;
	protected PublicationLink<?> mpPubLink;
	protected MpPublication mpPublication;
	protected LinkType linkType;
	protected LinkFileType linkFileType;

	@MockBean(name="mpPublicationDao")
	protected MpPublicationDao mpPublicationDao;
	@MockBean(name="publicationDao")
	protected IPublicationDao publicationDao;
	@MockBean(name="linkTypeDao")
	protected LinkTypeDao linkTypeDao;
	@MockBean(name="linkFileTypeDao")
	protected LinkFileTypeDao linkFileTypeDao;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		super.setUp();
		validator = new ParentExistsValidatorForMpPublicationLink();
		mpPubLink = new PublicationLink<>();
		mpPublication = new MpPublication();
		linkType = new LinkType();
		linkFileType = new LinkFileType();

		reset(mpPublicationDao, publicationDao, linkTypeDao, linkFileTypeDao);
	}

	@Test
	public void isValidNPETest() {
		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(mpPubLink, null));

		assertTrue(validator.isValid(mpPubLink, context));

		mpPubLink.setLinkType(linkType);
		assertTrue(validator.isValid(mpPubLink, context));

		mpPubLink.setLinkFileType(linkFileType);
		assertTrue(validator.isValid(mpPubLink, context));
	}

	@Test
	public void isValidTrueTest() {
		when(mpPublicationDao.getById(any(Integer.class))).thenReturn(new MpPublication());
		when(linkTypeDao.getById(any(Integer.class))).thenReturn(new LinkType());
		when(linkFileTypeDao.getById(any(Integer.class))).thenReturn(new LinkFileType());
		mpPubLink.setLinkType(linkType);
		mpPubLink.setLinkFileType(linkFileType);

		//works with all set
		mpPubLink.setPublicationId(1);
		linkType.setId(1);
		linkFileType.setId(1);
		assertTrue(validator.isValid(mpPubLink, context));
		verify(mpPublicationDao).getById(any(Integer.class));
		verify(linkTypeDao).getById(any(Integer.class));
		verify(linkFileTypeDao).getById(any(Integer.class));

		//works with mpPublication set
		mpPubLink.setPublicationId(1);
		linkType.setId("");
		linkFileType.setId("");
		assertTrue(validator.isValid(mpPubLink, context));
		verify(mpPublicationDao, times(2)).getById(any(Integer.class));
		verify(linkTypeDao).getById(any(Integer.class));
		verify(linkFileTypeDao).getById(any(Integer.class));

		//works with linkType set
		mpPubLink.setPublicationId(null);
		linkType.setId(1);
		linkFileType.setId("");
		assertTrue(validator.isValid(mpPubLink, context));
		verify(mpPublicationDao, times(2)).getById(any(Integer.class));
		verify(linkTypeDao, times(2)).getById(any(Integer.class));
		verify(linkFileTypeDao).getById(any(Integer.class));

		//works with linkFileType set
		mpPubLink.setPublicationId(null);
		linkType.setId("");
		linkFileType.setId(1);
		assertTrue(validator.isValid(mpPubLink, context));
		verify(mpPublicationDao, times(2)).getById(any(Integer.class));
		verify(linkTypeDao, times(2)).getById(any(Integer.class));
		verify(linkFileTypeDao, times(2)).getById(any(Integer.class));
	}

	@Test
	public void isValidFalseTest() {
		when(mpPublicationDao.getById(any(Integer.class))).thenReturn(null);
		when(linkTypeDao.getById(any(Integer.class))).thenReturn(null);
		when(linkFileTypeDao.getById(any(Integer.class))).thenReturn(null);
		mpPubLink.setLinkType(linkType);
		mpPubLink.setLinkFileType(linkFileType);

		//works with all set
		mpPubLink.setPublicationId(1);
		linkType.setId(1);
		linkFileType.setId(1);
		assertFalse(validator.isValid(mpPubLink, context));
		verify(mpPublicationDao).getById(any(Integer.class));
		verify(linkTypeDao).getById(any(Integer.class));
		verify(linkFileTypeDao).getById(any(Integer.class));

		//works with mpPublication set
		mpPubLink.setPublicationId(11);
		linkType.setId("");
		linkFileType.setId("");
		assertFalse(validator.isValid(mpPubLink, context));
		verify(mpPublicationDao, times(2)).getById(any(Integer.class));
		verify(linkTypeDao).getById(any(Integer.class));
		verify(linkFileTypeDao).getById(any(Integer.class));

		//works with linkType set
		mpPubLink.setPublicationId(null);
		linkType.setId(11);
		linkFileType.setId("");
		assertFalse(validator.isValid(mpPubLink, context));
		verify(mpPublicationDao, times(2)).getById(any(Integer.class));
		verify(linkTypeDao, times(2)).getById(any(Integer.class));
		verify(linkFileTypeDao).getById(any(Integer.class));

		//works with linkFileType set
		mpPubLink.setPublicationId(null);
		linkType.setId("");
		linkFileType.setId(11);
		assertFalse(validator.isValid(mpPubLink, context));
		verify(mpPublicationDao, times(2)).getById(any(Integer.class));
		verify(linkTypeDao, times(2)).getById(any(Integer.class));
		verify(linkFileTypeDao, times(2)).getById(any(Integer.class));
	}

}
