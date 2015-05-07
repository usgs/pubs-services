package gov.usgs.cida.pubs.transform;

import java.util.LinkedHashMap;
import java.util.Map;

public class PublicationColumns {

	private static Map<String, String> mappings;
	
	//TODO - possible dynamic contributors
	static {
		mappings = new LinkedHashMap<>();
		mappings.put("WAREHOUSE_URL", "URL");
		mappings.put("PUBLICATION_TYPE", "Publication type");
        mappings.put("PUBLICATION_SUBTYPE", "Publication Subtype");
        mappings.put("TITLE", "Title");
        mappings.put("SERIES_TITLE", "Series title");
        mappings.put("SERIES_NUMBER", "Series number");
        mappings.put("SUBSERIES_TITLE", "Subseries");
        mappings.put("CHAPTER", "Chapter");
        mappings.put("SUBCHAPTER", "Sub-chapter");
        mappings.put("AUTHORS", "Author(s)");
        mappings.put("EDITORS", "Editor(s)");
        mappings.put("COMPILERS", "Compiler(s)");
        mappings.put("ONLINE_ISSN", "ISSN (online)");
        mappings.put("PRINT_ISSN", "ISSN (print)");
        mappings.put("ISBN", "ISBN");
        mappings.put("DOI_NAME", "DOI");
        mappings.put("EDITION", "Edition");
        mappings.put("VOLUME", "Volume");
        mappings.put("ISSUE", "Issue");
        mappings.put("PUBLICATION_YEAR", "Year Published");
        mappings.put("LANGUAGE", "Language");
        mappings.put("PUBLISHER", "Publisher");
        mappings.put("PUBLISHER_LOCATION", "Publisher location");
        mappings.put("COST_CENTERS", "Contributing office(s)");
        mappings.put("PRODUCT_DESCRIPTION", "Description");
        mappings.put("LARGER_WORK_TYPE", "Larger Work Type");
        mappings.put("LARGER_WORK_SUBTYPE", "Larger Work Subtype");
        mappings.put("LARGER_WORK_TITLE", "Larger Work Title");
        mappings.put("START_PAGE", "First page");
        mappings.put("END_PAGE", "Last page");
        mappings.put("NUMBER_OF_PAGES", "Number of Pages");
        mappings.put("PUBLIC_COMMENTS", "Public Comments");
        mappings.put("TEMPORAL_START", "Time Range Start");
        mappings.put("TEMPORAL_END", "Time Range End");
        mappings.put("CONFERENCE_TITLE", "Conference Title");
        mappings.put("CONFERENCE_LOCATION", "Conference Location");
        mappings.put("CONFERENCE_DATE", "Conference Date");
        mappings.put("COUNTRY", "Country");
        mappings.put("STATE", "State");
        mappings.put("COUNTY", "County");
        mappings.put("CITY", "City");
        mappings.put("OTHER_GEOSPATIAL", "Other Geospatial");
        mappings.put("DATUM", "Datum");
        mappings.put("PROJECTION", "Projection");
        mappings.put("SCALE", "Scale");
        mappings.put("ONLINE_ONLY", "Online Only (Y/N)");
        mappings.put("ADDITIONAL_ONLINE_FILES", "Additional Online Files(Y/N)");
        mappings.put("NUMBER_OF_LINKS", "Number of Links");
	}
	
	public static Map<String, String> getMappings() {
		return mappings;
	}

}
