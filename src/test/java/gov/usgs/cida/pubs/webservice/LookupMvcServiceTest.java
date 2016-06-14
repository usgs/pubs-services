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
import gov.usgs.cida.pubs.dao.ContributorTypeDaoTest;
import gov.usgs.cida.pubs.dao.LinkFileTypeDaoTest;
import gov.usgs.cida.pubs.dao.LinkTypeDaoTest;

public class LookupMvcServiceTest extends BaseSpringTest {

	private MockMvc mockLookup;

	@Before
	public void setup() {
		mockLookup = MockMvcBuilders.webAppContextSetup(wac).build();
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

}
