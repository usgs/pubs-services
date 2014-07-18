package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;
import gov.usgs.cida.pubs.json.view.intfc.ILookupView;

import com.fasterxml.jackson.annotation.JsonView;

public class ContributorType extends BaseDomain<ContributorType> implements ILookup {

    private static IDao<ContributorType> contributorTypeDao;

    private String name;

    private String tabName;

    public String getName() {
        return name;
    }

    public void setName(final String inName) {
        name = inName;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(final String inTabName) {
        tabName = inTabName;
    }

    /**
     * @return the contributorTypeDao
     */
    public static IDao<ContributorType> getDao() {
        return contributorTypeDao;
    }

    /**
     * The setter for contributorTypeDao.
     * @param inContributorTypeDao the contributorTypeDao to set
     */
    public void setContributorTypeDao(final IDao<ContributorType> inContributorTypeDao) {
        contributorTypeDao = inContributorTypeDao;
    }

    @Override
    @JsonView(ILookupView.class)
    public String getText() {
        return tabName;
    }

    @Override
    @JsonView(ILookupView.class)
    public String getValue() {
        return String.valueOf(id);
    }

}
