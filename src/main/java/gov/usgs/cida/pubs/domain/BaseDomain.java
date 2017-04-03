package gov.usgs.cida.pubs.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.json.View;
import gov.usgs.cida.pubs.utility.PubsUtilities;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.validation.ValidatorResult;

/**
 * All Domain Objects extend this base class.
 * @author drsteini
 * @param <D> the specific domain of the object 
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseDomain<D> {

	/** The ID of this domain object. */
	protected Integer id;

	@JsonIgnore
	private LocalDateTime insertDate;

	@JsonIgnore
	private String insertUsername;

	@JsonIgnore
	private LocalDateTime updateDate;

	@JsonIgnore
	private String updateUsername;

	@JsonIgnore
	protected ValidationResults validationErrors;

	public Integer getId() {
		return id;
	}

	@JsonIgnore
	public void setId(final Integer inId) {
		id = inId;
	}

	@JsonProperty("id")
	@JsonView(View.Base.class)
	public void setId(final String inId) {
		id = PubsUtilities.parseInteger(inId);
	}

	public LocalDateTime getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(final LocalDateTime inInsertDate) {
		insertDate = inInsertDate;
	}

	public String getInsertUsername() {
		return insertUsername;
	}

	public void setInsertUsername(final String inInsertUsername) {
		insertUsername = inInsertUsername;
	}

	public LocalDateTime getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(final LocalDateTime inUpdateDate) {
		updateDate = inUpdateDate;
	}

	public String getUpdateUsername() {
		return updateUsername;
	}

	public void setUpdateUsername(final String inUpdateUsername) {
		updateUsername = inUpdateUsername;
	}

	@JsonIgnore
	public boolean isValid() {
		return getValidationErrors().isEmpty();
	}

	public ValidationResults getValidationErrors() {
		if (null != validationErrors) {
			return validationErrors;
		} else {
			return new ValidationResults();
		}
	}

	public void setValidationErrors(final Set<ConstraintViolation<D>> inValidationErrors) {
		validationErrors = new ValidationResults();
		if (null != inValidationErrors) {
			List<ValidatorResult> vResults = new ArrayList<ValidatorResult>();
			for (ConstraintViolation<D> vError : inValidationErrors) {
				ValidatorResult vResult = new ValidatorResult(vError.getPropertyPath().toString(), vError.getMessage(), SeverityLevel.FATAL, null);
				vResults.add(vResult);
			}
			validationErrors.setValidationErrors(vResults);
		}
	}

	public void addValidatorResult(final ValidatorResult inValidatorResult) {
		if (null == validationErrors) {
			validationErrors = new ValidationResults();
		}
		validationErrors.addValidatorResult(inValidatorResult);
	}

}
