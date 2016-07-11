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
		// It's a one of those funky AL32UTF8 but not UTF8 characters. Write nothing and say we've translated.
		return codepoint > 0x10000;
	}

}
