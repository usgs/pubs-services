package gov.usgs.cida.pubs.busservice.ipds;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Service
public class IpdsParserService {

	private DocumentBuilder builder;
	
	public IpdsParserService() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(false);
		builder = factory.newDocumentBuilder();
	}

	protected Document makeDocument(final String xmlStr) throws SAXException, IOException {
		if (StringUtils.isNotBlank(xmlStr)) {
			return builder.parse(new InputSource(new StringReader(xmlStr)));
		}
		return null;
	}
	
	protected String getFirstNodeText(final Element element, final String tagName) {
		String rtn = null;
		if (null != element) {
			NodeList nodes = element.getElementsByTagName(tagName);
			if (0 < nodes.getLength()) {
				rtn = element.getElementsByTagName(tagName).item(0).getTextContent().trim();
			}
		}
		return rtn;
	}
}