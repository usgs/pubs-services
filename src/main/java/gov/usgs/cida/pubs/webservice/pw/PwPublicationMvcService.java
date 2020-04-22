package gov.usgs.cida.pubs.webservice.pw;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.entity.mime.MIME;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import com.fasterxml.jackson.annotation.JsonView;

import freemarker.template.Configuration;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.busservice.intfc.IPwPublicationBusService;
import gov.usgs.cida.pubs.busservice.intfc.IXmlBusService;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDao;
import gov.usgs.cida.pubs.dao.resulthandler.StreamingResultHandler;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.SearchResults;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.query.CrossRefFilterParams;
import gov.usgs.cida.pubs.domain.query.PwPublicationFilterParams;
import gov.usgs.cida.pubs.json.View;
import gov.usgs.cida.pubs.transform.CrossrefTransformer;
import gov.usgs.cida.pubs.transform.DelimitedTransformer;
import gov.usgs.cida.pubs.transform.JsonTransformer;
import gov.usgs.cida.pubs.transform.PublicationColumnsHelper;
import gov.usgs.cida.pubs.transform.XlsxTransformer;
import gov.usgs.cida.pubs.transform.intfc.ITransformer;
import gov.usgs.cida.pubs.utility.PubsUtils;
import gov.usgs.cida.pubs.webservice.MvcService;

@RestController
@RequestMapping(value="publication")
@ResponseBody
public class PwPublicationMvcService extends MvcService<PwPublication> {
	private static final Logger LOG = LoggerFactory.getLogger(PwPublicationMvcService.class);
	private final IPwPublicationBusService busService;
	private final ConfigurationService configurationService;
	private final Configuration templateConfiguration;
	private final IXmlBusService xmlBusService;
	private final ContentNegotiationStrategy contentStrategy;
	@Autowired
	public PwPublicationMvcService(
			@Qualifier("pwPublicationBusService")
			IPwPublicationBusService busService,
			@Qualifier("xmlBusService")
			IXmlBusService xmlBusService,
			ConfigurationService configurationService,
			@Qualifier("freeMarkerConfiguration")
			Configuration templateConfiguration,
			ContentNegotiationStrategy contentStrategy
	) {
		this.busService = busService;
		this.xmlBusService = xmlBusService;
		this.configurationService = configurationService;
		this.templateConfiguration = templateConfiguration;
		this.contentStrategy = contentStrategy;
	}

	@GetMapping(produces={PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE,
			PubsConstantsHelper.MEDIA_TYPE_XLSX_VALUE, PubsConstantsHelper.MEDIA_TYPE_CSV_VALUE, PubsConstantsHelper.MEDIA_TYPE_TSV_VALUE})
	@JsonView(View.PW.class)
	public void getStreamPubs(HttpServletResponse response,
			HttpServletRequest request,
			PwPublicationFilterParams filterParams) {
		LOG.debug(filterParams.toString());

		setHeaders(response);

		filterParams.setMimeType(determineMimeType(request, filterParams.getMimeType()));

		streamResults(filterParams, response);
	}

	protected String determineMimeType(HttpServletRequest request, String mimeType) {
		if (null != mimeType) {
			return mimeType;
		} else {
			String calculated = PubsConstantsHelper.MEDIA_TYPE_JSON_EXTENSION;
			try {
				List<MediaType> mediaTypes = contentStrategy.resolveMediaTypes(new ServletWebRequest(request));
				if (mediaTypes.contains(PubsConstantsHelper.MEDIA_TYPE_CSV)) {
					calculated = PubsConstantsHelper.MEDIA_TYPE_CSV_EXTENSION;
				}
				if (mediaTypes.contains(PubsConstantsHelper.MEDIA_TYPE_TSV)) {
					calculated = PubsConstantsHelper.MEDIA_TYPE_TSV_EXTENSION;
				}
				if (mediaTypes.contains(PubsConstantsHelper.MEDIA_TYPE_XLSX)) {
					calculated = PubsConstantsHelper.MEDIA_TYPE_XLSX_EXTENSION;
				}
			} catch (HttpMediaTypeNotAcceptableException e) {
				LOG.debug(e.getLocalizedMessage());
			}
			return calculated;
		}
	}

