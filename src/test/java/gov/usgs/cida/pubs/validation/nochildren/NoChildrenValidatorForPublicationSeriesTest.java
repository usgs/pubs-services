package gov.usgs.cida.pubs.validation.nochildren;

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

import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={Publication.class})
public class NoChildrenValidatorForPublicationSeriesTest extends BaseValidatorTest {

	protected NoChildrenValidatorForPublicationSeries validator;
	protected PublicationSeries series;

	@MockBean(name="publicationDao")
	protected PublicationDao publicationDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		validator = new NoChildrenValidatorForPublicationSeries();
		series = new PublicationSeries();
		series.setId(1);

		reset(publicationDao);
	}

	@Test
	public void isValidNPETest() {
		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(series, null));
	}

	@Test
	public void isValidTrueTest() {
		when(publicationDao.getSeriesCount(any(Integer.class))).thenReturn(0);
		assertTrue(validator.isValid(series, context));
	}

	@Test
	public void isValidFalseTest() {
		when(publicationDao.getSeriesCount(any(Integer.class))).thenReturn(10);
		assertFalse(validator.isValid(series, context));
	}

}
