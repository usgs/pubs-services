package gov.usgs.cida.pubs.webservice.pw;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.busservice.intfc.IPwPublicationBusService;
import gov.usgs.cida.pubs.domain.pw.PwPublicationTest;
import gov.usgs.cida.pubs.domain.query.PwPublicationFilterParams;
import gov.usgs.cida.pubs.springinit.TestSpringConfig;

@SpringBootTest(webEnvironment=WebEnvironment.MOCK,
	classes={TestSpringConfig.class, ConfigurationService.class,
			PwPublicationFilterParams.class})
public class PwPublicationRssMvcServiceTest extends BaseTest {
	@MockBean
	private IPwPublicationBusService busService;

	@Resource(name="expectedGetRssPub")
	public String expectedGetRssPub;

	@Autowired
	public ConfigurationService configurationService;

	public String expectedGetPwRssTitle;
	public String expectedGetPwRssItemTitle;
	public String expectedGetPwRssItemDescription;

	private MockMvc mockMvc;

	@BeforeEach
	public void setUp() {
		PwPublicationRssMvcService mvcRssService = new PwPublicationRssMvcService(busService, configurationService);
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

	@Test
	public void getRssPubsTest() throws Exception {
		//Happy Path
		when(busService.getObjects(any(PwPublicationFilterParams.class))).thenReturn(List.of(PwPublicationTest.buildAPub(1)));

		MvcResult rtn = mockMvc.perform(get("/publication/rss?orderby=dispPubDate").accept(PubsConstantsHelper.MEDIA_TYPE_RSS))
		.andExpect(status().isOk())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_RSS_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
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
