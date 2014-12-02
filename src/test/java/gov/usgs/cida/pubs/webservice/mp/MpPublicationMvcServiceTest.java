package gov.usgs.cida.pubs.webservice.mp;


import static com.jayway.jsonassert.JsonAssert.with;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyMap;
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
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDaoTest;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.validation.ValidatorResult;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class MpPublicationMvcServiceTest extends BaseSpringTest {

    public static final String LOCK_MSG = "{\"validationErrors\":[{\"field\":\"Publication\",\"message\":\"This Publication is being edited by somebody\",\"level\":\"FATAL\",\"value\":\"somebody\"}]}\"";

    public static final ValidatorResult VR_LOCKED = new ValidatorResult("Publication", "This Publication is being edited by somebody", SeverityLevel.FATAL, "somebody");
    public static final ValidatorResult VR_NOT_LOCKED = null;

	@Mock
    private IBusService<Publication<?>> pubBusService;
	@Mock
    private IMpPublicationBusService busService;

    @Resource(name="expectedGetMpPub1")
    public String expectedGetMpPub1;

    public String expectedGetPubsDefault;
    
    private MockMvc mockMvc;

    private MpPublicationMvcService mvcService;
    
    @Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
    	mvcService = new MpPublicationMvcService(pubBusService, busService);
    	mockMvc = MockMvcBuilders.standaloneSetup(mvcService).build();

        when(busService.checkAvailability(1)).thenReturn(VR_NOT_LOCKED);
        when(busService.checkAvailability(2)).thenReturn(VR_LOCKED);
        when(busService.checkAvailability(3)).thenReturn(VR_NOT_LOCKED);

    	StringBuilder temp = new StringBuilder("{\"pageSize\":\"25\",\"pageRowStart\":\"0\",");
    	temp.append("\"pageNumber\":null,\"recordCount\":12,\"records\":[");
    	temp.append(expectedGetMpPub1);
    	temp.append("]}");
    	expectedGetPubsDefault = temp.toString();
    }

    @SuppressWarnings("unchecked")
	@Test
    public void getPubsTest() throws Exception {
    	//Happy Path
        when(pubBusService.getObjects(anyMap())).thenReturn(buildAPubList());
        when(pubBusService.getObjectCount(anyMap())).thenReturn(Integer.valueOf(12));
    	
        MvcResult rtn = mockMvc.perform(get("/mppublications?mimetype=json").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
                sameJSONObjectAs(new JSONObject(expectedGetPubsDefault)));
    }
    
    @Test
    public void getMpPublicationTest() throws Exception {
    	//Happy Path
        when(busService.getObject(1)).thenReturn(buildAPub(1));
        MvcResult rtn = mockMvc.perform(get("/mppublications/1?mimetype=json").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
                sameJSONObjectAs(new JSONObject(expectedGetMpPub1)));
        
        //Not available (locked by somebody)
        rtn = mockMvc.perform(get("/mppublications/2?mimetype=json").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
                .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
                .andReturn();
        assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
                sameJSONObjectAs(new JSONObject(LOCK_MSG)));
        
        //Pub not found
        rtn = mockMvc.perform(get("/mppublications/3?mimetype=json").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
                .andExpect(status().isNotFound())
                .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
                .andReturn();
        assertEquals(0, rtn.getResponse().getContentAsString().length());
    }

    //@Test
    public void getPubsTest_singleSearchTerm() throws Exception {
        MvcResult rtn = mockMvc.perform(get("/mppublication/1?q=1").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
                sameJSONObjectAs(new JSONObject(expectedGetMpPub1)));

        MvcResult rtn2 = mockMvc.perform(get("/mppublication/1?q=NoPublicationIsGoingToHaveThisSearchTermZZzzzzZZZZZwhatNO").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
                .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
                .andReturn();

        assertThat(new JSONObject(rtn2.getResponse().getContentAsString()),
                sameJSONObjectAs(new JSONObject(expectedGetMpPub1)));
    }

    // @Test
    public void createPubTest() throws Exception {
        MvcResult rtn = mockMvc.perform(post("/mppublications").content("{"
        		+ "\"publicationType\":{\"id\":" + PublicationType.REPORT + "}"
        		+ ",\"publicationSubtype\":{\"id\":" + PublicationSubtype.USGS_NUMBERED_SERIES + "}"
        		+ ",\"publicationYear\":\"1994\""
        		+ ",\"links\":[{\"rank\":1}]"
        		+ ",\"authors\":[{\"contributorId\": 81,\"corporation\": false,\"usgs\": true,\"family\": \"Wanda\","
        		+ "\"given\": \"Molina\",\"email\": \"wlmolina@usgs.gov\",\"affiliation\": {\"id\": 84,\"text\": \"Caribbean Water Science Center\""
    			+ "},\"id\": 110,\"rank\": 1}]"
        		+ "}").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        String result = rtn.getResponse().getContentAsString();
//        assertThat(new JSONObject(result),
//                sameJSONObjectAs(new JSONObject("{\"value\":\"4\",\"text\":\"Book\"}")));

        with(result)
        	.assertThat("links[0].rank", equalTo(1));
    }
    
    //@Test
    public void updateMpPublicationTest() throws Exception {
        MvcResult rtn = mockMvc.perform(put("/mppublication/2").content("{\"id\":2,\"publicationType\":{\"id\":"
                + PublicationType.REPORT + ",\"text\":\"abc\"},\"indexId\":\"abc\"}").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
                sameJSONObjectAs(new JSONObject("{\"value\":\"4\",\"text\":\"Book\"}")));
    }

    @Test
    public void deletePubTest() throws Exception {
    	//Happy Path/Pub not found
        when(busService.deleteObject(1)).thenReturn(new ValidationResults());
        MvcResult rtn = mockMvc.perform(delete("/mppublications/1").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();
        assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
                sameJSONObjectAs(new JSONObject("{\"validationErrors\":[]}")));
        
        //Not available (locked by somebody)
        rtn = mockMvc.perform(delete("/mppublications/2").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
                .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
                .andReturn();
        assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
                sameJSONObjectAs(new JSONObject(LOCK_MSG)));
    }
    
    @Test
    public void publishPubTest() throws Exception {
    	//Happy Path
        when(busService.publish(1)).thenReturn(new ValidationResults());
        MvcResult rtn = mockMvc.perform(post("/mppublications/publish").content("{\"id\":1}}").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();
        assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
                sameJSONObjectAs(new JSONObject("{\"validationErrors\":[]}")));
        
        //Not available (locked by somebody)
        rtn = mockMvc.perform(post("/mppublications/publish").content("{\"id\":2}}").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
                .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
                .andReturn();
        assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
                sameJSONObjectAs(new JSONObject(LOCK_MSG)));
        
        //Pub not found
        ValidationResults vr = new ValidationResults();
        vr.addValidatorResult(new ValidatorResult("Publication", "Publication does not exist.", SeverityLevel.FATAL, "3"));
        when(busService.publish(3)).thenReturn(vr);
        rtn = mockMvc.perform(post("/mppublications/publish").content("{\"id\":3}}").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
                .andExpect(status().isNotFound())
                .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
                .andReturn();
        assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
                sameJSONObjectAs(new JSONObject("{\"validationErrors\":[{\"field\":\"Publication\",\"message\":\"Publication does not exist.\",\"level\":\"FATAL\",\"value\":\"3\"}]}")));
        
    }
    
    @Test
    public void releasePubTest() throws Exception {
    	//Happy Path/Pub not found
        MvcResult rtn = mockMvc.perform(post("/mppublications/release").content("{\"id\":1}}").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();
        assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
                sameJSONObjectAs(new JSONObject("{\"validationErrors\":[]}")));
        verify(busService, times(1)).releaseLocksPub(anyInt());
        
        //Not available (locked by somebody)
        rtn = mockMvc.perform(post("/mppublications/release").content("{\"id\":2}}").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
                .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
                .andReturn();
        assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
                sameJSONObjectAs(new JSONObject(LOCK_MSG)));
        //We executed the releaseLocksPub above - this would be 2 if we also hit it in the locked test.
        verify(busService, times(1)).releaseLocksPub(anyInt());
    }
    
    public MpPublication buildAPub(Integer id) {
    	MpPublication pub = MpPublicationDaoTest.buildAPub(id);
    	return pub;
    }

    public List<Publication<?>> buildAPubList() {
    	List<Publication<?>> rtn = new ArrayList<>();
    	rtn.add(MpPublicationDaoTest.buildAPub(1));
    	return rtn;
    }
}
