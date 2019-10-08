package gov.usgs.cida.pubs.webservice.mp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;

import org.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.busservice.PublicationBusService;
import gov.usgs.cida.pubs.busservice.ext.ExtPublicationService;
import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.busservice.mp.MpPublicationBusService;
import gov.usgs.cida.pubs.busservice.mp.MpPublicationContributorBusService;
import gov.usgs.cida.pubs.busservice.mp.MpPublicationCostCenterBusService;
import gov.usgs.cida.pubs.busservice.mp.MpPublicationLinkBusService;
import gov.usgs.cida.pubs.busservice.sipp.SippConversionService;
import gov.usgs.cida.pubs.busservice.sipp.SippProcess;
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
import gov.usgs.cida.pubs.domain.PublicationIndexHelper;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@EnableWebMvc
@AutoConfigureMockMvc(secure=false)
@SpringBootTest(webEnvironment=WebEnvironment.MOCK,
classes={DbTestConfig.class, MpPublicationMvcService.class,
		LocalValidatorFactoryBean.class,
		MpPublication.class, MpPublicationDao.class,
		PublicationDao.class, PwPublicationDao.class,
		MpListPublication.class, MpListPublicationDao.class,
		MpPublicationContributor.class, MpPublicationContributorDao.class,
		MpPublicationCostCenter.class, MpPublicationCostCenterDao.class,
		MpPublicationLink.class, MpPublicationLinkDao.class,
		PwPublication.class, PwPublicationDao.class,
		DeletedPublication.class, DeletedPublicationDao.class,
		PublicationBusService.class, MpPublicationBusService.class,
		MpPublicationCostCenterBusService.class, MpPublicationLinkBusService.class,
		MpPublicationContributorBusService.class, SippProcess.class})
@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
public class MpPublicationMvcServiceIT extends BaseIT {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private ICrossRefBusService crossRefBusService;
	@MockBean
	private ConfigurationService configurationService;
	@MockBean
	private ExtPublicationService extPublicationService;
	@MockBean
	private SippConversionService sippConversionService;

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
			table=PublicationIndexHelper.TABLE_NAME,
			query=PublicationIndexHelper.QUERY_TEXT)
	@ExpectedDatabase(
			value="classpath:/testResult/purgeTest/deleted_publication.xml",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			table=DeletedPublicationHelper.TABLE_NAME,
			query=DeletedPublicationHelper.QUERY_TEXT)
	public void purge() throws Exception {
		//happy path in both databases
		MvcResult result = mockMvc.perform(delete("/mppublications/2/purge"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();
		assertThat(getRtnAsJSONObject(result), sameJSONObjectAs(new JSONObject("{\"validationErrors\":[]}")));
	}

	@Test
	public void verifyPagingParams() throws Exception {
		// check that the paging parameters are passed to the service
		// do not expect any records to be returned, but pageSize should be set
		String pageSize = "33";
		String pageRowStart = "56";
		String queryParms =  String.format("?page_size=%s&page_row_start=%s", pageSize, pageRowStart);

		MvcResult result = mockMvc.perform(get("/mppublications/" + queryParms))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();

		JSONObject rtnAsJson = getRtnAsJSONObject(result);

		assertNotNull(rtnAsJson.get("pageSize"));
		assertEquals(rtnAsJson.get("pageSize"), pageSize);

		assertNotNull(rtnAsJson.get("pageRowStart"));
		assertEquals(rtnAsJson.get("pageRowStart"), pageRowStart);
	}
}