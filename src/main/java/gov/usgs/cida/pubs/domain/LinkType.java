package gov.usgs.cida.pubs.domain;

import com.fasterxml.jackson.annotation.JsonView;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;
import gov.usgs.cida.pubs.json.view.intfc.ILookupView;

public class LinkType extends BaseDomain<LinkType> implements ILookup {

    private static IDao<LinkType> linkTypeDao;

    public static final String INDEX_PAGE = "15";

    private String name;

    public String getName() {
        return name;
    }

    public void setName(final String inName) {
        name = inName;
    }

    public static IDao<LinkType> getDao() {
        return linkTypeDao;
    }

    public void setLinkTypeDao(final IDao<LinkType> inLinkTypeDao) {
        linkTypeDao = inLinkTypeDao;
    }

    @Override
    @JsonView(ILookupView.class)
    public String getText() {
        return name;
    }

}
