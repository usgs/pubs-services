package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;
import gov.usgs.cida.pubs.json.view.intfc.IPwView;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * @author drsteini
 *
 */
@JsonPropertyOrder({"id", "text", "code", "seriesDoiName", "onlineIssn", "printIssn", "active"})
public class PublicationSeries extends BaseDomain<PublicationSeries> implements ILookup, Serializable {

	private static final long serialVersionUID = -4799472987508509766L;

	private static IDao<PublicationSeries> publicationSeriesDao;

    public static final Integer GIP = 315;

    public static final Integer SIR = 334;

    private PublicationSubtype publicationSubtype;

    private String text;

    private String code;

    private String seriesDoiName;

    @JsonView(IPwView.class)
    private String onlineIssn;

    @JsonView(IPwView.class)
    private String printIssn;

    @JsonIgnore
    private boolean active;

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

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean inActive) {
        active = inActive;
    }

    public static IDao<PublicationSeries> getDao() {
        return publicationSeriesDao;
    }

    /**
     * The setter for publicationSeriesDao.
     * @param inPublicationSeriesDao the publicationSeriesDao to set
     */
    public void setPublicationSeriesDao(final IDao<PublicationSeries> inPublicationSeriesDao) {
        publicationSeriesDao = inPublicationSeriesDao;
    }

}
