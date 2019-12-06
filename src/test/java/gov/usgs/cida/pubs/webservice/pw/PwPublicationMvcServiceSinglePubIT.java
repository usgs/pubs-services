package gov.usgs.cida.pubs.webservice.pw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;

import org.apache.http.entity.mime.MIME;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.context.ContextConfiguration;;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.busservice.PublicationBusService;
import gov.usgs.cida.pubs.busservice.pw.PwPublicationBusService;
import gov.usgs.cida.pubs.busservice.xml.XmlBusService;
import gov.usgs.cida.pubs.dao.ContributorTypeDao;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDao;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.springinit.DbTestConfig;
import gov.usgs.cida.pubs.springinit.SpringConfig;
import gov.usgs.cida.pubs.utility.CustomStringToArrayConverter;
import gov.usgs.cida.pubs.utility.CustomStringToStringConverter;
import gov.usgs.cida.pubs.utility.StringArrayCleansingConverter;

@EnableWebMvc
@AutoConfigureMockMvc(secure=false)
@ContextConfiguration(classes = FreeMarkerAutoConfiguration.class)
@SpringBootTest(webEnvironment=WebEnvironment.MOCK,
	classes={DbTestConfig.class, ConfigurationService.class, PwPublicationMvcService.class,
			PwPublicationBusService.class, XmlBusService.class, LocalValidatorFactoryBean.class,
			PublicationBusService.class, PwPublication.class,
			PwPublicationDao.class, PublicationDao.class, SpringConfig.class,
			CustomStringToArrayConverter.class, StringArrayCleansingConverter.class,
			CustomStringToStringConverter.class, ContributorType.class, ContributorTypeDao.class})
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/contributor.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/crossrefDataset.xml")
})
public class PwPublicationMvcServiceSinglePubIT extends BaseIT {

	private static final String CROSSREF_PUB_ID = "sir2";
	private static final String CROSSREF_PUB_JSON_FILE = "pwPublication/sir2.json";

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void getJSONWhenNoAcceptHeaderIsSpecified () throws Exception {
		MvcResult result = mockMvc.perform(get("/publication/" + CROSSREF_PUB_ID))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
			.andReturn();
		JSONObject content = getRtnAsJSONObject(result);
		assertTrue("expects non-empty response",  0 < content.length());
		assertThat(getRtnAsJSONObject(result),
			sameJSONObjectAs(
				new JSONObject(getCompareFile(CROSSREF_PUB_JSON_FILE))
			).allowingAnyArrayOrdering()
		);
	}

	@Test
	public void getJSONWhenUsingBrowserDefaultAcceptHeader () throws Exception {
		//Simulate a browser's default accept header value
		MvcResult result = mockMvc.perform(get("/publication/" + CROSSREF_PUB_ID)
			.accept(
				"text/html",
				"application/xhtml+xml",
				"application/xml;q=0.9",
				"*/*;q=0.8"
			))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
			.andReturn();
		JSONObject content = getRtnAsJSONObject(result);
		assertTrue("expects non-empty response",  0 < content.length());
		assertThat(getRtnAsJSONObject(result),
			sameJSONObjectAs(
				new JSONObject(getCompareFile(CROSSREF_PUB_JSON_FILE))
			).allowingAnyArrayOrdering()
		);
	}
	
	@Test
	public void getJSONWhenAcceptHeaderAsksForJSON () throws Exception {
		MvcResult result = mockMvc.perform(get("/publication/" + CROSSREF_PUB_ID)
			.accept(
				MediaType.APPLICATION_JSON_UTF8
			))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
			.andReturn();
		JSONObject content = getRtnAsJSONObject(result);
		assertTrue("expects non-empty response",  0 < content.length());
		assertThat(getRtnAsJSONObject(result),
			sameJSONObjectAs(
				new JSONObject(getCompareFile(CROSSREF_PUB_JSON_FILE))
			).allowingAnyArrayOrdering()
		);
	}
	
	@Test
	public void getJSONWhenQueryStringAsksForJSON () throws Exception {
		MvcResult result = mockMvc.perform(get("/publication/" + CROSSREF_PUB_ID + "?" + PubsConstantsHelper.CONTENT_PARAMETER_NAME +"=" + PubsConstantsHelper.MEDIA_TYPE_JSON_EXTENSION))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
			.andReturn();
		JSONObject content = getRtnAsJSONObject(result);
		assertTrue("expects non-empty response",  0 < content.length());
		assertThat(getRtnAsJSONObject(result),
			sameJSONObjectAs(
				new JSONObject(getCompareFile(CROSSREF_PUB_JSON_FILE))
			).allowingAnyArrayOrdering()
		);
	}
	
	@Test
	public void notFoundTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/publication/nonExistentPubId?mimetype=json").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();
		assertEquals(0, rtn.getResponse().getContentAsString().length());
	}
	
	@Test
	public void getCrossrefXMLWhenAcceptHeaderAsksForCrossref () throws Exception {
		MvcResult result = mockMvc.perform(get("/publication/" + CROSSREF_PUB_ID)
			.accept(PubsConstantsHelper.MEDIA_TYPE_CROSSREF)
			)
			.andExpect(status().isOk())
			.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_CROSSREF))
			.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
			.andExpect(header().string(MIME.CONTENT_DISPOSITION, "inline"))
			.andReturn();
		String resultText = result.getResponse().getContentAsString();
		
		assertTrue("expects non-empty response",  0 < resultText.length());
	}
	
	@Test
	public void getCrossrefXMLWhenQueryStringAsksForCrossref () throws Exception {
		MvcResult result = mockMvc.perform(get("/publication/" + CROSSREF_PUB_ID + "?" + PubsConstantsHelper.CONTENT_PARAMETER_NAME +"=" + PubsConstantsHelper.MEDIA_TYPE_CROSSREF_EXTENSION))
			.andExpect(status().isOk())
			.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_CROSSREF))
			.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
			.andExpect(header().string(MIME.CONTENT_DISPOSITION, "inline"))
			.andReturn();
		String resultText = result.getResponse().getContentAsString();
		
		assertTrue("expects non-empty response",  0 < resultText.length());
	}
	

}