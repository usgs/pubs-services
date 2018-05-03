package gov.usgs.cida.pubs.busservice.ipds;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Service
public class IpdsParserService {

	public static final String ORCID_PREFIX = "http://orcid.org/";
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
				if (rtn.isEmpty()) {
					rtn = null;
				}
			}
		}
		return rtn;
	}

	protected Integer getFirstNodeInteger(final Element element, final String tagName) {
		Integer rtn = NumberUtils.toInt(getFirstNodeText(element, tagName));
		if (rtn < 1) {
			rtn = null;
		}
		return rtn;
	}

	protected String formatOrcid(String orcid) {
		String formattedOrcid = null;
		if (null != orcid) {
			if (orcid.length() == 36 && orcid.startsWith(ORCID_PREFIX)) {
				formattedOrcid = orcid.substring(ORCID_PREFIX.length());
			} else {
				formattedOrcid = orcid;
			}
			if (formattedOrcid.length() == 19 && formattedOrcid.matches("^\\d{4}-\\d{4}-\\d{4}-(\\d{3}X|\\d{4})$")) {
				formattedOrcid = ORCID_PREFIX + formattedOrcid;
			} else {
				formattedOrcid = null;
			}
		}
		return formattedOrcid;
	}
}