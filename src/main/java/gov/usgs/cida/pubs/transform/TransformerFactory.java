package gov.usgs.cida.pubs.transform;

import freemarker.template.Configuration;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.intfc.IPublicationBusService;
import gov.usgs.cida.pubs.domain.SearchResults;
import gov.usgs.cida.pubs.transform.intfc.ITransformer;
import java.io.OutputStream;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TransformerFactory {
	
	protected final Configuration templateConfiguration;
	protected final String crossRefDepositorEmail;
	protected final IPublicationBusService pubBusService;
	
	public TransformerFactory(
		@Qualifier("freeMarkerConfiguration")
		final Configuration templateConfiguration,
		@Qualifier("crossRefDepositorEmail")
		final String crossRefDepositorEmail,
		final IPublicationBusService publicationBusService
	) {
		this.templateConfiguration = templateConfiguration;
		this.crossRefDepositorEmail = crossRefDepositorEmail;
		this.pubBusService = publicationBusService;
	}
	
	/**
	 * Constructs a transformer for a given file extension
	 * @param fileExtension typically constants ending in "_EXTENSION" from {@link gov.usgs.cida.pubs.PubsConstants PubsConstants}
	 * @param outputStream
	 * @param searchResults optional, only required for paged search results
	 * @return 
	 */
	public ITransformer getTransformer(String fileExtension, OutputStream outputStream, SearchResults searchResults) {
		ITransformer transformer;
		switch (fileExtension) {
			case PubsConstants.MEDIA_TYPE_TSV_EXTENSION:
				transformer = new DelimitedTransformer(outputStream, PublicationColumns.getMappings(), "\t");
				break;
			case PubsConstants.MEDIA_TYPE_XLSX_EXTENSION:
				transformer = new XlsxTransformer(outputStream, PublicationColumns.getMappings());
				break;
			case PubsConstants.MEDIA_TYPE_CSV_EXTENSION:
				transformer = new DelimitedTransformer(outputStream, PublicationColumns.getMappings(), ",");
				break;
			case PubsConstants.MEDIA_TYPE_CROSSREF_EXTENSION:
				transformer = new CrossrefTransformer(
					outputStream,
					templateConfiguration,
					crossRefDepositorEmail,
					pubBusService
				);
				break;
			default:
				//Let json be the default
				transformer = new JsonTransformer(outputStream, searchResults);
				break;
		}
		return transformer;
	}
}
