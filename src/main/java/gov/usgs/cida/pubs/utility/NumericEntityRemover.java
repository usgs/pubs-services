package gov.usgs.cida.pubs.utility;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.lang3.text.translate.CodePointTranslator;
/**
 * A combination of NumericEntityEscaper and UnicodeUnpairedSurrogateRemover. See other notes in PubsEscapeXML10.
 * @author drsteini
 *
 */
public class NumericEntityRemover extends CodePointTranslator {

	@Override
	public boolean translate(int codepoint, Writer out) throws IOException {
        if (codepoint > 0x10000) { //0x10FFFF) {
            // It's a one of those funky AL32UTF8 but not UTF8 characters. Write nothing and say we've translated.
            return true;
        } else {
            // It's not a funky. Don't translate it.
            return false;
        }
	}

}
