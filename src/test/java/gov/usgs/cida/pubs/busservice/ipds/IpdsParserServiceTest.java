package gov.usgs.cida.pubs.busservice.ipds;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.springinit.TestSpringConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={TestSpringConfig.class, IpdsParserService.class//, ConfigurationService.class//DbTestConfig.class, , LocalValidatorFactoryBean.class
			})
public class IpdsParserServiceTest extends BaseTest {

	@Autowired
	protected IpdsParserService ipdsParser;

	@Test
	public void makeDocumentTest() throws SAXException, IOException {
		assertNull(ipdsParser.makeDocument(null));
		assertNull(ipdsParser.makeDocument(""));
		try {
			ipdsParser.makeDocument("<root>");
		} catch (Exception e) {
			assertTrue(e instanceof SAXParseException);
		}
		Document doc = ipdsParser.makeDocument("<root/>");
		assertNotNull(doc);
		assertEquals(1, doc.getElementsByTagName("root").getLength());
	}

	@Test
	public void getFirstNodeTextTest() throws SAXException, IOException {
		Document d = ipdsParser.makeDocument("<root/>");
		assertNull(ipdsParser.getFirstNodeText(null, null));
		assertNull(ipdsParser.getFirstNodeText(d.getDocumentElement(), null));
		assertNull(ipdsParser.getFirstNodeText(null, ""));

		d = ipdsParser.makeDocument("<root><one>oneText</one><two>twoTextA</two><two>twoTextB</two><four m:null=\"true\" /><five></five></root>");
		assertEquals("oneText", ipdsParser.getFirstNodeText(d.getDocumentElement(), "one"));
		assertEquals("twoTextA", ipdsParser.getFirstNodeText(d.getDocumentElement(), "two"));
		assertNull(ipdsParser.getFirstNodeText(d.getDocumentElement(), "three"));
		assertNull(ipdsParser.getFirstNodeText(d.getDocumentElement(), "four"));
		assertNull(ipdsParser.getFirstNodeText(d.getDocumentElement(), "five"));
	}

	@Test
	public void testFormatOrcid() {
		String orcidNull = null;
		String orcidPrefixNoNumber = IpdsParserService.ORCID_PREFIX + "";
		String orcidPrefixBadNumber = IpdsParserService.ORCID_PREFIX + "ojae-hjrg-aag2-0020";
		String orcidBadPrefixNoNumber = "http://notorcid.org";
		String orcidBadPrefixBadNumber = "http://gro.dicro" + "1234-5678-9101-112K";

		assertNull(ipdsParser.formatOrcid(orcidNull));
		assertNull(ipdsParser.formatOrcid(orcidPrefixNoNumber));
		assertNull(ipdsParser.formatOrcid(orcidPrefixBadNumber));
		assertNull(ipdsParser.formatOrcid(orcidBadPrefixNoNumber));
		assertNull(ipdsParser.formatOrcid(orcidBadPrefixBadNumber));
		assertEquals("http://orcid.org/0000-0000-0000-0000", ipdsParser.formatOrcid("0000-0000-0000-0000"));
		assertEquals("http://orcid.org/0000-0000-0000-000X", ipdsParser.formatOrcid("0000-0000-0000-000X"));
		assertEquals("http://orcid.org/0000-0000-0000-000X", ipdsParser.formatOrcid("http://orcid.org/0000-0000-0000-000X"));
		assertEquals("http://orcid.org/0000-0000-0000-0009", ipdsParser.formatOrcid("http://orcid.org/0000-0000-0000-0009"));

		assertEquals("http://orcid.org/0000-0000-0000-000X", ipdsParser.formatOrcid("https://orcid.org/0000-0000-0000-000X"));
		assertEquals("http://orcid.org/0000-0000-0000-0009", ipdsParser.formatOrcid("https://orcid.org/0000-0000-0000-0009"));
	}
}