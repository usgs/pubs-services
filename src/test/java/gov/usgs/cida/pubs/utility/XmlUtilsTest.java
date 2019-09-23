package gov.usgs.cida.pubs.utility;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import gov.usgs.cida.pubs.BaseTest;

public class XmlUtilsTest extends BaseTest {
	
	// path in resource
	private static final String PUB_XML = "testData/sac19-4232_text4LAYOUT.xml";
	private static final String PUB_HTML = "sac19-4232_text4LAYOUT.html"; // in testResult resource directory
	private static final String XSLT_STYLE_SHEET = "xslt/pubs-html.xsl";

	private URL xmlPubUrl;
	private File xslStylesheet;
	
	@Before
	public void setUp() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		xmlPubUrl = getXmlPubUrl();
		xslStylesheet = new File(classLoader.getResource(XSLT_STYLE_SHEET).getFile());
	}

	@Test
	public void getDocumentHtmlTest() throws Exception {
		String docHtml = XmlUtils.getDocumentHtml(xmlPubUrl, xslStylesheet, true);
		assertEquals("publication html does not match", getCompareFile(PUB_HTML), docHtml);
	}

	public static URL getXmlPubUrl() throws IOException {
		return new ClassPathResource(PUB_XML).getURL();
	}

	public static String getPublicationHtml() throws IOException {
		return new XmlUtilsTest().getCompareFile(PUB_HTML);
	}

}