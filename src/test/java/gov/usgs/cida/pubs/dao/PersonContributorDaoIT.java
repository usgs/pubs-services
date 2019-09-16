package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, PersonContributorDao.class})
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/affiliation.xml"),
	@DatabaseSetup("classpath:/testData/contributor.xml")
})
public class PersonContributorDaoIT extends BaseIT {

	@Autowired
	PersonContributorDao personContributorDao;

	@Test
	public void getByIdInteger() {
		//USGS Contributor
		Contributor<?> contributor = personContributorDao.getById(1);
		ContributorDaoIT.assertContributor1(contributor);

		//Non-USGS Contributor
		contributor = personContributorDao.getById(3);
		ContributorDaoIT.assertContributor3(contributor);
	}

	@Test
	public void getByIdString() {
		//USGS Contributor
		Contributor<?> contributor = personContributorDao.getById("1");
		ContributorDaoIT.assertContributor1(contributor);

		//Non-USGS Contributor
		contributor = personContributorDao.getById("3");
		ContributorDaoIT.assertContributor3(contributor);
	}

	@Test
	public void getByMap() {
		List<Contributor<?>> contributors = personContributorDao.getByMap(null);
		assertEquals(ContributorDaoIT.PERSON_CONTRIBUTOR_CNT, contributors.size());

		Map<String, Object> filters = new HashMap<>();
		filters.put(PersonContributorDao.ID_SEARCH, 1);
		contributors = personContributorDao.getByMap(filters);
		assertEquals(1, contributors.size());
		assertEquals(1, contributors.get(0).getId().intValue());

		filters.clear();
		filters.put(PersonContributorDao.TEXT_SEARCH, "con:*");
		contributors = personContributorDao.getByMap(filters);
		assertEquals(3, contributors.size());
		for (Contributor<?> contributor : contributors) {
			if (!((PersonContributor<?>)contributor).getEmail().substring(0, 3).equalsIgnoreCase("con")) {
				fail("Got wrong contributor(Text) ID: " + contributor.getId());
			}
		}

		filters.clear();
		filters.put(PersonContributorDao.GIVEN, new String[]{"con"});
		contributors = personContributorDao.getByMap(filters);
		assertEquals(1, contributors.size());
		for (Contributor<?> contributor : contributors) {
			if (!((PersonContributor<?>)contributor).getGiven().substring(0, 3).equalsIgnoreCase("con")) {
				fail("Got wrong contributor Given: " + ((PersonContributor<?>)contributor).getGiven());
			}
		}

		filters.clear();
		filters.put(PersonContributorDao.FAMILY, new String[]{"con"});
		contributors = personContributorDao.getByMap(filters);
		assertEquals(1, contributors.size());
		for (Contributor<?> contributor : contributors) {
			if (!((PersonContributor<?>)contributor).getFamily().substring(0, 3).equalsIgnoreCase("con")) {
				fail("Got wrong contributor Family: " + ((PersonContributor<?>)contributor).getFamily());
			}
		}

		filters.clear();
		filters.put(PersonContributorDao.ORCID, new String[]{"0000-0000-0000-0004"});
		contributors = personContributorDao.getByMap(filters);
		assertEquals(5, contributors.size());
		for (Contributor<?> contributor : contributors) {
			if (!((PersonContributor<?>)contributor).getOrcid().equalsIgnoreCase("0000-0000-0000-0004")) {
				fail("Got wrong contributor ORCID: " + ((PersonContributor<?>)contributor).getOrcid());
			}
		}

		filters.clear();
		filters.put(PersonContributorDao.FAMILY, new String[]{"out"});
		contributors = personContributorDao.getByMap(filters);
		assertEquals(1, contributors.size());
		filters.put(PersonContributorDao.GIVEN, new String[]{"out"});
		contributors = personContributorDao.getByMap(filters);
		assertEquals(1, contributors.size());
		filters.put(PersonContributorDao.TEXT_SEARCH, "oute:*");
		contributors = personContributorDao.getByMap(filters);
		assertEquals(1, contributors.size());
		filters.put(PersonContributorDao.ID_SEARCH, 3);
		contributors = personContributorDao.getByMap(filters);
		assertEquals(1, contributors.size());
		filters.put(PersonContributorDao.ORCID, new String[]{"0000-0000-0000-0001"});
		contributors = personContributorDao.getByMap(filters);
		assertEquals(1, contributors.size());
	}

