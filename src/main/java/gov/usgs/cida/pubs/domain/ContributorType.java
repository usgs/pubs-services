package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;
import gov.usgs.cida.pubs.json.view.intfc.ILookupView;

import com.fasterxml.jackson.annotation.JsonView;

public class ContributorType extends BaseDomain<ContributorType> implements ILookup {

    public static final Integer AUTHORS = 1;
    public static final Integer EDITORS = 2;

    private static IDao<ContributorType> contributorTypeDao;

    private String text;

    @Override
    @JsonView(ILookupView.class)
    public String getText() {
        return text;
    }

    public void setText(final String inText) {
        text = inText;
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

}
