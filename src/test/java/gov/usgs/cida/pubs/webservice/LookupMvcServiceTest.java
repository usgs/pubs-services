package gov.usgs.cida.pubs.webservice;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONArrayAs;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.dao.ContributorDaoTest;
import gov.usgs.cida.pubs.dao.ContributorTypeDaoTest;
import gov.usgs.cida.pubs.dao.LinkFileTypeDaoTest;
import gov.usgs.cida.pubs.dao.LinkTypeDaoTest;
import gov.usgs.cida.pubs.dao.PublishingServiceCenterDaoTest;

public class LookupMvcServiceTest extends BaseSpringTest {

    private MockMvc mockLookup;

    @Before
    public void setup() {
    	mockLookup = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void getPublicationType() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/publicationtypes?text=b").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

		assertThat(getRtnAsJSONArray(rtn),
				sameJSONArrayAs(new JSONArray("[{\"id\":4,\"text\":\"Book\"},{\"id\":5,\"text\":\"Book chapter\"}]"))
						.allowingAnyArrayOrdering());
    }

    @Test
    public void getPublicationSubtypeREST() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/publicationtype/4/publicationsubtypes?text=b").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

		assertThat(getRtnAsJSONArray(rtn),
				sameJSONArrayAs(new JSONArray("[{\"id\":11,\"text\":\"Bibliography\"}]")).allowingAnyArrayOrdering());
    }

    @Test
    public void getPublicationSubtypeQuery() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/publicationsubtypes?text=b&publicationtypeid=4").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

		assertThat(getRtnAsJSONArray(rtn),
				sameJSONArrayAs(new JSONArray("[{\"id\":11,\"text\":\"Bibliography\"}]")).allowingAnyArrayOrdering());
    }

    @Test
    public void getContributorTypes() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/contributortypes?mimetype=json").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertEquals(ContributorTypeDaoTest.contributorTypeCnt, getRtnAsJSONArray(rtn).length());

        rtn = mockLookup.perform(get("/lookup/contributortypes?text=au").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

		assertThat(getRtnAsJSONArray(rtn),
				sameJSONArrayAs(new JSONArray("[{\"id\":1,\"text\":\"Authors\"}]")).allowingAnyArrayOrdering());
    }

    @Test
    public void getPublishingServiceCenters() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/publishingServiceCenters?mimetype=json").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertEquals(PublishingServiceCenterDaoTest.PSC_CNT, getRtnAsJSONArray(rtn).length());

        rtn = mockLookup.perform(get("/lookup/publishingServiceCenters?text=r").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        JSONArray rtnAsJSONArray = getRtnAsJSONArray(rtn);
        
        assertEquals(PublishingServiceCenterDaoTest.PSC_R_CNT, rtnAsJSONArray.length());

		assertThat(rtnAsJSONArray,
				sameJSONArrayAs(new JSONArray(
						"[{\"id\":4,\"text\":\"Rolla PSC\"},{\"id\":8,\"text\":\"Raleigh PSC\"},{\"id\":9,\"text\":\"Reston PSC\"}]"))
								.allowingAnyArrayOrdering());
    }

    @Test
    public void getLinkTypes() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/linktypes?mimetype=json").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertEquals(LinkTypeDaoTest.LINK_TYPES_CNT, getRtnAsJSONArray(rtn).length());

        rtn = mockLookup.perform(get("/lookup/linktypes?text=r").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        JSONArray rtnAsJSONArray = getRtnAsJSONArray(rtn);
        
        assertEquals(LinkTypeDaoTest.LINK_TYPES_R_CNT, rtnAsJSONArray.length());

		assertThat(rtnAsJSONArray,
				sameJSONArrayAs(new JSONArray(
						"[{\"id\":19,\"text\":\"Raw Data\"},{\"id\":20,\"text\":\"Read Me\"},{\"id\":21,\"text\":\"Referenced Work\"},{\"id\":22,\"text\":\"Related Work\"}]"))
								.allowingAnyArrayOrdering());
    }

    @Test
    public void getLinkFileTypes() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/linkfiletypes?mimetype=json").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        JSONArray rtnAsJSONArray = getRtnAsJSONArray(rtn);
        
        assertEquals(LinkFileTypeDaoTest.LINK_FILE_TYPES_CNT, getRtnAsJSONArray(rtn).length());

		assertThat(rtnAsJSONArray,
				sameJSONArrayAs(new JSONArray("[{\"id\":1,\"text\":\"pdf\"},{\"id\":2,\"text\":\"txt\"},"
						+ "{\"id\":3,\"text\":\"xlsx\"},{\"id\":4,\"text\":\"shapefile\"},{\"id\":5,\"text\":\"html\"}"
						+ ",{\"id\":6,\"text\":\"zip\"},{\"id\":7,\"text\":\"csv\"}]")).allowingAnyArrayOrdering());
        
        rtn = mockLookup.perform(get("/lookup/linkfiletypes?text=s").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        rtnAsJSONArray = getRtnAsJSONArray(rtn);
        
        assertEquals(LinkFileTypeDaoTest.LINK_FILE_TYPES_S_CNT, rtnAsJSONArray.length());

		assertThat(rtnAsJSONArray,
				sameJSONArrayAs(new JSONArray("[{\"id\":4,\"text\":\"shapefile\"}]")).allowingAnyArrayOrdering());
    }

    @Test
    public void getPeople() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/people?mimetype=json").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertEquals(ContributorDaoTest.PERSON_CONTRIBUTOR_CNT, getRtnAsJSONArray(rtn).length());

        rtn = mockLookup.perform(get("/lookup/people?text=out").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        JSONArray rtnAsJSONArray = getRtnAsJSONArray(rtn);
        
        assertEquals(1, rtnAsJSONArray.length());

		assertThat(rtnAsJSONArray,
				sameJSONArrayAs(
						new JSONArray("[{\"id\":3,\"text\":\"outerfamily, outerGiven outerSuffix outer@gmail.com\"}]"))
								.allowingAnyArrayOrdering());
    }

    @Test
    public void getCorporations() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/corporations?mimetype=json").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertEquals(ContributorDaoTest.CORPORATE_CONTRIBUTOR_CNT, getRtnAsJSONArray(rtn).length());

        rtn = mockLookup.perform(get("/lookup/corporations?text=us geo").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        JSONArray rtnAsJSONArray = getRtnAsJSONArray(rtn);
        
        assertEquals(1, rtnAsJSONArray.length());

		assertThat(rtnAsJSONArray,
				sameJSONArrayAs(new JSONArray("[{\"id\":2,\"text\":\"US Geological Survey Ice Survey Team\"}]"))
						.allowingAnyArrayOrdering());
    }
    
}
