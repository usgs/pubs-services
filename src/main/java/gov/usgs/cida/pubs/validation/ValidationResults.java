package gov.usgs.cida.pubs.validation;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class ValidationResults {

    private List<ValidatorResult> vErrors = new ArrayList<ValidatorResult>();

    public void setValidationErrors(final List<ValidatorResult> inValidationErrors) {
        vErrors = inValidationErrors;
    }

    public void addValidatorResult(final ValidatorResult inValidatorResult) {
        vErrors.add(inValidatorResult);
    }
    
    public void addValidationResults(final ValidationResults inValidationResults) {
        vErrors.addAll(inValidationResults.getValidatorResults());
    }
    
    public List<ValidatorResult> getValidatorResults() {
        return vErrors;
    }
    
    @JsonIgnore
    public boolean isEmpty() {
        return null == vErrors || 0 == vErrors.size();
    }
    
    /** {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ValidatorResult result : vErrors) {
            sb.append("Field:").append(result.getField()).append(" - ");
            sb.append("Message:").append(result.getMessage()).append(" - ");
            sb.append("Level:").append(result.getLevel()).append(" - ");
            sb.append("Value:").append(result.getValue()).append("\n");
        }
        sb.append("Validator Results: ")
            .append(vErrors.size())
            .append(" result(s)\n");
        return sb.toString();
    }

}
