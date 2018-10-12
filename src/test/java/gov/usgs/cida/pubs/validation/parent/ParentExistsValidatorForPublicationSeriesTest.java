package gov.usgs.cida.pubs.validation.parent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import gov.usgs.cida.pubs.dao.PublicationSubtypeDao;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={PublicationSubtype.class})
public class ParentExistsValidatorForPublicationSeriesTest extends BaseValidatorTest {

	protected ParentExistsValidatorForPublicationSeries validator;
	protected PublicationSubtype subtype;
	protected PublicationSeries series;

	@MockBean(name="publicationSubtypeDao")
	protected PublicationSubtypeDao publicationSubtypeDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		validator = new ParentExistsValidatorForPublicationSeries();
		series = new PublicationSeries();
		subtype = new PublicationSubtype();
		subtype.setId(999);

		reset(publicationSubtypeDao);
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
