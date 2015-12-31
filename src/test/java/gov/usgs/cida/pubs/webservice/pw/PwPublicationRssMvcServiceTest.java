package gov.usgs.cida.pubs.webservice.pw;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.intfc.IPwPublicationBusService;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDaoTest;

public class PwPublicationRssMvcServiceTest extends BaseSpringTest {
	@Mock
    private IPwPublicationBusService busService;

    @Resource(name="expectedGetRssPub")
    public String expectedGetRssPub;
    
	@Autowired
    public String warehouseEndpoint;

	public String expectedGetPwRssTitle;
    public String expectedGetPwRssItemTitle;
    public String expectedGetPwRssItemDescription;

    private MockMvc mockMvc;

    private PwPublicationRssMvcService mvcRssService;
    
    @Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
    	mvcRssService = new PwPublicationRssMvcService(busService, warehouseEndpoint);
    	mockMvc = MockMvcBuilders.standaloneSetup(mvcRssService).build();
    	
    	int titleStart = expectedGetRssPub.indexOf("<title>") + "<title>".length();
    	int titleEnd = expectedGetRssPub.indexOf("</title>", titleStart);
    	expectedGetPwRssTitle = expectedGetRssPub.substring(titleStart, titleEnd);
    	
    	int itemTitleStart = expectedGetRssPub.indexOf("<title>", titleEnd) + "<title>".length();
    	int itemTitleEnd = expectedGetRssPub.indexOf("</title>", itemTitleStart);
    	expectedGetPwRssItemTitle = expectedGetRssPub.substring(itemTitleStart, itemTitleEnd);
    	
    	int itemDescStart = expectedGetRssPub.indexOf("<description>", itemTitleEnd) + "<description>".length();
    	int itemDescEnd = expectedGetRssPub.indexOf("</description>", itemTitleStart);
    	expectedGetPwRssItemDescription = expectedGetRssPub.substring(itemDescStart, itemDescEnd);
    }
    
    @SuppressWarnings("unchecked")
	@Test
    public void getRssPubsTest() throws Exception {
    	//Happy Path
        when(busService.getObjects(anyMap())).thenReturn(Arrays.asList(PwPublicationDaoTest.buildAPub(1)));
    	
        MvcResult rtn = mockMvc.perform(get("/publication/rss?orderby=dispPubDate").accept(PubsConstants.MEDIA_TYPE_RSS))
        .andExpect(status().isOk())
        .andExpect(content().contentType(PubsConstants.MEDIA_TYPE_RSS_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        String response = rtn.getResponse().getContentAsString();
        
        // Not in the business of building an XML object, that is the RSS client's responsibility.
        // Just interested in our values matching up to where they need to be.
        int titleStart = response.indexOf("<title>") + "<title>".length();
    	int titleEnd = response.indexOf("</title>", titleStart);
    	String resultingPwRssTitle = response.substring(titleStart, titleEnd);
    	assertTrue(resultingPwRssTitle.equals(expectedGetPwRssTitle));
    	
    	int itemTitleStart = response.indexOf("<title>", titleEnd) + "<title>".length();
    	int itemTitleEnd = response.indexOf("</title>", itemTitleStart);
    	String resultingPwRssItemTitle = response.substring(itemTitleStart, itemTitleEnd);
    	assertTrue(resultingPwRssItemTitle.equals(expectedGetPwRssItemTitle));
    	
    	int itemDescStart = response.indexOf("<description>", itemTitleEnd) + "<description>".length();
    	int itemDescEnd = response.indexOf("</description>", itemTitleStart);
    	String resultingPwRssItemDescription = response.substring(itemDescStart, itemDescEnd);
    	assertTrue(resultingPwRssItemDescription.equals(expectedGetPwRssItemDescription));
    }

}
