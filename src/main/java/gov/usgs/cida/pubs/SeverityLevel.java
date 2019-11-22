package gov.usgs.cida.pubs;

public enum SeverityLevel {
	//Note that these are listed in ascending severity level. i.e. FATAL is more severe than SKIPPED.
	//This ordering must be preserved so the ValidationResults.maxSeverityLevel() functions correctly.

	// This record had issues, but was still processed/persisted. isValid() == true.
	INFORMATIONAL,
	// This record was not processed. (SIPP) isValid() == false.
	SKIPPED,
	// This record has errors that prevented it being persisted to the database. isValid() == false.
	FATAL

}
