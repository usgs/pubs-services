package gov.usgs.cida.pubs.domain.sipp;

public final class SippContributorHelper {
	private SippContributorHelper() {
	}

	public static final Author NEW_OUTSIDE_AUTHOR = new Author();
	static {
		NEW_OUTSIDE_AUTHOR.setIpNumber("IP-050927");
		NEW_OUTSIDE_AUTHOR.setAuthorName(null);
		NEW_OUTSIDE_AUTHOR.setAuthorNameText("ODoe, Jane ");
		NEW_OUTSIDE_AUTHOR.setOrcid("http://orcid.org/1234-1234-1234-1234");
		NEW_OUTSIDE_AUTHOR.setCostCenter(null);
		NEW_OUTSIDE_AUTHOR.setContributorRole("1");
		NEW_OUTSIDE_AUTHOR.setNonUSGSAffiliation("Outside Affiliation 3");
		NEW_OUTSIDE_AUTHOR.setNonUSGSContributor("ODoe, Jane ");
		NEW_OUTSIDE_AUTHOR.setRank("1");
		NEW_OUTSIDE_AUTHOR.setCreated("2013-08-21T11:36:28");
		NEW_OUTSIDE_AUTHOR.setCreatedBy("Doe, J");
		NEW_OUTSIDE_AUTHOR.setModified("2013-08-21T11:37:08");
		NEW_OUTSIDE_AUTHOR.setModifiedBy("Doe, J");
	}

	public static final Author NEW_OUTSIDE_CONTRIBUTOR = new Author();
	static {
		NEW_OUTSIDE_CONTRIBUTOR.setIpNumber("IP-0509271");
		NEW_OUTSIDE_CONTRIBUTOR.setAuthorName(null);
		NEW_OUTSIDE_CONTRIBUTOR.setAuthorNameText("ODoul, Jane ");
		NEW_OUTSIDE_CONTRIBUTOR.setOrcid("http://orcid.org/0060-0000-0000-0001");
		NEW_OUTSIDE_CONTRIBUTOR.setCostCenter(null);
		NEW_OUTSIDE_CONTRIBUTOR.setContributorRole("1");
		NEW_OUTSIDE_CONTRIBUTOR.setNonUSGSAffiliation("Affiliation Cost Center 1");
		NEW_OUTSIDE_CONTRIBUTOR.setNonUSGSContributor("ODoul, Jane ");
		NEW_OUTSIDE_CONTRIBUTOR.setRank("1");
		NEW_OUTSIDE_CONTRIBUTOR.setCreated("2013-08-21T11:36:28");
		NEW_OUTSIDE_CONTRIBUTOR.setCreatedBy("Doe, J");
		NEW_OUTSIDE_CONTRIBUTOR.setModified("2013-08-21T11:37:08");
		NEW_OUTSIDE_CONTRIBUTOR.setModifiedBy("Doe, J");
	}

	public static final Author NEW_OUTSIDE_CONTRIBUTOR_NO_AFFILIATION = new Author();
	static {
		NEW_OUTSIDE_CONTRIBUTOR_NO_AFFILIATION.setIpNumber("IP-0509271");
		NEW_OUTSIDE_CONTRIBUTOR_NO_AFFILIATION.setAuthorName(null);
		NEW_OUTSIDE_CONTRIBUTOR_NO_AFFILIATION.setAuthorNameText("ODoul, Jane Q");
		NEW_OUTSIDE_CONTRIBUTOR_NO_AFFILIATION.setOrcid(null);
		NEW_OUTSIDE_CONTRIBUTOR_NO_AFFILIATION.setCostCenter(null);
		NEW_OUTSIDE_CONTRIBUTOR_NO_AFFILIATION.setContributorRole("1");
		NEW_OUTSIDE_CONTRIBUTOR_NO_AFFILIATION.setNonUSGSAffiliation(null);
		NEW_OUTSIDE_CONTRIBUTOR_NO_AFFILIATION.setNonUSGSContributor("ODoul, Jane Q");
		NEW_OUTSIDE_CONTRIBUTOR_NO_AFFILIATION.setRank("1");
		NEW_OUTSIDE_CONTRIBUTOR_NO_AFFILIATION.setCreated("2013-08-21T11:36:28");
		NEW_OUTSIDE_CONTRIBUTOR_NO_AFFILIATION.setCreatedBy("Doe, J");
		NEW_OUTSIDE_CONTRIBUTOR_NO_AFFILIATION.setModified("2013-08-21T11:37:08");
		NEW_OUTSIDE_CONTRIBUTOR_NO_AFFILIATION.setModifiedBy("Doe, J");
	}

