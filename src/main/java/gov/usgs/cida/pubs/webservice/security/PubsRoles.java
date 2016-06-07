package gov.usgs.cida.pubs.webservice.security;

public enum PubsRoles {
	PUBS_ADMIN ("ROLE_PUBS_ADMIN"),
	PUBS_TAGGING_USER ("ROLE_PUBS_TAGGING_USER"),
	PUBS_SPN_USER ("ROLE_PUBS_SPN_USER"),
	PUBS_CATALOGER_USER ("ROLE_PUBS_CATALOGER_USER"),
	PUBS_SPN_SUPERVISOR ("ROLE_PUBS_SPN_SUPERVISOR"),
	PUBS_CATALOGER_SUPERVISOR ("ROLE_PUBS_CATALOGER_SUPERVISOR"),
	AD_AUTHENTICATED ("ROLE_AD_AUTHENTICATED"),
	PUBS_AUTHORIZED ("ROLE_PUBS_AUTHORIZED");

	private String springRole;

	PubsRoles(String inSpringRole) {
		springRole = inSpringRole;
	}

	public String getSpringRole() {
		return springRole;
	}
}
