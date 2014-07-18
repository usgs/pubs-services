package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.json.view.intfc.IBaseView;
import gov.usgs.cida.pubs.json.view.intfc.IMpView;
import gov.usgs.cida.pubs.utility.PubsUtilities;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.validation.ValidatorResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * All Domain Objects extend this base class.
 * @author drsteini
 * @param <D> the specific domain of the object 
 */
//@JsonInclude(JsonInclude.Include.NON_EMPTY or .NON_NULL?)
public abstract class BaseDomain<D> implements IBaseView {

    /** The ID of this domain object. */
    protected Integer id;

    @JsonIgnore
//    @JsonSerialize(using=PubsJsonLocalDateTimeSerializer.class)
//    @JsonDeserialize(using=PubsJsonLocalDateTimeDeSerializer.class)
    private LocalDateTime insertDate;

    @JsonIgnore
    private String insertUsername;

    @JsonIgnore
//    @JsonSerialize(using=PubsJsonLocalDateTimeSerializer.class)
//    @JsonDeserialize(using=PubsJsonLocalDateTimeDeSerializer.class)
    private LocalDateTime updateDate;

    @JsonIgnore
    private String updateUsername;

    private ValidationResults validationErrors;

    public BaseDomain() {
    }
    // better clone model than the clone method
    public BaseDomain(BaseDomain<D> domain) {
        id = domain.id;
        insertDate     = domain.insertDate;
        insertUsername = domain.insertUsername;
        updateDate     = domain.updateDate;
        updateUsername = domain.updateUsername;
    }

    /**
     * The getter for id.
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * The setter for id.
     * @param inId the id to set
     */
    @JsonIgnore
    public void setId(final Integer inId) {
        id = inId;
    }

    /**
     * The string setter for id.
     * @param inId the id to set
     */
    @JsonProperty("id")
    @JsonView(IMpView.class)
    public void setId(final String inId) {
        id = PubsUtilities.parseInteger(inId);
    }

    /**
     * @return the insertDate
     */
    public LocalDateTime getInsertDate() {
        return insertDate;
    }

    /**
     * @param inInsertDate the insertDate to set
     */
    public void setInsertDate(final LocalDateTime inInsertDate) {
        insertDate = inInsertDate;
    }

    /**
     * @return the insertUsername
     */
    public String getInsertUsername() {
        return insertUsername;
    }

    /**
     * @param inInsertUsername the insertUsername to set
     */
    public void setInsertUsername(final String inInsertUsername) {
        insertUsername = inInsertUsername;
    }

    /**
     * @return the updateDate
     */
    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    /**
     * @param inUpdateDate the updateDate to set
     */
    public void setUpdateDate(final LocalDateTime inUpdateDate) {
        updateDate = inUpdateDate;
    }

    /**
     * @return the updateUsername
     */
    public String getUpdateUsername() {
        return updateUsername;
    }

    /**
     * @param inUpdateUsername the updateUsername to set
     */
    public void setUpdateUsername(final String inUpdateUsername) {
        updateUsername = inUpdateUsername;
    }

    /**
     * @return the validationErrors
     */
    public ValidationResults getValidationErrors() {
        return validationErrors;
    }

    /**
     * @param inValidationErrors the validationErrors to set
     */
    public void setValidationErrors(final Set<ConstraintViolation<D>> inValidationErrors) {
        validationErrors = new ValidationResults();
        if (null != inValidationErrors) {
            List<ValidatorResult> vResults = new ArrayList<ValidatorResult>();
            for (ConstraintViolation<D> vError : inValidationErrors) {
                ValidatorResult vResult = new ValidatorResult(vError.getPropertyPath().toString(), vError.getMessage(), "fatal", null);
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
