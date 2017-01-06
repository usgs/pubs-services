package gov.usgs.cida.pubs.validation.parent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import gov.usgs.cida.pubs.dao.PublicationSubtypeDao;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

//The Dao mocking works because the getDao() methods are all static and JAVA/Spring don't redo them 
//for each reference. This does mean that we need to let Spring know that the context is now dirty...
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class ParentExistsValidatorForPublicationSeriesTest extends BaseValidatorTest {

	protected ParentExistsValidatorForPublicationSeries validator;
	protected PublicationSubtype subtype;
	protected PublicationSeries series;

	@Mock
	protected PublicationSubtypeDao publicationSubtypeDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		validator = new ParentExistsValidatorForPublicationSeries();
		series = new PublicationSeries();
		subtype = new PublicationSubtype();
		subtype.setId(999);
		subtype.setPublicationSubtypeDao(publicationSubtypeDao);
	}

	@Test
	public void isValidNPETest() {
		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(series, null));
		assertTrue(validator.isValid(series, context));
	}

	@Test
	public void isValidTrueTest() {
		when(publicationSubtypeDao.getById(any(Integer.class))).thenReturn(new PublicationSubtype());
		series.setPublicationSubtype(subtype);
		assertTrue(validator.isValid(series, context));
	}

	@Test
	public void isValidFalseTest() {
		when(publicationSubtypeDao.getById(any(Integer.class))).thenReturn(null);
		series.setPublicationSubtype(subtype);
		assertFalse(validator.isValid(series, context));
	}

}
