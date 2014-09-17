package gov.usgs.cida.pubs.validation.mp.parent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.CorporateContributor;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

import org.junit.Test;

public class ParentExistsValidatorForMpPublicationContributorTest extends BaseValidatorTest {

	ParentExistsValidatorForMpPublicationContributor validator;

	@Test
	public void isValidTest() {
		validator = new ParentExistsValidatorForMpPublicationContributor();
		PublicationContributor<MpPublicationContributor> mpPubContributor = new MpPublicationContributor();
		Contributor<?> contributor = new CorporateContributor();
		ContributorType type = new ContributorType();

		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(mpPubContributor, null));
		
		assertTrue(validator.isValid(mpPubContributor, context));
		
		mpPubContributor.setContributor(contributor);
		assertTrue(validator.isValid(mpPubContributor, context));
		
		mpPubContributor.setContributorType(type);
		assertTrue(validator.isValid(mpPubContributor, context));
		
		mpPubContributor.setPublicationId(1);
		assertTrue(validator.isValid(mpPubContributor, context));

		mpPubContributor.setPublicationId(-1);
		assertFalse(validator.isValid(mpPubContributor, context));
		
		mpPubContributor.setPublicationId(null);
		contributor.setId(1);
		assertTrue(validator.isValid(mpPubContributor, context));
		
		contributor.setId(-1);
		assertFalse(validator.isValid(mpPubContributor, context));
		
		contributor.setId("");
		type.setId(1);
		assertTrue(validator.isValid(mpPubContributor, context));
		
		type.setId(-1);
		assertFalse(validator.isValid(mpPubContributor, context));
		
		mpPubContributor.setPublicationId(-1);
		contributor.setId(-1);
		assertFalse(validator.isValid(mpPubContributor, context));
	}

}
