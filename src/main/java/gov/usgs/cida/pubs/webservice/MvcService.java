package gov.usgs.cida.pubs.webservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;

import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.dao.CorporateContributorDao;
import gov.usgs.cida.pubs.dao.PersonContributorDao;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDao;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDao;
import gov.usgs.cida.pubs.domain.query.PersonContributorFilterParams;
import gov.usgs.cida.pubs.domain.query.PublicationFilterParams;
import gov.usgs.cida.pubs.utility.DataNormalizationUtils;
import gov.usgs.cida.pubs.utility.PubsUtils;

public abstract class MvcService<D> {

	public static final String TEXT_SEARCH = BaseDao.TEXT_SEARCH;
	public static final String ACTIVE_SEARCH = "active";
	public static final String TEXT_SEARCH_STARTS_WITH_SUFFIX = ":*";
	public static final String TEXT_SEARCH_AND = " & ";

	private static final Pattern G_PATTERN = Pattern.compile("^polygon\\(\\((-?\\d+\\.?\\d* -?\\d+\\.?\\d*,){3,}-?\\d+\\.?\\d* -?\\d+\\.?\\d*\\)\\)$");

	protected Map<String, Object> buildFilters(PublicationFilterParams filterParams) {
		Map<String, Object> filters = new HashMap<>();

		filters.put(PublicationDao.PUB_ABSTRACT, filterParams.getPubAbstract());
		filters.put(PwPublicationDao.CHORUS, filterParams.getChorus());
		filters.put(PublicationDao.CONTRIBUTING_OFFICE, filterParams.getContributingOffice());
		filters.put(PublicationDao.CONTRIBUTOR, configureContributorFilter(filterParams.getContributor()));
		filters.put(PublicationDao.ORCID, DataNormalizationUtils.normalizeOrcid(filterParams.getOrcid()));
		filters.put(PublicationDao.DOI, filterParams.getDoi());
		filters.put(PublicationDao.HAS_DOI, filterParams.getHasDoi());
		filters.put(PublicationDao.END_YEAR, filterParams.getEndYear());
		if (null != filterParams.getG() && G_PATTERN.matcher(filterParams.getG().toLowerCase()).matches()) {
			filters.put(PwPublicationDao.G, filterParams.getG().toLowerCase());
		}
		filters.put(MpPublicationDao.GLOBAL, filterParams.getGlobal());
		filters.put(PublicationDao.INDEX_ID, filterParams.getIndexId());
		filters.put(PublicationDao.IPDS_ID, filterParams.getIpdsId());
		filters.put(MpPublicationDao.LIST_ID, null == filterParams.getListId() ? null : mapListId(filterParams.getListId()));
		filters.put(PublicationDao.ORDER_BY, filterParams.getOrderBy());
		filters.put(PublicationDao.PROD_ID, filterParams.getProdId());
		filters.putAll(configureSingleSearchFilters(filterParams.getQ()));
		filters.put(PublicationDao.LINK_TYPE, filterParams.getLinkType());
		filters.put(PublicationDao.NO_LINK_TYPE, filterParams.getNoLinkType());
		filters.put(PublicationDao.REPORT_NUMBER, filterParams.getReportNumber());
		filters.put(PublicationDao.SERIES_NAME, filterParams.getSeriesName());
		filters.put(PublicationDao.START_YEAR, filterParams.getStartYear());
		filters.put(PublicationDao.SUBTYPE_NAME, filterParams.getSubtypeName());
		filters.put(PublicationDao.TITLE, filterParams.getTitle());
		filters.put(PublicationDao.TYPE_NAME, filterParams.getTypeName());
		filters.put(PublicationDao.YEAR, filterParams.getYear());

		return filters;
	}

	protected Map<String, Object> buildFilters(PersonContributorFilterParams filterParams) {
		Map<String, Object> filters = new HashMap<>();

		filters.put(BaseDao.ID_SEARCH,filterParams.getId());
		filters.put(BaseDao.TEXT_SEARCH, configureContributorFilter(filterParams.getText()));
		filters.put(CorporateContributorDao.CORPORATION,filterParams.getCorporation());
		filters.put(PersonContributorDao.USGS,filterParams.getUsgs());
		filters.put(PersonContributorDao.FAMILY,filterParams.getFamilyName());
		filters.put(PersonContributorDao.GIVEN,filterParams.getGivenName());
		filters.put(PersonContributorDao.EMAIL,filterParams.getEmail());
		filters.put(PersonContributorDao.ORCID,DataNormalizationUtils.normalizeOrcid(filterParams.getOrcid()));
		filters.put(PersonContributorDao.PREFERRED,filterParams.getPreferred());

		return filters;
	}

