/**
 * Class to hold Query parameters used as a search filter in a http Get on a publication end point. This field will be populated by Spring
 * when calling the method in the controller.
 */
package gov.usgs.cida.pubs.domain;

import java.util.Arrays;

import io.swagger.annotations.Api;

@Api
public class PublicationFilterParams {
	private Boolean chorus;

	private String[] contributingOffice;

	private String[] contributor;

	private String[] orcid;

	private String[] doi;

	private Boolean hasDoi;

	private String endYear;

	private String g;

	private String global;

	private String[] indexId;

	private String[] ipdsId;

	private String[] listId;

	private String orderBy;

	private String[] prodId;

	private String[] pubAbstract;

	private String q;

	private String[] linkType;

	private String[] noLinkType;

	private String[] reportNumber;

	private String[] seriesName;

	private String startYear;

	private String[] subtypeName;

	private String[] title;

	private String[] typeName;

	private String[] year;

	public Boolean getChorus() {
		return chorus;
	}

	public void setChorus(Boolean chorus) {
		this.chorus = chorus;
	}

	public String[] getContributingOffice() {
		return contributingOffice;
	}

	public void setContributingOffice(String[] contributingOffice) {
		this.contributingOffice = contributingOffice;
	}

	public String[] getContributor() {
		return contributor;
	}

	public void setContributor(String[] contributor) {
		this.contributor = contributor;
	}

	public String[] getOrcid() {
		return orcid;
	}

	public void setOrcid(String[] orcid) {
		this.orcid = orcid;
	}

	public String[] getDoi() {
		return doi;
	}

	public void setDoi(String[] doi) {
		this.doi = doi;
	}

	public Boolean getHasDoi() {
		return hasDoi;
	}

	public void setHasDoi(Boolean hasDoi) {
		this.hasDoi = hasDoi;
	}

	public String getEndYear() {
		return endYear;
	}

	public void setEndYear(String endYear) {
		this.endYear = endYear;
	}

	public String getG() {
		return g;
	}

	public void setG(String g) {
		this.g = g;
	}

	public String getGlobal() {
		return global;
	}

	public void setGlobal(String global) {
		this.global = global;
	}

	public String[] getIndexId() {
		return indexId;
	}

	public void setIndexId(String[] indexId) {
		this.indexId = indexId;
	}

	public String[] getIpdsId() {
		return ipdsId;
	}

	public void setIpdsId(String[] ipdsId) {
		this.ipdsId = ipdsId;
	}

	public String[] getListId() {
		return listId;
	}

	public void setListId(String[] listId) {
		this.listId = listId;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String[] getProdId() {
		return prodId;
	}

	public void setProdId(String[] prodId) {
		this.prodId = prodId;
	}

	public String[] getPubAbstract() {
		return pubAbstract;
	}

	public void setPubAbstract(String[] pubAbstract) {
		this.pubAbstract = pubAbstract;
	}

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
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

	public String[] getReportNumber() {
		return reportNumber;
	}

	public void setReportNumber(String[] reportNumber) {
		this.reportNumber = reportNumber;
	}

	public String[] getSeriesName() {
		return seriesName;
	}

	public void setSeriesName(String[] seriesName) {
		this.seriesName = seriesName;
	}

	public String getStartYear() {
		return startYear;
	}

	public void setStartYear(String startYear) {
		this.startYear = startYear;
	}

	public String[] getSubtypeName() {
		return subtypeName;
	}

	public void setSubtypeName(String[] subtypeName) {
		this.subtypeName = subtypeName;
	}

	public String[] getTitle() {
		return title;
	}

	public void setTitle(String[] title) {
		this.title = title;
	}

	public String[] getTypeName() {
		return typeName;
	}

	public void setTypeName(String[] typeName) {
		this.typeName = typeName;
	}

	public String[] getYear() {
		return year;
	}

	public void setYear(String[] year) {
		this.year = year;
	}

	@Override
	public String toString() {
		return "PublicationFilterParams [chorus=" + chorus + ", contributingOffice="
				+ Arrays.toString(contributingOffice) + ", contributor=" + Arrays.toString(contributor) + ", orcid="
				+ Arrays.toString(orcid) + ", doi=" + Arrays.toString(doi) + ", hasDoi=" + hasDoi + ", endYear="
				+ endYear + ", g=" + g + ", global=" + global + ", indexId=" + Arrays.toString(indexId) + ", ipdsId="
				+ Arrays.toString(ipdsId) + ", listId=" + Arrays.toString(listId) + ", orderBy=" + orderBy + ", prodId="
				+ Arrays.toString(prodId) + ", pubAbstract=" + Arrays.toString(pubAbstract) + ", q=" + q + ", linkType="
				+ Arrays.toString(linkType) + ", noLinkType=" + Arrays.toString(noLinkType) + ", reportNumber="
				+ Arrays.toString(reportNumber) + ", seriesName=" + Arrays.toString(seriesName) + ", startYear="
				+ startYear + ", subtypeName=" + Arrays.toString(subtypeName) + ", title=" + Arrays.toString(title)
				+ ", typeName=" + Arrays.toString(typeName) + ", year=" + Arrays.toString(year) + "]";
	}

}