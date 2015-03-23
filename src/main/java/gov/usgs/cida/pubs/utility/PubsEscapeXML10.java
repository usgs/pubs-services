package gov.usgs.cida.pubs.utility;

import org.apache.commons.lang3.text.translate.AggregateTranslator;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;

/** 
 * This is a modified version of org.apache.commons.lang3.StringEscapeUtils.ESCAPE_XML10
 * We occasionally get characters from IPDS that cause LPX-00217 Oracle errors in an UTF8 database.
 * This routine will strip out anything above 0x10000 as these seem to cause problems even if escaped. 
 * None of this should be needed when we move to an AL32UTF8 database. 
 * @author drsteini
 *
 */
public class PubsEscapeXML10 {

	public PubsEscapeXML10() {}
	
    public static final CharSequenceTranslator ESCAPE_XML10 =
		new AggregateTranslator(new NumericEntityRemover());

}