	protected void streamResults(PwPublicationFilterParams filters, HttpServletResponse response) {
		response.setCharacterEncoding(PubsConstantsHelper.DEFAULT_ENCODING);
		String statement = PwPublicationDao.NS;

		try {
			ITransformer transformer;
			SearchResults searchResults = null;
			switch (filters.getMimeType()) {
			case PubsConstantsHelper.MEDIA_TYPE_TSV_EXTENSION:
				statement = statement + PwPublicationDao.GET_STREAM_BY_MAP;
				response.setContentType(PubsConstantsHelper.MEDIA_TYPE_TSV_VALUE);
				response.setHeader(MIME.CONTENT_DISPOSITION, "attachment; filename=publications." + filters.getMimeType());
				transformer = new DelimitedTransformer(response.getOutputStream(), PublicationColumnsHelper.getMappings(), "\t");
				break;
			case PubsConstantsHelper.MEDIA_TYPE_XLSX_EXTENSION:
				statement = statement + PwPublicationDao.GET_STREAM_BY_MAP;
				response.setContentType(PubsConstantsHelper.MEDIA_TYPE_XLSX_VALUE);
				response.setHeader(MIME.CONTENT_DISPOSITION, "attachment; filename=publications." + filters.getMimeType());
				transformer = new XlsxTransformer(response.getOutputStream(), PublicationColumnsHelper.getMappings());
				break;
			case PubsConstantsHelper.MEDIA_TYPE_CSV_EXTENSION:
				statement = statement + PwPublicationDao.GET_STREAM_BY_MAP;
				response.setContentType(PubsConstantsHelper.MEDIA_TYPE_CSV_VALUE);
				response.setHeader(MIME.CONTENT_DISPOSITION, "attachment; filename=publications." + filters.getMimeType());
				transformer = new DelimitedTransformer(response.getOutputStream(), PublicationColumnsHelper.getMappings(), ",");
				break;
			default:
				//Let json be the default
				searchResults = getCountAndPaging(filters);
				statement = statement + PwPublicationDao.GET_BY_MAP;
				response.setContentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE);
				transformer = new JsonTransformer(response.getOutputStream(), searchResults);
				break;
			}
			busService.stream(statement, filters, new StreamingResultHandler<PwPublication>(transformer));

			transformer.end();

		} catch(IOException e) {
			throw new RuntimeException(e);
		}

