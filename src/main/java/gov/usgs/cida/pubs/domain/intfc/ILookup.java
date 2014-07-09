package gov.usgs.cida.pubs.domain.intfc;

import com.fasterxml.jackson.annotation.JsonView;

public interface ILookup {

    @JsonView(LookupView.class)
    String getText();

    @JsonView(LookupView.class)
    String getValue();

    public static interface LookupView extends BaseView {}

}
