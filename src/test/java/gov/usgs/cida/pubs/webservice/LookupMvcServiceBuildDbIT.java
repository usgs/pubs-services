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
import gov.usgs.cida.pubs.FullPubsDatabaseSetup;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.dao.ContributorDaoIT;
import gov.usgs.cida.pubs.dao.CostCenterDaoIT;
import gov.usgs.cida.pubs.dao.OutsideAffiliationDaoIT;
import gov.usgs.cida.pubs.dao.PublishingServiceCenterDaoIT;
import gov.usgs.cida.pubs.domain.PublicationType;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment=WebEnvironment.MOCK)
@FullPubsDatabaseSetup
public class LookupMvcServiceBuildDbIT extends BaseIT {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void getCorporations() throws Exception {
		MvcResult rtn = performGetRequest("/lookup/corporations?mimetype=json");

		assertEquals(ContributorDaoIT.CORPORATE_CONTRIBUTOR_CNT, new JSONArray(rtn.getResponse().getContentAsString()).length());

		rtn = performGetRequest("/lookup/corporations?text=us geo");

		JSONArray rtnAsJSONArray = new JSONArray(rtn.getResponse().getContentAsString());

		assertEquals(1, new JSONArray(rtn.getResponse().getContentAsString()).length());

		assertThat(rtnAsJSONArray,
				sameJSONArrayAs(new JSONArray("[{\"id\":2,\"text\":\"US Geological Survey Ice Survey Team\", \"corporation\":true, \"usgs\":false}]"))
						.allowingAnyArrayOrdering());
	}

	@Test
	public void getPeople() throws Exception {
		MvcResult rtn = performGetRequest("/lookup/people?text=oute");

		JSONArray rtnAsJSONArray = new JSONArray(rtn.getResponse().getContentAsString());

		assertEquals(1, rtnAsJSONArray.length());

		assertThat(rtnAsJSONArray,
				sameJSONArrayAs(new JSONArray(getCompareFile("lookups/outePeople.json"))).allowingAnyArrayOrdering());

		rtn = performGetRequest("/lookup/people?orcid=0000-0000-0000-0004");

		rtnAsJSONArray = new JSONArray(rtn.getResponse().getContentAsString());

		assertEquals(5, rtnAsJSONArray.length());

		assertThat(rtnAsJSONArray,
				sameJSONArrayAs(new JSONArray(getCompareFile("lookups/orcid4People.json"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getCostCenters() throws Exception {
		String endPoint = "/lookup/costcenters";
		MvcResult rtn = performGetRequest(endPoint + "?mimetype=json");

		assertEquals(CostCenterDaoIT.COST_CENTER_CNT, new JSONArray(rtn.getResponse().getContentAsString()).length());

		rtn = performGetRequest(endPoint + "?text=xa");

		assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
				sameJSONArrayAs(new JSONArray("[{\"id\":3,\"text\":\"xAffiliation Cost Center 3\"},{\"id\":4,\"text\":\"xAffiliation Cost Center 4\"}]")));

		rtn = performGetRequest(endPoint + "?active=false");
		assertEquals(1, new JSONArray(rtn.getResponse().getContentAsString()).length());

		rtn = performGetRequest(endPoint + "?active=true");
		assertEquals(4, new JSONArray(rtn.getResponse().getContentAsString()).length());
	}

	@Test
	public void getOutsideAffiliates() throws Exception {
		String endPoint = "/lookup/outsideaffiliates?mimetype=json";
		MvcResult rtn = performGetRequest(endPoint);

		assertEquals(OutsideAffiliationDaoIT.OUTSIDE_AFFILIATES_CNT, new JSONArray(rtn.getResponse().getContentAsString()).length());

		rtn = performGetRequest("/lookup/outsideaffiliates?text=xo");

		assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
				sameJSONArrayAs(new JSONArray("[{\"id\":6,\"text\":\"xOutside Affiliation 2\"}]")));

		rtn = performGetRequest(endPoint + "&active=false");
		assertEquals(1, new JSONArray(rtn.getResponse().getContentAsString()).length());

		rtn = performGetRequest(endPoint + "&active=true");
		assertEquals(2, new JSONArray(rtn.getResponse().getContentAsString()).length());
	}

	@Test
	public void getPublications() throws Exception {
		MvcResult rtn = performGetRequest("/lookup/publications?mimetype=json");

		assertEquals(8, new JSONArray(rtn.getResponse().getContentAsString()).length());

		rtn = performGetRequest("/lookup/publications?text=9");

		assertEquals(1, new JSONArray(rtn.getResponse().getContentAsString()).length());

		assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
				sameJSONArrayAs(new JSONArray("[{\"id\":5,\"text\":\"9 - No Year -  future title\"}]")));
	}

	@Test
	public void getPublicationSeriesQuery() throws Exception {
		MvcResult rtn = performGetRequest("/lookup/publicationseries?text=zeit&publicationsubtypeid=10");

		assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
				sameJSONArrayAs(new JSONArray("[{\"id\":3803,\"text\":\"Zeitschrift fur Geomorphologie\"},"
						+ "{\"id\":3804,\"text\":\"Zeitschrift fur Geomorphologie, Supplementband\"},"
						+ "{\"id\":3805,\"text\":\"Zeitschrift fur Tierpsychologie\"}]")));

		String endPoint = "/lookup/publicationseries";
		rtn = performGetRequest(endPoint);
		assertEquals(17, new JSONArray(rtn.getResponse().getContentAsString()).length());

		rtn = performGetRequest(endPoint + "?active=false");
		assertEquals(7, new JSONArray(rtn.getResponse().getContentAsString()).length());

		rtn = performGetRequest(endPoint + "?active=true");
		assertEquals(10, new JSONArray(rtn.getResponse().getContentAsString()).length());
	}

	@Test
	public void getPublicationSeriesREST() throws Exception {
		MvcResult rtn = performGetRequest("/lookup/publicationtype/"
				+ PublicationType.REPORT + "/publicationsubtype/10/publicationseries?text=zeit");

		assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
				sameJSONArrayAs(new JSONArray("[{\"id\":3803,\"text\":\"Zeitschrift fur Geomorphologie\"},"
						+ "{\"id\":3804,\"text\":\"Zeitschrift fur Geomorphologie, Supplementband\"},"
						+ "{\"id\":3805,\"text\":\"Zeitschrift fur Tierpsychologie\"}]")));

		String endPoint = "/lookup/publicationtype/5/publicationsubtype/5/publicationseries";
		rtn = performGetRequest(endPoint);
		assertEquals(9, new JSONArray(rtn.getResponse().getContentAsString()).length());

		rtn = performGetRequest(endPoint + "?active=false");
		assertEquals(4, new JSONArray(rtn.getResponse().getContentAsString()).length());

		rtn = performGetRequest(endPoint + "?active=true");
		assertEquals(5, new JSONArray(rtn.getResponse().getContentAsString()).length());
	}

