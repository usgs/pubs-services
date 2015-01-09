package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.domain.intfc.ILookup;

public enum Predicate implements ILookup {

    DO_NOT_USE (0, "doNotUse"), 
    IS_PART_OF (1, "isPartOf"), 
    SUPERSEDED_BY (2, "supersededBy");

    private Integer id;
    
    private String text;

    Predicate(Integer inId, String inText) {
        id = inId;
        text = inText;
    }

    public Integer getId() {
        return id;
    }

    public String getText() {
        return text;
    }

}