	@Test
	public void addUpdateDeleteTest() {
		//USGSContributor
		UsgsContributor person = new UsgsContributor();
		person.setFamily("family");
		person.setGiven("given");
		person.setSuffix("suffix");
		person.setEmail("email");
		person.setOrcid("0000-0002-1825-0097");
		person.setPreferred(true);
		personContributorDao.add(person);
		UsgsContributor persisted = (UsgsContributor) personContributorDao.getById(person.getId());
		assertDaoTestResults(UsgsContributor.class, person, persisted, ContributorDaoIT.IGNORE_PROPERTIES_PERSON, true, true);

		person.setFamily("family2");
		person.setGiven("given2");
		person.setSuffix("suffix2");
		person.setEmail("email2");
		person.setOrcid("0000-0002-1825-009X");
		person.setPreferred(false);
		personContributorDao.update(person);
		persisted = (UsgsContributor) personContributorDao.getById(person.getId());
		assertDaoTestResults(UsgsContributor.class, person, persisted, ContributorDaoIT.IGNORE_PROPERTIES_PERSON, true, true);

		//OutsideContributor
		OutsideContributor outperson = new OutsideContributor();
		outperson.setFamily("outfamily");
		outperson.setGiven("outgiven");
		outperson.setSuffix("outsuffix");
		outperson.setEmail("outemail");
		outperson.setOrcid("0000-0002-1825-0097");
		outperson.setPreferred(true);
		personContributorDao.add(outperson);
		OutsideContributor outpersisted = (OutsideContributor) personContributorDao.getById(outperson.getId());
		assertDaoTestResults(OutsideContributor.class, outperson, outpersisted, ContributorDaoIT.IGNORE_PROPERTIES_PERSON, true, true);

		outperson.setFamily("outfamily2");
		outperson.setGiven("outgiven2");
		outperson.setSuffix("outsuffix2");
		outperson.setEmail("outemail2");
		outperson.setOrcid("0000-0002-1825-009X");
		outperson.setPreferred(false);
		personContributorDao.update(outperson);
		outpersisted = (OutsideContributor) personContributorDao.getById(outperson.getId());
		assertDaoTestResults(OutsideContributor.class, outperson, outpersisted, ContributorDaoIT.IGNORE_PROPERTIES_PERSON, true, true);

		personContributorDao.deleteById(person.getId());
		assertNull(personContributorDao.getById(person.getId()));
	}

	@Test
	public void getObjectCount() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PersonContributorDao.ID_SEARCH, 1);
		assertEquals("One contrib with id = 1", 1, personContributorDao.getObjectCount(params).intValue());
	}

	@Test
	public void getByPreferredAll() {
		List<Contributor<?>> contributors = personContributorDao.getByPreferred(null);
		assertEquals(ContributorDaoIT.CONTRIBUTOR_CNT, contributors.size());
		List<Integer> ids = ContributorDaoIT.getIds(contributors);
		List<Integer> expectedIds = List.of(1, 2, 3, 4, 5, 11, 14, 21, 24, 34, 44, 51, 54, 60);
		assertTrue(String.format("getByPreferred(null) expected ids %s, got %s", ContributorDaoIT.toString(expectedIds), ContributorDaoIT.toString(ids)),
				ids.equals(expectedIds));
	}

	@Test
	public void getByPreferredByOrcid() {
		UsgsContributor filter = new UsgsContributor();
		filter.setOrcid("0000-0000-0000-0004");
		List<Contributor<?>> contributors = personContributorDao.getByPreferred(filter);
		List<Integer> ids = ContributorDaoIT.getIds(contributors);
		List<Integer> expectedIds = List.of(4, 14, 24, 34, 44);
		assertTrue(String.format("getByPreferredByOrcid(null) expected ids %s, got %s", ContributorDaoIT.toString(expectedIds), ContributorDaoIT.toString(ids)),
				ids.equals(expectedIds));
	}

	@Test
	public void getByPreferredByEmail() {
		UsgsContributor filter = new UsgsContributor();
		filter.setEmail("con@usgs.gov");;
		List<Contributor<?>> contributors = personContributorDao.getByPreferred(filter);
		List<Integer> ids = ContributorDaoIT.getIds(contributors);
		List<Integer> expectedIds = List.of(1, 11);
		assertTrue(String.format("getByPreferredByEmail(null) expected ids %s, got %s", ContributorDaoIT.toString(expectedIds), ContributorDaoIT.toString(ids)),
				ids.equals(expectedIds));
	}

	public static PersonContributor<?> buildAPerson(final Integer personId, final String type) {
		PersonContributor<?> newPerson = type=="USGS" ? new UsgsContributor() : new OutsideContributor();
		newPerson.setId(personId);
		return newPerson;
	}
}
