package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author drsteini
 *
 */
@Component
public class PublicationType extends BaseDomain<PublicationType> implements ILookup, Serializable {

	private static final long serialVersionUID = 6859787295076399446L;

	private static IDao<PublicationType> publicationTypeDao;

    public static final Integer ARTICLE = 2;

    public static final Integer REPORT = 18;

    private String text;

    private Collection<PublicationSubtype> publicationSubtypes;

    @Override
    public String getText() {
        return text;
    }
    
	public void setText(String text) {
		this.text = text;
	}

    /**
     * @return the publicationSubtypes
     */
    public Collection<PublicationSubtype> getpublicationSubtypes() {
        return publicationSubtypes;
    }

    /**
     * @param inPublicationSubtypes the publicationSubtypes to set
     */
    public void setPublicationSubtypes(final Collection<PublicationSubtype> inPublicationSubtypes) {
        publicationSubtypes = inPublicationSubtypes;
    }

    /**
     * @return the publicationTypeDao
     */
    public static IDao<PublicationType> getDao() {
        return publicationTypeDao;
    }

    /**
     * The setter for publicationTypeDao.
     * @param inPublicationTypeDao the publicationTypeDao to set
     */
    @Autowired
    public void setPublicationTypeDao(final IDao<PublicationType> inPublicationTypeDao) {
        publicationTypeDao = inPublicationTypeDao;
    }

}
