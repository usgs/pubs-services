package gov.usgs.cida.pubs.busservice.intfc;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public interface IXmlBusService {

	public String getPublicationHtml(String xmlDocUrl)
			throws TransformerException, IOException, ParserConfigurationException, SAXException;

	public String getDocumentHtml(URL xmlPubUrl, File xslStylesheet, boolean validate)
			throws TransformerException, IOException, ParserConfigurationException, SAXException;

}
