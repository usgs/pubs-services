package gov.usgs.cida.pubs.busservice.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.dao.ContributorDao;
import gov.usgs.cida.pubs.dao.ContributorTypeDao;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationContributorDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDao;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.domain.pw.PwPublicationContributor;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, LocalValidatorFactoryBean.class,
			MpPublication.class, MpPublicationDao.class, PublicationDao.class,
			MpPublicationContributor.class, MpPublicationContributorDao.class,
			Contributor.class, ContributorDao.class, ContributorType.class, ContributorTypeDao.class})
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/dataset.xml")
})
public class MpPublicationContributorBusServiceIT extends BaseIT {

	@Autowired
	public Validator validator;

	private MpPublicationContributorBusService busService;

	@Before
	public void initTest() throws Exception {
		MockitoAnnotations.initMocks(this);
		busService = new MpPublicationContributorBusService(validator);
	}

	@Test
	public void mergeAndDeleteTest() {
		MpPublication mpPub = MpPublication.getDao().getById(2);
		Integer id = MpPublication.getDao().getNewProdId();
		mpPub.setId(id);
		mpPub.setIndexId(String.valueOf(id));
		mpPub.setIpdsId("ipds_" + id);
		mpPub.setContributors(null);
		MpPublication.getDao().add(mpPub);

		//update with no contributors either side
		busService.merge(id, null);
		Map<String, Object> filters = new HashMap<>();
		filters.put("publicationId", id);
		assertEquals(0, MpPublicationContributor.getDao().getByMap(filters).size());
		busService.merge(id, new ArrayList<PublicationContributor<?>>());
		assertEquals(0, MpPublicationContributor.getDao().getByMap(filters).size());

		//Add some contributors
		Collection<PublicationContributor<?>> mpContributors = new ArrayList<>();
		PwPublicationContributor mpContributor1 = new PwPublicationContributor();
		mpContributor1.setContributor(Contributor.getDao().getById(1));
		ContributorType contributorType = ContributorType.getDao().getById(1);
		mpContributor1.setContributorType(contributorType);
		mpContributor1.setRank(1);
		mpContributors.add(mpContributor1);
		PublicationContributor<?> mpContributor2 = new PublicationContributor<MpPublicationContributor>();
		mpContributor2.setContributor(Contributor.getDao().getById(2));
		mpContributor2.setContributorType(contributorType);
		mpContributors.add(mpContributor2);
		mpContributor2.setRank(2);
		busService.merge(id, mpContributors);
		Collection<MpPublicationContributor> addedContributors = MpPublicationContributor.getDao().getByMap(filters);
		assertEquals(2, addedContributors.size());
		Map<Integer, Object> contributorMap = new HashMap<>();
		boolean gotOne = false;
		boolean gotTwo = false;
		for (MpPublicationContributor contributor : addedContributors) {
			assertEquals(id, contributor.getPublicationId());
			contributorMap.put(contributor.getId(), contributor);
			if (1 == contributor.getContributor().getId()) {
				gotOne = true;
			} else if (2 == contributor.getContributor().getId()) {
				gotTwo = true;
			}
		}
		assertTrue(gotOne);
		assertTrue(gotTwo);

		//Now add one, take one away, and update one.
		mpContributors = new ArrayList<>();
		int contributor2Id = mpContributor2.getId();
		mpContributor2.setRank(4);
		mpContributors.add(mpContributor2);
		MpPublicationContributor mpContributor3 = new MpPublicationContributor();
		mpContributor3.setContributor(Contributor.getDao().getById(3));
		mpContributor3.setContributorType(contributorType);
		mpContributor3.setRank(3);
		mpContributors.add(mpContributor3);
		busService.merge(id, mpContributors);
		Collection<MpPublicationContributor> updContributors = MpPublicationContributor.getDao().getByMap(filters);
		assertEquals(2, updContributors.size());
		gotOne = false;
		gotTwo = false;
		boolean gotThree = false;
		for (MpPublicationContributor contributor : updContributors) {
			assertEquals(id, contributor.getPublicationId());
			if (1 == contributor.getContributor().getId()) {
				gotOne = true;
			} else if (2 == contributor.getContributor().getId()) {
				gotTwo = true;
				assertEquals(contributor2Id, contributor.getId().intValue());
				assertEquals(4, contributor.getRank().intValue());
			} else if (3 == contributor.getContributor().getId()) {
				gotThree = true;
			}
		}
		assertFalse(gotOne);
		assertTrue(gotTwo);
		assertTrue(gotThree);

		//Now do a straight delete.
		busService.deleteObject(mpContributor3);
		updContributors = MpPublicationContributor.getDao().getByMap(filters);
		assertEquals(1, updContributors.size());
		gotOne = false;
		gotTwo = false;
		gotThree = false;
		for (MpPublicationContributor contributor : updContributors) {
			assertEquals(id, contributor.getPublicationId());
			if (1 == contributor.getContributor().getId()) {
				gotOne = true;
			} else if (2 == contributor.getContributor().getId()) {
				gotTwo = true;
			} else if (3 == contributor.getContributor().getId()) {
				gotThree = true;
			}
		}
		assertFalse(gotOne);
		assertTrue(gotTwo);
		assertFalse(gotThree);
	}

}
