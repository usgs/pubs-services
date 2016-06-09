package gov.usgs.cida.pubs.validation.mp.crossproperty;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

public class CrossPropertyValidatorForMpPublicationTest extends BaseValidatorTest {

	CrossPropertyValidatorForMpPublication validator;

	@Test
	public void isValidTest() {
		validator = new CrossPropertyValidatorForMpPublication();
		Publication<MpPublication> mpPub = new MpPublication();
		PublicationSubtype pubSubtype = new PublicationSubtype();
		PublicationSeries pubSeries = new PublicationSeries();

		//NPE tests
		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(mpPub, null));
		
		//Nothing filled in is ok
		assertTrue(validator.isValid(mpPub, context));
		mpPub.setPublicationSubtype(pubSubtype);
		assertTrue(validator.isValid(mpPub, context));
		mpPub.setSeriesTitle(pubSeries);
		assertTrue(validator.isValid(mpPub, context));
		
		//Non-USGS Numbered = no errors
		pubSubtype.setId(1);
		assertTrue(validator.isValid(mpPub, context));
		pubSeries.setId(1);
		assertTrue(validator.isValid(mpPub, context));

		//USGS Numbered with Series Title is good
		pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		assertTrue(validator.isValid(mpPub, context));

		//USGS Numbered with out Series Title is bad
		mpPub.setSeriesTitle(null);
		assertFalse(validator.isValid(mpPub, context));
		mpPub.setSeriesTitle(new PublicationSeries());
		assertFalse(validator.isValid(mpPub, context));
	}

}
