package gov.usgs.cida.pubs.domain.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.utility.DataNormalizationUtils;
import gov.usgs.cida.pubs.utility.PubsUtils;
import gov.usgs.cida.pubs.webservice.MvcService;
import io.swagger.annotations.Api;
import springfox.documentation.annotations.ApiIgnore;

@Api
public abstract class PublicationFilterParams implements IFilterParams {
	private String[] contributingOffice;
	private String[] contributor;
	private String[] doi;
	private String endYear;
	private Boolean hasDoi;
	private String[] indexId;
	private String[] ipdsId;
	private String[] listId;
	private String mimeType;
	private String[] orcid;
	private String orderBy;
	private String pageNumber;
	private String pageRowStart;
	private String pageSize;
	private String[] prodId;
	private String[] pubAbstract;
	private String q;
	private String[] reportNumber;
	private String[] searchTerms;
	private String[] seriesName;
	private String startYear;
	private String[] subtypeName;
	private String[] title;
	private String[] typeName;
	private String[] year;

	public String[] getContributingOffice() {
		return contributingOffice;
	}
	public void setContributingOffice(String[] contributingOffice) {
		this.contributingOffice = contributingOffice;
	}
	public String getContributor() {
		//This is overly complicated because any comma that the user enters in the search box is interpreted as an
		//addition parameter, rather than as a comma - for instance "carvin, rebecca" ends up as {"carvin", " rebecca"}
		String rtn = null;
		List<String> values = new ArrayList<String>();
		if (null != contributor && 0 < contributor.length) {
			//Arrays.asList returns a fixed size java.util.Arrays$ArrayList, so we actually need to create a real list to 
			//be able to add entries to it.
			List<String> textList = new LinkedList<>(Arrays.asList(contributor));
			for (String temp : textList) {
				if (null != temp) {
					List<String> splitTerms = Arrays.asList(temp.trim().toLowerCase().split(" "));
					Iterator<String> i = splitTerms.iterator(); 
					while (i.hasNext()) {
						String term = i.next();
						if (StringUtils.isNotBlank(term)) {
							values.add(term + MvcService.TEXT_SEARCH_STARTS_WITH_SUFFIX);
						}
					}					
				}
			}
			if (!values.isEmpty()) {
				rtn = StringUtils.join(values, MvcService.TEXT_SEARCH_AND);
			}
		}
		return rtn;
	}
	public void setContributor(String[] contributor) {
		this.contributor = contributor;
	}
	public String[] getDoi() {
		return doi;
	}
	public void setDoi(String[] doi) {
		this.doi = doi;
	}
	public String getEndYear() {
		return endYear;
	}
	public void setEndYear(String endYear) {
		this.endYear = endYear;
	}
	public Boolean getHasDoi() {
		return hasDoi;
	}
	public void setHasDoi(Boolean hasDoi) {
		this.hasDoi = hasDoi;
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
	public Integer[] getListId() {
		if (null == listId) {
			return null;
		}
		Integer[] listIdInt = new Integer[listId.length];
		int index = 0;
		for (int i=0; i < listId.length; i++) {
			Integer x = PubsUtils.parseInteger(listId[i]);
			if (null != x) {
				listIdInt[index] = x;
				index++;
			}
		}
		return Arrays.copyOf(listIdInt, index);
	}
	public void setListId(String[] listId) {
		this.listId = listId;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	//Take all lowercase too
	public void setMimetype(String mimeType) {
		this.mimeType = mimeType;
	}
	public String[] getOrcid() {
		return DataNormalizationUtils.normalizeOrcid(orcid);
	}
	public void setOrcid(String[] orcid) {
		this.orcid = orcid;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	//This next set is not camel case to match the lower/underscore or the parameter.
	public Integer getPage_number() {
		if (PubsUtils.isInteger(pageNumber)) {
			return PubsUtils.parseInteger(pageNumber);
		} else {
			return (getPage_row_start() / getPage_size()) + 1;
		}
	}
	public void setPage_number(String pageNumber) {
		this.pageNumber = pageNumber;
	}
	public Integer getPage_row_start() {
		if (null != pageNumber) {
			return ((getPage_number() - 1) * getPage_size());
		} else {
			return NumberUtils.toInt(pageRowStart, 0);
		}
	}
	public void setPage_row_start(String pageRowStart) {
		this.pageRowStart = pageRowStart;
	}
	public Integer getPage_size() {
		if (this.getClass().getSimpleName().equalsIgnoreCase(PwPublicationFilterParams.class.getSimpleName())) {
			return NumberUtils.toInt(pageSize, 15);
		} else {
			return NumberUtils.toInt(pageSize, 25);
		}
	}
	public void setPage_size(String pageSize) {
		this.pageSize = pageSize;
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
	public String[] getAbstract() {
		return pubAbstract;
	}
	public void setPubAbstract(String[] pubAbstract) {
		this.pubAbstract = pubAbstract;
	}
	public void setAbstract(String[] pubAbstract) {
		this.pubAbstract = pubAbstract;
	}
	public String getQ() {
		return q;
	}
	public void setQ(String q) {
		//On the MP side, We split the input on spaces and commas to ultimately create an "and" query on each word
		//On the warehouse side, we are doing Text queries
		if (StringUtils.isNotBlank(q)) {
			List<String> splitTerms = Arrays.stream(q.trim().toLowerCase().split(PubsConstantsHelper.SEARCH_TERMS_SPLIT_REGEX))
					.filter(x -> StringUtils.isNotEmpty(x))
					.collect(Collectors.toList());
			if (!splitTerms.isEmpty()) {
				this.searchTerms = buildSearchTerms(splitTerms);

				this.q = buildQ(splitTerms);
			}
		}
	}
	public String[] getReportNumber() {
		return reportNumber;
	}
	public void setReportNumber(String[] reportNumber) {
		this.reportNumber = reportNumber;
	}
	@ApiIgnore
	public String[] getSearchTerms() {
		return searchTerms;
	}
	@ApiIgnore
	public void setSearchTerms(String[] searchTerms) {
		this.searchTerms = searchTerms;
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

	protected String[] buildSearchTerms(List<String> splitTerms) {
		List<String> newList = new LinkedList<>();
		if (null != splitTerms) {
			Iterator<String> i = splitTerms.iterator(); 
			while (i.hasNext()) {
				String term = i.next();
				if (StringUtils.isNotBlank(term)) {
					newList.add(term);
				}
			}
		}
		if (newList.isEmpty()) {
			return null;
		} else {
			return newList.toArray(new String[newList.size()]);
		}
	}

	protected String buildQ(List<String> splitTerms) {
		//The context search should suffix each term with TEXT_SEARCH_STARTS_WITH_SUFFIX and join them with TEXT_SEARCH_AND.
		//This gives us a "stem" search with the logical "and" applied to the terms. 
		if (null == splitTerms || splitTerms.isEmpty()) {
			return null;
		}
		List<String> newList = new LinkedList<>();
		Iterator<String> i = splitTerms.iterator(); 
		while (i.hasNext()) {
			String term = i.next();
			if (StringUtils.isNotBlank(term)) {
				newList.add(term + MvcService.TEXT_SEARCH_STARTS_WITH_SUFFIX);
			}
		}
		return StringUtils.join(newList, MvcService.TEXT_SEARCH_AND);
	}

	@Override
	public String toString() {
		return "PublicationFilterParams [contributingOffice=" + Arrays.toString(getContributingOffice())
				+ ", contributor=" + getContributor()
				+ ", doi=" + Arrays.toString(getDoi())
				+ ", endYear=" + getEndYear()
				+ ", hasDoi=" + getHasDoi()
				+ ", indexId=" + Arrays.toString(getIndexId())
				+ ", ipdsId=" + Arrays.toString(getIpdsId())
				+ ", listId=" + Arrays.toString(getListId())
				+ ", mimeType=" + getMimeType()
				+ ", orcid=" + Arrays.toString(getOrcid())
				+ ", orderBy=" + getOrderBy()
				+ ", pageNumber=" + getPage_number()
				+ ", pageRowStart=" + getPage_row_start()
				+ ", pageSize=" + getPage_number()
				+ ", prodId=" + Arrays.toString(getProdId())
				+ ", pubAbstract=" + Arrays.toString(getPubAbstract())
				+ ", q=" + getQ()
				+ ", reportNumber=" + Arrays.toString(getReportNumber())
				+ ", seriesName=" + Arrays.toString(getSeriesName())
				+ ", startYear=" + getStartYear()
				+ ", subtypeName=" + Arrays.toString(getSubtypeName())
				+ ", title=" + Arrays.toString(getTitle())
				+ ", typeName=" + Arrays.toString(getTypeName())
				+ ", year=" + Arrays.toString(getYear())
				+ "]";
	}
}