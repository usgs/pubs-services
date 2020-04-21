package gov.usgs.cida.pubs.webservice;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.busservice.PublicationSeriesBusService;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSeriesTest;
import gov.usgs.cida.pubs.utility.PubsUtilitiesTest;
import gov.usgs.cida.pubs.validation.ValidationResults;

@SpringBootTest(webEnvironment=WebEnvironment.MOCK)
public class PublicationSeriesMvcServiceTest extends BaseTest {

	@MockBean
	private PublicationSeriesBusService busService;

	private MockMvc mockMvc;

	private PublicationSeriesMvcService mvcService;

	@BeforeEach
	public void setup() {
		mvcService = new PublicationSeriesMvcService(busService);
		mockMvc = MockMvcBuilders.standaloneSetup(mvcService).build();
	}

	@Test
	public void getListTest() throws Exception {
		//Happy Path
		when(busService.getObjects(anyMap())).thenReturn(buildAPublicationSeriesList());
		when(busService.getObjectCount(anyMap())).thenReturn(Integer.valueOf(12));

		MvcResult rtn = mockMvc.perform(get("/publicationSeries?mimetype=json").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject("{\"pageSize\":\"25\",\"pageRowStart\":\"0\",\"pageNumber\":null,\"recordCount\":12,\"records\":["
						+ PublicationSeriesTest.DEFAULT_AS_JSON + "]}")));
	}

	@Test
	public void getTest() throws Exception {
		//Happy Path
		when(busService.getObject(1)).thenReturn(PublicationSeriesTest.buildAPubSeries(13));
		MvcResult rtn = mockMvc.perform(get("/publicationSeries/1?mimetype=json").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(PublicationSeriesTest.DEFAULT_AS_JSON)));

		//PublicationSeries not found
		rtn = mockMvc.perform(get("/publicationSeries/3?mimetype=json").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();
		assertEquals(0, rtn.getResponse().getContentAsString().length());
	}

	@Test
	public void createTest() throws Exception {
		when(busService.createObject(any(PublicationSeries.class))).thenReturn(PublicationSeriesTest.buildAPubSeries(13));
		MvcResult rtn = mockMvc.perform(post("/publicationSeries").content(PublicationSeriesTest.DEFAULT_AS_JSON).contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(PublicationSeriesTest.DEFAULT_MAINT_AS_JSON)));
	}

	@Test
	public void createErrorsTest() throws Exception {
		when(busService.createObject(any(PublicationSeries.class))).thenReturn(PublicationSeriesTest.BuildAPubSeriesWithErrors(13));
		MvcResult rtn = mockMvc.perform(post("/publicationSeries").content(PublicationSeriesTest.DEFAULT_AS_JSON).contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(PublicationSeriesTest.DEFAULT_WITH_ERRORS_AS_JSON)));
	}

	@Test
	public void updateSameIdTest() throws Exception {
		when(busService.updateObject(any(PublicationSeries.class))).thenReturn(PublicationSeriesTest.buildAPubSeries(13));
		MvcResult rtn = mockMvc.perform(put("/publicationSeries/13").content(PublicationSeriesTest.DEFAULT_AS_JSON).contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(PublicationSeriesTest.DEFAULT_MAINT_AS_JSON)));
	}

	@Test
	public void updateDifferentIdTest() throws Exception {
		MvcResult rtn = mockMvc.perform(put("/publicationSeries/30").content(PublicationSeriesTest.DEFAULT_AS_JSON).contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		String expectedJSON = PublicationSeriesTest.DEFAULT_AS_JSON.replaceFirst("}$", "," + PubsUtilitiesTest.ID_NOT_MATCH_VALIDATION_JSON + "}");
		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(expectedJSON)));
	}

	@Test
	public void deleteTest() throws Exception {
		//Happy Path/PublicationSeries not found
		when(busService.deleteObject(1)).thenReturn(new ValidationResults());
		MvcResult rtn = mockMvc.perform(delete("/publicationSeries/1").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();
		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject("{\"validationErrors\":[]}")));
	}

	public List<PublicationSeries> buildAPublicationSeriesList() {
		List<PublicationSeries> rtn = new ArrayList<>();
		PublicationSeries pubSeries = PublicationSeriesTest.buildAPubSeries(13);
		rtn.add(pubSeries);
		return rtn;
	}

}
