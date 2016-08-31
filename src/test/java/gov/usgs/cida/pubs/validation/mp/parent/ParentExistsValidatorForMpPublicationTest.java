package gov.usgs.cida.pubs.validation.mp.parent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.PublicationSeriesDao;
import gov.usgs.cida.pubs.dao.PublicationSubtypeDao;
import gov.usgs.cida.pubs.dao.PublicationTypeDao;
import gov.usgs.cida.pubs.dao.PublishingServiceCenterDao;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.PublishingServiceCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

//The Dao mocking works because the getDao() methods are all static and JAVA/Spring don't redo them 
//for each reference. This does mean that we need to let Spring know that the context is now dirty...
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class ParentExistsValidatorForMpPublicationTest extends BaseValidatorTest {

	protected ParentExistsValidatorForMpPublication validator;
	protected Publication<?> mpPub;
	protected PublicationType pubType;
	protected PublicationSubtype pubSubtype;
	protected PublicationSeries pubSeries;
	protected PublicationType largerWorkType;
	protected Publication<?> po;
	protected Publication<?> sb;
	protected PublishingServiceCenter psc;

	@Mock
	protected PublicationTypeDao publicationTypeDao;
	@Mock
	protected PublicationSubtypeDao publicationSubtypeDao;
	@Mock
	protected PublicationSeriesDao publicationSeriesDao;
	@Mock
	protected PublicationDao publicationDao;
	@Mock
	protected PublishingServiceCenterDao publishingServiceCenterDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		validator = new ParentExistsValidatorForMpPublication();
		mpPub = new MpPublication();
		mpPub.setPublicationDao(publicationDao);
		pubType = new PublicationType();
		pubType.setPublicationTypeDao(publicationTypeDao);
		pubSubtype = new PublicationSubtype();
		pubSubtype.setPublicationSubtypeDao(publicationSubtypeDao);
		pubSeries = new PublicationSeries();
		pubSeries.setPublicationSeriesDao(publicationSeriesDao);
		largerWorkType = new PublicationType();
		po = new MpPublication();
		sb = new MpPublication();
		psc = new PublishingServiceCenter();
		psc.setPublishingServiceCenterDao(publishingServiceCenterDao);
	}

	@Test
	public void isValidNPETest() {
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

		mpPub.setPublishingServiceCenter(psc);
		assertTrue(validator.isValid(mpPub, context));
}

	@Test
	public void isValidTrueTest() {
		when(publicationDao.getById(any(Integer.class))).thenReturn(new Publication<>());
		when(publicationTypeDao.getById(any(Integer.class))).thenReturn(new PublicationType());
		when(publicationSubtypeDao.getById(any(Integer.class))).thenReturn(new PublicationSubtype());
		when(publicationSeriesDao.getById(any(Integer.class))).thenReturn(new PublicationSeries());
		when(publishingServiceCenterDao.getById(any(Integer.class))).thenReturn(new PublishingServiceCenter());

		mpPub.setPublicationType(pubType);
		mpPub.setPublicationSubtype(pubSubtype);
		mpPub.setSeriesTitle(pubSeries);
		mpPub.setLargerWorkType(largerWorkType);
		mpPub.setIsPartOf(po);
		mpPub.setSupersededBy(sb);
		mpPub.setPublishingServiceCenter(psc);

		//Works with all set
		pubType.setId(1);
		pubSubtype.setId(1);
		pubSeries.setId(1);
		largerWorkType.setId(1);
		po.setId(1);
		sb.setId(1);
		psc.setId(1);
		assertTrue(validator.isValid(mpPub, context));
		verify(publicationDao, times(2)).getById(any(Integer.class));
		verify(publicationTypeDao, times(2)).getById(any(Integer.class));
		verify(publicationSubtypeDao).getById(any(Integer.class));
		verify(publicationSeriesDao).getById(any(Integer.class));
		verify(publishingServiceCenterDao).getById(any(Integer.class));

		//Works with pubType set
		pubType.setId(1);
		pubSubtype.setId("");
		pubSeries.setId("");
		largerWorkType.setId("");
		po.setId("");
		sb.setId("");
		psc.setId("");
		assertTrue(validator.isValid(mpPub, context));
		verify(publicationDao, times(2)).getById(any(Integer.class));
		verify(publicationTypeDao, times(3)).getById(any(Integer.class));
		verify(publicationSubtypeDao).getById(any(Integer.class));
		verify(publicationSeriesDao).getById(any(Integer.class));
		verify(publishingServiceCenterDao).getById(any(Integer.class));

		//Works with pubSubtype set
		pubType.setId("");
		pubSubtype.setId(1);
		pubSeries.setId("");
		largerWorkType.setId("");
		po.setId("");
		sb.setId("");
		psc.setId("");
		assertTrue(validator.isValid(mpPub, context));
		verify(publicationDao, times(2)).getById(any(Integer.class));
		verify(publicationTypeDao, times(3)).getById(any(Integer.class));
		verify(publicationSubtypeDao, times(2)).getById(any(Integer.class));
		verify(publicationSeriesDao).getById(any(Integer.class));
		verify(publishingServiceCenterDao).getById(any(Integer.class));

		//Works with pubSeries set
		pubType.setId("");
		pubSubtype.setId("");
		pubSeries.setId(1);
		largerWorkType.setId("");
		po.setId("");
		sb.setId("");
		psc.setId("");
		assertTrue(validator.isValid(mpPub, context));
		verify(publicationDao, times(2)).getById(any(Integer.class));
		verify(publicationTypeDao, times(3)).getById(any(Integer.class));
		verify(publicationSubtypeDao, times(2)).getById(any(Integer.class));
		verify(publicationSeriesDao, times(2)).getById(any(Integer.class));
		verify(publishingServiceCenterDao).getById(any(Integer.class));

		//Works with largerWorkType set
		pubType.setId("");
		pubSubtype.setId("");
		pubSeries.setId("");
		largerWorkType.setId(1);
		po.setId("");
		sb.setId("");
		psc.setId("");
		assertTrue(validator.isValid(mpPub, context));
		verify(publicationDao, times(2)).getById(any(Integer.class));
		verify(publicationTypeDao, times(4)).getById(any(Integer.class));
		verify(publicationSubtypeDao, times(2)).getById(any(Integer.class));
		verify(publicationSeriesDao, times(2)).getById(any(Integer.class));
		verify(publishingServiceCenterDao).getById(any(Integer.class));

		//Works with partOf set
		pubType.setId("");
		pubSubtype.setId("");
		pubSeries.setId("");
		largerWorkType.setId("");
		po.setId(1);
		sb.setId("");
		psc.setId("");
		assertTrue(validator.isValid(mpPub, context));
		verify(publicationDao, times(3)).getById(any(Integer.class));
		verify(publicationTypeDao, times(4)).getById(any(Integer.class));
		verify(publicationSubtypeDao, times(2)).getById(any(Integer.class));
		verify(publicationSeriesDao, times(2)).getById(any(Integer.class));
		verify(publishingServiceCenterDao).getById(any(Integer.class));

		//Works with supercededBy set
		pubType.setId("");
		pubSubtype.setId("");
		pubSeries.setId("");
		largerWorkType.setId("");
		po.setId("");
		sb.setId(1);
		psc.setId("");
		assertTrue(validator.isValid(mpPub, context));
		verify(publicationDao, times(4)).getById(any(Integer.class));
		verify(publicationTypeDao, times(4)).getById(any(Integer.class));
		verify(publicationSubtypeDao, times(2)).getById(any(Integer.class));
		verify(publicationSeriesDao, times(2)).getById(any(Integer.class));
		verify(publishingServiceCenterDao).getById(any(Integer.class));

		//Works with publishingServiceCenter set
		pubType.setId("");
		pubSubtype.setId("");
		pubSeries.setId("");
		largerWorkType.setId("");
		po.setId("");
		sb.setId("");
		psc.setId(1);
		assertTrue(validator.isValid(mpPub, context));
		verify(publicationDao, times(4)).getById(any(Integer.class));
		verify(publicationTypeDao, times(4)).getById(any(Integer.class));
		verify(publicationSubtypeDao, times(2)).getById(any(Integer.class));
		verify(publicationSeriesDao, times(2)).getById(any(Integer.class));
		verify(publishingServiceCenterDao, times(2)).getById(any(Integer.class));
	}

	@Test
	public void isValidFalseTest() {
		when(publicationDao.getById(any(Integer.class))).thenReturn(null);
		when(publicationTypeDao.getById(any(Integer.class))).thenReturn(null);
		when(publicationSubtypeDao.getById(any(Integer.class))).thenReturn(null);
		when(publicationSeriesDao.getById(any(Integer.class))).thenReturn(null);
		when(publishingServiceCenterDao.getById(any(Integer.class))).thenReturn(null);

		mpPub.setPublicationType(pubType);
		mpPub.setPublicationSubtype(pubSubtype);
		mpPub.setSeriesTitle(pubSeries);
		mpPub.setLargerWorkType(largerWorkType);
		mpPub.setIsPartOf(po);
		mpPub.setSupersededBy(sb);
		mpPub.setPublishingServiceCenter(psc);

		//Works with all set
		pubType.setId(1);
		pubSubtype.setId(1);
		pubSeries.setId(1);
		largerWorkType.setId(1);
		po.setId(1);
		sb.setId(1);
		psc.setId(1);
		assertFalse(validator.isValid(mpPub, context));
		verify(publicationDao, times(2)).getById(any(Integer.class));
		verify(publicationTypeDao, times(2)).getById(any(Integer.class));
		verify(publicationSubtypeDao).getById(any(Integer.class));
		verify(publicationSeriesDao).getById(any(Integer.class));
		verify(publishingServiceCenterDao).getById(any(Integer.class));

		//Works with pubType set
		pubType.setId(1);
		pubSubtype.setId("");
		pubSeries.setId("");
		largerWorkType.setId("");
		po.setId("");
		sb.setId("");
		psc.setId("");
		assertFalse(validator.isValid(mpPub, context));
		verify(publicationDao, times(2)).getById(any(Integer.class));
		verify(publicationTypeDao, times(3)).getById(any(Integer.class));
		verify(publicationSubtypeDao).getById(any(Integer.class));
		verify(publicationSeriesDao).getById(any(Integer.class));
		verify(publishingServiceCenterDao).getById(any(Integer.class));

		//Works with pubSubtype set
		pubType.setId("");
		pubSubtype.setId(1);
		pubSeries.setId("");
		largerWorkType.setId("");
		po.setId("");
		sb.setId("");
		psc.setId("");
		assertFalse(validator.isValid(mpPub, context));
		verify(publicationDao, times(2)).getById(any(Integer.class));
		verify(publicationTypeDao, times(3)).getById(any(Integer.class));
		verify(publicationSubtypeDao, times(2)).getById(any(Integer.class));
		verify(publicationSeriesDao).getById(any(Integer.class));
		verify(publishingServiceCenterDao).getById(any(Integer.class));

		//Works with pubSeries set
		pubType.setId("");
		pubSubtype.setId("");
		pubSeries.setId(1);
		largerWorkType.setId("");
		po.setId("");
		sb.setId("");
		psc.setId("");
		assertFalse(validator.isValid(mpPub, context));
		verify(publicationDao, times(2)).getById(any(Integer.class));
		verify(publicationTypeDao, times(3)).getById(any(Integer.class));
		verify(publicationSubtypeDao, times(2)).getById(any(Integer.class));
		verify(publicationSeriesDao, times(2)).getById(any(Integer.class));
		verify(publishingServiceCenterDao).getById(any(Integer.class));

		//Works with largerWorkType set
		pubType.setId("");
		pubSubtype.setId("");
		pubSeries.setId("");
		largerWorkType.setId(1);
		po.setId("");
		sb.setId("");
		psc.setId("");
		assertFalse(validator.isValid(mpPub, context));
		verify(publicationDao, times(2)).getById(any(Integer.class));
		verify(publicationTypeDao, times(4)).getById(any(Integer.class));
		verify(publicationSubtypeDao, times(2)).getById(any(Integer.class));
		verify(publicationSeriesDao, times(2)).getById(any(Integer.class));
		verify(publishingServiceCenterDao).getById(any(Integer.class));

		//Works with partOf set
		pubType.setId("");
		pubSubtype.setId("");
		pubSeries.setId("");
		largerWorkType.setId("");
		po.setId(1);
		sb.setId("");
		psc.setId("");
		assertFalse(validator.isValid(mpPub, context));
		verify(publicationDao, times(3)).getById(any(Integer.class));
		verify(publicationTypeDao, times(4)).getById(any(Integer.class));
		verify(publicationSubtypeDao, times(2)).getById(any(Integer.class));
		verify(publicationSeriesDao, times(2)).getById(any(Integer.class));
		verify(publishingServiceCenterDao).getById(any(Integer.class));

		//Works with supercededBy set
		pubType.setId("");
		pubSubtype.setId("");
		pubSeries.setId("");
		largerWorkType.setId("");
		po.setId("");
		sb.setId(1);
		psc.setId("");
		assertFalse(validator.isValid(mpPub, context));
		verify(publicationDao, times(4)).getById(any(Integer.class));
		verify(publicationTypeDao, times(4)).getById(any(Integer.class));
		verify(publicationSubtypeDao, times(2)).getById(any(Integer.class));
		verify(publicationSeriesDao, times(2)).getById(any(Integer.class));
		verify(publishingServiceCenterDao).getById(any(Integer.class));

		//Works with publishingServiceCenter set
		pubType.setId("");
		pubSubtype.setId("");
		pubSeries.setId("");
		largerWorkType.setId("");
		po.setId("");
		sb.setId("");
		psc.setId(1);
		assertFalse(validator.isValid(mpPub, context));
		verify(publicationDao, times(4)).getById(any(Integer.class));
		verify(publicationTypeDao, times(4)).getById(any(Integer.class));
		verify(publicationSubtypeDao, times(2)).getById(any(Integer.class));
		verify(publicationSeriesDao, times(2)).getById(any(Integer.class));
		verify(publishingServiceCenterDao, times(2)).getById(any(Integer.class));
	}

}
