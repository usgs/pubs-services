package gov.usgs.cida.pubs.webservice;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONArrayAs;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.dao.CostCenterDaoTest;
import gov.usgs.cida.pubs.dao.OutsideAffiliationDaoTest;
import gov.usgs.cida.pubs.domain.PublicationType;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

@Category(IntegrationTest.class)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testData/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/dataset.xml")
})
public class LookupMvcServiceTestBuildDB extends BaseSpringTest {

    private MockMvc mockLookup;

    @Before
    public void setup() {
    	mockLookup = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void getCostCenters() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/costcenters?mimetype=json").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertEquals(CostCenterDaoTest.COST_CENTER_CNT, new JSONArray(rtn.getResponse().getContentAsString()).length());

        rtn = mockLookup.perform(get("/lookup/costcenters?text=xa").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
                sameJSONArrayAs(new JSONArray("[{\"id\":3,\"text\":\"xAffiliation Cost Center 3\"},{\"id\":4,\"text\":\"xAffiliation Cost Center 4\"}]")));
    }

    @Test
    public void getOutsideAffiliates() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/outsideaffiliates?mimetype=json").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertEquals(OutsideAffiliationDaoTest.OUTSIDE_AFFILIATES_CNT, new JSONArray(rtn.getResponse().getContentAsString()).length());

        rtn = mockLookup.perform(get("/lookup/outsideaffiliates?text=xo").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
                sameJSONArrayAs(new JSONArray("[{\"id\":6,\"text\":\"xOutside Affiliation 2\"}]")));
    }

    @Test
    public void getPublications() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/publications?mimetype=json").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertEquals(6, new JSONArray(rtn.getResponse().getContentAsString()).length());

        rtn = mockLookup.perform(get("/lookup/publications?text=9").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertEquals(1, new JSONArray(rtn.getResponse().getContentAsString()).length());

        assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
                sameJSONArrayAs(new JSONArray("[{\"id\":5,\"text\":\"9 - null -  future title\"}]")));
    }

    @Test
    public void getPublicationSeriesQuery() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/publicationseries?text=zeit&publicationsubtypeid=10").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
                sameJSONArrayAs(new JSONArray("[{\"id\":3803,\"text\":\"Zeitschrift fur Geomorphologie\"},"
                		+ "{\"id\":3804,\"text\":\"Zeitschrift fur Geomorphologie, Supplementband\"},"
                		+ "{\"id\":3805,\"text\":\"Zeitschrift fur Tierpsychologie\"}]")));
    }

    @Test
    public void getPublicationSeriesREST() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/publicationtype/"
                + PublicationType.REPORT + "/publicationsubtype/10/publicationseries?text=zeit").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
                sameJSONArrayAs(new JSONArray("[{\"id\":3803,\"text\":\"Zeitschrift fur Geomorphologie\"},"
                		+ "{\"id\":3804,\"text\":\"Zeitschrift fur Geomorphologie, Supplementband\"},"
                		+ "{\"id\":3805,\"text\":\"Zeitschrift fur Tierpsychologie\"}]")));
    }

}
