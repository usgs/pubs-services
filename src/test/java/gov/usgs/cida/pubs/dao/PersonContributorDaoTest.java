package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;

@Category(IntegrationTest.class)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/affiliation.xml"),
	@DatabaseSetup("classpath:/testData/contributor.xml")
})
@DatabaseTearDown("classpath:/testCleanup/clearAll.xml")
public class PersonContributorDaoTest extends BaseSpringTest {

    @Test
    public void getByIdInteger() {
        //USGS Contributor
        Contributor<?> contributor = UsgsContributor.getDao().getById(1);
        ContributorDaoTest.assertContributor1(contributor);

        //Non-USGS Contributor
        contributor = OutsideContributor.getDao().getById(3);
        ContributorDaoTest.assertContributor3(contributor);
    }

    @Test
    public void getByIdString() {
        //USGS Contributor
        Contributor<?> contributor = UsgsContributor.getDao().getById("1");
        ContributorDaoTest.assertContributor1(contributor);

        //Non-USGS Contributor
        contributor = OutsideContributor.getDao().getById("3");
        ContributorDaoTest.assertContributor3(contributor);
    }

    @Test
    public void getByMap() {
        List<Contributor<?>> contributors = PersonContributor.getDao().getByMap(null);
        assertEquals(ContributorDaoTest.PERSON_CONTRIBUTOR_CNT, contributors.size());

        Map<String, Object> filters = new HashMap<>();
        filters.put("id", "1");
        contributors = PersonContributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(1, contributors.get(0).getId().intValue());

        filters.clear();
        filters.put("text", "con%");
        contributors = PersonContributor.getDao().getByMap(filters);
        assertEquals(2, contributors.size());
        boolean got1 = false;
        boolean got4 = false;
        for (Contributor<?> contributor : contributors) {
        	if (1 == contributor.getId()) {
        		got1 = true;
        	} else if (4 == contributor.getId()) {
        		got4 = true;
        	} else {
        		fail("Got wrong contributor" + contributor.getId());
        	}
        }
        assertTrue("Got 1", got1);
        assertTrue("Got 4", got4);

        filters.clear();
        filters.put("given", "con");
        contributors = PersonContributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(1, contributors.get(0).getId().intValue());

        filters.clear();
        filters.put("family", "con");
        contributors = PersonContributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(1, contributors.get(0).getId().intValue());

        filters.clear();
        filters.put("family", "out");
        contributors = PersonContributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        filters.put("given", "out");
        contributors = PersonContributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        filters.put("text", "out%");
        contributors = PersonContributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        filters.put("id", 3);
        contributors = PersonContributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        filters.put("ipdsContributorId", 1);
        contributors = PersonContributor.getDao().getByMap(filters);
        assertEquals(0, contributors.size());
    }

    @Test
    public void addUpdateDeleteTest() {
        //USGSContributor
        UsgsContributor person = new UsgsContributor();
        person.setFamily("family");
        person.setGiven("given");
        person.setSuffix("suffix");
        person.setEmail("email");
        person.setIpdsContributorId(12);
        person.setAffiliation(Affiliation.getDao().getById(1));
        UsgsContributor.getDao().add(person);
        UsgsContributor persisted = (UsgsContributor) UsgsContributor.getDao().getById(person.getId());
        assertDaoTestResults(UsgsContributor.class, person, persisted, ContributorDaoTest.IGNORE_PROPERTIES_PERSON, true, true);

        person.setFamily("family2");
        person.setGiven("given2");
        person.setSuffix("suffix2");
        person.setEmail("email2");
        person.setIpdsContributorId(122);
        person.setAffiliation(Affiliation.getDao().getById(2));
        UsgsContributor.getDao().update(person);
        persisted = (UsgsContributor) UsgsContributor.getDao().getById(person.getId());
        assertDaoTestResults(UsgsContributor.class, person, persisted, ContributorDaoTest.IGNORE_PROPERTIES_PERSON, true, true);

        //OutsideContributor
        OutsideContributor outperson = new OutsideContributor();
        outperson.setFamily("outfamily");
        outperson.setGiven("outgiven");
        outperson.setSuffix("outsuffix");
        outperson.setEmail("outemail");
        outperson.setIpdsContributorId(13);
        outperson.setAffiliation(Affiliation.getDao().getById(7));
        OutsideContributor.getDao().add(outperson);
        OutsideContributor outpersisted = (OutsideContributor) OutsideContributor.getDao().getById(outperson.getId());
        assertDaoTestResults(OutsideContributor.class, outperson, outpersisted, ContributorDaoTest.IGNORE_PROPERTIES_PERSON, true, true);

        outperson.setFamily("outfamily2");
        outperson.setGiven("outgiven2");
        outperson.setSuffix("outsuffix2");
        outperson.setEmail("outemail2");
        outperson.setIpdsContributorId(123);
        outperson.setAffiliation(Affiliation.getDao().getById(5));
        OutsideContributor.getDao().update(outperson);
        outpersisted = (OutsideContributor) OutsideContributor.getDao().getById(outperson.getId());
        assertDaoTestResults(OutsideContributor.class, outperson, outpersisted, ContributorDaoTest.IGNORE_PROPERTIES_PERSON, true, true);

        PersonContributor.getDao().deleteById(person.getId());
        assertNull(PersonContributor.getDao().getById(person.getId()));
    }

    @Test
    public void notImplemented() {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(PublicationDao.PROD_ID, 1);
            PersonContributor.getDao().getObjectCount(params);
            fail("Was able to get count.");
        } catch (Exception e) {
            assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
        }
    }

    public static PersonContributor<?> buildAPerson(final Integer personId, final String type) {
    	PersonContributor<?> newPerson = type=="USGS" ? new UsgsContributor() : new OutsideContributor();
    	newPerson.setId(personId);
    	return newPerson;
    }

}
