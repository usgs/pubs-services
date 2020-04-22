package gov.usgs.cida.pubs.webservice;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONArrayAs;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.dao.intfc.IPersonContributorDao;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.security.UserDetailTestService;
import gov.usgs.cida.pubs.utility.DataNormalizationUtils;

@DirtiesContext
public class LkupMvcServiceTest extends BaseTest {

	@MockBean(name="personContributorDao")
	private IPersonContributorDao personContributorDao;
	@MockBean(name="contributorDao")
	private IDao<Contributor<?>> contributorDao;
	@SuppressWarnings("unused")
	private PersonContributor<?> personContributor;

	private LookupMvcService mvcService;
	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		personContributor = new UsgsContributor();
		personContributor.setContributorDao(contributorDao);
		personContributor.setPersonContributorDao(personContributorDao);
		mvcService = new LookupMvcService();
		mockMvc = MockMvcBuilders.standaloneSetup(mvcService).build();
	}

	@Test
	@WithMockUser(username=UserDetailTestService.ANONOMOUS_USER)
	public void getPeopleTest() throws Exception {
		mockMvc.perform(get("/lookup/people").accept(MediaType.APPLICATION_JSON)).andReturn();
		mockMvc.perform(get("/lookup/people?text=a").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		when(personContributorDao.getByMap(anyMap())).thenReturn(getPeople());

		MvcResult rtn = mockMvc.perform(get("/lookup/people?text=kr").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
				sameJSONArrayAs(contributorJsonArray(contributor1())).allowingAnyArrayOrdering());
	}

	List<Contributor<?>> getPeople() throws JSONException {
		List<Contributor<?>> rtn = new ArrayList<>();
		rtn.add(contributor1());
		return rtn;
	}

	private PersonContributor<?> contributor1() throws JSONException {
		PersonContributor<?> contributor = new UsgsContributor();

		contributor.setId(1);
		contributor.setFamily("Kreft");
		contributor.setGiven("James M.");
		contributor.setEmail("outer@gmail.com");
		contributor.setEmail("jkreft@usgs.gov");
		contributor.setSuffix("Mr.");
		contributor.setOrcid("https://orcid.org/0000-0000-0000-0001");

		return contributor;
	}

	private JSONArray contributorJsonArray(PersonContributor<?> contributor) throws JSONException {
		JSONObject json = new JSONObject();

		json.put("id", contributor.getId());
		json.put("email", contributor.getEmail());
		json.put("given", contributor.getGiven());
		json.put("preferred", contributor.isPreferred());
		json.put("corporation", contributor.isCorporation());
		json.put("family", contributor.getFamily());
		json.put("suffix", contributor.getSuffix());
		json.put("orcid", contributor.getOrcid());
		json.put("usgs", contributor.isUsgs());
		json.put("text", contributor.getText());
		json.put("affiliations", new JSONArray());

		String jsonStr = json.toString();
		jsonStr =  jsonStr.replace(DataNormalizationUtils.normalizeOrcid(contributor.getOrcid()), DataNormalizationUtils.denormalizeOrcid(contributor.getOrcid()));
		JSONArray jsonArray = new JSONArray("[" + jsonStr + "]");

		return jsonArray;
	}
}
