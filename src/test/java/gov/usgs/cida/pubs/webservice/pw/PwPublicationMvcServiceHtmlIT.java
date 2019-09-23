package gov.usgs.cida.pubs.webservice.pw;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.busservice.PublicationBusService;
import gov.usgs.cida.pubs.busservice.pw.PwPublicationBusService;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.intfc.IPwPublicationDao;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDao;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublicationLink;
import gov.usgs.cida.pubs.springinit.DbTestConfig;
import gov.usgs.cida.pubs.springinit.FreemarkerConfig;
import gov.usgs.cida.pubs.springinit.SpringConfig;
import gov.usgs.cida.pubs.springinit.TestSpringConfig;
import gov.usgs.cida.pubs.utility.CustomStringToArrayConverter;
import gov.usgs.cida.pubs.utility.CustomStringToStringConverter;
import gov.usgs.cida.pubs.utility.StringArrayCleansingConverter;
import gov.usgs.cida.pubs.utility.XmlUtilsTest;
import gov.usgs.cida.pubs.webservice.MvcService;

@EnableWebMvc
@AutoConfigureMockMvc(secure=false)
@SpringBootTest(webEnvironment=WebEnvironment.MOCK,
	classes={DbTestConfig.class, ConfigurationService.class, TestSpringConfig.class, PwPublicationMvcService.class,
			PwPublicationBusService.class, LocalValidatorFactoryBean.class,
			FreemarkerConfig.class, PublicationBusService.class, PwPublication.class,
			PwPublicationDao.class, PublicationDao.class, SpringConfig.class,
			CustomStringToArrayConverter.class, StringArrayCleansingConverter.class,
			CustomStringToStringConverter.class})
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationStream.xml")
})
public class PwPublicationMvcServiceHtmlIT extends BaseIT {
	@MockBean
	IPwPublicationDao publicationDao;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void indexIdNotFoundTest() throws Exception {
		MvcResult rtn = doGetPublicationHtml("3", status().isNotFound());
		assertEquals("Unexpected error message", MvcService.formatHtmlErrMess("Publication with indexId '3' not found."), rtn.getResponse().getContentAsString());
	}

	@Test
	public void getPublicationHtmlTest() throws Exception {
		when(publicationDao.getByIndexId("4")).thenReturn(getPwPublication4()); // adds the publication XML link
		MvcResult rtn = doGetPublicationHtml("4", status().isOk());
		assertEquals("publication html does not match", XmlUtilsTest.getPublicationHtml(), rtn.getResponse().getContentAsString());
	}

	private MvcResult doGetPublicationHtml(String indexId, ResultMatcher expectedStatus) throws Exception {
		MockHttpServletRequestBuilder request = getPubHtmlReq("/publication/full/" + indexId);
		MvcResult rtn = mockMvc.perform(request)
			//.andExpect(expectedStatus)
			.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_HTML_VALUE))
			.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
			.andReturn();
		
		return rtn;
	}

	private MockHttpServletRequestBuilder getPubHtmlReq(String path) {
		MockHttpServletRequestBuilder req = get(path).accept(PubsConstantsHelper.MEDIA_TYPE_HTML_VALUE);
		return req;
	}
	
	private PwPublication getPwPublication4() throws IOException {
		PwPublication pub = new PwPublication();
		pub.setId(4);
		pub.setIndexId("4");
		PwPublicationLink link = new PwPublicationLink();
		link.setId(LinkType.PUBLICATION_XML);
		link.setUrl(XmlUtilsTest.getXmlPubUrl().toString());
		pub.setLinks(List.of(link));

		return pub;
	}
}