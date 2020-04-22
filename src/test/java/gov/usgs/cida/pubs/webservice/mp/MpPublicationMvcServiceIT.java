package gov.usgs.cida.pubs.webservice.mp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.busservice.ext.ExtPublicationService;
import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.busservice.intfc.IPublicationBusService;
import gov.usgs.cida.pubs.busservice.intfc.ISippProcess;
import gov.usgs.cida.pubs.busservice.sipp.SippConversionService;
import gov.usgs.cida.pubs.domain.DeletedPublicationHelper;
import gov.usgs.cida.pubs.domain.PublicationIndexHelper;

@SpringBootTest(webEnvironment=WebEnvironment.MOCK)
@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
public class MpPublicationMvcServiceIT extends BaseIT {

	private MockMvc mockMvc;
	@MockBean
	private ICrossRefBusService crossRefBusService;
	@MockBean
	private ExtPublicationService extPublicationService;
	@MockBean
	private SippConversionService sippConversionService;
	private MpPublicationMvcService mvcService;
	@Autowired
	private IPublicationBusService publicationBusService;
	@Autowired
	private IMpPublicationBusService mpPublicationBusService;
	@Autowired
	private ISippProcess sippProcess;

	@BeforeEach
	public void setup() {
		mvcService = new MpPublicationMvcService(publicationBusService,
				mpPublicationBusService,
				sippProcess);
		mockMvc = MockMvcBuilders.standaloneSetup(mvcService).build();
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
				.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();

				assertThat(new JSONObject(result.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject("{\"validationErrors\":[]}")));
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
				.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();

		JSONObject rtnAsJson = new JSONObject(result.getResponse().getContentAsString());

		assertNotNull(rtnAsJson.get("pageSize"));
		assertEquals(rtnAsJson.get("pageSize"), pageSize);

		assertNotNull(rtnAsJson.get("pageRowStart"));
		assertEquals(rtnAsJson.get("pageRowStart"), pageRowStart);
	}
}