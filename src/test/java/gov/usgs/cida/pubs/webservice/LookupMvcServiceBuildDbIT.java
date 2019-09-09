package gov.usgs.cida.pubs.webservice;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONArrayAs;

import org.json.JSONArray;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.dao.AffiliationDao;
import gov.usgs.cida.pubs.dao.ContributorDao;
import gov.usgs.cida.pubs.dao.ContributorDaoIT;
import gov.usgs.cida.pubs.dao.CorporateContributorDao;
import gov.usgs.cida.pubs.dao.CostCenterDao;
import gov.usgs.cida.pubs.dao.CostCenterDaoIT;
import gov.usgs.cida.pubs.dao.OutsideAffiliationDao;
import gov.usgs.cida.pubs.dao.OutsideAffiliationDaoIT;
import gov.usgs.cida.pubs.dao.PersonContributorDao;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.PublicationSeriesDao;
import gov.usgs.cida.pubs.dao.PublicationSubtypeDao;
import gov.usgs.cida.pubs.dao.PublicationTypeDao;
import gov.usgs.cida.pubs.dao.PublishingServiceCenterDao;
import gov.usgs.cida.pubs.dao.PublishingServiceCenterDaoIT;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.CorporateContributor;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.OutsideAffiliation;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.PublishingServiceCenter;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@EnableWebMvc
@AutoConfigureMockMvc(secure=false)
@SpringBootTest(webEnvironment=WebEnvironment.MOCK,
	classes={DbTestConfig.class, ConfigurationService.class, LookupMvcService.class,
			CostCenter.class, CostCenterDao.class, AffiliationDao.class,
			Publication.class, PublicationDao.class, PublicationSeries.class, PublicationSeriesDao.class,
			PublicationType.class, PublicationTypeDao.class, PublicationSubtype.class,
			PublicationSubtypeDao.class, PublicationSeries.class, PublicationSeriesDao.class,
			CorporateContributor.class, CorporateContributorDao.class, ContributorDao.class,
			PublishingServiceCenter.class, PublishingServiceCenterDao.class,
			OutsideAffiliation.class, OutsideAffiliationDao.class,
			PersonContributor.class, PersonContributorDao.class, Contributor.class})
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/dataset.xml")
})
public class LookupMvcServiceBuildDbIT extends BaseIT {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void getCorporations() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/lookup/corporations?mimetype=json").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertEquals(ContributorDaoIT.CORPORATE_CONTRIBUTOR_CNT, getRtnAsJSONArray(rtn).length());

		rtn = mockMvc.perform(get("/lookup/corporations?text=us geo").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		JSONArray rtnAsJSONArray = getRtnAsJSONArray(rtn);

		assertEquals(1, rtnAsJSONArray.length());

		assertThat(rtnAsJSONArray,
				sameJSONArrayAs(new JSONArray("[{\"id\":2,\"text\":\"US Geological Survey Ice Survey Team\"}]"))
						.allowingAnyArrayOrdering());
	}

	@Test
	public void getPeople() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/lookup/people?text=oute").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		JSONArray rtnAsJSONArray = getRtnAsJSONArray(rtn);

		assertEquals(1, rtnAsJSONArray.length());

		assertThat(rtnAsJSONArray,
				sameJSONArrayAs(
						new JSONArray("[{\"id\":3,\"text\":\"outerfamily, outerGiven outerSuffix outer@gmail.com\",\"preferred\":false}]"))
								.allowingAnyArrayOrdering());

		rtn = mockMvc.perform(get("/lookup/people?orcid=0000-0000-0000-0004").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();

		rtnAsJSONArray = getRtnAsJSONArray(rtn);

		assertEquals(1, rtnAsJSONArray.length());

		assertThat(rtnAsJSONArray,
				sameJSONArrayAs(
						new JSONArray("[{\"id\":4,\"text\":\"4Family, 4Given 4Suffix con4@usgs.gov\",\"preferred\":true}]"))
				.allowingAnyArrayOrdering());

		mockMvc.perform(get("/lookup/people").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
			.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING));

