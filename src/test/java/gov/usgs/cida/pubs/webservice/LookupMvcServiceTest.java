package gov.usgs.cida.pubs.webservice;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONArrayAs;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.dao.ContributorTypeDaoTest;
import gov.usgs.cida.pubs.dao.LinkFileTypeDaoTest;
import gov.usgs.cida.pubs.dao.LinkTypeDaoTest;
import gov.usgs.cida.pubs.dao.PublishingServiceCenterDaoTest;
import gov.usgs.cida.pubs.domain.PublicationType;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * These tests can use the liquibase loaded db data.  Note that I have no yet figured out how to get the
 * tests to respect the ILookupView annotation.
 * @author drsteini
 *
 */
public class LookupMvcServiceTest extends BaseSpringTest {

    private MockMvc mockLookup;

    @Before
    public void setup() {
    	mockLookup = MockMvcBuilders.standaloneSetup(new LookupMvcService()).build();
    }

    @Test
    public void getPublicationType() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/publicationtypes?text=b").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
                sameJSONArrayAs(new JSONArray("[{\"id\":4,\"text\":\"Book\"},{\"id\":5,\"text\":\"Book chapter\"}]")));
    }

    @Test
    public void getPublicationSubtypeREST() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/publicationtype/4/publicationsubtypes?text=b").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
                sameJSONArrayAs(new JSONArray("[{\"id\":11,\"text\":\"Bibliography\",\"publicationType\":{\"id\":4}}]")));
    }

    @Test
    public void getPublicationSubtypeQuery() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/publicationsubtypes?text=b&publicationtypeid=4").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
                sameJSONArrayAs(new JSONArray("[{\"id\":11,\"text\":\"Bibliography\",\"publicationType\":{\"id\":4}}]")));
    }

    @Test
    public void getPublicationSeriesREST() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/publicationtype/"
                + PublicationType.REPORT + "/publicationsubtype/10/publicationseries?text=zeit").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
                sameJSONArrayAs(new JSONArray("[{\"id\":3803,\"text\":\"Zeitschrift fur Geomorphologie\",\"publicationSubtype\":{\"id\":10}},"
                		+ "{\"id\":3804,\"text\":\"Zeitschrift fur Geomorphologie, Supplementband\",\"publicationSubtype\":{\"id\":10}},"
                		+ "{\"id\":3805,\"text\":\"Zeitschrift fur Tierpsychologie\",\"publicationSubtype\":{\"id\":10}}]")));
    }

    @Test
    public void getPublicationSeriesQuery() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/publicationseries?text=zeit&publicationsubtypeid=10").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
                sameJSONArrayAs(new JSONArray("[{\"id\":3803,\"text\":\"Zeitschrift fur Geomorphologie\",\"publicationSubtype\":{\"id\":10}},"
                		+ "{\"id\":3804,\"text\":\"Zeitschrift fur Geomorphologie, Supplementband\",\"publicationSubtype\":{\"id\":10}},"
                		+ "{\"id\":3805,\"text\":\"Zeitschrift fur Tierpsychologie\",\"publicationSubtype\":{\"id\":10}}]")));
    }

    @Test
    public void getContributorTypes() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/contributortypes?mimetype=json").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertEquals(ContributorTypeDaoTest.contributorTypeCnt, new JSONArray(rtn.getResponse().getContentAsString()).length());

        rtn = mockLookup.perform(get("/lookup/contributortypes?text=au").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
                sameJSONArrayAs(new JSONArray("[{\"id\":1,\"text\":\"Authors\"}]")));
    }

    @Test
    public void getPublishingServiceCenters() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/publishingServiceCenters?mimetype=json").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertEquals(PublishingServiceCenterDaoTest.PSC_CNT, new JSONArray(rtn.getResponse().getContentAsString()).length());

        rtn = mockLookup.perform(get("/lookup/publishingServiceCenters?text=r").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertEquals(PublishingServiceCenterDaoTest.PSC_R_CNT, new JSONArray(rtn.getResponse().getContentAsString()).length());

        assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
                sameJSONArrayAs(new JSONArray("[{\"id\":4,\"text\":\"Rolla PSC\"},{\"id\":8,\"text\":\"Raleigh PSC\"},{\"id\":9,\"text\":\"Reston PSC\"}]")));
    }

    @Test
    public void getLinkTypes() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/linktypes?mimetype=json").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertEquals(LinkTypeDaoTest.LINK_TYPES_CNT, new JSONArray(rtn.getResponse().getContentAsString()).length());

        rtn = mockLookup.perform(get("/lookup/linktypes?text=r").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertEquals(LinkTypeDaoTest.LINK_TYPES_R_CNT, new JSONArray(rtn.getResponse().getContentAsString()).length());

        assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
                sameJSONArrayAs(new JSONArray("[{\"id\":19,\"text\":\"Raw Data\"},{\"id\":20,\"text\":\"Read Me\"},{\"id\":21,\"text\":\"Referenced Work\"},{\"id\":22,\"text\":\"Related Work\"}]")));
    }

    @Test
    public void getLinkFileTypes() throws Exception {
        MvcResult rtn = mockLookup.perform(get("/lookup/linkfiletypes?mimetype=json").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertEquals(LinkFileTypeDaoTest.LINK_FILE_TYPES_CNT, new JSONArray(rtn.getResponse().getContentAsString()).length());

        assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
                sameJSONArrayAs(new JSONArray("[{\"id\":1,\"text\":\"pdf\"},{\"id\":2,\"text\":\"txt\"}," 
                		+ "{\"id\":3,\"text\":\"xlsx\"},{\"id\":4,\"text\":\"shapefile\"},{\"id\":5,\"text\":\"html\"}]")));
        
        rtn = mockLookup.perform(get("/lookup/linkfiletypes?text=s").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertEquals(LinkFileTypeDaoTest.LINK_FILE_TYPES_S_CNT, new JSONArray(rtn.getResponse().getContentAsString()).length());

        assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
                sameJSONArrayAs(new JSONArray("[{\"id\":4,\"text\":\"shapefile\"}]")));
    }

}
