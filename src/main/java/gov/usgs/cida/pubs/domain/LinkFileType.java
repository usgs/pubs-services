package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;
import gov.usgs.cida.pubs.json.view.intfc.ILookupView;

import com.fasterxml.jackson.annotation.JsonView;

public class LinkFileType extends BaseDomain<LinkFileType> implements ILookup {

    private static IDao<LinkFileType> linkFileTypeDao;


    private String name;

    public String getName() {
        return name;
    }

    public void setName(final String inName) {
        name = inName;
    }

    public static IDao<LinkFileType> getDao() {
        return linkFileTypeDao;
    }

    public void setLinkFileTypeDao(final IDao<LinkFileType> inLinkFileTypeDao) {
        linkFileTypeDao = inLinkFileTypeDao;
    }

    @Override
    @JsonView(ILookupView.class)
    public String getText() {
        return name;
    }

}
