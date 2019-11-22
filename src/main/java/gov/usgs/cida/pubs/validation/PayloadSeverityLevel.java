package gov.usgs.cida.pubs.validation;

import javax.validation.Payload;

public interface PayloadSeverityLevel {
	interface INFORMATIONAL extends Payload { }
	interface FATAL extends Payload { }
}
