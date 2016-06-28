package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
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
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.CorporateContributor;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;

@Category(IntegrationTest.class)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/affiliation.xml"),
	@DatabaseSetup("classpath:/testData/contributor.xml")
})
@DatabaseTearDown("classpath:/testCleanup/clearAll.xml")
public class ContributorDaoTest extends BaseSpringTest {

    public static final int CONTRIBUTOR_CNT = 4;
	public static final int PERSON_CONTRIBUTOR_CNT = 3;
	public static final int CORPORATE_CONTRIBUTOR_CNT = 1;

    public static final List<String> IGNORE_PROPERTIES_PERSON = Arrays.asList("validationErrors", "valErrors", "organization");
    public static final List<String> IGNORE_PROPERTIES_CORPORATION = Arrays.asList("validationErrors", "valErrors", "family", 
            "given", "suffix", "email", "affiliation");

    @Test
    public void getByIdInteger() {
        //USGS Contributor
        Contributor<?> contributor = Contributor.getDao().getById(1);
        assertContributor1(contributor);

        //Non-USGS Contributor
        contributor = Contributor.getDao().getById(3);
        assertContributor3(contributor);

        //Corporate Contributor
        contributor = Contributor.getDao().getById(2);
        assertEquals(2, contributor.getId().intValue());
        assertTrue(contributor instanceof CorporateContributor);
        CorporateContributor corpContributor = (CorporateContributor) contributor;
        assertEquals("US Geological Survey Ice Survey Team", corpContributor.getOrganization());
    }

    @Test
    public void getByIdString() {
        //USGS Contributor
        Contributor<?> contributor = Contributor.getDao().getById("1");
        assertContributor1(contributor);

        //Non-USGS Contributor
        contributor = Contributor.getDao().getById("3");
        assertContributor3(contributor);

        //Corporate Contributor
        contributor = Contributor.getDao().getById("2");
        assertEquals(2, contributor.getId().intValue());
        assertTrue(contributor instanceof CorporateContributor);
        CorporateContributor corpContributor = (CorporateContributor) contributor;
        assertEquals("US Geological Survey Ice Survey Team", corpContributor.getOrganization());
    }

    @Test
    public void getByMap() {
        List<Contributor<?>> contributors = Contributor.getDao().getByMap(null);
        assertEquals(CONTRIBUTOR_CNT, contributors.size());

        Map<String, Object> filters = new HashMap<>();
        filters.put("id", "1");
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(1, contributors.get(0).getId().intValue());

        filters.clear();
        filters.put("text", "con%");
        contributors = Contributor.getDao().getByMap(filters);
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
        filters.put("text", "us% and ge%");
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(2, contributors.get(0).getId().intValue());

        filters.clear();
        filters.put("given", "con");
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(1, contributors.get(0).getId().intValue());

        filters.clear();
        filters.put("family", "con");
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(1, contributors.get(0).getId().intValue());

        filters.clear();
        filters.put("ipdsContributorId", 3);
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(1, contributors.get(0).getId().intValue());

        filters.clear();
        filters.put("corporation", false);
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(PERSON_CONTRIBUTOR_CNT, contributors.size());
        assertEquals(4, contributors.get(0).getId().intValue());
        assertEquals(1, contributors.get(1).getId().intValue());
        assertEquals(3, contributors.get(2).getId().intValue());
        filters.put("text", "out%");
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(3, contributors.get(0).getId().intValue());
        filters.put("family", "out");
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        filters.put("given", "out");
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        filters.put("id", 3);
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        filters.put("ipdsContributorId", 2);
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(0, contributors.size());


        filters.clear();
        filters.put("corporation", true);
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(CORPORATE_CONTRIBUTOR_CNT, contributors.size());
        assertEquals(2, contributors.get(0).getId().intValue());
        filters.put("text", "us%");
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(2, contributors.get(0).getId().intValue());
        filters.put("ipdsContributorId", 1);
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(0, contributors.size());
    }

    @Test
    public void notImplemented() {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(PublicationDao.PROD_ID, 1);
            Contributor.getDao().getObjectCount(params);
            fail("Was able to get count.");
        } catch (Exception e) {
            assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
        }
        try {
            Contributor.getDao().add(new CorporateContributor());
            fail("Was able to add.");
        } catch (Exception e) {
            assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
        }
        try {
            Contributor.getDao().update(new CorporateContributor());
            fail("Was able to update.");
        } catch (Exception e) {
            assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
        }
    }

    public static void assertContributor1(Contributor<?> contributor) {
        assertEquals(1, contributor.getId().intValue());
        assertTrue(contributor instanceof UsgsContributor);
        UsgsContributor usgsContributor = (UsgsContributor) contributor;
        assertEquals("ConFamily", usgsContributor.getFamily());
        assertEquals("ConGiven", usgsContributor.getGiven());
        assertEquals("ConSuffix", usgsContributor.getSuffix());
        assertEquals("con@usgs.gov", usgsContributor.getEmail());
        assertEquals(1, usgsContributor.getAffiliation().getId().intValue());
    }

    public static void assertContributor3(Contributor<?> contributor) {
        assertEquals(3, contributor.getId().intValue());
        assertTrue(contributor instanceof OutsideContributor);
        OutsideContributor outsideContributor = (OutsideContributor) contributor;
        assertEquals("outerfamily", outsideContributor.getFamily());
        assertEquals("outerGiven", outsideContributor.getGiven());
        assertEquals("outerSuffix", outsideContributor.getSuffix());
        assertEquals("outer@gmail.com", outsideContributor.getEmail());
        assertEquals(5, outsideContributor.getAffiliation().getId().intValue());
    }

    public static void assertContributor4(Contributor<?> contributor, Integer affiliationId) {
        assertEquals(4, contributor.getId().intValue());
        assertTrue(contributor instanceof UsgsContributor);
        UsgsContributor usgsContributor = (UsgsContributor) contributor;
        assertEquals("4Family", usgsContributor.getFamily());
        assertEquals("4Given", usgsContributor.getGiven());
        assertEquals("4Suffix", usgsContributor.getSuffix());
        assertEquals("con4@usgs.gov", usgsContributor.getEmail());
        assertEquals(affiliationId, usgsContributor.getAffiliation().getId());
    }

}
