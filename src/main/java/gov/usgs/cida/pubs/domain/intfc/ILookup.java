package gov.usgs.cida.pubs.domain.intfc;

import gov.usgs.cida.pubs.json.view.intfc.ILookupView;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;

@JsonPropertyOrder({"id", "text"})
public interface ILookup {

    @JsonView(ILookupView.class)
    String getText();

}
