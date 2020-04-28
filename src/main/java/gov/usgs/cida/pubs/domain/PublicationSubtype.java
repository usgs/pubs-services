package gov.usgs.cida.pubs.domain;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;
import io.swagger.v3.oas.annotations.media.Schema;

@Component
public class PublicationSubtype extends BaseDomain<PublicationSubtype> implements ILookup, Serializable {

	private static final long serialVersionUID = 122305624975493957L;

	private static IDao<PublicationSubtype> publicationSubtypeDao;

	public static final Integer USGS_NUMBERED_SERIES = 5;

	public static final Integer USGS_UNNUMBERED_SERIES = 6;

	public static final Integer USGS_DATA_RELEASE = 7;

	public static final Integer USGS_WEBSITE = 8;

	private PublicationType publicationType;

	private String text;

	private Collection<PublicationSeries> publicationSeries;

	public PublicationType getPublicationType() {
		return publicationType;
	}

	public void setPublicationType(final PublicationType inPublicationType) {
		publicationType = inPublicationType;
	}

	@Override
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Collection<PublicationSeries> getpublicationSeries() {
		return publicationSeries;
	}

	public void setPublicationSeries(final Collection<PublicationSeries> inPublicationSeries) {
		publicationSeries = inPublicationSeries;
	}

	public static IDao<PublicationSubtype> getDao() {
		return publicationSubtypeDao;
	}

	@Autowired
	@Qualifier("publicationSubtypeDao")
	@Schema(hidden = true)
	public void setPublicationSubtypeDao(final IDao<PublicationSubtype> inPublicationSubtypeDao) {
		publicationSubtypeDao = inPublicationSubtypeDao;
	}

}
