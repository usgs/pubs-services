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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.dao.ContributorTypeDao;
import gov.usgs.cida.pubs.dao.ContributorTypeDaoIT;
import gov.usgs.cida.pubs.dao.LinkFileTypeDao;
import gov.usgs.cida.pubs.dao.LinkFileTypeDaoIT;
import gov.usgs.cida.pubs.dao.LinkTypeDao;
import gov.usgs.cida.pubs.dao.LinkTypeDaoIT;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.LinkFileType;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@EnableWebMvc
@SpringBootTest(webEnvironment=WebEnvironment.MOCK,
	classes={DbTestConfig.class, ConfigurationService.class, LookupMvcService.class,
			ContributorType.class, ContributorTypeDao.class, LinkType.class, LinkTypeDao.class,
			LinkFileType.class, LinkFileTypeDao.class})
public class LookupMvcServiceIT extends BaseIT {
	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void getContributorTypes() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/lookup/contributortypes?mimetype=json").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertEquals(ContributorTypeDaoIT.CONTRIBUTOR_TYPE_CNT, getRtnAsJSONArray(rtn).length());

		rtn = mockMvc.perform(get("/lookup/contributortypes?text=au").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertThat(getRtnAsJSONArray(rtn),
				sameJSONArrayAs(new JSONArray("[{\"id\":1,\"text\":\"Authors\"}]")).allowingAnyArrayOrdering());
	}

	@Test
	public void getLinkFileTypes() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/lookup/linkfiletypes?mimetype=json").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		JSONArray rtnAsJSONArray = getRtnAsJSONArray(rtn);

		assertEquals(LinkFileTypeDaoIT.LINK_FILE_TYPES_CNT, getRtnAsJSONArray(rtn).length());

		assertThat(rtnAsJSONArray,
				sameJSONArrayAs(new JSONArray("[{\"id\":1,\"text\":\"pdf\"},{\"id\":2,\"text\":\"txt\"},"
						+ "{\"id\":3,\"text\":\"xlsx\"},{\"id\":4,\"text\":\"shapefile\"},{\"id\":5,\"text\":\"html\"}"
						+ ",{\"id\":6,\"text\":\"zip\"},{\"id\":7,\"text\":\"csv\"},{\"id\":8,\"text\":\"xml\"}]")).allowingAnyArrayOrdering());
		
		rtn = mockMvc.perform(get("/lookup/linkfiletypes?text=s").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		rtnAsJSONArray = getRtnAsJSONArray(rtn);

		assertEquals(LinkFileTypeDaoIT.LINK_FILE_TYPES_S_CNT, rtnAsJSONArray.length());

		assertThat(rtnAsJSONArray,
				sameJSONArrayAs(new JSONArray("[{\"id\":4,\"text\":\"shapefile\"}]")).allowingAnyArrayOrdering());
	}

	@Test
	public void getLinkTypes() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/lookup/linktypes?mimetype=json").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertEquals(LinkTypeDaoIT.LINK_TYPES_CNT, getRtnAsJSONArray(rtn).length());

		rtn = mockMvc.perform(get("/lookup/linktypes?text=r").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		JSONArray rtnAsJSONArray = getRtnAsJSONArray(rtn);

		assertEquals(LinkTypeDaoIT.LINK_TYPES_R_CNT, rtnAsJSONArray.length());

		assertThat(rtnAsJSONArray,
				sameJSONArrayAs(new JSONArray(
						"[{\"id\":19,\"text\":\"Raw Data\"},{\"id\":20,\"text\":\"Read Me\"},{\"id\":21,\"text\":\"Referenced Work\"},{\"id\":22,\"text\":\"Related Work\"}]"))
								.allowingAnyArrayOrdering());
	}

}
