package gov.usgs.cida.pubs.validation.mp.parent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

import org.junit.Test;

public class ParentExistsValidatorForMpPublicationTest extends BaseValidatorTest {

	ParentExistsValidatorForMpPublication validator;

	@Test
	public void isValidTest() {
		validator = new ParentExistsValidatorForMpPublication();
		Publication<MpPublication> mpPub = new MpPublication();
		PublicationType pubType = new PublicationType();
		PublicationSubtype pubSubtype = new PublicationSubtype();
		PublicationSeries pubSeries = new PublicationSeries();
		PublicationType largerWorkType = new PublicationType();
		Publication<?> po = new MpPublication();
		Publication<?> sb = new MpPublication();

		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(mpPub, null));
		
		assertTrue(validator.isValid(mpPub, context));
		
		mpPub.setPublicationType(pubType);
		assertTrue(validator.isValid(mpPub, context));
		
		mpPub.setPublicationSubtype(pubSubtype);
		assertTrue(validator.isValid(mpPub, context));
		
		mpPub.setSeriesTitle(pubSeries);
		assertTrue(validator.isValid(mpPub, context));
		
		mpPub.setLargerWorkType(largerWorkType);
		assertTrue(validator.isValid(mpPub, context));
		
		mpPub.setIsPartOf(po);
		assertTrue(validator.isValid(mpPub, context));
		
		mpPub.setSupersededBy(sb);
		assertTrue(validator.isValid(mpPub, context));
		
		pubType.setId(2);
		assertTrue(validator.isValid(mpPub, context));

		pubType.setId(-1);
		assertFalse(validator.isValid(mpPub, context));
		
		pubType.setId("");
		pubSubtype.setId(1);
		assertTrue(validator.isValid(mpPub, context));
		
		pubSubtype.setId(-1);
		assertFalse(validator.isValid(mpPub, context));

		pubSubtype.setId("");
		pubSeries.setId(1);
		assertTrue(validator.isValid(mpPub, context));

		pubSeries.setId(-1);
		assertFalse(validator.isValid(mpPub, context));

		pubSeries.setId("");
		largerWorkType.setId(2);
		assertTrue(validator.isValid(mpPub, context));

		largerWorkType.setId(-1);
		assertFalse(validator.isValid(mpPub, context));

		largerWorkType.setId("");
		po.setId(1);
		assertTrue(validator.isValid(mpPub, context));

		po.setId(-1);
		assertFalse(validator.isValid(mpPub, context));

		po.setId("");
		sb.setId(2);
		assertTrue(validator.isValid(mpPub, context));

		sb.setId(-1);
		assertFalse(validator.isValid(mpPub, context));

		pubType.setId(-1);
		pubSubtype.setId(-1);
		pubSeries.setId(-1);
		largerWorkType.setId(-1);
		po.setId(-1);
		sb.setId(-1);
		assertFalse(validator.isValid(mpPub, context));
	}

}
