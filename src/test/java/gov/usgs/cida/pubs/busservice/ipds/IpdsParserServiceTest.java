package gov.usgs.cida.pubs.busservice.ipds;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import gov.usgs.cida.pubs.IntegrationTest;

@Category(IntegrationTest.class)
public class IpdsParserServiceTest extends BaseIpdsTest {

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

		d = ipdsParser.makeDocument("<root><one>oneText</one><two>twoTextA</two><two>twoTextB</two></root>");
		assertEquals("oneText", ipdsParser.getFirstNodeText(d.getDocumentElement(), "one"));
		assertEquals("twoTextA", ipdsParser.getFirstNodeText(d.getDocumentElement(), "two"));
		assertNull(ipdsParser.getFirstNodeText(d.getDocumentElement(), "three"));
   }
}