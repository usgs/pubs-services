package gov.usgs.cida.pubs.domain.query;

import java.util.Arrays;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;

@Api
public class PersonContributorFilterParams {
	private Integer[] id;

	private Boolean corporation;

	private Boolean usgs;

	private String[] familyName;

	private String[] givenName;

	private String[] email;

	@ApiParam("The ORCID(s) to search for. The http/https prefix is optional. Example: 0000-0002-1825-0097")
	private String[] orcid;

	@ApiParam("If provided, will limit to either preferred or not preferred contributor information.")
	private Boolean preferred;

	@ApiParam("A 'contains' search value which if provided, must be at least 2 characters long.")
	private String[] text;

	public Integer[] getId() {
		return id;
	}

	public void setId(Integer[] id) {
		this.id = id;
	}

	public Boolean getCorporation() {
		return corporation;
	}

	public Boolean isCorporation() {
		return corporation;
	}

	public void setCorporation(Boolean corporation) {
		this.corporation = corporation;
	}

	public Boolean getUsgs() {
		return usgs;
	}

	public Boolean isUsgs() {
		return usgs;
	}

	public void setUsgs(Boolean usgs) {
		this.usgs = usgs;
	}

	public String[] getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String[] familyName) {
		this.familyName = familyName;
	}

	public String[] getGivenName() {
		return givenName;
	}

	public void setGivenName(String[] givenName) {
		this.givenName = givenName;
	}

	public String[] getEmail() {
		return email;
	}

	public void setEmail(String[] email) {
		this.email = email;
	}

	public String[] getOrcid() {
		return orcid;
	}

	public void setOrcid(String[] orcid) {
		this.orcid = orcid;
	}

	public Boolean getPreferred() {
		return preferred;
	}

	public void setPreferred(Boolean preferred) {
		this.preferred = preferred;
	}

	public String[] getText() {
		return text;
	}

	public void setText(String[] text) {
		this.text = text;
	}
	
	public boolean hasParamSet() {
		return id != null || corporation != null || usgs != null || familyName != null ||
				givenName != null || email != null || orcid != null || preferred != null || text != null;
	}

	@Override
	public String toString() {
		return "PersonContributorFilterParams [id=" + Arrays.toString(id) + ", corporation=" + corporation + ", usgs="
				+ usgs + ", familyName=" + Arrays.toString(familyName) + ", givenName=" + Arrays.toString(givenName)
				+ ", email=" + Arrays.toString(email) + ", orcid=" + Arrays.toString(orcid) + ", preferred=" + preferred
				+ ", text=" + Arrays.toString(text) + "]";
	}

}