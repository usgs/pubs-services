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
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.busservice.PublicationBusService;
import gov.usgs.cida.pubs.busservice.pw.PwPublicationBusService;
import gov.usgs.cida.pubs.busservice.xml.XmlBusService;
import gov.usgs.cida.pubs.busservice.xml.XmlBusServiceTest;
import gov.usgs.cida.pubs.dao.intfc.IPublicationDao;
import gov.usgs.cida.pubs.dao.intfc.IPwPublicationDao;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublicationLink;
import gov.usgs.cida.pubs.webservice.MvcService;

@EnableWebMvc
@AutoConfigureMockMvc(secure=false)
@ContextConfiguration(classes = FreeMarkerAutoConfiguration.class)
@SpringBootTest(webEnvironment=WebEnvironment.MOCK,
	classes={ConfigurationService.class, PwPublicationMvcService.class,
			PwPublicationBusService.class, XmlBusService.class, LocalValidatorFactoryBean.class,
			PublicationBusService.class, PwPublication.class})
public class PwPublicationMvcServiceHtmlTest extends BaseTest {
	@MockBean
	public IPublicationDao publicationDao;

	@MockBean
	public IPwPublicationDao pwPublicationDao;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void indexIdNotFoundTest() throws Exception {
		when(pwPublicationDao.getByIndexId("3")).thenReturn(null);
		MvcResult rtn = doGetPublicationHtml("3", status().isNotFound());
		assertEquals("Unexpected error message", MvcService.formatHtmlErrMess("Publication with indexId '3' not found."), rtn.getResponse().getContentAsString());
	}

	@Test
	public void getPublicationHtmlTest() throws Exception {
		when(pwPublicationDao.getByIndexId("4")).thenReturn(getPwPublication4()); // adds the publication XML link
		MvcResult rtn = doGetPublicationHtml("4", status().isOk());
		assertEquals("publication html does not match", XmlBusServiceTest.getPublicationHtml(), rtn.getResponse().getContentAsString());
	}

	private MvcResult doGetPublicationHtml(String indexId, ResultMatcher expectedStatus) throws Exception {
		MockHttpServletRequestBuilder request = getPubHtmlReq("/publication/full/" + indexId);
		MvcResult rtn = mockMvc.perform(request)
			.andExpect(expectedStatus)
			.andExpect(content().contentType(MediaType.TEXT_HTML_VALUE))
			.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
			.andReturn();
		
		return rtn;
	}

	private MockHttpServletRequestBuilder getPubHtmlReq(String path) {
		MockHttpServletRequestBuilder req = get(path).accept(MediaType.TEXT_HTML_VALUE);
		return req;
	}
	
	private PwPublication getPwPublication4() throws IOException {
		PwPublication pub = new PwPublication();
		pub.setId(4);
		pub.setIndexId("4");
		PwPublicationLink link = new PwPublicationLink();
		link.setId(99);
		link.setUrl(XmlBusServiceTest.getXmlPubUrl().toString());

		LinkType linkType = new LinkType();
		linkType.setId(LinkType.PUBLICATION_XML);
		link.setLinkType(linkType);

		pub.setLinks(List.of(link));

		return pub;
	}
}