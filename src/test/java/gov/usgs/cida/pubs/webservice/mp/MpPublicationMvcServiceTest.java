package gov.usgs.cida.pubs.webservice.mp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.busservice.intfc.IPublicationBusService;
import gov.usgs.cida.pubs.busservice.intfc.ISippProcess;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDaoIT;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.query.MpPublicationFilterParams;
import gov.usgs.cida.pubs.springinit.SpringConfig;
import gov.usgs.cida.pubs.springinit.TestSpringConfig;
import gov.usgs.cida.pubs.utility.CustomStringToArrayConverter;
import gov.usgs.cida.pubs.utility.CustomStringToStringConverter;
import gov.usgs.cida.pubs.utility.PubsUtilitiesTest;
import gov.usgs.cida.pubs.utility.StringArrayCleansingConverter;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.validation.ValidatorResult;
import gov.usgs.cida.pubs.webservice.GlobalDefaultExceptionHandler;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={TestSpringConfig.class, SpringConfig.class, CustomStringToArrayConverter.class,
			StringArrayCleansingConverter.class, CustomStringToStringConverter.class, GlobalDefaultExceptionHandler.class})
public class MpPublicationMvcServiceTest extends BaseTest {

	private static final String MP_PUB_1_JSON = "{"
			+ "\"publicationType\":{\"id\":" + PublicationType.REPORT + "}"
			+ ",\"publicationSubtype\":{\"id\":" + PublicationSubtype.USGS_NUMBERED_SERIES + "}"
			+ ",\"publicationYear\":\"1994\""
			+ ",\"links\":[{\"rank\":1}]"
			+ ",\"authors\":[{\"contributorId\": 81,\"corporation\": false,\"usgs\": true,\"family\": \"Wanda\","
			+ "\"given\": \"Molina\",\"email\": \"wlmolina@usgs.gov\",\"affiliation\": {\"id\": 84,\"text\": \"Caribbean Water Science Center\""
			+ "},\"id\": 110,\"rank\": 1}]"
			+ "}";

	public static final String LOCK_MSG = "{\"validationErrors\":[{\"field\":\"Publication\",\"message\":\"This Publication is being edited by somebody\",\"level\":\"FATAL\",\"value\":\"somebody\"}]}\"";
	public static final String LOCK_MSG2 = LOCK_MSG.replace("}]}", "}],\"text\":\"null - null - null\",\"noYear\":false,\"published\":false,\"noUsgsAuthors\":false}");
	public static final String LOCK_MSG3 = LOCK_MSG.replace("}]}", "}],\"id\":2,\"indexId\":\"abc\",\"publicationType\":{\"id\":18,\"text\":\"abc\"},"
			+"\"text\":\"abc - null - null\",\"noYear\":false,\"published\":false,\"noUsgsAuthors\":false}");

	public static final String SIPP_PROCTYPE_ERRR_TEMPLATE = "Unknown ProcessType specified ('XXX') must be one of: 'DISSEMINATION', 'SPN_PRODUCTION'";
	public static final String SIPP_IPNUMBER_ERRR_TEMPLATE = "Invalid IPNumber specified ('XXX') must be 'IP-' followed by 6 digits [0-9].";

	public static final ValidatorResult VR_LOCKED = new ValidatorResult("Publication", "This Publication is being edited by somebody", SeverityLevel.FATAL, "somebody");
	public static final ValidatorResult VR_NOT_LOCKED = null;

	public static final String NO_RECORDS = "{\"pageSize\":\"25\",\"pageRowStart\":\"0\",\"pageNumber\":\"1\",\"recordCount\":0,\"records\":[]}";

	@Autowired
	protected MappingJackson2HttpMessageConverter jackson2HttpMessageConverter;

	@MockBean
	private IPublicationBusService pubBusService;
	@MockBean
	private IMpPublicationBusService busService;
	@MockBean
	private ISippProcess sippService;

	@Resource(name="expectedGetMpPub1")
	public String expectedGetMpPub1;

	@Resource(name="expectedGetPubsDefault")
	public String expectedGetPubsDefault;
	private MockMvc mockMvc;

	private MpPublicationMvcService mvcService;

	@BeforeEach
	public void setup() {
		mvcService = new MpPublicationMvcService(pubBusService, busService, sippService);
		mockMvc = MockMvcBuilders.standaloneSetup(mvcService).setMessageConverters(jackson2HttpMessageConverter)
				.setControllerAdvice(GlobalDefaultExceptionHandler.class).build();

		when(busService.checkAvailability(1)).thenReturn(VR_NOT_LOCKED);
		when(busService.checkAvailability(2)).thenReturn(VR_LOCKED);
		when(busService.checkAvailability(3)).thenReturn(VR_NOT_LOCKED);
	}

