package gov.usgs.cida.pubs.validation.mp.unique;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

//The Dao mocking works because the getDao() methods are all static and JAVA/Spring don't redo them 
//for each reference. This does mean that we need to let Spring know that the context is now dirty...
@DirtiesContext(classMode=ClassMode.AFTER_EACH_TEST_METHOD)
public class UniqueKeyValidatorForMpPublicationTest extends BaseValidatorTest {

	protected UniqueKeyValidatorForMpPublication validator;
	protected Publication<?> mpPub;

	@MockBean
	protected PublicationDao publicationDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		validator = new UniqueKeyValidatorForMpPublication();
		mpPub = new MpPublication();
		mpPub.setPublicationDao(publicationDao);
	}

	@Test
	public void isValidNPETest() {
		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(mpPub, null));

		assertTrue(validator.isValid(mpPub, context));
	}

	@Test
	public void isValidAddTest() {
		when(publicationDao.validateByMap(anyMap())).thenReturn(new ArrayList<>());

		//With indexId
		mpPub.setIndexId("123");
		mpPub.setIpdsId(null);
		assertTrue(validator.isValid(mpPub, context));
		verify(publicationDao).validateByMap(anyMap());

		//With ipdsId
		mpPub.setIndexId(null);
		mpPub.setIpdsId("IPDS-456");
		assertTrue(validator.isValid(mpPub, context));
		verify(publicationDao, times(2)).validateByMap(anyMap());

		//With both
		mpPub.setIndexId("123");
		mpPub.setIpdsId("IPDS-456");
		assertTrue(validator.isValid(mpPub, context));
		verify(publicationDao, times(4)).validateByMap(anyMap());
	}

	@Test
	public void isValidAddFailTest() {
		when(publicationDao.validateByMap(anyMap())).thenReturn(buildList());

		//With indexId
		mpPub.setIndexId("123");
		mpPub.setIpdsId(null);
		assertFalse(validator.isValid(mpPub, context));
		verify(publicationDao).validateByMap(anyMap());

		//With ipdsId
		mpPub.setIndexId(null);
		mpPub.setIpdsId("IPDS-456");
		assertFalse(validator.isValid(mpPub, context));
		verify(publicationDao, times(2)).validateByMap(anyMap());

		//With both
		mpPub.setIndexId("123");
		mpPub.setIpdsId("IPDS-456");
		assertFalse(validator.isValid(mpPub, context));
		verify(publicationDao, times(4)).validateByMap(anyMap());
	}

	@Test
	public void isValidNoMatchTest() {
		when(publicationDao.validateByMap(anyMap())).thenReturn(new ArrayList<>());
		mpPub.setId(1);

		//With indexId
		mpPub.setIndexId("123");
		mpPub.setIpdsId(null);
		assertTrue(validator.isValid(mpPub, context));
		verify(publicationDao).validateByMap(anyMap());

		//With ipdsId
		mpPub.setIndexId(null);
		mpPub.setIpdsId("IPDS-456");
		assertTrue(validator.isValid(mpPub, context));
		verify(publicationDao, times(2)).validateByMap(anyMap());

		//With both
		mpPub.setIndexId("123");
		mpPub.setIpdsId("IPDS-456");
		assertTrue(validator.isValid(mpPub, context));
		verify(publicationDao, times(4)).validateByMap(anyMap());
	}

	@Test
	public void isValidMatchTest() {
		when(publicationDao.validateByMap(anyMap())).thenReturn(buildList());
		mpPub.setId(1);

		//With indexId
		mpPub.setIndexId("123");
		mpPub.setIpdsId(null);
		assertTrue(validator.isValid(mpPub, context));
		verify(publicationDao).validateByMap(anyMap());

		//With ipdsId
		mpPub.setIndexId(null);
		mpPub.setIpdsId("IPDS-456");
		assertTrue(validator.isValid(mpPub, context));
		verify(publicationDao, times(2)).validateByMap(anyMap());

		//With both
		mpPub.setIndexId("123");
		mpPub.setIpdsId("IPDS-456");
		assertTrue(validator.isValid(mpPub, context));
		verify(publicationDao, times(4)).validateByMap(anyMap());
	}

	@Test
	public void isValidFalseTest() {
		when(publicationDao.validateByMap(anyMap())).thenReturn(buildList());
		mpPub.setId(2);

		//With indexId
		mpPub.setIndexId("123");
		mpPub.setIpdsId(null);
		assertFalse(validator.isValid(mpPub, context));
		verify(publicationDao).validateByMap(anyMap());

		//With ipdsId
		mpPub.setIndexId(null);
		mpPub.setIpdsId("IPDS-456");
		assertFalse(validator.isValid(mpPub, context));
		verify(publicationDao, times(2)).validateByMap(anyMap());

		//With both
		mpPub.setIndexId("123");
		mpPub.setIpdsId("IPDS-456");
		assertFalse(validator.isValid(mpPub, context));
		verify(publicationDao, times(4)).validateByMap(anyMap());
	}

	public static List<Publication<?>> buildList() {
		List<Publication<?>> rtn = new ArrayList<>();
		Publication<?> pub = new Publication<>();
		pub.setId(1);
		rtn.add(pub);
		return rtn;
	}
}
