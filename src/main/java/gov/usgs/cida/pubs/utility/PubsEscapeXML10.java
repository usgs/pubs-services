package gov.usgs.cida.pubs.utility;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usgs.cida.pubs.busservice.ipds.IpdsCostCenterService;

/** 
 * This is a modified version of org.apache.commons.text.StringEscapeUtils.ESCAPE_XML10
 * We occasionally get characters from IPDS that cause LPX-00217 Oracle errors in an UTF8 database.
 * This routine will strip out anything above 0x10000 as these seem to cause problems even if escaped. 
 * None of this should be needed when we move to an AL32UTF8 database.
 * 
 * It replace the entire message if anything is found before the first "<"
 * Some of the messages have been starting with "ï»¿" and then contain html instead of xml,
 * this causes bad things to happen in the Oracle XDB
 * @author drsteini
 *
 */
public class PubsEscapeXML10 {

	private static final Logger LOG = LoggerFactory.getLogger(IpdsCostCenterService.class);

	public static final String ERROR_CLEANSING = "<error>Service returned unreadable message.</error>";
	private PubsEscapeXML10() {}

	public static final CharSequenceTranslator ESCAPE_XML10 =
		new AggregateTranslator(new NumericEntityRemover());

	public static  String cleanseXml(String xml) {
		if (!StringUtils.startsWith(xml, "<") || StringUtils.containsIgnoreCase(xml, "<!DOCTYPE html")) {
			LOG.info("ERROR RESPONSE FROM IPDS!!!", xml);
			return ERROR_CLEANSING;
		} else {
			return PubsEscapeXML10.ESCAPE_XML10.translate(xml);
		}
	}

}