		response.setStatus(HttpStatus.OK.value());
	}

	protected SearchResults getCountAndPaging(PwPublicationFilterParams filters) {
		SearchResults results = new SearchResults();
		results.setPageSize(filters.getPage_size());
		results.setPageRowStart(filters.getPage_row_start());
		results.setPageNumber(filters.getPage_number());
		results.setRecordCount(busService.getObjectCount(filters));
		return results;
	}

	@GetMapping(value="{indexId}", produces=PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE)
	@JsonView(View.PW.class)
	public PwPublication getPwPublication(HttpServletRequest request, HttpServletResponse response, @PathVariable("indexId") String indexId)
			throws IOException {
		setHeaders(response);
		PwPublication rtn = busService.getByIndexId(indexId);
		if (null == rtn) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		return rtn;
	}

	@GetMapping(value="{indexId}", produces={PubsConstantsHelper.MEDIA_TYPE_CROSSREF_VALUE})
	@JsonView(View.PW.class)
	public void getPwPublicationCrossref(HttpServletRequest request, HttpServletResponse response, @PathVariable("indexId") String indexId)
			throws IOException {
		setHeaders(response);
		PwPublication pub = busService.getByIndexId(indexId);
		if (null == pub || !PubsUtils.isUsgsSeries(pub.getPublicationSubtype())) {
			response.sendError(HttpStatus.NOT_FOUND.value());
		} else {
			writeCrossrefForPub(response, pub);
		}
	}

	@GetMapping(value = "/crossref", produces = { PubsConstantsHelper.MEDIA_TYPE_CROSSREF_VALUE })
	public void getBulkCrossref(HttpServletRequest request, HttpServletResponse response) throws IOException{
		setHeaders(response);
		try (
				OutputStream outputStream = response.getOutputStream();
				CrossrefTransformer transformer = new CrossrefTransformer(
						outputStream,
						templateConfiguration,
						configurationService
					);
			) {

			response.setContentType(PubsConstantsHelper.MEDIA_TYPE_CROSSREF_VALUE);
			response.setHeader(MIME.CONTENT_DISPOSITION, "attachment; filename=publications." + PubsConstantsHelper.MEDIA_TYPE_CROSSREF_EXTENSION);

			String statement = PwPublicationDao.NS + PwPublicationDao.GET_CROSSREF_PUBLICATIONS;

			CrossRefFilterParams filters = new CrossRefFilterParams();
			filters.setSubtypeId(new int[]{
					PublicationSubtype.USGS_NUMBERED_SERIES,
					PublicationSubtype.USGS_UNNUMBERED_SERIES
				}
			);
			busService.stream(statement, filters, new StreamingResultHandler<>(transformer));

			transformer.end();
		}
	}

	/**
	 *   Get the Html document for the publication specified by indexId
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@GetMapping(
		path = "/full/{indexId}",
		produces = { MediaType.TEXT_HTML_VALUE }
	)
	public void getPublicationHtml(HttpServletRequest request,
						HttpServletResponse response, @PathVariable("indexId") String indexId) throws Exception {
		PwPublication publication = busService.getByIndexId(indexId);
		String xmlUrl = getXmlDocUrl(publication);
		String doi = publication == null || publication.getDoi() == null ? "[Unknown]" : String.format("'%s'",publication.getDoi());
		String htmlOutput = "";

		if(publication == null) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			htmlOutput = formatHtmlErrMess(String.format("Publication with indexId '%s' not found.", indexId));
		} else if(xmlUrl == null) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			htmlOutput = formatHtmlErrMess(String.format("Full publication link not found (indexId '%s' doi %s)", indexId, doi));
		} else {
			htmlOutput = xmlBusService.getPublicationHtml(xmlUrl);
		}

		response.setCharacterEncoding(PubsConstantsHelper.DEFAULT_ENCODING);
		response.setContentType(MediaType.TEXT_HTML_VALUE);
		response.getOutputStream().write(htmlOutput.getBytes());

	}

	/**
	 * Writes the specified USGS Series Publication to Crossref XML.
	 * This method will error if the specified publication is not a USGS Series.
	 * @param response
	 * @param pub
	 * @throws IOException 
	 */
	protected void writeCrossrefForPub(HttpServletResponse response, PwPublication pub) throws IOException {
		try (
				OutputStream outputStream = response.getOutputStream();
				CrossrefTransformer transformer = new CrossrefTransformer(
						outputStream,
						templateConfiguration,
						configurationService
					);
			) {
			response.setCharacterEncoding(PubsConstantsHelper.DEFAULT_ENCODING);
			response.setContentType(PubsConstantsHelper.MEDIA_TYPE_CROSSREF_VALUE);
			response.setHeader(MIME.CONTENT_DISPOSITION, "inline");
			transformer.write(pub);
			transformer.end();
		}
	}

	protected String getXmlDocUrl(PwPublication pub) {
		String url = null;

		if(pub != null) {
			List<PublicationLink<?>> xmlLinks = pub.getLinksByLinkTypeId(LinkType.PUBLICATION_XML);

			if(xmlLinks.size() > 0) {
				url = xmlLinks.get(0).getUrl();
			}
		}

		return url;
	}
}
