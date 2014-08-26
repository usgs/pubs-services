package gov.usgs.cida.pubs.busservice;

import static org.junit.Assert.assertNotNull;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.dao.ContributorDaoTest;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

public class PersonContributorBusServiceTest extends BaseSpringTest {

    @Autowired
    public Validator validator;

    private PersonContributorBusService busService;

    @Before
    public void initTest() throws Exception {
        MockitoAnnotations.initMocks(this);
        busService = new PersonContributorBusService(validator);
    }

    @Test
    public void addObjectTest() {
        UsgsContributor person = new UsgsContributor();
        person.setFamily("family");
        person.setGiven("given");
        person.setSuffix("suffix");
        person.setEmail("email");
        person.setIpdsContributorId(12);
        person.setAffiliation(Affiliation.getDao().getById(1));
        busService.createObject(person);
        assertNotNull(person.getId());
        UsgsContributor persisted = (UsgsContributor) Contributor.getDao().getById(person.getId());
        assertDaoTestResults(UsgsContributor.class, person, persisted, ContributorDaoTest.IGNORE_PROPERTIES_PERSON, true, true);


        OutsideContributor outperson = new OutsideContributor();
        outperson.setFamily("outfamily");
        outperson.setGiven("outgiven");
        outperson.setSuffix("outsuffix");
        outperson.setEmail("outemail");
        outperson.setIpdsContributorId(13);
        outperson.setAffiliation(Affiliation.getDao().getById(182));
        busService.createObject(outperson);
        assertNotNull(outperson.getId());
        OutsideContributor outpersisted = (OutsideContributor) Contributor.getDao().getById(outperson.getId());
        assertDaoTestResults(OutsideContributor.class, outperson, outpersisted, ContributorDaoTest.IGNORE_PROPERTIES_PERSON, true, true);
    }

}
