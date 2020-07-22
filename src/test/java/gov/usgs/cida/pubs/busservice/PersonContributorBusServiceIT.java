package gov.usgs.cida.pubs.busservice;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.dao.ContributorDao;
import gov.usgs.cida.pubs.dao.ContributorDaoIT;
import gov.usgs.cida.pubs.dao.PersonContributorDao;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.springinit.DbTestConfig;
import gov.usgs.cida.pubs.validation.constraint.ManagerChecks;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, LocalValidatorFactoryBean.class, Contributor.class,
			PersonContributor.class, PersonContributorDao.class, ContributorDao.class})
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/dataset.xml")
})
public class PersonContributorBusServiceIT extends BaseIT {

	@Autowired
	public Validator validator;

	private PersonContributorBusService busService;

	@BeforeEach
	public void initTest() throws Exception {
		busService = new PersonContributorBusService(validator);
	}

	@Test
	public void addObjectTest() {
		UsgsContributor person = new UsgsContributor();
		person.setFamily("family");
		person.setGiven("given");
		person.setSuffix("suffix");
		person.setEmail("email@usgs.gov");
		person.setOrcid("http://orcid.org/0000-0002-1825-0097");
		person.setPreferred(true);
		busService.createObject(person, ManagerChecks.class);
		assertTrue(person.isValid(), "Expected isValid() true, got validation errors: " + person.getValidationErrors());
		assertNotNull(person.getId());
		UsgsContributor persisted = (UsgsContributor) Contributor.getDao().getById(person.getId());
		assertDaoTestResults(UsgsContributor.class, person, persisted, ContributorDaoIT.IGNORE_PROPERTIES_PERSON, true, true);


		OutsideContributor outperson = new OutsideContributor();
		outperson.setFamily("outfamily");
		outperson.setGiven("outgiven");
		outperson.setSuffix("outsuffix");
		outperson.setEmail("outemail@usgs.gov");
		// service stores normalized orcid
		outperson.setOrcid("0000-0002-1825-0097");
		outperson.setPreferred(true);
		busService.createObject(outperson, ManagerChecks.class);
		assertNotNull(outperson.getId());
		OutsideContributor outpersisted = (OutsideContributor) Contributor.getDao().getById(outperson.getId());
		assertDaoTestResults(OutsideContributor.class, outperson, outpersisted, ContributorDaoIT.IGNORE_PROPERTIES_PERSON, true, true);
	}

	@Test
	public void orcidValidationTest() {
		UsgsContributor person = new UsgsContributor();
		person.setFamily("family");
		person.setGiven("given");
		person.setSuffix("suffix");
		person.setEmail("email@usgs.gov");
		person.setOrcid("http://orcid.org/0000-0002-1825-009R");
		person.setPreferred(true);
		busService.createObject(person, ManagerChecks.class);

		assertTrue(person.getId() == null || person.getId() == 0, "Expected id not to be set: " + person.getId());

		boolean hasValidationMess = false;
		String expectedMess = PersonContributor.ORCID_VALIDATION_MESS.replace("${validatedValue}", person.getOrcid());
		String validationMessage = "[no validation message found]";

		if(person.getValidationErrors() != null && !person.getValidationErrors().isValid()) {
			validationMessage = person.getValidationErrors().toString();
			if(validationMessage.contains(expectedMess)) {
				hasValidationMess = true;
			}
		}
		String testMess = "Expected validation error message: " + expectedMess + " got: " + validationMessage;
		assertTrue(hasValidationMess, testMess);

		assertFalse(person.isValid(), "Expected isValid() to be false");
	}

}