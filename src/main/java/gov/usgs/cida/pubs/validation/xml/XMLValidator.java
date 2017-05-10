package gov.usgs.cida.pubs.validation.xml;

import java.io.IOException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

@Component
public class XMLValidator {
	protected final transient Logger logger = LoggerFactory.getLogger(getClass());
	SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	
	/**
	 * Throws an exception if the specified XML is not valid according to 
	 * the specified XSD. Otherwise has no effect
	 * @param xsdUrl URL pointing to an XSD file
	 * @param xmlContent the XML to validate
	 * @throws gov.usgs.cida.pubs.validation.xml.XMLValidationException
	 */
	public void validate(String xsdUrl, String xmlContent) throws XMLValidationException {
		try {
			URL schemaUrl = new URL(xsdUrl);
			Schema schema = factory.newSchema(schemaUrl);
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(xmlContent));
		} catch (IOException | SAXException e) {
			throw new XMLValidationException("Error validating XML document against XSD schema.", e);
		}
	}
}
