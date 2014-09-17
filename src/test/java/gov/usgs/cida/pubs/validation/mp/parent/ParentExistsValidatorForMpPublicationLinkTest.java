package gov.usgs.cida.pubs.validation.mp.parent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.domain.LinkFileType;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

import org.junit.Test;

public class ParentExistsValidatorForMpPublicationLinkTest extends BaseValidatorTest {

	ParentExistsValidatorForMpPublicationLink validator;

	@Test
	public void isValidTest() {
		validator = new ParentExistsValidatorForMpPublicationLink();
		PublicationLink<MpPublicationLink> mpPubLink = new MpPublicationLink();
		LinkType linkType = new LinkType();
		LinkFileType linkFileType = new LinkFileType();

		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(mpPubLink, null));
		
		assertTrue(validator.isValid(mpPubLink, context));
		
		mpPubLink.setLinkType(linkType);
		assertTrue(validator.isValid(mpPubLink, context));
		
		mpPubLink.setLinkFileType(linkFileType);
		assertTrue(validator.isValid(mpPubLink, context));
		
		mpPubLink.setPublicationId(1);
		assertTrue(validator.isValid(mpPubLink, context));

		mpPubLink.setPublicationId(-1);
		assertFalse(validator.isValid(mpPubLink, context));
		
		mpPubLink.setPublicationId(null);
		linkType.setId(1);
		assertTrue(validator.isValid(mpPubLink, context));
		
		linkType.setId(-1);
		assertFalse(validator.isValid(mpPubLink, context));
		
		linkType.setId("");
		linkFileType.setId(1);
		assertTrue(validator.isValid(mpPubLink, context));

		linkFileType.setId(-1);
		assertFalse(validator.isValid(mpPubLink, context));

		linkType.setId(-1);
		mpPubLink.setPublicationId(-1);
		assertFalse(validator.isValid(mpPubLink, context));
	}

}
