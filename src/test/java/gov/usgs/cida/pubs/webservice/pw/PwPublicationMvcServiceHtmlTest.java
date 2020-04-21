package gov.usgs.cida.pubs.webservice.pw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.busservice.xml.XmlBusServiceTest;
import gov.usgs.cida.pubs.dao.intfc.IPublicationDao;
import gov.usgs.cida.pubs.dao.intfc.IPwPublicationDao;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublicationLink;
import gov.usgs.cida.pubs.webservice.MvcService;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment=WebEnvironment.MOCK)
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
		MockHttpServletRequestBuilder request = get("/publication/full/3").accept(MediaType.TEXT_HTML_VALUE);
		MvcResult rtn = mockMvc.perform(request)
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.TEXT_HTML_VALUE))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();
		assertEquals(MvcService.formatHtmlErrMess("Publication with indexId '3' not found."), rtn.getResponse().getContentAsString(), "Unexpected error message");
	}

	@Test
	public void getPublicationHtmlTest() throws Exception {
		when(pwPublicationDao.getByIndexId("4")).thenReturn(getPwPublication4());
		MockHttpServletRequestBuilder request = get("/publication/full/4").accept(MediaType.TEXT_HTML_VALUE);
		MvcResult rtn = mockMvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.TEXT_HTML_VALUE))
				.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
				.andReturn();
		assertEquals(XmlBusServiceTest.getPublicationHtml(), rtn.getResponse().getContentAsString(), "publication html does not match");
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