package gov.usgs.cida.pubs.webservice.mp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONArrayAs;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.busservice.intfc.IMpListPublicationBusService;
import gov.usgs.cida.pubs.dao.mp.MpListDaoIT;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.ValidationResults;

public class MpListPublicationMvcServiceTest extends BaseTest {

	@MockBean
	private IMpListPublicationBusService busService;

	private MockMvc mockMvc;

	private MpListPublicationMvcService mvcService;

	private String expected = "[{\"mpList\":{\"id\":66,\"text\":\"List 66\",\"description\":\"Description 66\",\"type\":\"SPN\"},\"mpPublication\":"
			+ "{\"id\":12,\"validationErrors\":[],\"text\":\"null - null - null\",\"noYear\":false,\"noUsgsAuthors\":false,\"published\":false}}]";

	@Before
	public void setup() {
		mvcService = new MpListPublicationMvcService(busService);
		mockMvc = MockMvcBuilders.standaloneSetup(mvcService).build();
	}

	@Test
	public void addPubToListTest() throws Exception {
		String[] ids = new String[]{"12"};
		when(busService.addPubToList(66, ids)).thenReturn(buildIt());
		MvcResult rtn = mockMvc.perform(post("/lists/66/pubs?publicationId=12")
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertThat(getRtnAsJSONArray(rtn),
				sameJSONArrayAs(new JSONArray(expected)));
	}

	@Test
	public void removePubFromListTest() throws Exception {
		when(busService.removePubFromList(anyInt(), anyInt())).thenReturn(new ValidationResults());
		MvcResult rtn = mockMvc.perform(delete("/lists/66/pubs/12")
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();
		
		assertThat(getRtnAsJSONObject(rtn),
				sameJSONObjectAs(new JSONObject("{\"validationErrors\":[]}")));
	}

	private List<MpListPublication> buildIt() {
		MpListPublication it = new MpListPublication();
		it.setMpList(MpListDaoIT.buildMpList(66));
		MpPublication mpPub = new MpPublication();
		mpPub.setId(12);
		it.setMpPublication(mpPub);
		List<MpListPublication> list = new ArrayList<>();
		list.add(it);
		return list;
	}
}