	@Test
	public void getPublicationSubtypeQuery() throws Exception {
		MvcResult rtn = performGetRequest("/lookup/publicationsubtypes?text=b&publicationtypeid=4");

		assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
				sameJSONArrayAs(new JSONArray("[{\"id\":11,\"text\":\"Bibliography\"}]")).allowingAnyArrayOrdering());
	}

	@Test
	public void getPublicationSubtypeREST() throws Exception {
		MvcResult rtn = performGetRequest("/lookup/publicationtype/4/publicationsubtypes?text=b");

		assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
				sameJSONArrayAs(new JSONArray("[{\"id\":11,\"text\":\"Bibliography\"}]")).allowingAnyArrayOrdering());

	}

	@Test
	public void getPublicationType() throws Exception {
		MvcResult rtn = performGetRequest("/lookup/publicationtypes?text=b");

		assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
				sameJSONArrayAs(new JSONArray("[{\"id\":4,\"text\":\"Book\"},{\"id\":5,\"text\":\"Book chapter\"}]"))
						.allowingAnyArrayOrdering());
	}

	@Test
	public void getPublishingServiceCenters() throws Exception {
		MvcResult rtn = performGetRequest("/lookup/publishingServiceCenters?mimetype=json");

		assertEquals(PublishingServiceCenterDaoIT.PSC_CNT, new JSONArray(rtn.getResponse().getContentAsString()).length());

		rtn = performGetRequest("/lookup/publishingServiceCenters?text=r");

		JSONArray rtnAsJSONArray = new JSONArray(rtn.getResponse().getContentAsString());

		assertEquals(PublishingServiceCenterDaoIT.PSC_R_CNT, rtnAsJSONArray.length());

		assertThat(rtnAsJSONArray,
				sameJSONArrayAs(new JSONArray(
						"[{\"id\":4,\"text\":\"Rolla PSC\"},{\"id\":8,\"text\":\"Raleigh PSC\"},{\"id\":9,\"text\":\"Reston PSC\"}]"))
								.allowingAnyArrayOrdering());
	}

	private MvcResult performGetRequest(String path) throws Exception {
		MvcResult rtn = mockMvc
				.perform(get(path).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();
		return rtn;
	}

}
