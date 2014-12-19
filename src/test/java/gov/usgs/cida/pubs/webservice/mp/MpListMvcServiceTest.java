package gov.usgs.cida.pubs.webservice.mp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONArrayAs;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.dao.mp.MpListDaoTest;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.validation.ValidationResults;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class MpListMvcServiceTest extends BaseSpringTest {

	@Mock
	private IBusService<MpList> busService;

    private MockMvc mockMvc;

    private MpListMvcService mvcService;
    
    @Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
    	mvcService = new MpListMvcService(busService);
    	mockMvc = MockMvcBuilders.standaloneSetup(mvcService).build();
    }

    @SuppressWarnings("unchecked")
	@Test
    public void getListsTest() throws Exception {
        when(busService.getObjects(anyMap())).thenReturn(getListOfMpList());
        MvcResult rtn = mockMvc.perform(get("/lists?mimetype=json").accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
                sameJSONArrayAs(new JSONArray("[{\"id\":1,\"text\":\"List 1\",\"description\":\"Description 1\",\"type\":\"Type 1\"},"
                		+ "{\"id\":2,\"text\":\"List 2\",\"description\":\"Description 2\",\"type\":\"Type 2\"}]")));
    }

    @Test
    public void createListTest() throws Exception {
        when(busService.createObject(any(MpList.class))).thenReturn(MpListDaoTest.buildMpList(66));
        MvcResult rtn = mockMvc.perform(post("/lists").content("{"
        		+ "\"text\":\"My Name\""
        		+ ",\"description\":\"My Description\""
        		+ ",\"type\":\"My Type\""
        		+ "}").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
                sameJSONObjectAs(new JSONObject("{\"id\":66,\"text\":\"List 66\",\"description\":\"Description 66\",\"type\":\"Type 66\"}")));
    }

    @Test
    public void updateListTest() throws Exception {
        when(busService.updateObject(any(MpList.class))).thenReturn(MpListDaoTest.buildMpList(66));
        MvcResult rtn = mockMvc.perform(put("/lists/66").content("{"
        		+ "\"text\":\"My Name\""
        		+ ",\"description\":\"My Description\""
        		+ ",\"type\":\"My Type\""
        		+ "}").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
                sameJSONObjectAs(new JSONObject("{\"id\":66,\"text\":\"List 66\",\"description\":\"Description 66\",\"type\":\"Type 66\"}")));
    }

    @Test
    public void deleteListTest() throws Exception {
        when(busService.deleteObject(anyInt())).thenReturn(new ValidationResults());
        MvcResult rtn = mockMvc.perform(delete("/lists/66")
        .accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();
        
        assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
                sameJSONObjectAs(new JSONObject("{\"validationErrors\":[]}")));
    }

    public static List<MpList> getListOfMpList() {
    	List<MpList> rtn = new ArrayList<>();
    	rtn.add(MpListDaoTest.buildMpList(1));
    	rtn.add(MpListDaoTest.buildMpList(2));
    	return rtn;
    }
    
}
