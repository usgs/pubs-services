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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.busservice.intfc.IPwPublicationBusService;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDao;
import gov.usgs.cida.pubs.domain.BaseDomain;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.CorporateContributor;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationFilterParams;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.webservice.MvcService;

/**
 * Note: This service doesn't actually produce JSON, it directly writes XML to the output stream and
 * sets the content type appropriately. It is annotated to produce JSON because the app default is JSON and
 * Spring throws a 404 if this endpoint gets hit with no headers because it doesn't find a method to render the response.
 */
@RestController
@RequestMapping(value = "publication/rss", produces={PubsConstantsHelper.MEDIA_TYPE_RSS_VALUE, MediaType.APPLICATION_JSON_VALUE})
public class PwPublicationRssMvcService extends MvcService<PwPublication> {
	private static final String DEFAULT_RECORDS = "30";

	private static final Logger LOG = LoggerFactory.getLogger(PwPublicationRssMvcService.class);

	private final IPwPublicationBusService busService;
	private final ConfigurationService configurationService;

	@Autowired
	public PwPublicationRssMvcService(@Qualifier("pwPublicationBusService")
			final IPwPublicationBusService busService,
			final ConfigurationService configurationService) {
		this.busService = busService;
		this.configurationService = configurationService;
	}

	@GetMapping
	@RequestMapping(method=RequestMethod.GET)
	public void getRSS(HttpServletResponse response, PublicationFilterParams filterParams,
			@RequestParam(value=PwPublicationDao.PUB_X_DAYS, required=false) String pubXDays,
			@RequestParam(value=PwPublicationDao.PUB_DATE_LOW, required=false) String pubDateLow,
			@RequestParam(value=PwPublicationDao.PUB_DATE_HIGH, required=false) String pubDateHigh,
			@RequestParam(value=PwPublicationDao.MOD_X_DAYS, required=false) String modXDays,
			@RequestParam(value=PwPublicationDao.MOD_DATE_LOW, required=false) String modDateLow,
			@RequestParam(value=PwPublicationDao.MOD_DATE_HIGH, required=false) String modDateHigh) {
		/**
		 * Per JIMK on JIRA PUBSTWO-971:
		 * 
		 * 	"if mod_x_days is there, and pub_x_days is not, mod_x_days should override the default pub_x_days = 30"
		 */
		String pubXDaysOverride = pubXDays;
		if (((pubXDays == null) || (pubXDays.isEmpty()))
				&& ((modXDays == null) || (modXDays.isEmpty()))) {
			pubXDaysOverride = DEFAULT_RECORDS;
		}

		Map<String, Object> filters = buildFilters(filterParams);
		filters.putAll(buildFilters(modDateHigh, modDateLow, modXDays, pubDateHigh, pubDateLow, pubXDaysOverride));

		List<PwPublication> pubs = busService.getObjects(filters);

		String rssResults = getSearchResultsAsRSS(pubs);

		response.setCharacterEncoding(PubsConstantsHelper.DEFAULT_ENCODING);
		response.setContentType(PubsConstantsHelper.MEDIA_TYPE_RSS_VALUE);
		try {
			response.setContentLength(rssResults.getBytes(PubsConstantsHelper.DEFAULT_ENCODING).length);
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
		rssResults.append("\t\t<link>" + configurationService.getWarehouseEndpoint() + "</link>\n");
		rssResults.append("\t\t<description>New publications of the USGS.</description>\n");
		rssResults.append("\t\t<language>en-us</language>\n");
		rssResults.append("\t\t<lastBuildDate>" + todayDate + "</lastBuildDate>\n");
		rssResults.append("\t\t<webmaster>" + configurationService.getWarehouseEndpoint() + "/feedback</webmaster>\n");
		rssResults.append("\t\t<pubDate>" + todayDate + "</pubDate>\n");

		/**
		 * Now per item
		 */
		if (records != null) {
			for(BaseDomain<?> record : records) {
				rssResults.append(processRecord(record));
			}
		}

		rssResults.append("\t</channel>\n");
		rssResults.append("</rss>\n");

		return rssResults.toString();
	}

	private StringBuffer processRecord(BaseDomain<?> record) {

		Publication<?> publication = (Publication<?>)record;

		StringBuffer rssRecord = new StringBuffer("\t\t<item>\n");

		// ==== TITLE
		rssRecord.append(addTitle(publication));

		// ==== AUTHORS
		rssRecord.append(addAuthors(publication));

		// ==== LINKS
		rssRecord.append(addLinks(publication.getIndexId()));

		// ==== DESCRIPTION
		rssRecord.append(addDescription(publication.getDocAbstract()));

		// ==== PUBLICATION DATE
		rssRecord.append(addPublicationDate(publication.getUpdateDate()));

		// ==== CATEGORY
		rssRecord.append(addCategory(publication.getSeriesTitle()));

		rssRecord.append("\t\t</item>\n");
		return rssRecord;
	}

	private StringBuffer addTitle(Publication<?> publication) {
		StringBuffer title = new StringBuffer("\t\t\t<title>");
		String itemTitle = publication.getTitle();
		if (itemTitle != null) {
			title.append(StringEscapeUtils.escapeXml10(itemTitle.trim()));
		}
		title.append("</title>\n");
		return title;
	}

	private StringBuffer addAuthors(Publication<?> publication) {
		StringBuffer authors = new StringBuffer("\t\t\t<author>");
		/**
		 * Authors is a list
		 */
		if (null != publication && null != publication.getContributors() && !publication.getContributors().isEmpty()) {
			authors.append(processAuthors(publication.getContributorsToMap().get(ContributorType.AUTHOR_KEY)));
		}
		authors.append("</author>\n");
		return authors;
	}

	private StringBuffer addLinks(String pubId) {
		StringBuffer links = new StringBuffer("\t\t\t<link>");
		if (pubId != null) {
			links.append(configurationService.getWarehouseEndpoint() + "/publication/" + pubId.trim());
		}
		links.append("</link>\n");
		return links;
	}

	private StringBuffer addDescription(String itemDesc) {
		StringBuffer description = new StringBuffer("\t\t\t<description>");
		if (itemDesc != null) {
			description.append(StringEscapeUtils.escapeXml10(itemDesc.trim()));
		}
		description.append("</description>\n");
		return description;
	}

	private StringBuffer addPublicationDate(LocalDateTime pubLocalDateTime) {
		StringBuffer publicationDate = new StringBuffer("\t\t\t<pubDate>");
		if (pubLocalDateTime != null) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss");
			String pubDate = pubLocalDateTime.format(dtf);
			publicationDate.append(pubDate);
		}
		publicationDate.append("</pubDate>\n");
		return publicationDate;
	}

