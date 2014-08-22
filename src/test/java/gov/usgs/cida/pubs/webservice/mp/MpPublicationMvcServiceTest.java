package gov.usgs.cida.pubs.webservice.mp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.domain.PublicationType;

import javax.annotation.Resource;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Ignore
@WebAppConfiguration
public class MpPublicationMvcServiceTest extends BaseSpringTest {

    @Autowired
    private WebApplicationContext wac;

    @Resource(name="expectedGetMpPub1")
    public String expectedGetMpPub1;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void getPublicationTest() throws Exception {
        MvcResult rtn = mockMvc.perform(get("/mppublication/1?mimetype=json").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
                sameJSONObjectAs(new JSONObject(expectedGetMpPub1)));
    }
    
    @Test
    public void getPublicationTest_singleSearchTerm() throws Exception {
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

    @Test
    public void updatePublicationTest() throws Exception {
        MvcResult rtn = mockMvc.perform(put("/mppublication/2").content("{\"id\":2,\"publicationType\":{\"id\":"
                + PublicationType.REPORT + ",\"text\":\"abc\"},\"indexId\":\"abc\"}").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

//        assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
//                sameJSONObjectAs(new JSONObject("{\"value\":\"4\",\"text\":\"Book\"}")));
    }
}
