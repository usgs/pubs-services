package gov.usgs.cida.pubs.domain;

import java.io.Serializable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonView;

import gov.usgs.cida.pubs.json.View;
import gov.usgs.cida.pubs.utility.PubsUtilities;
import gov.usgs.cida.pubs.validation.constraint.ParentExists;
import gov.usgs.cida.pubs.validation.constraint.UniqueKey;

@UniqueKey(message = "{publication.indexid.duplicate}")
@ParentExists
public class PublicationCostCenter<D> extends BaseDomain<D> implements Serializable {
	private static final long serialVersionUID = -1839682568695179903L;

	@JsonIgnore
	private Integer publicationId;

	@JsonView(View.PW.class)
	@JsonUnwrapped
	@NotNull
	@Valid
	private CostCenter costCenter;

	public Integer getPublicationId() {
		return publicationId;
	}

	public void setPublicationId(final Integer inPublicationId) {
		publicationId = inPublicationId;
	}

	public void setPublicationId(final String inPublicationId) {
		publicationId = PubsUtilities.parseInteger(inPublicationId);
	}

	public CostCenter getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(final CostCenter inCostCenter) {
		costCenter = inCostCenter;
	}

	//We don't want the publicationCostCenter.id in the json view
	@JsonIgnore
	@Override
	public Integer getId() {
		return id;
	}

	//We also don't want to deserialize the costCenter.id to the publicationCostCenter.id
	@JsonIgnore
	@Override
	public void setId(final String inId) {
		id = PubsUtilities.parseInteger(inId);
	}
}