	private StringBuffer addCategory(PublicationSeries pubSeries) {
		StringBuffer category = new StringBuffer("\t\t\t<category>");
		if (pubSeries != null) {
			String pubSeriesTitle = pubSeries.getText();
			if(pubSeriesTitle != null) {
				category.append(StringEscapeUtils.escapeXml10(pubSeriesTitle.trim()));
			}
		}
		category.append("</category>\n");
		return category;
	}

	private StringBuffer processAuthors(List<PublicationContributor<?>> contributors) {
		StringBuffer authorship = new StringBuffer();
		if (contributors != null) {
			try {
				for(int i = 0; i < contributors.size(); i++) {
					PublicationContributor<?> author = contributors.get(i);

					Contributor<?> contributor = author.getContributor();

					if (contributor != null) {
						if (contributor.isCorporation()) {
							authorship.append(addCorporation((CorporateContributor) contributor));
						} else if (contributor.isUsgs()) {
							authorship.append(addUsgsContributor((UsgsContributor) contributor));
						} else {
							if(contributor instanceof PersonContributor) {
								authorship.append(addPersonContributor((PersonContributor<?>) contributor));
							} else {
								authorship.append(contributor.getId());
							}
						}
					}

					if ((i + 1) < contributors.size()) {
						authorship.append("; ");
					}
				}
			} catch (ClassCastException e) {
				LOG.error("Error extracting contributor information: " + e);
			}
		}
		return authorship;
	}

	private String addCorporation(CorporateContributor corpContributor) {
		String organization = corpContributor.getOrganization();
		if (organization != null) {
			return organization.trim();
		}
		return null;
	}

	private StringBuffer addUsgsContributor(UsgsContributor usgsContributor) {
		StringBuffer contributor = new StringBuffer();
		String family = usgsContributor.getFamily();
		if (family != null) {
			contributor.append(family.trim());
			contributor.append(", ");
		}

		String given = usgsContributor.getGiven();
		if(given != null) {
			contributor.append(given.trim());
		}
		return contributor;
	}

	private StringBuffer addPersonContributor(PersonContributor<?> person) {
		StringBuffer contributor = new StringBuffer();
		String family = person.getFamily();
		if (family != null) {
			contributor.append(family.trim());
			contributor.append(", ");
		}

		String given = person.getGiven();
		if (given != null) {
			contributor.append(given.trim());
		}
		return contributor;
	}
}
