package gov.usgs.cida.pubs.webservice;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;

import org.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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
@AutoConfigureMockMvc(secure=false)
@SpringBootTest(webEnvironment=WebEnvironment.MOCK,
	classes={DbTestConfig.class, SpringConfig.class, CustomStringToArrayConverter.class,
			StringArrayCleansingConverter.class, CustomStringToStringConverter.class, GlobalDefaultExceptionHandler.class,
			DeletedPublicationMvcService.class, DeletedPublicationDao.class})
@DatabaseSetup("classpath:/testData/deletedPublication.xml")
public class DeletedPublicationMvcServiceIT extends BaseIT {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void getAllNoPaging() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/publication/deleted").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();

		assertThat(getRtnAsJSONObject(rtn),
				sameJSONObjectAs(new JSONObject(getFile("testResult/deletedPublication/allWithoutPaging.json"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getAllPageOne() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/publication/deleted?pageNumber=1&pageSize=2").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();

		assertThat(getRtnAsJSONObject(rtn),
				sameJSONObjectAs(new JSONObject(getFile("testResult/deletedPublication/allPage1.json"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getAllPageThree() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/publication/deleted?pageNumber=3&pageSize=2").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();

		assertThat(getRtnAsJSONObject(rtn),
				sameJSONObjectAs(new JSONObject(getFile("testResult/deletedPublication/allPage3.json"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getSinceNoPaging() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/publication/deleted?deletedSince=2017-12-31").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();

		assertThat(getRtnAsJSONObject(rtn),
				sameJSONObjectAs(new JSONObject(getFile("testResult/deletedPublication/sinceWithoutPaging.json"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getSincePageOne() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/publication/deleted?deletedSince=2017-12-31&pageNumber=1&pageSize=1").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();

		assertThat(getRtnAsJSONObject(rtn),
				sameJSONObjectAs(new JSONObject(getFile("testResult/deletedPublication/sincePage1.json"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getSincePageThree() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/publication/deleted?deletedSince=2017-12-31&pageNumber=3&pageSize=1").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();

		assertThat(getRtnAsJSONObject(rtn),
				sameJSONObjectAs(new JSONObject(getFile("testResult/deletedPublication/sincePage3.json"))).allowingAnyArrayOrdering());
	}
}
