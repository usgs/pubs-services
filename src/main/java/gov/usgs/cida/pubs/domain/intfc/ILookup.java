package gov.usgs.cida.pubs.domain.intfc;

import gov.usgs.cida.pubs.json.view.intfc.ILookupView;
import gov.usgs.cida.pubs.json.view.intfc.IPwView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;

@JsonPropertyOrder({"id", "text"})
public interface ILookup {

	@JsonProperty("text")
	@JsonView({ILookupView.class, IPwView.class})
    String getText();

}
