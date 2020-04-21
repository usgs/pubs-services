package gov.usgs.cida.pubs.webservice;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONArrayAs;

import org.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.dao.ContributorTypeDaoIT;
import gov.usgs.cida.pubs.dao.LinkFileTypeDaoIT;
import gov.usgs.cida.pubs.dao.LinkTypeDaoIT;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment=WebEnvironment.MOCK)
public class LookupMvcServiceIT extends BaseIT {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void getContributorTypes() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/lookup/contributortypes?mimetype=json").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertEquals(ContributorTypeDaoIT.CONTRIBUTOR_TYPE_CNT, new JSONArray(rtn.getResponse().getContentAsString()).length());

		rtn = mockMvc.perform(get("/lookup/contributortypes?text=au").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
				sameJSONArrayAs(new JSONArray("[{\"id\":1,\"text\":\"Authors\"}]")).allowingAnyArrayOrdering());
	}

	@Test
	public void getLinkFileTypes() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/lookup/linkfiletypes?mimetype=json").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		JSONArray rtnAsJSONArray = new JSONArray(rtn.getResponse().getContentAsString());

		assertEquals(LinkFileTypeDaoIT.LINK_FILE_TYPES_CNT, rtnAsJSONArray.length());

		assertThat(rtnAsJSONArray,
				sameJSONArrayAs(new JSONArray("[{\"id\":1,\"text\":\"pdf\"},{\"id\":2,\"text\":\"txt\"},"
						+ "{\"id\":3,\"text\":\"xlsx\"},{\"id\":4,\"text\":\"shapefile\"},{\"id\":5,\"text\":\"html\"}"
						+ ",{\"id\":6,\"text\":\"zip\"},{\"id\":7,\"text\":\"csv\"},{\"id\":8,\"text\":\"xml\"}]")).allowingAnyArrayOrdering());
		
		rtn = mockMvc.perform(get("/lookup/linkfiletypes?text=s").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		rtnAsJSONArray = new JSONArray(rtn.getResponse().getContentAsString());

		assertEquals(LinkFileTypeDaoIT.LINK_FILE_TYPES_S_CNT, rtnAsJSONArray.length());

		assertThat(rtnAsJSONArray,
				sameJSONArrayAs(new JSONArray("[{\"id\":4,\"text\":\"shapefile\"}]")).allowingAnyArrayOrdering());
	}

	@Test
	public void getLinkTypes() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/lookup/linktypes?mimetype=json").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertEquals(LinkTypeDaoIT.LINK_TYPES_CNT, new JSONArray(rtn.getResponse().getContentAsString()).length());

		rtn = mockMvc.perform(get("/lookup/linktypes?text=r").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		JSONArray rtnAsJSONArray = new JSONArray(rtn.getResponse().getContentAsString());

		assertEquals(LinkTypeDaoIT.LINK_TYPES_R_CNT, rtnAsJSONArray.length());

		assertThat(rtnAsJSONArray,
				sameJSONArrayAs(new JSONArray(
						"[{\"id\":19,\"text\":\"Raw Data\"},{\"id\":20,\"text\":\"Read Me\"},{\"id\":21,\"text\":\"Referenced Work\"},{\"id\":22,\"text\":\"Related Work\"}]"))
								.allowingAnyArrayOrdering());
	}

}