	protected Map<String, Object> buildFilters(String modDateHigh, String modDateLow, String modXDays,
			String pubDateHigh, String pubDateLow, String pubXDays) {
		Map<String, Object> filters = new HashMap<>();

		filters.put(PwPublicationDao.MOD_DATE_HIGH, modDateHigh);
		filters.put(PwPublicationDao.MOD_DATE_LOW, modDateLow);
		filters.put(PwPublicationDao.MOD_X_DAYS, modXDays);

		filters.put(PwPublicationDao.PUB_DATE_HIGH, pubDateHigh);
		filters.put(PwPublicationDao.PUB_DATE_LOW, pubDateLow);
		filters.put(PwPublicationDao.PUB_X_DAYS, pubXDays);

		return filters;
	}

	protected Integer[] mapListId(String[] listId) {
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

	protected Map<String, Object> configureSingleSearchFilters(String searchTerms) {
		Map<String, Object> rtn = new HashMap<>();
		//On the MP side, We split the input on spaces and commas to ultimately create an "and" query on each word
		//On the warehouse side, we are doing Text queries
		if (StringUtils.isNotBlank(searchTerms)) {
			List<String> splitTerms = Arrays.stream(searchTerms.trim().toLowerCase().split(PubsConstantsHelper.SEARCH_TERMS_SPLIT_REGEX))
					.filter(x -> StringUtils.isNotEmpty(x))
					.collect(Collectors.toList());
			if (!splitTerms.isEmpty()) {
				rtn.put(MpPublicationDao.SEARCH_TERMS, buildSearchTerms(splitTerms));

				rtn.put(PublicationDao.Q, buildQ(splitTerms));
			}
		}
		return rtn;
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
				newList.add(term + TEXT_SEARCH_STARTS_WITH_SUFFIX);
			}
		}
		return StringUtils.join(newList, TEXT_SEARCH_AND);
	}

	protected String configureContributorFilter(String[] text) {
		//This is overly complicated because any comma that the user enters in the search box is interpreted as an
		//addition parameter, rather than as a comma - for instance "carvin, rebecca" ends up as {"carvin", " rebecca"}
		String rtn = null;
		List<String> values = new ArrayList<String>();
		if (null != text && 0 < text.length) {
			//Arrays.asList returns a fixed size java.util.Arrays$ArrayList, so we actually need to create a real list to 
			//be able to add entries to it.
			List<String> textList = new LinkedList<>(Arrays.asList(text));
			for (String temp : textList) {
				if (null != temp) {
					List<String> splitTerms = Arrays.asList(temp.trim().toLowerCase().split(" "));
					Iterator<String> i = splitTerms.iterator(); 
					while (i.hasNext()) {
						String term = i.next();
						if (StringUtils.isNotBlank(term)) {
							values.add(term + TEXT_SEARCH_STARTS_WITH_SUFFIX);
						}
					}					
				}
			}
			if (!values.isEmpty()) {
				rtn = StringUtils.join(values, TEXT_SEARCH_AND);
			}
		}
		return rtn;
	}

	protected Map<String, Object> buildPaging (String inPageRowStart, String inPageSize, String inPageNumber) {
		Integer pageRowStart = PubsUtils.parseInteger(inPageRowStart);
		Integer pageSize = PubsUtils.parseInteger(inPageSize);
		Integer pageNumber = PubsUtils.parseInteger(inPageNumber);
		Map<String, Object> paging = new HashMap<>();
		if (null != pageNumber) {
			//pageNumber overrides the pageRowStart
			if (null == pageSize) {
				//default pageSize with pageNumber
				pageSize = 25;
			}
			pageRowStart = ((pageNumber - 1) * pageSize);
		} else {
			if (null == pageRowStart) {
				pageRowStart = 0;
			}
			if (null == pageSize) {
				//default pageSize with pageRowStart
				pageSize = 15;
			}
		}
		paging.put(BaseDao.PAGE_ROW_START, pageRowStart);
		paging.put(BaseDao.PAGE_SIZE, pageSize);
		paging.put(BaseDao.PAGE_NUMBER, pageNumber);
		return paging;
	}

	//This check is meant to slow any denial-of-service attacks against insecure ("permitAll") endpoints (see the securityContext.xml).
	//It should not be necessary to use it on any other endpoints. 
	protected boolean validateParametersSetHeaders(HttpServletRequest request, HttpServletResponse response) {
		boolean rtn = true;
		setHeaders(response);
		if (request.getParameterMap().isEmpty()
				&& ObjectUtils.isEmpty(request.getHeader(PubsConstantsHelper.ACCEPT_HEADER))) {
			rtn = false;
			response.setStatus(HttpStatus.BAD_REQUEST.value());
		}
		return rtn;
	}

	protected void setHeaders(HttpServletResponse response) {
		response.setCharacterEncoding(PubsConstantsHelper.DEFAULT_ENCODING);
	}

	public static String formatHtmlErrMess(String message) {
		StringBuilder sb = new StringBuilder();
		sb.append("<h3>");
		sb.append(message.replace("\n", "<br>")); // replace newlines with Html Line Break element
		sb.append("</h3>");

		return sb.toString();
	}

}