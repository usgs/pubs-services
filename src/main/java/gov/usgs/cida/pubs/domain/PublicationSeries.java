package gov.usgs.cida.pubs.domain;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonView;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;
import gov.usgs.cida.pubs.json.View;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.validation.constraint.DeleteChecks;
import gov.usgs.cida.pubs.validation.constraint.NoChildren;
import gov.usgs.cida.pubs.validation.constraint.ParentExists;
import gov.usgs.cida.pubs.validation.constraint.UniqueKey;

/**
 * @author drsteini
 *
 */
@Component
@UniqueKey(message = "{publicationseries.duplicate}")
@ParentExists
@NoChildren(groups = DeleteChecks.class)
@JsonPropertyOrder({"id", "text", "code", "seriesDoiName", "onlineIssn", "printIssn", "active"})
public class PublicationSeries extends BaseDomain<PublicationSeries> implements ILookup, Serializable {

	private static final long serialVersionUID = -4799472987508509766L;

	private static IDao<PublicationSeries> publicationSeriesDao;

	public static final Integer GIP = 315;

	public static final Integer SIR = 334;

	@JsonView(View.PW.class)
	@NotNull
	private PublicationSubtype publicationSubtype;

	@JsonView(View.PW.class)
	@Length(min=1, max=250)
	private String text;

	@JsonView(View.PW.class)
	@Length(min=0, max=7)
	private String code;

	@JsonView(View.PW.class)
	@Length(min=0, max=2000)
	private String seriesDoiName;

	@JsonView(View.PW.class)
	@Length(min=0, max=9)
	private String onlineIssn;

	@JsonView(View.PW.class)
	@Length(min=0, max=9)
	private String printIssn;

	@JsonView(View.PW.class)
	private Boolean active;

	/**
	 * @return the publicationSubtype
	 */
	public PublicationSubtype getPublicationSubtype() {
		return publicationSubtype;
	}

	/**
	 * @param inPublicationSubtype the publicationSubtype to set
	 */
	public void setPublicationSubtype(final PublicationSubtype inPublicationSubtype) {
		publicationSubtype = inPublicationSubtype;
	}

	@Override
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCode() {
		return code;
	}

	public void setCode(final String inCode) {
		code = inCode;
	}

	public String getSeriesDoiName() {
		return seriesDoiName;
	}

	public void setSeriesDoiName(final String inSeriesDoiName) {
		seriesDoiName = inSeriesDoiName;
	}

	public String getOnlineIssn() {
		return onlineIssn;
	}

	public void setOnlineIssn(final String inOnlineIssn) {
		onlineIssn = inOnlineIssn;
	}

	public String getPrintIssn() {
		return printIssn;
	}

	public void setPrintIssn(final String inPrintIssn) {
		printIssn = inPrintIssn;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(final Boolean inActive) {
		active = inActive;
	}

	@JsonProperty("validationErrors")
	@JsonView(View.LookupMaint.class)
	@JsonUnwrapped
	@Override
	public ValidationResults getValidationErrors() {
		return super.getValidationErrors();
	}

	public static IDao<PublicationSeries> getDao() {
		return publicationSeriesDao;
	}

	/**
	 * The setter for publicationSeriesDao.
	 * @param inPublicationSeriesDao the publicationSeriesDao to set
	 */
	@Autowired
	@Qualifier("publicationSeriesDao")
	public void setPublicationSeriesDao(final IDao<PublicationSeries> inPublicationSeriesDao) {
		publicationSeriesDao = inPublicationSeriesDao;
	}

}
