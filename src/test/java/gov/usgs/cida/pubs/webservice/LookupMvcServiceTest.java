package gov.usgs.cida.pubs.webservice;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONArrayAs;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.PubsConstants;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@WebAppConfiguration
public class LookupMvcServiceTest extends BaseSpringTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void getPublicationType() throws Exception {
        MvcResult rtn = mockMvc.perform(get("/lookup/publicationtypes?text=b").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONArray("[{\"value\":\"4\",\"text\":\"Book\"},{\"value\":\"5\",\"text\":\"Book chapter\"}]"),
                sameJSONArrayAs(new JSONArray(rtn.getResponse().getContentAsString())));
    }

    @Test
    public void getPublicationSubtypeREST() throws Exception {
        MvcResult rtn = mockMvc.perform(get("/lookup/publicationtype/4/publicationsubtypes?text=b").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONArray("[{\"value\":\"11\",\"text\":\"Bibliography\"}]"),
                sameJSONArrayAs(new JSONArray(rtn.getResponse().getContentAsString())));
    }

    @Test
    public void getPublicationSubtypeQuery() throws Exception {
        MvcResult rtn = mockMvc.perform(get("/lookup/publicationsubtypes?text=b&publicationtypeid=4").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONArray("[{\"value\":\"11\",\"text\":\"Bibliography\"}]"),
                sameJSONArrayAs(new JSONArray(rtn.getResponse().getContentAsString())));
    }

    @Test
    public void getPublicationSeriesREST() throws Exception {
        MvcResult rtn = mockMvc.perform(get("/lookup/publicationtype/18/publicationsubtype/1/publicationseries?text=a").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONArray("[{\"value\":\"1\",\"text\":\"Administrative Report\"},{\"value\":\"2\",\"text\":\"Advisory Report\"},{\"value\":\"3\",\"text\":\"Annual Report\"}]"),
                sameJSONArrayAs(new JSONArray(rtn.getResponse().getContentAsString())));
    }

    @Test
    public void getPublicationSeriesQuery() throws Exception {
        MvcResult rtn = mockMvc.perform(get("/lookup/publicationseries?text=a&publicationsubtypeid=1").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONArray("[{\"value\":\"1\",\"text\":\"Administrative Report\"},{\"value\":\"2\",\"text\":\"Advisory Report\"},{\"value\":\"3\",\"text\":\"Annual Report\"}]"),
                sameJSONArrayAs(new JSONArray(rtn.getResponse().getContentAsString())));
    }

}
