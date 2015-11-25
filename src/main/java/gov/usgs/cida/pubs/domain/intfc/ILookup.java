package gov.usgs.cida.pubs.domain.intfc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;

import gov.usgs.cida.pubs.json.View;

@JsonPropertyOrder({"id", "text"})
public interface ILookup {

	@JsonProperty("text")
    @JsonView({View.Lookup.class, View.PW.class})
    String getText();

}
