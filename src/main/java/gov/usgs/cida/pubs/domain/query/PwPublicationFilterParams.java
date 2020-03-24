package gov.usgs.cida.pubs.domain.query;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import springfox.documentation.annotations.ApiIgnore;

public class PwPublicationFilterParams extends PublicationFilterParams {
	private static final Pattern G_PATTERN = Pattern.compile("^polygon\\(\\((-?\\d+\\.?\\d* -?\\d+\\.?\\d*,){3,}-?\\d+\\.?\\d* -?\\d+\\.?\\d*\\)\\)$");
	private static ConfigurationService configurationService;

	private Boolean chorus;
	private String g;
	private String[] linkType;
	private String[] noLinkType;
	private String pubXDays;
	private String pubDateLow;
	private String pubDateHigh;
	private String modXDays;
	private String modDateLow;
	private String modDateHigh;

	public Boolean getChorus() {
		return chorus;
	}
	public void setChorus(Boolean chorus) {
		this.chorus = chorus;
	}
	public String getG() {
		if (null != g && G_PATTERN.matcher(g.toLowerCase()).matches()) {
			return g.toLowerCase();
		} else {
			return null;
		}
	}
	public void setG(String g) {
		this.g = g;
	}
	public String[] getLinkType() {
		return linkType;
	}
	public void setLinkType(String[] linkType) {
		this.linkType = linkType;
	}
	public String[] getNoLinkType() {
		return noLinkType;
	}
	public void setNoLinkType(String[] noLinkType) {
		this.noLinkType = noLinkType;
	}
	//This next set is not camel case to match the lower/underscore or the parameter.
	public String getPub_x_days() {
		return pubXDays;
	}
	public void setPub_x_days(String pubXDays) {
		this.pubXDays = pubXDays;
	}
	public String getPub_date_low() {
		return pubDateLow;
	}
	public void setPub_date_low(String pubDateLow) {
		this.pubDateLow = pubDateLow;
	}
	public String getPub_date_high() {
		return pubDateHigh;
	}
	public void setPub_date_high(String pubDateHigh) {
		this.pubDateHigh = pubDateHigh;
	}
	public String getMod_x_days() {
		return modXDays;
	}
	public void setMod_x_days(String modXDays) {
		this.modXDays = modXDays;
	}
	public String getMod_date_low() {
		return modDateLow;
	}
	public void setMod_date_low(String modDateLow) {
		this.modDateLow = modDateLow;
	}
	public String getMod_date_high() {
		return modDateHigh;
	}
	public void setMod_date_high(String modDateHigh) {
		this.modDateHigh = modDateHigh;
	}
	@ApiIgnore
	public String getUrl() {
		return configurationService.getWarehouseEndpoint() + "/publication/";
	}
	//This next set is not camel case to match the lower/underscore or the parameter.
	@Override
	public Integer getPage_number() {
		if (jsonOutput()) {
			return super.getPage_number();
		} else {
			return null;
		}
	}
	@Override
	public Integer getPage_row_start() {
		if (jsonOutput()) {
			return super.getPage_row_start();
		} else {
			return null;
		}
	}
	@Override
	public Integer getPage_size() {
		if (jsonOutput()) {
			return super.getPage_size();
		} else {
			return null;
		}
	}

	protected boolean jsonOutput() {
		return PubsConstantsHelper.MEDIA_TYPE_JSON_EXTENSION.equalsIgnoreCase(getMimeType());
	}

	@Override
	public String toString() {
		return super.toString()
				+ " PwPublicationFilterParams [chorus=" + getChorus()
				+ ", g=" + getG()
				+ ", linkType=" + Arrays.toString(getLinkType())
				+ ", noLinkType=" + Arrays.toString(getNoLinkType())
				+ ", pubXDays=" + getPub_x_days()
				+ ", pubDateLow=" + getPub_date_low()
				+ ", pubDateHigh=" + getPub_date_high()
				+ ", modXDays=" + getMod_x_days()
				+ ", modDateLow=" + getMod_date_low()
				+ ", modDateHigh=" + getMod_date_high()
				+ ", url=" + getUrl()
				+ ", pageNumber=" + getPage_number()
				+ ", pageRowStart=" + getPage_row_start()
				+ ", pageSize=" + getPage_size()
				+ "]";
	}

	@Autowired
	public void setConfigurationService(final ConfigurationService inConfigurationService) {
		configurationService = inConfigurationService;
	}
}
