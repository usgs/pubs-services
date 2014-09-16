package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;
import gov.usgs.cida.pubs.json.view.intfc.ILookupView;
import gov.usgs.cida.pubs.json.view.intfc.IMpView;

import com.fasterxml.jackson.annotation.JsonView;

public class LinkFileType extends BaseDomain<LinkFileType> implements ILookup {

    private static IDao<LinkFileType> linkFileTypeDao;


    private String text;

    @Override
    @JsonView({ILookupView.class, IMpView.class})
    public String getText() {
        return text;
    }

    public void setText(final String inText) {
        text = inText;
    }

    public static IDao<LinkFileType> getDao() {
        return linkFileTypeDao;
    }

    public void setLinkFileTypeDao(final IDao<LinkFileType> inLinkFileTypeDao) {
        linkFileTypeDao = inLinkFileTypeDao;
    }

}
