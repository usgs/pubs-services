package gov.usgs.cida.pubs.domain;

public enum ProcessType {

    DISSEMINATION ("Dissemination"),
    SPN_PRODUCTION ("SPN Production"), 
    COST_CENTER ("Cost Center");

    private String ipdsValue;

    ProcessType(String inIpdsValue) {
        ipdsValue = inIpdsValue;
    }

    public String getIpdsValue() {
        return ipdsValue;
    }

    public String getIpdsValueEncoded() {
        return ipdsValue.replace(" ", "%20");
    }

}
