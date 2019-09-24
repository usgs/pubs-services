package gov.usgs.cida.pubs.utility;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlUtils {
	// This files are from BITS-XHTML-TABLES-DTD.zip :
	// ftp://ftp.ncbi.nih.gov/pub/jats/extensions/bits/2.0/
	private static final String[] PUBS_DTDS = {"BITS-book-part-wrap2.ent", "BITS-book-part2.ent", "BITS-book2.dtd",
			"BITS-bookcustom-classes2.ent", "BITS-bookcustom-mixes2.ent", "BITS-bookcustom-models2.ent",
			"BITS-bookcustom-modules2.ent", "BITS-bookmeta2.ent", "BITS-common2.ent", "BITS-embedded-index2.ent",
			"BITS-index2.ent", "BITS-question-answer2.ent", "BITS-toc-index-nav2.ent", "BITS-toc2.ent",
			"BITS-xinclude2.ent", "JATS-XHTMLtablesetup1.ent", "JATS-ali-namespace1.ent", "JATS-articlemeta1.ent",
			"JATS-backmatter1.ent", "JATS-chars1.ent", "JATS-common-atts1.ent", "JATS-common1.ent",
			"JATS-default-classes1.ent", "JATS-default-mixes1.ent", "JATS-display1.ent", "JATS-format1.ent",
			"JATS-funding1.ent", "JATS-journalmeta1.ent", "JATS-link1.ent", "JATS-list1.ent", "JATS-math1.ent",
			"JATS-mathml3-mathmlsetup1.ent", "JATS-mathml3-modules1.ent", "JATS-modules1.ent", "JATS-nlmcitation1.ent",
			"JATS-notat1.ent", "JATS-para1.ent", "JATS-phrase1.ent", "JATS-references1.ent", "JATS-related-object1.ent",
			"JATS-section1.ent", "JATS-xmlspecchars1.ent", "mathml3.dtd", "mathml/mmlalias.ent", "mathml/mmlextra.ent",
			"mathml3-qname1.mod", "xhtml-inlstyle-1.mod", "xhtml-table-1.mod", "iso8879/isobox.ent",
			"iso8879/isocyr1.ent", "iso8879/isocyr2.ent", "iso8879/isodia.ent", "iso8879/isolat1.ent",
			"iso8879/isolat2.ent", "iso8879/isonum.ent", "iso8879/isopub.ent", "iso9573-13/isoamsa.ent",
			"iso9573-13/isoamsb.ent", "iso9573-13/isoamsc.ent", "iso9573-13/isoamsn.ent", "iso9573-13/isoamso.ent",
			"iso9573-13/isoamsr.ent", "iso9573-13/isogrk3.ent", "iso9573-13/isomfrk.ent", "iso9573-13/isomopf.ent",
			"iso9573-13/isomscr.ent", "iso9573-13/isotech.ent", "xmlchars/isogrk1.ent", "xmlchars/isogrk2.ent",
			"xmlchars/isogrk4.ent" };

	private static final List<String> IMAGE_NODE_NAMES = List.of("graphic");

	public static final String xsltResourceDir = "xslt";
	public static final String pubsStyleSheet = "pubs-html.xsl";

	// TODO: make this a configurable parameter
	// This is a url for pulling images from SPN
	public static final String SPN_IMAGE_URL = "https://pubs.usgs.gov/xml_test/Images/";

	/**
	 * Return HTML derived from the given xml document. This html is generating by applying a pubs xslt
	 * style sheet. DTD schema validation is not done during the transform, the dtd is still read during the 
	 * transform to bring in Xml entities as needed.
	 * 
	 * @param xmlDocUrl The url to the xml doc to convert to HTML.
	 * @param context
	 * @return
	 * 
	 * @throws TransformerException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public static String getPublicationHtml(String xmlDocUrl, ServletContext context)
			throws TransformerException, IOException, ParserConfigurationException, SAXException {
		File xsltDir = new ClassPathResource(xsltResourceDir).getFile();
		if (xsltDir == null || !xsltDir.exists() || !xsltDir.isDirectory()) {
			throw new FileNotFoundException("xslt resource directory not found.");
		}

		File xlsStylesheet = new File(xsltDir.getAbsolutePath() + "/" + pubsStyleSheet);
		if (!xlsStylesheet.exists()) {
			throw new FileNotFoundException(
					String.format("xslt stylesheet '%s' not found in resource directory", pubsStyleSheet));
		}

		return getDocumentHtml(new URL(xmlDocUrl), xlsStylesheet, false);
	}

	/**
	 * Return the HTML definition for the specified xml. The done via a xslt
	 * transformation.
	 * 
	 * @param xmlDoc        The xml doc to convert to HTML.
	 * @param xlsStylesheet The xsl style sheet to apply during the
	 *                        transformation.
	 * @param validate      If true, perform xml DTD schema validation as part
	 *                        of the xslt transformation.
	 * 
	 * @return The HTML text corresponding to the xml document.
	 * 
	 * @throws TransformerException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public static String getDocumentHtml(URL xmlDoc, File xslStylesheet, boolean validate)
			throws TransformerException, IOException, ParserConfigurationException, SAXException {
		// Create a transform factory instance.
		TransformerFactory tfactory = TransformerFactory.newInstance();

		// Create a transformer for the stylesheet.
		InputStream xslIS = new BufferedInputStream(new FileInputStream(xslStylesheet));
		StreamSource xslSource = new StreamSource(xslIS, "file://" + xslStylesheet.getPath());
		Transformer transformer = tfactory.newTransformer(xslSource);

		DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
		dfactory.setValidating(validate);

		DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
		docBuilder.setEntityResolver(new PubsEntityResolver(xslStylesheet.getParent()));
		Document doc = docBuilder.parse(new InputSource(xmlDoc.openStream()));
		updateImageLinks(xmlDoc, doc.getDocumentElement());

		// Transform the source XML to bytes
		ByteArrayOutputStream htmlBytes = new ByteArrayOutputStream();
		transformer.transform(new DOMSource(doc), new StreamResult(htmlBytes));

		return htmlBytes.toString();
	}

	private static void updateImageLinks(URL xmlUrl, Node node) {
		if (node.hasAttributes() && IMAGE_NODE_NAMES.contains(node.getNodeName())) {
			for (int i = 0; i < node.getAttributes().getLength(); i++) {
				Node attrNode = node.getAttributes().item(i);
				if ("xlink:href".equals(attrNode.getNodeName()) && !attrNode.getNodeValue().contains(":/")) {
					// update relative path
					String imageUrl = SPN_IMAGE_URL;
					if (!imageUrl.endsWith("/")) {
						imageUrl = imageUrl + "/";
					}
					String imageName = attrNode.getNodeValue();
					String ext = FilenameUtils.getExtension(imageName);
					if (ext == null || ext.isEmpty()) {
						imageName = imageName + ".png";
					}
					attrNode.setNodeValue(imageUrl + imageName);
				}
			}
		}

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node currentNode = nodeList.item(i);
			// recurse on children Element nodes
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				updateImageLinks(xmlUrl, currentNode);
			}
		}
	}

	private static class PubsEntityResolver implements EntityResolver {
		private String dtdDirectory = "";

		PubsEntityResolver(String dtdDirectory) {
			this.dtdDirectory = dtdDirectory;
		}

		@Override
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			InputSource inputSource = null;
			for (String dtd : PUBS_DTDS) {
				if (systemId.contains(dtd)) {
					inputSource = new InputSource(new FileReader(dtdDirectory + "/" + dtd));
					break;
				}
			}

			return inputSource;
		}
	}

}