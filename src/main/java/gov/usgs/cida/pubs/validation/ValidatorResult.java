package gov.usgs.cida.pubs.validation;

import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.json.View;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * @author drsteini
 *
 */
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

    /**
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * @param inField the field to set
     */
    public void setField(final String inField) {
        field = inField;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param inMessage the message to set
     */
    public void setMessage(final String inMessage) {
        message = inMessage;
    }

    /**
     * @return the level
     */
    public SeverityLevel getLevel() {
        return level;
    }

    /**
     * @param inLevel the level to set
     */
    public void setLevel(final SeverityLevel inLevel) {
        level = inLevel;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param inValue the value to set
     */
    public void setValue(final String inValue) {
        value = inValue;
    }

}
