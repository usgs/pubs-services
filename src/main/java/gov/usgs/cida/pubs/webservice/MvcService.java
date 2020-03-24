package gov.usgs.cida.pubs.webservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;

import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.dao.CorporateContributorDao;
import gov.usgs.cida.pubs.dao.PersonContributorDao;
import gov.usgs.cida.pubs.domain.query.PersonContributorFilterParams;
import gov.usgs.cida.pubs.utility.DataNormalizationUtils;
import gov.usgs.cida.pubs.utility.PubsUtils;

public abstract class MvcService<D> {

	public static final String ACTIVE_SEARCH = "active";
	public static final String TEXT_SEARCH_STARTS_WITH_SUFFIX = ":*";
	public static final String TEXT_SEARCH_AND = " & ";

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