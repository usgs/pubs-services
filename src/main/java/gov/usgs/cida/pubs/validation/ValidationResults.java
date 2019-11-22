package gov.usgs.cida.pubs.validation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.json.View;


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
	public boolean isValid() {
		return null == validationErrors || validationErrors.isEmpty() || SeverityLevel.INFORMATIONAL == maxSeverityLevel();
	}

	@JsonIgnore
	public boolean isEmpty() {
		return null == validationErrors || validationErrors.isEmpty();
	}

	@JsonIgnore
	public SeverityLevel maxSeverityLevel() {
		return validationErrors.stream().map(ValidatorResult::getLevel).max(Comparator.comparing(SeverityLevel::ordinal)).get();
	}

	/** {@inheritDoc}
	 * @see java.lang.Object#toString()
	 */
	@Override
	@JsonIgnore
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (ValidatorResult result : validationErrors) {
			sb.append(result.toString()).append("\n");
		}
		sb.append("Validator Results: ")
			.append(validationErrors.size())
			.append(" result(s)\n");
		return sb.toString();
	}

}