	public static final Author EXISTING_OUTSIDE_CONTRIBUTOR = new Author();
	static {
		EXISTING_OUTSIDE_CONTRIBUTOR.setIpNumber("IP-050927");
		EXISTING_OUTSIDE_CONTRIBUTOR.setAuthorName("Doe, Jane M.");
		EXISTING_OUTSIDE_CONTRIBUTOR.setAuthorNameText("Doe, Jane M.");
		EXISTING_OUTSIDE_CONTRIBUTOR.setOrcid("http://orcid.org/0000-0000-0000-0001");
		EXISTING_OUTSIDE_CONTRIBUTOR.setCostCenter(null);
		EXISTING_OUTSIDE_CONTRIBUTOR.setContributorRole("1");
		EXISTING_OUTSIDE_CONTRIBUTOR.setNonUSGSAffiliation("Outer Affiliation 1");
		EXISTING_OUTSIDE_CONTRIBUTOR.setNonUSGSContributor("outerFamily, outerGiven");
		EXISTING_OUTSIDE_CONTRIBUTOR.setRank("1");
		EXISTING_OUTSIDE_CONTRIBUTOR.setCreated("2013-08-21T11:36:28");
		EXISTING_OUTSIDE_CONTRIBUTOR.setCreatedBy("Doe, J");
		EXISTING_OUTSIDE_CONTRIBUTOR.setModified("2013-08-21T11:37:08");
		EXISTING_OUTSIDE_CONTRIBUTOR.setModifiedBy("Doe, J");
	}

	public static final Author USGS_CONTRIBUTOR = new Author();
	static {
		USGS_CONTRIBUTOR.setIpNumber("IP-050927");
		USGS_CONTRIBUTOR.setAuthorName("Doe, Jane");
		USGS_CONTRIBUTOR.setAuthorNameText("Doe, Jane");
		USGS_CONTRIBUTOR.setOrcid("http://orcid.org/0000-0000-0000-0000");
		USGS_CONTRIBUTOR.setCostCenter("Affiliation Cost Center 1");
		USGS_CONTRIBUTOR.setContributorRole("1");
		USGS_CONTRIBUTOR.setNonUSGSAffiliation(null);
		USGS_CONTRIBUTOR.setNonUSGSContributor(null);
		USGS_CONTRIBUTOR.setRank("1");
		USGS_CONTRIBUTOR.setCreated("2013-07-01T09:27:49");
		USGS_CONTRIBUTOR.setCreatedBy("Doe, J");
		USGS_CONTRIBUTOR.setModified("2014-07-25T04:01:01");
		USGS_CONTRIBUTOR.setModifiedBy("Doe, J");
	}

	public static final Author USGS_CONTRIBUTOR_NO_COST_CENTER = new Author();
	static {
		USGS_CONTRIBUTOR_NO_COST_CENTER.setIpNumber("IP-050927");
		USGS_CONTRIBUTOR_NO_COST_CENTER.setAuthorName("Doe, Jane N.");
		USGS_CONTRIBUTOR_NO_COST_CENTER.setAuthorNameText("Doe, Jane N.");
		USGS_CONTRIBUTOR_NO_COST_CENTER.setOrcid("http://orcid.org/0000-0000-0080-0000");
		USGS_CONTRIBUTOR_NO_COST_CENTER.setCostCenter(null);
		USGS_CONTRIBUTOR_NO_COST_CENTER.setContributorRole("1");
		USGS_CONTRIBUTOR_NO_COST_CENTER.setNonUSGSAffiliation(null);
		USGS_CONTRIBUTOR_NO_COST_CENTER.setNonUSGSContributor(null);
		USGS_CONTRIBUTOR_NO_COST_CENTER.setRank("1");
		USGS_CONTRIBUTOR_NO_COST_CENTER.setCreated("2013-07-01T09:27:49");
		USGS_CONTRIBUTOR_NO_COST_CENTER.setCreatedBy("Doe, J");
		USGS_CONTRIBUTOR_NO_COST_CENTER.setModified("2014-07-25T04:01:01");
		USGS_CONTRIBUTOR_NO_COST_CENTER.setModifiedBy("Doe, J");
	}

	public static final Author ONE = new Author();
	static {
		ONE.setIpNumber("IP-050927");
		ONE.setAuthorName("con@usgs.gov");
		ONE.setAuthorNameText("1outerFamily, 1outerGiven");
		ONE.setOrcid("http://orcid.org/0000-0000-0000-0001");
		ONE.setCostCenter(null);
		ONE.setContributorRole("1");
		ONE.setNonUSGSAffiliation("Outer Affiliation 1");
		ONE.setNonUSGSContributor("1outerFamily, 1outerGiven");
		ONE.setRank("1");
		ONE.setCreated("2013-08-21T11:36:28");
		ONE.setCreatedBy("Doe, J");
		ONE.setModified("2013-08-21T11:37:08");
		ONE.setModifiedBy("Doe, J");
	}


