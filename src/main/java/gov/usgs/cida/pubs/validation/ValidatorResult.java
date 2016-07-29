package gov.usgs.cida.pubs.validation;

import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.json.View;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

public class ValidatorResult {

	@JsonView(View.PW.class)
	private String field;
	@JsonView(View.PW.class)
	private String message;
	@JsonView(View.PW.class)
	private SeverityLevel level;
	@JsonView(View.PW.class)
	private String value;

	public ValidatorResult (final String inField, final String inMessage, final SeverityLevel inLevel, final String inValue) {
		field = inField;
		message = inMessage;
		level = inLevel;
		value = inValue;
	}

	public String getField() {
		return field;
	}

	public void setField(final String inField) {
		field = inField;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(final String inMessage) {
		message = inMessage;
	}

	public SeverityLevel getLevel() {
		return level;
	}

	public void setLevel(final SeverityLevel inLevel) {
		level = inLevel;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String inValue) {
		value = inValue;
	}

	@Override
	@JsonIgnore
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Field:").append(getField()).append(" - ");
		sb.append("Message:").append(getMessage()).append(" - ");
		sb.append("Level:").append(getLevel()).append(" - ");
		sb.append("Value:").append(getValue());
		return sb.toString();
	}

}
