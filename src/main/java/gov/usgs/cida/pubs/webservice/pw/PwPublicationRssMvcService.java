package gov.usgs.cida.pubs.webservice.pw;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.intfc.IPwPublicationBusService;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDao;
import gov.usgs.cida.pubs.domain.BaseDomain;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.CorporateContributor;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.utility.PubsUtilities;
import gov.usgs.cida.pubs.webservice.MvcService;

/**
 * Note: This service doesn't actually produce JSON, it directly writes XML to the output stream and
 * sets the content type appropriately. It is annotated to produce JSON because the app default is JSON and
 * Spring throws a 404 if this endpoint gets hit with no headers because it doesn't find a method to render the response.
 */
@RestController
@RequestMapping(value = "publication/rss", produces={PubsConstants.MEDIA_TYPE_RSS_VALUE, MediaType.APPLICATION_JSON_VALUE})
public class PwPublicationRssMvcService extends MvcService<PwPublication> {
	private static final String DEFAULT_RECORDS = "30";

	private static final Logger LOG = LoggerFactory.getLogger(PwPublicationRssMvcService.class);

	private final IPwPublicationBusService busService;
	private final String warehouseEndpoint;

	@Autowired
	public PwPublicationRssMvcService(@Qualifier("pwPublicationBusService")
			final IPwPublicationBusService busService,
			@Qualifier("warehouseEndpoint")
			final String warehouseEndpoint) {
		this.busService = busService;
		this.warehouseEndpoint = warehouseEndpoint;
	}

	@GetMapping
	public void getRSS(
			@RequestParam(value=PublicationDao.Q, required=false) String q,
			@RequestParam(value=PwPublicationDao.G, required=false) String g,
			@RequestParam(value=PublicationDao.TITLE, required=false) String[] title,
			@RequestParam(value=PublicationDao.PUB_ABSTRACT, required=false) String[] pubAbstract,
			@RequestParam(value=PublicationDao.CONTRIBUTOR, required=false) String[] contributor,
			@RequestParam(value=PublicationDao.ORCID, required=false) String[] orcid,
			@RequestParam(value=PublicationDao.PROD_ID, required=false) String[] prodId,
			@RequestParam(value=PublicationDao.INDEX_ID, required=false) String[] indexId,
			@RequestParam(value=PublicationDao.IPDS_ID, required=false) String[] ipdsId,
			@RequestParam(value=PublicationDao.YEAR, required=false) String[] year,
			@RequestParam(value=PublicationDao.START_YEAR, required=false) String startYear,
			@RequestParam(value=PublicationDao.END_YEAR, required=false) String endYear,
			@RequestParam(value=PublicationDao.CONTRIBUTING_OFFICE, required=false) String[] contributingOffice,
			@RequestParam(value=PublicationDao.TYPE_NAME, required=false) String[] typeName,
			@RequestParam(value=PublicationDao.SUBTYPE_NAME, required=false) String[] subtypeName,
			@RequestParam(value=PublicationDao.SERIES_NAME, required=false) String[] seriesName,
			@RequestParam(value=PublicationDao.REPORT_NUMBER, required=false) String[] reportNumber,
			@RequestParam(value=PublicationDao.LINK_TYPE, required=false) String[] linkType,
			@RequestParam(value=PublicationDao.NO_LINK_TYPE, required=false) String[] noLinkType,
			@RequestParam(value=PwPublicationDao.PUB_X_DAYS, required=false) String pubXDays,
			@RequestParam(value=PwPublicationDao.PUB_DATE_LOW, required=false) String pubDateLow,
			@RequestParam(value=PwPublicationDao.PUB_DATE_HIGH, required=false) String pubDateHigh,
			@RequestParam(value=PwPublicationDao.MOD_X_DAYS, required=false) String modXDays,
			@RequestParam(value=PwPublicationDao.MOD_DATE_LOW, required=false) String modDateLow,
			@RequestParam(value=PwPublicationDao.MOD_DATE_HIGH, required=false) String modDateHigh,
			@RequestParam(value=PublicationDao.ORDER_BY, required=false) String orderBy,
			@RequestParam(value=PwPublicationDao.CHORUS, required=false) Boolean chorus,
			HttpServletResponse response) {

		/**
		 * Per JIMK on JIRA PUBSTWO-971:
		 * 
		 * 	"if mod_x_days is there, and pub_x_days is not, mod_x_days should override the default pub_x_days = 30"
		 */
		if((pubXDays == null) || (pubXDays.isEmpty())) {
			if((modXDays == null) || (modXDays.isEmpty())) {
				pubXDays = DEFAULT_RECORDS;
			}
		}

		Map<String, Object> filters = buildFilters(chorus, contributingOffice, contributor, orcid, null, endYear, g, null,
				indexId, ipdsId, null, modDateHigh, modDateLow, modXDays, orderBy, null, null,
				null, prodId, pubAbstract, pubDateHigh, pubDateLow, pubXDays, q, linkType, noLinkType, reportNumber,
				seriesName, startYear, subtypeName, title, typeName, year);

		List<PwPublication> pubs = busService.getObjects(filters);

		String rssResults = getSearchResultsAsRSS(pubs);

		response.setCharacterEncoding(PubsConstants.DEFAULT_ENCODING);
		response.setContentType(PubsConstants.MEDIA_TYPE_RSS_VALUE);
		try {
			response.setContentLength(rssResults.getBytes(PubsConstants.DEFAULT_ENCODING).length);
		} catch (UnsupportedEncodingException e) {
			LOG.error("Unable to set content length of resulting RSS content: " + e);
		}

		response.setStatus(HttpStatus.SC_OK);
		try {
			response.getWriter().write(rssResults);
		} catch (IOException e) {
			LOG.error("Unable to write to response: " + e);
		}
	}

