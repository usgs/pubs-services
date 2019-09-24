package gov.usgs.cida.pubs.busservice.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.busservice.intfc.IListBusService;
import gov.usgs.cida.pubs.dao.DeletedPublicationDao;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.mp.MpListPublicationDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationContributorDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationCostCenterDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationLinkDao;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDao;
import gov.usgs.cida.pubs.domain.DeletedPublication;
import gov.usgs.cida.pubs.domain.DeletedPublicationHelper;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.springinit.DbTestConfig;
import gov.usgs.cida.pubs.validation.ValidationResults;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class,
			MpPublication.class, MpPublicationDao.class,
			PublicationDao.class, PwPublicationDao.class,
			MpListPublication.class, MpListPublicationDao.class,
			MpPublicationContributor.class, MpPublicationContributorDao.class,
			MpPublicationCostCenter.class, MpPublicationCostCenterDao.class,
			MpPublicationLink.class, MpPublicationLinkDao.class,
			PwPublication.class, PwPublicationDao.class,
			DeletedPublication.class, DeletedPublicationDao.class})
@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
public class MpPublicationBusServicePurgeIT extends BaseIT {

	@MockBean
	private Validator validator;
	@MockBean
	private ICrossRefBusService crossRefBusService;
	@MockBean
	private IListBusService<PublicationCostCenter<MpPublicationCostCenter>> ccBusService;
	@MockBean
	private IListBusService<PublicationLink<MpPublicationLink>> linkBusService;
	@MockBean
	private IListBusService<PublicationContributor<MpPublicationContributor>> contributorBusService;
	@MockBean
	private ConfigurationService configurationService;

	private MpPublicationBusService busService;

	@Before
	public void initTest() throws Exception {
		busService = new MpPublicationBusService(validator, configurationService, crossRefBusService, ccBusService, linkBusService, contributorBusService);
	}

	@Test
	public void nullID() {
		ValidationResults validationResults = busService.purgePublication(null);
		assertNotNull(validationResults);
		assertTrue(validationResults.isEmpty());
	}

	@Test
	public void notFound() {
		ValidationResults validationResults = busService.purgePublication(123);
		assertNotNull(validationResults);
		assertFalse(validationResults.isEmpty());
		assertEquals("Field:Publication - Message:Publication does not exist. - Level:FATAL - Value:123\n" + 
				"Validator Results: 1 result(s)\n",
				validationResults.toString());
	}

	@Test
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
	@DatabaseSetup("classpath:/testData/purgeTest/common/")
	@DatabaseSetup("classpath:/testData/purgeTest/mp/")
	@ExpectedDatabase(
			value="classpath:/testResult/purgeTest/mp/",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@ExpectedDatabase(
			value="classpath:/testResult/purgeTest/common/",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void onlyMp() {
		ValidationResults validationResults = busService.purgePublication(2);
		assertNotNull(validationResults);
		assertTrue(validationResults.isEmpty());
	}

	@Test
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
	@DatabaseSetup("classpath:/testData/purgeTest/common/")
	@DatabaseSetup("classpath:/testData/purgeTest/pw/")
	@ExpectedDatabase(
			value="classpath:/testResult/purgeTest/pw/",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@ExpectedDatabase(
			value="classpath:/testResult/purgeTest/common/",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@ExpectedDatabase(
			value="classpath:/testResult/purgeTest/publication_index.xml",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			table="publication_index",
			query="select publication_id, q from publication_index")
	@ExpectedDatabase(
			value="classpath:/testResult/purgeTest/deleted_publication.xml",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			table=DeletedPublicationHelper.TABLE_NAME,
			query=DeletedPublicationHelper.QUERY_TEXT)
	public void onlyPw() {
		ValidationResults validationResults = busService.purgePublication(2);
		assertNotNull(validationResults);
		assertTrue(validationResults.isEmpty());
	}

	@Test
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
	@DatabaseSetup("classpath:/testData/purgeTest/common/")
	@DatabaseSetup("classpath:/testData/purgeTest/mp/")
	@DatabaseSetup("classpath:/testData/purgeTest/pw/")
	@ExpectedDatabase(
			value="classpath:/testResult/purgeTest/mp/",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@ExpectedDatabase(
			value="classpath:/testResult/purgeTest/pw/",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@ExpectedDatabase(
			value="classpath:/testResult/purgeTest/common/",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@ExpectedDatabase(
			value="classpath:/testResult/purgeTest/publication_index.xml",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			table="publication_index",
			query="select publication_id, q from publication_index")
	@ExpectedDatabase(
			value="classpath:/testResult/purgeTest/deleted_publication.xml",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			table=DeletedPublicationHelper.TABLE_NAME,
			query=DeletedPublicationHelper.QUERY_TEXT)
	public void both() {
		ValidationResults validationResults = busService.purgePublication(2);
		assertNotNull(validationResults);
		assertTrue(validationResults.isEmpty());
	}
}
