package gov.usgs.cida.pubs.webservice;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONArrayAs;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.dao.BaseSpringDaoTest;
import gov.usgs.cida.pubs.dao.CostCenterDaoTest;
import gov.usgs.cida.pubs.dao.OutsideAffiliationDaoTest;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * These tests require the database to be populated.  Note that I have not yet figured out how to get the
 * tests to respect the ILookupView annotation.
 * @author drsteini
 *
 */
public class LookupMvcServiceTestBuildDB extends BaseSpringDaoTest {

    private MockMvc mockLookup;

    @Before
    public void setup() {
    	mockLookup = MockMvcBuilders.standaloneSetup(new LookupMvcService()).build();
    }

    @Test
    public void getCostCenters() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/costcenters?mimetype=json").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertEquals(CostCenterDaoTest.COST_CENTER_CNT, new JSONArray(rtn.getResponse().getContentAsString()).length());

        rtn = mockLookup.perform(get("/lookup/costcenters?text=xa").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
                sameJSONArrayAs(new JSONArray("[{\"id\":3,\"text\":\"xAffiliation Cost Center 3\",\"active\":false,\"usgs\":true},{\"id\":4,\"text\":\"xAffiliation Cost Center 4\",\"active\":true,\"usgs\":true}]")));
    }

    @Test
    public void getOutsideAffiliates() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/outsideaffiliates?mimetype=json").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertEquals(OutsideAffiliationDaoTest.OUTSIDE_AFFILIATES_CNT, new JSONArray(rtn.getResponse().getContentAsString()).length());

        rtn = mockLookup.perform(get("/lookup/outsideaffiliates?text=xo").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
                sameJSONArrayAs(new JSONArray("[{\"id\":6,\"text\":\"xOutside Affiliation 2\",\"active\":false,\"usgs\":false}]")));
    }

    @Test
    public void getPublications() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/publications?mimetype=json").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertEquals(6, new JSONArray(rtn.getResponse().getContentAsString()).length());

        rtn = mockLookup.perform(get("/lookup/publications?text=9").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertEquals(1, new JSONArray(rtn.getResponse().getContentAsString()).length());

        assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
                sameJSONArrayAs(new JSONArray("[{\"id\":5,\"text\":\"9 - null -  future title\",\"indexId\":\"9\",\"title\":\" future title\"}]")));
    }

}
