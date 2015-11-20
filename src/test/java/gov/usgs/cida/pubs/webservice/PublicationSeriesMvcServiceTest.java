package gov.usgs.cida.pubs.webservice;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
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
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.PublicationSeriesBusService;
import gov.usgs.cida.pubs.dao.PublicationSeriesDaoTest;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.validation.ValidationResults;

public class PublicationSeriesMvcServiceTest extends BaseSpringTest {

	@Mock
    private PublicationSeriesBusService busService;

    private MockMvc mockMvc;

    private PublicationSeriesMvcService mvcService;
    
    public static String defaultPubSeriesJSON = "{\"id\":13,\"text\":\"New Video\",\"code\":\"XYZ\",\"seriesDoiName\":\"doiname is here\","
    		+ "\"onlineIssn\":\"5678-8765\",\"printIssn\":\"1234-4321\",\"publicationSubtype\":{\"id\":29}}";
 
    @Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
    	mvcService = new PublicationSeriesMvcService(busService);
    	mockMvc = MockMvcBuilders.standaloneSetup(mvcService).build();
    }

	@SuppressWarnings("unchecked")
	@Test
    public void getListTest() throws Exception {
    	//Happy Path
        when(busService.getObjects(anyMap())).thenReturn(buildAPublicationSeriesList());
        when(busService.getObjectCount(anyMap())).thenReturn(Integer.valueOf(12));
    	
        MvcResult rtn = mockMvc.perform(get("/publicationSeries?mimetype=json").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(getRtnAsJSONObject(rtn),
                sameJSONObjectAs(new JSONObject("{\"pageSize\":\"25\",\"pageRowStart\":\"0\",\"pageNumber\":null,\"recordCount\":12,\"records\":["
                		+ defaultPubSeriesJSON + "]}")));
    }


    @Test
    public void getTest() throws Exception {
    	//Happy Path
        when(busService.getObject(1)).thenReturn(PublicationSeriesDaoTest.buildAPubSeries(13));
        MvcResult rtn = mockMvc.perform(get("/publicationSeries/1?mimetype=json").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(getRtnAsJSONObject(rtn),
                sameJSONObjectAs(new JSONObject(defaultPubSeriesJSON)));
        
        //PublicationSeries not found
        rtn = mockMvc.perform(get("/publicationSeries/3?mimetype=json").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
                .andReturn();
        assertEquals(0, rtn.getResponse().getContentAsString().length());
    }

    @Test
    public void createTest() throws Exception {
        when(busService.createObject(any(PublicationSeries.class))).thenReturn(PublicationSeriesDaoTest.buildAPubSeries(13));
        MvcResult rtn = mockMvc.perform(post("/publicationSeries").content(defaultPubSeriesJSON).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(getRtnAsJSONObject(rtn),
                sameJSONObjectAs(new JSONObject(defaultPubSeriesJSON)));
    }
    
    @Test
    public void updateTest() throws Exception {
        when(busService.updateObject(any(PublicationSeries.class))).thenReturn(PublicationSeriesDaoTest.buildAPubSeries(13));
        MvcResult rtn = mockMvc.perform(put("/publicationSeries/330").content(defaultPubSeriesJSON).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(getRtnAsJSONObject(rtn),
                sameJSONObjectAs(new JSONObject(defaultPubSeriesJSON)));
    }

    @Test
    public void deleteTest() throws Exception {
    	//Happy Path/PublicationSeries not found
        when(busService.deleteObject(1)).thenReturn(new ValidationResults());
        MvcResult rtn = mockMvc.perform(delete("/publicationSeries/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();
        assertThat(getRtnAsJSONObject(rtn),
                sameJSONObjectAs(new JSONObject("{\"validationErrors\":[]}")));
    }
    
    public List<PublicationSeries> buildAPublicationSeriesList() {
    	List<PublicationSeries> rtn = new ArrayList<>();
    	PublicationSeries pubSeries = PublicationSeriesDaoTest.buildAPubSeries(13);
    	rtn.add(pubSeries);
    	return rtn;
    }

}