	public static final Author FOUR = new Author();
	static {
		FOUR.setIpNumber("IP-050927");
		FOUR.setAuthorName("4Family, 4Given");
		FOUR.setAuthorNameText("4Family, 4Given");
		FOUR.setOrcid("http://orcid.org/0000-0000-0000-0004");
		FOUR.setCostCenter("xAffiliation Cost Center 4");
		FOUR.setContributorRole("2");
		FOUR.setNonUSGSAffiliation(null);
		FOUR.setNonUSGSContributor(null);
		FOUR.setRank("1");
		FOUR.setCreated("2013-08-21T11:36:28");
		FOUR.setCreatedBy("Doe, J");
		FOUR.setModified("2013-08-21T11:37:08");
		FOUR.setModifiedBy("Doe, J");
	}


	public static final Author FIVE = new Author();
	static {
		FIVE.setIpNumber("IP-050927");
		FIVE.setAuthorName("con5@usgs.gov");
		FIVE.setAuthorNameText("5outerFamily, 5outerGiven");
		FIVE.setOrcid("http://orcid.org/0000-0000-0000-0001");
		FIVE.setCostCenter(null);
		FIVE.setContributorRole("1");
		FIVE.setNonUSGSAffiliation("Outer Affiliation 1");
		FIVE.setNonUSGSContributor("5outerFamily, 5outerGiven");
		FIVE.setRank("1");
		FIVE.setCreated("2013-08-21T11:36:28");
		FIVE.setCreatedBy("Doe, J");
		FIVE.setModified("2013-08-21T11:37:08");
		FIVE.setModifiedBy("Doe, J");
	}


	public static final Author SIX = new Author();
	static {
		SIX.setIpNumber("IP-050927");
		SIX.setAuthorName(null);
		SIX.setAuthorNameText("6outerFamily, 6outerGiven");
		SIX.setOrcid("http://orcid.org/0000-0000-0000-0006");
		SIX.setCostCenter(null);
		SIX.setContributorRole("2");
		SIX.setNonUSGSAffiliation("Outer Affiliation 1");
		SIX.setNonUSGSContributor("6outerFamily, 6outerGiven");
		SIX.setRank("1");
		SIX.setCreated("2013-08-21T11:36:28");
		SIX.setCreatedBy("Doe, J");
		SIX.setModified("2013-08-21T11:37:08");
		SIX.setModifiedBy("Doe, J");
	}


	public static final Author ONE_ZERO_ONE = new Author();
	static {
		ONE_ZERO_ONE.setIpNumber("IP-050927");
		ONE_ZERO_ONE.setAuthorName("con101@usgs.gov");
		ONE_ZERO_ONE.setAuthorNameText("101outerFamily, 101outerGiven");
		ONE_ZERO_ONE.setOrcid("http://orcid.org/0000-0000-0000-0001");
		ONE_ZERO_ONE.setCostCenter(null);
		ONE_ZERO_ONE.setContributorRole("1");
		ONE_ZERO_ONE.setNonUSGSAffiliation("Outer Affiliation 1");
		ONE_ZERO_ONE.setNonUSGSContributor("101outerFamily, 101outerGiven");
		ONE_ZERO_ONE.setRank("1");
		ONE_ZERO_ONE.setCreated("2013-08-21T11:36:28");
		ONE_ZERO_ONE.setCreatedBy("Doe, J");
		ONE_ZERO_ONE.setModified("2013-08-21T11:37:08");
		ONE_ZERO_ONE.setModifiedBy("Doe, J");
	}

	public static final Author ONE_ZERO_FOUR = new Author();
	static {
		ONE_ZERO_FOUR.setIpNumber("IP-050927");
		ONE_ZERO_FOUR.setAuthorName("104Family, 104Given");
		ONE_ZERO_FOUR.setAuthorNameText("104Family, 104Given");
		ONE_ZERO_FOUR.setOrcid("http://orcid.org/0000-0000-0000-0104");
		ONE_ZERO_FOUR.setCostCenter(null);
		ONE_ZERO_FOUR.setContributorRole("2");
		ONE_ZERO_FOUR.setNonUSGSAffiliation(null);
		ONE_ZERO_FOUR.setNonUSGSContributor(null);
		ONE_ZERO_FOUR.setRank("1");
		ONE_ZERO_FOUR.setCreated("2013-08-21T11:36:28");
		ONE_ZERO_FOUR.setCreatedBy("Doe, J");
		ONE_ZERO_FOUR.setModified("2013-08-21T11:37:08");
		ONE_ZERO_FOUR.setModifiedBy("Doe, J");
	}
}