	@Test
	public void getPubsTest() throws Exception {
		//Happy Path
		when(pubBusService.getObjects(any(MpPublicationFilterParams.class))).thenReturn(buildAPubList());
		when(pubBusService.getObjectCount(any(MpPublicationFilterParams.class))).thenReturn(Integer.valueOf(12));

		MvcResult rtn = mockMvc.perform(get("/mppublications?mimetype=json").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(expectedGetPubsDefault)));
	}

	@Test
	public void getPreviewTest() throws Exception {
		//Happy Path
		when(busService.getByIndexId(anyString())).thenReturn(buildAPub(1));

		MvcResult rtn = mockMvc.perform(get("/mppublications/" + MpPublicationDaoIT.MPPUB1_INDEXID
				+ "/preview").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(expectedGetMpPub1)));
	}

	@Test
	public void getMpPublicationTest() throws Exception {
		//Happy Path
		when(busService.getObject(1)).thenReturn(buildAPub(1));
		MvcResult rtn = mockMvc.perform(get("/mppublications/1?mimetype=json").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(expectedGetMpPub1)));

		//Not available (locked by somebody)
		rtn = mockMvc.perform(get("/mppublications/2?mimetype=json").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict())
				.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();
		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(LOCK_MSG2)));

		//Pub not found
		rtn = mockMvc.perform(get("/mppublications/3?mimetype=json").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();
		assertTrue(StringUtils.isBlank(rtn.getResponse().getContentAsString()));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getPubsTest_singleSearchTerm() throws Exception {
		when(pubBusService.getObjects(any(MpPublicationFilterParams.class))).thenReturn(buildAPubList(), new ArrayList<Publication<?>>());
		when(pubBusService.getObjectCount(any(MpPublicationFilterParams.class))).thenReturn(Integer.valueOf(12), 0);
		MvcResult rtn = mockMvc.perform(get("/mppublications?q=1").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(expectedGetPubsDefault)));

		MvcResult rtn2 = mockMvc.perform(get("/mppublications?q=NoPublicationIsGoingToHaveThisSearchTermZZzzzzZZZZZwhatNO").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();

		assertThat(new JSONObject(rtn2.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(NO_RECORDS)));
	}

	@Test
	public void createPubTest() throws Exception {
		when(busService.createObject(any(MpPublication.class))).thenReturn(null, buildAPub(1));

		MvcResult rtn = mockMvc.perform(post("/mppublications").content(MP_PUB_1_JSON).contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertEquals("", rtn.getResponse().getContentAsString());

		rtn = mockMvc.perform(post("/mppublications").content(MP_PUB_1_JSON).contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(expectedGetMpPub1)));
	}

	@Test
	public void createPubViaSippTest() throws Exception {
		String path = "/mppublications/sipp";

		String errMess = SIPP_PROCTYPE_ERRR_TEMPLATE.replace("XXX", "DIS");
		runTestCasePostErr(path, buildCreateFromSippJson("DIS", "IP-123456"), buildSippErrorJson(errMess), "Case with unknown ProcessType");

		errMess = SIPP_IPNUMBER_ERRR_TEMPLATE.replace("XXX", "IP-1234");
		runTestCasePostErr(path, buildCreateFromSippJson(ProcessType.DISSEMINATION.name(), "IP-1234"), buildSippErrorJson(errMess), "Case with illegal IPNumber");
	}

	@Test
	public void updateMpPublicationTest() throws Exception {
		when(busService.checkAvailability(anyInt())).thenReturn(VR_LOCKED, VR_NOT_LOCKED);
		when(busService.updateObject(any(MpPublication.class))).thenReturn(null, buildAPub(1));

		MvcResult rtn = mockMvc.perform(put("/mppublications/2").content("{\"id\":2,\"publicationType\":{\"id\":"
				+ PublicationType.REPORT + ",\"text\":\"abc\"},\"indexId\":\"abc\"}").contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isConflict())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();
		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(LOCK_MSG3)));

		rtn = mockMvc.perform(put("/mppublications/2").content("{\"id\":2,\"publicationType\":{\"id\":"
				+ PublicationType.REPORT + ",\"text\":\"abc\"},\"indexId\":\"abc\"}").contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();
		assertEquals("", rtn.getResponse().getContentAsString());

		rtn = mockMvc.perform(put("/mppublications/1").content("{\"id\":1,\"publicationType\":{\"id\":"
				+ PublicationType.REPORT + ",\"text\":\"abc\"},\"indexId\":\"abc\"}").contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();
		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(expectedGetMpPub1)));
	}

	@Test
	public void updateMpPublicationIdNotMatchingTest() throws Exception {
		String pubJson = "{\"id\":1,\"indexId\":\"abc\",\"publicationType\":{\"id\":18,\"text\":\"abc\"},\"noYear\":false,\"text\":\"abc - null - null\"}";
		String pubJsonWithError = "{\"id\":1," + PubsUtilitiesTest.ID_NOT_MATCH_VALIDATION_JSON + ",\"indexId\":\"abc\",\"publicationType\":{\"id\":18,\"text\":\"abc\"},\"noYear\":false,\"text\":\"abc - null - null\","
				+ "\"published\":false,\"noUsgsAuthors\":false}";
		MvcResult rtn = mockMvc.perform(put("/mppublications/30").content(pubJson).contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();
		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(pubJsonWithError)));
	}

	@Test
	public void deletePubTest() throws Exception {
		//Happy Path/Pub not found
		when(busService.deleteObject(1)).thenReturn(new ValidationResults());
		MvcResult rtn = mockMvc.perform(delete("/mppublications/1").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();
		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject("{\"validationErrors\":[]}")));

		//Not available (locked by somebody)
		rtn = mockMvc.perform(delete("/mppublications/2").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict())
				.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();
		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(LOCK_MSG)));
	}

	@Test
	public void publishPubTest() throws Exception {
		//Happy Path
		when(busService.publish(1)).thenReturn(new ValidationResults());
		MvcResult rtn = mockMvc.perform(post("/mppublications/publish").content("{\"id\":1}}").contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();
		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject("{\"validationErrors\":[]}")));

		//Not available (locked by somebody)
		rtn = mockMvc.perform(post("/mppublications/publish").content("{\"id\":2}}").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict())
				.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();
		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(LOCK_MSG)));

		//Pub not found
		ValidationResults vr = new ValidationResults();
		vr.addValidatorResult(new ValidatorResult("Publication", "Publication does not exist.", SeverityLevel.FATAL, "3"));
		when(busService.publish(3)).thenReturn(vr);
		rtn = mockMvc.perform(post("/mppublications/publish").content("{\"id\":3}}").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();
		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject("{\"validationErrors\":[{\"field\":\"Publication\",\"message\":\"Publication does not exist.\",\"level\":\"FATAL\",\"value\":\"3\"}]}")));

	}

	@Test
	public void releasePubTest() throws Exception {
		//Happy Path/Pub not found
		MvcResult rtn = mockMvc.perform(post("/mppublications/release").content("{\"id\":1}}").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();
		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject("{\"validationErrors\":[]}")));
		verify(busService, times(1)).releaseLocksPub(anyInt());

		//Not available (locked by somebody)
		rtn = mockMvc.perform(post("/mppublications/release").content("{\"id\":2}}").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict())
				.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();
		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(LOCK_MSG)));
		//We executed the releaseLocksPub above - this would be 2 if we also hit it in the locked test.
		verify(busService, times(1)).releaseLocksPub(anyInt());
	}


	@Test
	public void purgePubTest() throws Exception {
		ValidationResults vrnf = new ValidationResults();
		vrnf.addValidatorResult(new ValidatorResult("Publication", "Publication does not exist.", SeverityLevel.FATAL, "3"));
		ValidationResults vrOther = new ValidationResults();
		vrOther.addValidatorResult(new ValidatorResult("Publication", "Something else validated wrong.", SeverityLevel.FATAL, "3"));
		when(busService.purgePublication(1)).thenReturn(new ValidationResults());
		when(busService.purgePublication(3)).thenReturn(vrnf);
		when(busService.purgePublication(4)).thenReturn(vrOther);

		//Happy Path
		MvcResult rtn = mockMvc.perform(delete("/mppublications/1/purge").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();
		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject("{\"validationErrors\":[]}")));

		//Not available (locked by somebody)
		rtn = mockMvc.perform(delete("/mppublications/2/purge").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict())
				.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();
		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(LOCK_MSG)));

		//Pub not found
		rtn = mockMvc.perform(delete("/mppublications/3/purge").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();
		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject("{\"validationErrors\":[{\"field\":\"Publication\",\"message\":\"Publication does not exist.\",\"level\":\"FATAL\",\"value\":\"3\"}]}")));

		//Pub not found
		rtn = mockMvc.perform(delete("/mppublications/4/purge").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();
		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject("{\"validationErrors\":[{\"field\":\"Publication\",\"message\":\"Something else validated wrong.\",\"level\":\"FATAL\",\"value\":\"3\"}]}")));
	}

	public MpPublication buildAPub(Integer id) {
		MpPublication pub = MpPublicationDaoIT.buildAPub(id);
		return pub;
	}

	public List<Publication<?>> buildAPubList() {
		List<Publication<?>> rtn = new ArrayList<>();
		rtn.add(MpPublicationDaoIT.buildAPub(1));
		return rtn;
	}

	private String buildCreateFromSippJson(String processType, String ipNumber) throws JSONException {
		JSONObject json = new JSONObject();
		json.put("ProcessType", processType);
		json.put("IPNumber", ipNumber);
		return json.toString();
	}

	private JSONObject buildSippErrorJson(String errMess) throws JSONException {
		JSONObject json = new JSONObject();

		json.put("Error Message", errMess);
		return json;
	}

	private void runTestCasePostErr(String path, String jsonToPost, JSONObject expectedReturn, String desc) throws Exception, JSONException {
		MvcResult rtn = mockMvc.perform(post(path).content(jsonToPost).contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andReturn();

		assertThat(desc, new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(expectedReturn));
	}

}