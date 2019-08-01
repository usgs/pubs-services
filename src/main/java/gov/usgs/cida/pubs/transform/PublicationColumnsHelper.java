package gov.usgs.cida.pubs.transform;

import java.util.LinkedHashMap;
import java.util.Map;

public class PublicationColumnsHelper {

	private static Map<String, String> mappings;

	private PublicationColumnsHelper() {}

	//TODO - possible dynamic contributors
	static {
		mappings = new LinkedHashMap<>();
		mappings.put("publication_id", "Publication ID");
		mappings.put("index_id", "Index ID");
		mappings.put("warehouse_url", "URL");
		mappings.put("publication_type", "Publication type");
		mappings.put("publication_subtype", "Publication Subtype");
		mappings.put("display_title", "Display title");
		mappings.put("title", "Title");
		mappings.put("series_title", "Series title");
		mappings.put("series_number", "Series number");
		mappings.put("subseries_title", "Subseries");
		mappings.put("chapter", "Chapter");
		mappings.put("subchapter", "Sub-chapter");
		mappings.put("authors", "Author(s)");
		mappings.put("editors", "Editor(s)");
		mappings.put("compilers", "Compiler(s)");
		mappings.put("online_issn", "ISSN (online)");
		mappings.put("print_issn", "ISSN (print)");
		mappings.put("isbn", "ISBN");
		mappings.put("doi_name", "DOI");
		mappings.put("edition", "Edition");
		mappings.put("volume", "Volume");
		mappings.put("issue", "Issue");
		mappings.put("publication_year", "Year Published");
		mappings.put("no_year", "No Year Published");
		mappings.put("language", "Language");
		mappings.put("publisher", "Publisher");
		mappings.put("publisher_location", "Publisher location");
		mappings.put("cost_centers", "Contributing office(s)");
		mappings.put("product_description", "Description");
		mappings.put("larger_work_type", "Larger Work Type");
		mappings.put("larger_work_subtype", "Larger Work Subtype");
		mappings.put("larger_work_title", "Larger Work Title");
		mappings.put("start_page", "First page");
		mappings.put("end_page", "Last page");
		mappings.put("number_of_pages", "Number of Pages");
		mappings.put("public_comments", "Public Comments");
		mappings.put("temporal_start", "Time Range Start");
		mappings.put("temporal_end", "Time Range End");
		mappings.put("conference_title", "Conference Title");
		mappings.put("conference_location", "Conference Location");
		mappings.put("conference_date", "Conference Date");
		mappings.put("country", "Country");
		mappings.put("state", "State");
		mappings.put("county", "County");
		mappings.put("city", "City");
		mappings.put("other_geospatial", "Other Geospatial");
		mappings.put("datum", "Datum");
		mappings.put("projection", "Projection");
		mappings.put("scale", "Scale");
		mappings.put("online_only", "Online Only (Y/N)");
		mappings.put("additional_online_files", "Additional Online Files(Y/N)");
		mappings.put("number_of_links", "Number of Links");
		mappings.put("sciencebase_uri", "ScienceBase URI");
		mappings.put("chrs_doi", "CHORUS DOI");
		mappings.put("chrs_url", "CHORUS URL");
		mappings.put("chrs_publisher", "CHORUS Publisher");
		mappings.put("chrs_authors", "CHORUS Authors");
		mappings.put("chrs_journal_name", "CHORUS Journal Name");
		mappings.put("chrs_publication_date", "CHORUS Publication Date");
		mappings.put("chrs_audited_on", "CHORUS Audited On");
		mappings.put("chrs_pblclly_access_date", "CHORUS Publicly Accessible Date");
	}

	public static Map<String, String> getMappings() {
		return mappings;
	}

}
