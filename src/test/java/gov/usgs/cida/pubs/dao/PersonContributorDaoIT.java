package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
		// verify that 'order by' in the query returned results in the expected order
		assertEquals(51, contributors.get(0).getId().intValue());
		assertEquals(54, contributors.get(1).getId().intValue());
		assertEquals(4, contributors.get(2).getId().intValue());
		assertEquals(1, contributors.get(3).getId().intValue());
		assertEquals(5, contributors.get(4).getId().intValue());
		assertEquals(60, contributors.get(5).getId().intValue());
		assertEquals(11, contributors.get(6).getId().intValue());
		assertEquals(21, contributors.get(7).getId().intValue());
		assertEquals(14, contributors.get(8).getId().intValue());
		assertEquals(24, contributors.get(9).getId().intValue());
		assertEquals(34, contributors.get(10).getId().intValue());
		assertEquals(44, contributors.get(11).getId().intValue());
		assertEquals(3, contributors.get(12).getId().intValue());
		assertEquals(2, contributors.get(13).getId().intValue());
	}

	@Test
	public void getByPreferredByOrcid() {
		UsgsContributor filter = new UsgsContributor();
		filter.setOrcid("0000-0000-0000-0004");
		List<Contributor<?>> contributors = personContributorDao.getByPreferred(filter);
		assertEquals(5, contributors.size());
		// verify that 'order by' in the query returned results in the expected order
		assertEquals(4, contributors.get(0).getId().intValue());
		assertEquals(14, contributors.get(1).getId().intValue());
		assertEquals(24, contributors.get(2).getId().intValue());
		assertEquals(34, contributors.get(3).getId().intValue());
		assertEquals(44, contributors.get(4).getId().intValue());
	}

	@Test
	public void getByPreferredByEmail() {
		UsgsContributor filter = new UsgsContributor();
		filter.setEmail("con@usgs.gov");;
		List<Contributor<?>> contributors = personContributorDao.getByPreferred(filter);
		// verify that 'order by' in the query returned results in the expected order
		assertEquals(2, contributors.size());
		assertEquals(1, contributors.get(0).getId().intValue());
		assertEquals(11, contributors.get(1).getId().intValue());
	}

	public static PersonContributor<?> buildAPerson(final Integer personId, final String type) {
		PersonContributor<?> newPerson = type=="USGS" ? new UsgsContributor() : new OutsideContributor();
		newPerson.setId(personId);
		return newPerson;
	}
}