	private String getSearchResultsAsRSS(List<PwPublication> records) {
		/**
		 * Per JIM JIRA PUBSTWO-971  ā
		 * 
		 * 		<rss version="2.0">
		 * 			<channel>
		 * 				<title>USGS Publications Warehouse</title>
		 * 				<link>http://pubs.er.usgs.gov</link>
		 * 				<description>New publications of the USGS.</description>
		 * 				<language>en-us</language>
		 * 				<lastBuildDate>{{ current date/time }}</lastBuildDate>
		 * 				<webMaster>http://pubs.er.usgs.gov/feedback</webmaster>
		 * 				<pubDate>{{ current date/time }}</pubDate>
		 * 				<item>
		 * 					<title>{{pubdata["title"]}}</title>
		 * 					<author>{{pubdata["authors["text"], order by ["rank"]"]}}</author>
		 * 					<link>http://pubs.er.usgs.gov/publication/{{pubdata["indexId"]}}</link>
		 * 					<description>{{pubdata["abstract"]}}</description>
		 * 					<pubDate>{{pubdata["displayToPublicDate"]}}</pubDate>
		 * 					<category>{{pubdata['seriesTitle']["text"]}}</category>
		 * 				</item>
		 * 				...
		 * 			</channel>
		 * 		</rss>
		 * 
		 */
		StringBuffer rssResults = new StringBuffer("<?xml version='1.0' encoding='UTF-8'?>\n");

		//Date in the form of "Sat, 29 Nov 2014 10:38 -0600"
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
		String todayDate = sdf.format(new Date());

		rssResults.append("<rss version=\"2.0\">\n");
		rssResults.append("\t<channel>\n");
		rssResults.append("\t\t<title>USGS Publications Warehouse</title>\n");
		rssResults.append("\t\t<link>" + warehouseEndpoint + "</link>\n");
		rssResults.append("\t\t<description>New publications of the USGS.</description>\n");
		rssResults.append("\t\t<language>en-us</language>\n");
		rssResults.append("\t\t<lastBuildDate>" + todayDate + "</lastBuildDate>\n");
		rssResults.append("\t\t<webmaster>" + warehouseEndpoint + "/feedback</webmaster>\n");
		rssResults.append("\t\t<pubDate>" + todayDate + "</pubDate>\n");

		/**
		 * Now per item
		 */
		if(records != null) {
			for(BaseDomain<?> record : records) {
				Publication<?> publication = (Publication<?>)record;

				rssResults.append("\t\t<item>\n");

				// ==== TITLE
				rssResults.append("\t\t\t<title>");

				String itemTitle = publication.getTitle();
				if(itemTitle != null) {
					rssResults.append(StringEscapeUtils.escapeXml10(itemTitle.trim()));
				}
				rssResults.append("</title>\n");

				// ==== AUTHORS
				/**
				 * Authors is a list
				 */
				rssResults.append("\t\t\t<author>");
				StringBuffer authorship = new StringBuffer();
				if (null != publication && null != publication.getContributors() && !publication.getContributors().isEmpty()) {
					List<PublicationContributor<?>> contributors = (List<PublicationContributor<?>>) publication.getContributorsToMap().get(PubsUtilities.getAuthorKey());
					try {
						if(contributors != null) {
							for(int i = 0; i < contributors.size(); i++) {
								PublicationContributor<?> author = contributors.get(i);

								Contributor<?> contributor = author.getContributor();

								if(contributor != null) {
									if(contributor.isCorporation()) {
										CorporateContributor corpContributor = (CorporateContributor) contributor;

										String organization = corpContributor.getOrganization();
										if(organization != null) {
											authorship.append(organization.trim());
										}
									} else if(contributor.isUsgs()) {
										UsgsContributor usgsContributor = (UsgsContributor) contributor;

										String family = usgsContributor.getFamily();
										if(family != null) {
											authorship.append(family.trim());
											authorship.append(", ");
										}

										String given = usgsContributor.getGiven();
										if(given != null) {
											authorship.append(given.trim());
										}
									} else {
										if(contributor instanceof PersonContributor) {
											PersonContributor<?> person = (PersonContributor<?>) contributor;

											String family = person.getFamily();
											if(family != null) {
												authorship.append(family.trim());
												authorship.append(", ");
											}
											
											String given = person.getGiven();
											if(given != null) {
												authorship.append(given.trim());
											}
										} else {
											authorship.append(contributor.getId());
										}
									}
								}

								if((i + 1) < contributors.size()) {
									authorship.append("; ");
								}
							}
						}
					} catch (ClassCastException e) {
						LOG.error("Error extracting contributor information: " + e);
					}
				}
				rssResults.append(StringEscapeUtils.escapeXml10(authorship.toString()));
				rssResults.append("</author>\n");

				// ==== LINKS
				/**
				 * Links is a list
				 */
				rssResults.append("\t\t\t<link>");
				String pubId = publication.getIndexId();
				if(pubId != null) {
					rssResults.append(warehouseEndpoint + "/publication/" + pubId.trim());
				}
				rssResults.append("</link>\n");

				// ==== DESCRIPTION
				rssResults.append("\t\t\t<description>");

				String itemDesc = publication.getDocAbstract();
				if(itemDesc != null) {
					rssResults.append(StringEscapeUtils.escapeXml10(itemDesc.trim()));
				}
				rssResults.append("</description>\n");

				// ==== PUBLICATION DATE
				rssResults.append("\t\t\t<pubDate>");
				LocalDateTime pubLocalDateTime = publication.getUpdateDate();
				if (pubLocalDateTime != null) {
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss");
					String pubDate = pubLocalDateTime.format(dtf);
					rssResults.append(pubDate);
				}
				rssResults.append("</pubDate>\n");

				// ==== CATEGORY
				rssResults.append("\t\t\t<category>");
				PublicationSeries pubSeries = publication.getSeriesTitle();
				if(pubSeries != null) {
					String pubSeriesTitle = pubSeries.getText();
					if(pubSeriesTitle != null) {
						rssResults.append(StringEscapeUtils.escapeXml10(pubSeriesTitle.trim()));
					}
				}
				rssResults.append("</category>\n");
				rssResults.append("\t\t</item>\n");
			}
		}

		rssResults.append("\t</channel>\n");
		rssResults.append("</rss>\n");

		return rssResults.toString();
	}
}
