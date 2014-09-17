package gov.usgs.cida.pubs.domain;

import java.io.Serializable;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;
import gov.usgs.cida.pubs.json.view.intfc.ILookupView;
import gov.usgs.cida.pubs.json.view.intfc.IMpView;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * @author drsteini
 *
 */
public class PublicationSeries extends BaseDomain<PublicationSeries> implements ILookup, Serializable {

	private static final long serialVersionUID = -4799472987508509766L;

	private static IDao<PublicationSeries> publicationSeriesDao;

    public static final Integer GIP = 315;

    public static final Integer SIR = 334;

    private PublicationSubtype publicationSubtype;

    private String text;

    private String code;

    private String seriesDoiName;

    @JsonView(IMpView.class)
    private String onlineIssn;

    @JsonView(IMpView.class)
    private String printIssn;

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
    @JsonView({ILookupView.class, IMpView.class})
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
