package gov.usgs.cida.pubs;

public final class PubsConstants {

    public static final String DEFAULT_ENCODING = "UTF-8";

    public static final String MIME_TYPE_APPLICATION_JSON = "application/json";
    public static final String MIME_TYPE_TEXT_PLAIN = "text/plain";
    
    public static final String SPACES_OR_NUMBER_REGEX = "^ *\\d*$";
    
    public static final String FOUR_DIGIT_REGEX = "^\\d{4}$";
    
    /** The default username for anonymous access. */
    public static final String ANONYMOUS_USER = "anonymous";

    //SQL config for single search 
    public static final String SEARCH_TERM_ORDERBY = "publication_year";
    public static final String SEARCH_TERM_ORDERBY_DIR = "DESC";

    public static final String DOI_PREFIX = "10.3133";

    public static final Integer DEFAULT_LOCK_TIMEOUT_HOURS = 3;

    private PubsConstants() {
    };

}
