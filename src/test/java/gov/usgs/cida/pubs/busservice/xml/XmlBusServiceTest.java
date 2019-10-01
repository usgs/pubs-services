package gov.usgs.cida.pubs.busservice.xml;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.ClassPathResource;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.springinit.TestSpringConfig;

@SpringBootTest(webEnvironment=WebEnvironment.MOCK,
classes={TestSpringConfig.class, ConfigurationService.class})
public class XmlBusServiceTest extends BaseTest {
	
	// path in resource
	private static final String PUB_XML = "testData/sac19-4232_text4LAYOUT.xml";
	private static final String PUB_HTML = "sac19-4232_text4LAYOUT.html"; // in testResult resource directory

	private URL xmlPubUrl;
	private URL xsltDir;

	@Autowired
	public ConfigurationService configurationService;

	private XmlBusService xmlBusService;

	@Before
	public void setUp() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		xmlPubUrl = getXmlPubUrl();
		xsltDir = new URL(classLoader.getResource(XmlBusService.XSLT_RESOURCE_DIR).toString());
		xmlBusService = new XmlBusService(configurationService);
	}

	@Test
	public void getDocumentHtmlTest() throws Exception {
		String docHtml = xmlBusService.getDocumentHtml(xmlPubUrl, xsltDir, true);
		assertEquals("publication html does not match", getCompareFile(PUB_HTML), docHtml);
	}

	public static URL getXmlPubUrl() throws IOException {
		return new ClassPathResource(PUB_XML).getURL();
	}

	public static String getPublicationHtml() throws IOException {
		return new XmlBusServiceTest().getCompareFile(PUB_HTML);
	}

}