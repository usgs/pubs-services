package gov.usgs.cida.pubs.webservice;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.dao.DeletedPublicationDao;
import gov.usgs.cida.pubs.springinit.DbTestConfig;
import gov.usgs.cida.pubs.springinit.SpringConfig;
import gov.usgs.cida.pubs.utility.CustomStringToArrayConverter;
import gov.usgs.cida.pubs.utility.CustomStringToStringConverter;
import gov.usgs.cida.pubs.utility.StringArrayCleansingConverter;

@EnableWebMvc
@SpringBootTest(webEnvironment=WebEnvironment.MOCK,
	classes={DbTestConfig.class, SpringConfig.class, CustomStringToArrayConverter.class,
			StringArrayCleansingConverter.class, CustomStringToStringConverter.class, GlobalDefaultExceptionHandler.class,
			DeletedPublicationMvcService.class, DeletedPublicationDao.class})
@DatabaseSetup("classpath:/testData/deletedPublication.xml")
public class DeletedPublicationMvcServiceIT extends BaseIT {

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void getAllNoPaging() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/publication/deleted").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();

		assertThat(getRtnAsJSONObject(rtn),
				sameJSONObjectAs(new JSONObject(getFile("testResult/deletedPublication/allWithoutPaging.json"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getAllPageOne() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/publication/deleted?page_number=1&page_size=2").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();

		assertThat(getRtnAsJSONObject(rtn),
				sameJSONObjectAs(new JSONObject(getFile("testResult/deletedPublication/allPage1.json"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getAllPageThree() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/publication/deleted?page_number=3&page_size=2").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();

		assertThat(getRtnAsJSONObject(rtn),
				sameJSONObjectAs(new JSONObject(getFile("testResult/deletedPublication/allPage3.json"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getSinceNoPaging() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/publication/deleted?deletedSince=2017-12-31").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();

		assertThat(getRtnAsJSONObject(rtn),
				sameJSONObjectAs(new JSONObject(getFile("testResult/deletedPublication/sinceWithoutPaging.json"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getSincePageOne() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/publication/deleted?deletedSince=2017-12-31&page_number=1&page_size=1").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();

		assertThat(getRtnAsJSONObject(rtn),
				sameJSONObjectAs(new JSONObject(getFile("testResult/deletedPublication/sincePage1.json"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getSincePageThree() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/publication/deleted?deletedSince=2017-12-31&page_number=3&page_size=1").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();

		assertThat(getRtnAsJSONObject(rtn),
				sameJSONObjectAs(new JSONObject(getFile("testResult/deletedPublication/sincePage3.json"))).allowingAnyArrayOrdering());
	}
}
