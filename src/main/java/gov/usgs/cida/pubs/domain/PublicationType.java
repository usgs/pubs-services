package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * @author drsteini
 *
 */
public class PublicationType extends BaseDomain<PublicationType> implements ILookup {

    private static IDao<PublicationType> publicationTypeDao;

    public static final String ARTICLE = "2";

    private String name;

    private Collection<PublicationSubtype> publicationSubtypes;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param inName the name to set
     */
    public void setName(final String inName) {
        name = inName;
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
    public void setPublicationTypeDao(final IDao<PublicationType> inPublicationTypeDao) {
        publicationTypeDao = inPublicationTypeDao;
    }

    @Override
    @JsonView(LookupView.class)
    public String getText() {
        return name;
    }

    @Override
    @JsonView(LookupView.class)
    public String getValue() {
        return String.valueOf(id);
    }

}
