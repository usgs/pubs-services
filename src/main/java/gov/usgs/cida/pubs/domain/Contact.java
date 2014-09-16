package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.intfc.IDao;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "text", "addressLine1", "addressLine2", "addressLine3", "city", "state", "zipcode",
	"website", "linkText", "link"})
public class Contact extends BaseDomain<Contact> implements Serializable {

    private static final long serialVersionUID = 7112475559005537898L;

    private static IDao<Contact> contactDao;

    @JsonProperty("text")
    private String text;

    @JsonProperty("addressLine1")
    private String addressLine1;

    @JsonProperty("addressLine2")
    private String addressLine2;

    @JsonProperty("addressLine3")
    private String addressLine3;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("zipcode")
    private String zipcode;

    @JsonProperty("website")
    private String website;

    @JsonProperty("linkText")
    private String linkText;

    @JsonProperty("link")
    private String link;

    public String getText() {
        return text;
    }

    public void setText(final String inText) {
        text = inText;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(final String inAddressLine1) {
        addressLine1 = inAddressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(final String inAddressLine2) {
        addressLine2 = inAddressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(final String inAddressLine3) {
        addressLine3 = inAddressLine3;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String inCity) {
        city = inCity;
    }

    public String getState() {
        return state;
    }

    public void setState(final String inState) {
        state = inState;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(final String inZipcode) {
        zipcode = inZipcode;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(final String inWebsite) {
        website = inWebsite;
    }

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(final String inLinkText) {
        linkText = inLinkText;
    }

    public String getLink() {
        return link;
    }

    public void setLink(final String inLink) {
        link = inLink;
    }

    /**
     * @return the contactDao
     */
    public static IDao<Contact> getContactDao() {
        return contactDao;
    }

    /**
     * The setter for contactDao.
     * 
     * @param inContactDao the contactDao to set
     */
    public void setContactDao(final IDao<Contact> inContactDao) {
        contactDao = inContactDao;
    }

}
