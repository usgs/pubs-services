package gov.usgs.cida.pubs.validation.nochildren;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

//The Dao mocking works because the getDao() methods are all static and JAVA/Spring don't redo them 
//for each reference. This does mean that we nned to let Spring know that the context is now dirty...
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class NoChildrenValidatorForPublicationSeriesTest extends BaseValidatorTest {

	protected NoChildrenValidatorForPublicationSeries validator;
	protected Publication<?> pub;
	protected PublicationSeries series;

	@Mock
	protected PublicationDao publicationDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		validator = new NoChildrenValidatorForPublicationSeries();
		series = new PublicationSeries();
		pub = new Publication<>();
		pub.setPublicationDao(publicationDao);
	}

	@Test
	public void isValidNPETest() {
		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(series, null));
	}

	@Test
	public void isValidTrueTest() {
		when(publicationDao.getObjectCount(anyMapOf(String.class, Object.class))).thenReturn(0);
		assertTrue(validator.isValid(series, context));
	}

	@Test
	public void isValidFalseTest() {
		when(publicationDao.getObjectCount(anyMapOf(String.class, Object.class))).thenReturn(10);
		assertFalse(validator.isValid(series, context));
	}

}
