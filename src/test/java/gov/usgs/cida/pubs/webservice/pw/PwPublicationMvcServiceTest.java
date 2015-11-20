package gov.usgs.cida.pubs.webservice.pw;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.intfc.IPwPublicationBusService;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDaoTest;
import gov.usgs.cida.pubs.domain.pw.PwPublication;

import java.util.Arrays;

import javax.annotation.Resource;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class PwPublicationMvcServiceTest extends BaseSpringTest {

	@Autowired
    public String warehouseEndpoint;
    @Autowired
    private IPwPublicationBusService busService;

    @Resource(name="expectedGetMpPub1")
    public String expectedGetMpPub1;
    
    public String expectedGetPwPub1;

    public String expectedGetPwPub1_1;

    public String expectedGetPubsDefault;
    
    public String expectedGetPubsPageNumber;

    private MockMvc mockMvc;

    @Before
    public void setup() {
    	Mockito.reset(busService);
    	
    	mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    	
    	//Drop the MP only fields
    	expectedGetPwPub1 = expectedGetMpPub1.replace("\"validationErrors\": [],", "")
    			.replace("\"notes\": \"notes\",", "")
    			.replace("\"ipdsReviewProcessState\": \"Dissemination\",", "")
    			.replace("\"ipdsInternalId\": \"12\",", "");
    	
    	StringBuilder temp = new StringBuilder("{\"pageSize\":\"15\",\"pageRowStart\":\"0\",");
    	temp.append("\"pageNumber\":null,\"recordCount\":12,\"records\":[");
    	temp.append(expectedGetPwPub1);
    	temp.append("]}");
    	expectedGetPubsDefault = temp.toString();
    	expectedGetPubsPageNumber = temp.replace(13,15,"25").replace(33,34,"125").replace(51, 55, "\"6\"").toString();
    }

    @SuppressWarnings("unchecked")
	@Test
    public void getPubsTest() throws Exception {
    	//Happy Path
        when(busService.getObjects(anyMap())).thenReturn(Arrays.asList(PwPublicationDaoTest.buildAPub(1)));
        when(busService.getObjectCount(anyMap())).thenReturn(Integer.valueOf(12));
    	
        MvcResult rtn = mockMvc.perform(get("/publication?mimetype=json").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(getRtnAsJSONObject(rtn),
                sameJSONObjectAs(new JSONObject(expectedGetPubsDefault)));
        
        //With page number
        rtn = mockMvc.perform(get("/publication?mimetype=json&page_number=6").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(getRtnAsJSONObject(rtn),
                sameJSONObjectAs(new JSONObject(expectedGetPubsPageNumber)));
    }
    
    @Test
    public void getByIndexIdTest() throws Exception {
    	//Happy Path
        when(busService.getByIndexId("1")).thenReturn(PwPublicationDaoTest.buildAPub(1));
        MvcResult rtn = mockMvc.perform(get("/publication/1?mimetype=json").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(getRtnAsJSONObject(rtn),
                sameJSONObjectAs(new JSONObject(expectedGetPwPub1)));
        
        //Pub not found
        rtn = mockMvc.perform(get("/publication/3?mimetype=json").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
                .andReturn();
        assertEquals(0, rtn.getResponse().getContentAsString().length());
    }
    
    @Test
    public void getByIndexIdPeriodTest() throws Exception {
        //dot in index
        PwPublication pub1_1 = new PwPublication();
        pub1_1.setIndexId("1.1");
        when(busService.getByIndexId("1.1")).thenReturn(pub1_1);
        MvcResult rtn = mockMvc.perform(get("/publication/1.1?mimetype=json").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(getRtnAsJSONObject(rtn),
                sameJSONObjectAs(new JSONObject("{\"text\":\"1.1 - null - null\",\"indexId\":\"1.1\"}")));
    }

}
