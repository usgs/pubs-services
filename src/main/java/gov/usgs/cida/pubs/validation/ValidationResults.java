package gov.usgs.cida.pubs.validation;

import gov.usgs.cida.pubs.json.View;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;


public class ValidationResults {

    @JsonView(View.PW.class)
    @JsonProperty("validationErrors")
    private List<ValidatorResult> validationErrors = new ArrayList<ValidatorResult>();

    public void setValidationErrors(final List<ValidatorResult> inValidationErrors) {
    	validationErrors = inValidationErrors;
    }

    public void addValidatorResult(final ValidatorResult inValidatorResult) {
    	validationErrors.add(inValidatorResult);
    }

    public void addValidationResults(final ValidationResults inValidationResults) {
    	validationErrors.addAll(inValidationResults.getValidationErrors());
    }

    public List<ValidatorResult> getValidationErrors() {
        return validationErrors;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return null == validationErrors || validationErrors.isEmpty();
    }

    /** {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    @JsonIgnore
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ValidatorResult result : validationErrors) {
            sb.append("Field:").append(result.getField()).append(" - ");
            sb.append("Message:").append(result.getMessage()).append(" - ");
            sb.append("Level:").append(result.getLevel()).append(" - ");
            sb.append("Value:").append(result.getValue()).append("\n");
        }
        sb.append("Validator Results: ")
            .append(validationErrors.size())
            .append(" result(s)\n");
        return sb.toString();
    }

}
