package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.intfc.IDao;

public class Contributor extends BaseDomain<Contributor> {

    private static IDao<Contributor> contributorDao;

    private String first;

    private String given;

    private String suffix;

    private String email;

    private String affiliation;

    private String literal;

    public String getFirst() {
        return first;
    }

    public void setFirst(final String inFirst) {
        first = inFirst;
    }

    public String getGiven() {
        return given;
    }

    public void setGiven(final String inGiven) {
        given = inGiven;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(final String inSuffix) {
        suffix = inSuffix;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String inEmail) {
        email = inEmail;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(final String inAffiliation) {
        affiliation = inAffiliation;
    }

    public String getLiteral() {
        return literal;
    }

    public void setLiteral(final String inLiteral) {
        literal = inLiteral;
    }

    /**
     * @return the contributorDao
     */
    public static IDao<Contributor> getDao() {
        return contributorDao;
    }

    /**
     * The setter for contributorDao.
     * @param inContributorDao the contributorDao to set
     */
    public void setContributorDao(final IDao<Contributor> inContributorDao) {
        contributorDao = inContributorDao;
    }

}
