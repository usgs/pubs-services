package gov.usgs.cida.pubs;

import org.springframework.http.MediaType;

public final class PubsConstantsHelper {

	public static final String DEFAULT_ENCODING = "UTF-8";

	/**
	 * The URL_ENCODING constant should only be used when calling 
	 * URLEncoder.encode. In ALL other cases, use PubsConstants.DEFAULT_ENCODING.
	 * Example:
	 * URLEncoder.encode("someValue", PubsConstants.URL_ENCODING);
	 * 
	 * URL_ENCODING should always equal UTF-8 because that is what W3C specs
	 * mandate. Even if the document that contains the URL is in a different
	 * encoding, we still need to pass UTF-8 to URLEncoder.encode.
	 * 
	 * Details:
	 * https://www.w3.org/TR/html40/appendix/notes.html#non-ascii-chars
	 * http://www.ietf.org/rfc/rfc2141.txt
	 * https://tools.ietf.org/html/rfc3986#section-2.5
	 */
	public static final String URL_ENCODING = "UTF-8";

	public static final String MEDIA_TYPE_RSS_VALUE = MediaType.TEXT_XML_VALUE;
	public static final String MEDIA_TYPE_CSV_VALUE = "text/csv";
	public static final String MEDIA_TYPE_HTML_VALUE = MediaType.TEXT_HTML_VALUE;
	public static final String MEDIA_TYPE_TSV_VALUE = "text/tab-separated-values";
	public static final String MEDIA_TYPE_XLSX_VALUE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	public static final String MEDIA_TYPE_CROSSREF_VALUE = "application/vnd.crossref.deposit+xml";

	public static final MediaType MEDIA_TYPE_CSV = new MediaType("text", "csv");
	public static final MediaType MEDIA_TYPE_TSV = new MediaType("text", "tab-separated-values");
	public static final MediaType MEDIA_TYPE_XLSX = new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	public static final MediaType MEDIA_TYPE_RSS = MediaType.TEXT_XML;
	public static final MediaType MEDIA_TYPE_CROSSREF = new MediaType("application", "vnd.crossref.deposit+xml");

	public static final String MEDIA_TYPE_CSV_EXTENSION = "csv";
	public static final String MEDIA_TYPE_TSV_EXTENSION = "tsv";
	public static final String MEDIA_TYPE_XLSX_EXTENSION = "xlsx";
	public static final String MEDIA_TYPE_XML_EXTENSION = "xml";
	public static final String MEDIA_TYPE_JSON_EXTENSION = "json";
	public static final String MEDIA_TYPE_RSS_EXTENSION = "rss";
	public static final String MEDIA_TYPE_CROSSREF_EXTENSION = "crossref.xml";

	public static final String CONTENT_PARAMETER_NAME = "mimeType";
	public static final String ACCEPT_HEADER = "Accept";

	public static final String SPACES_OR_NUMBER_REGEX = "^ *\\d*$";
	public static final String FOUR_DIGIT_REGEX = "^\\d{4}$";
	public static final String SEARCH_TERMS_SPLIT_REGEX = "[^a-zA-Z\\d]";

	/** The default username for anonymous access. */
	public static final String ANONYMOUS_USER = "anonymous";

	public static final String DOI_PREFIX = "10.3133";

	public static final Integer DEFAULT_LOCK_TIMEOUT_HOURS = 3;

	public static final String NOT_IMPLEMENTED = "NOT IMPLEMENTED.";

	public static final String API_KEY_NAME = "pubsApiKey";

	private PubsConstantsHelper() {
	}

}