		mockMvc.perform(get("/lookup/people?text=a").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
			.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING));
	}

	@Test
	public void getCostCenters() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/lookup/costcenters?mimetype=json").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertEquals(CostCenterDaoIT.COST_CENTER_CNT, getRtnAsJSONArray(rtn).length());

		rtn = mockMvc.perform(get("/lookup/costcenters?text=xa").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertThat(getRtnAsJSONArray(rtn),
				sameJSONArrayAs(new JSONArray("[{\"id\":3,\"text\":\"xAffiliation Cost Center 3\"},{\"id\":4,\"text\":\"xAffiliation Cost Center 4\"}]")));
	}

	@Test
	public void getOutsideAffiliates() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/lookup/outsideaffiliates?mimetype=json").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertEquals(OutsideAffiliationDaoIT.OUTSIDE_AFFILIATES_CNT, getRtnAsJSONArray(rtn).length());

		rtn = mockMvc.perform(get("/lookup/outsideaffiliates?text=xo").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertThat(getRtnAsJSONArray(rtn),
				sameJSONArrayAs(new JSONArray("[{\"id\":6,\"text\":\"xOutside Affiliation 2\"}]")));
	}

	@Test
	public void getPublications() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/lookup/publications?mimetype=json").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertEquals(8, getRtnAsJSONArray(rtn).length());

		rtn = mockMvc.perform(get("/lookup/publications?text=9").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertEquals(1, getRtnAsJSONArray(rtn).length());

		assertThat(getRtnAsJSONArray(rtn),
				sameJSONArrayAs(new JSONArray("[{\"id\":5,\"text\":\"9 - No Year -  future title\"}]")));
	}

	@Test
	public void getPublicationSeriesQuery() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/lookup/publicationseries?text=zeit&publicationsubtypeid=10").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertThat(getRtnAsJSONArray(rtn),
				sameJSONArrayAs(new JSONArray("[{\"id\":3803,\"text\":\"Zeitschrift fur Geomorphologie\"},"
						+ "{\"id\":3804,\"text\":\"Zeitschrift fur Geomorphologie, Supplementband\"},"
						+ "{\"id\":3805,\"text\":\"Zeitschrift fur Tierpsychologie\"}]")));
	}

	@Test
	public void getPublicationSeriesREST() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/lookup/publicationtype/"
				+ PublicationType.REPORT + "/publicationsubtype/10/publicationseries?text=zeit").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertThat(getRtnAsJSONArray(rtn),
				sameJSONArrayAs(new JSONArray("[{\"id\":3803,\"text\":\"Zeitschrift fur Geomorphologie\"},"
						+ "{\"id\":3804,\"text\":\"Zeitschrift fur Geomorphologie, Supplementband\"},"
						+ "{\"id\":3805,\"text\":\"Zeitschrift fur Tierpsychologie\"}]")));
	}

	@Test
	public void getPublicationSubtypeQuery() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/lookup/publicationsubtypes?text=b&publicationtypeid=4").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertThat(getRtnAsJSONArray(rtn),
				sameJSONArrayAs(new JSONArray("[{\"id\":11,\"text\":\"Bibliography\"}]")).allowingAnyArrayOrdering());
	}

	@Test
	public void getPublicationSubtypeREST() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/lookup/publicationtype/4/publicationsubtypes?text=b").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertThat(getRtnAsJSONArray(rtn),
				sameJSONArrayAs(new JSONArray("[{\"id\":11,\"text\":\"Bibliography\"}]")).allowingAnyArrayOrdering());
	}

	@Test
	public void getPublicationType() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/lookup/publicationtypes?text=b").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertThat(getRtnAsJSONArray(rtn),
				sameJSONArrayAs(new JSONArray("[{\"id\":4,\"text\":\"Book\"},{\"id\":5,\"text\":\"Book chapter\"}]"))
						.allowingAnyArrayOrdering());
	}

	@Test
	public void getPublishingServiceCenters() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/lookup/publishingServiceCenters?mimetype=json").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertEquals(PublishingServiceCenterDaoIT.PSC_CNT, getRtnAsJSONArray(rtn).length());

		rtn = mockMvc.perform(get("/lookup/publishingServiceCenters?text=r").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		JSONArray rtnAsJSONArray = getRtnAsJSONArray(rtn);

		assertEquals(PublishingServiceCenterDaoIT.PSC_R_CNT, rtnAsJSONArray.length());

		assertThat(rtnAsJSONArray,
				sameJSONArrayAs(new JSONArray(
						"[{\"id\":4,\"text\":\"Rolla PSC\"},{\"id\":8,\"text\":\"Raleigh PSC\"},{\"id\":9,\"text\":\"Reston PSC\"}]"))
								.allowingAnyArrayOrdering());
	}

}
