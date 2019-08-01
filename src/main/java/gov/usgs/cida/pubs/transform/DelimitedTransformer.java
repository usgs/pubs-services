package gov.usgs.cida.pubs.transform;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import gov.usgs.cida.pubs.PubsConstantsHelper;

public class DelimitedTransformer extends Transformer {

	protected static final String LF = StringUtils.LF;
	protected static final String CR = StringUtils.CR;
	protected static final String TAB = "\t";
	protected static final String SPACE = " ";

	protected String delimiter;

	public DelimitedTransformer(OutputStream target, Map<String, String> mapping, String delimiter) {
		super(target, mapping);
		this.delimiter = delimiter;
	}

	/**
	 * Write the header.
	 * 
	 * @throws IOException when issues with the streaming.
	 */
	@Override
	protected void writeHeader(Map<?, ?> result) throws IOException {
		boolean firstPass = true;
		for (Entry<?, ?> entry: result.entrySet()) {
			String value = getMappedName(entry);
			if (null != value) {
				if (firstPass) {
					firstPass = false;
				} else {
					copyString(delimiter);
				}
				copyString(StringEscapeUtils.escapeCsv(value));
			}
		}
		endRow();
	}

	/**
	 * Write the data.
	 * 
	 * @throws IOException when issues with the streaming.
	 */
	protected void writeData(Map<?, ?> result) throws IOException {
		boolean firstPass = true;
		for (Entry<?, ?> entry: result.entrySet()) {
			if (null != getMappedName(entry)) {
				if (firstPass) {
					firstPass = false;
				} else {
					copyString(delimiter);
				}
				Object value = entry.getValue();
				if (null != value) {
					String stringValue = delimiter == TAB ? value.toString().replace(LF, SPACE).replace(CR, SPACE).replace(TAB, SPACE) : value.toString();
					copyString(StringEscapeUtils.escapeCsv(stringValue));
				}
			}
		}
		endRow();
	}

	/**
	 * Output the new line.
	 * @throws IOException when issues with the streaming.
	 */
	protected void endRow() throws IOException {
		copyString(LF);
	}

	/** 
	 * Converts a string to a byte array and stream it.
	 * @param in the string to be streamed.
	 * @throws IOException when issues with the streaming.
	 */
	protected void copyString(final String in) throws IOException {
		if (null != in) {
			target.write(in.getBytes(PubsConstantsHelper.DEFAULT_ENCODING));
		}
	}

	@Override
	protected void init() throws IOException {
		//Nothing to do here for the delimited formats. 
	}

}
