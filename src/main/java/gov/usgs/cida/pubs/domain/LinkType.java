package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;
import gov.usgs.cida.pubs.json.view.intfc.ILookupView;
import gov.usgs.cida.pubs.json.view.intfc.IMpView;

import com.fasterxml.jackson.annotation.JsonView;

public class LinkType extends BaseDomain<LinkType> implements ILookup {

    private static IDao<LinkType> linkTypeDao;

    public static final Integer INDEX_PAGE = 15;

	public static final Integer THUMBNAIL = 24;

    private String text;

    @Override
    @JsonView({ILookupView.class, IMpView.class})
    public String getText() {
        return text;
    }

    public void setText(final String inText) {
        text = inText;
    }

    public static IDao<LinkType> getDao() {
        return linkTypeDao;
    }

    public void setLinkTypeDao(final IDao<LinkType> inLinkTypeDao) {
        linkTypeDao = inLinkTypeDao;
    }

}
