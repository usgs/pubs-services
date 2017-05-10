package gov.usgs.cida.pubs.transform;

import freemarker.template.Configuration;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.intfc.IPublicationBusService;
import gov.usgs.cida.pubs.domain.SearchResults;
import gov.usgs.cida.pubs.transform.intfc.ITransformer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Qualifier;
import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;
import org.springframework.beans.factory.annotation.Autowired;

public class TransformerFactoryTest extends BaseSpringTest {
	@Autowired
	@Qualifier("freeMarkerConfiguration")
	protected Configuration templateConfiguration;
	protected String crossRefDepositorEmail = "nobody@usgs.gov";
	@Mock
	protected IPublicationBusService pubBusService;
	@Mock
	protected SearchResults searchResults;
	
	protected OutputStream outputStream;
	
	TransformerFactory factory;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		outputStream = new ByteArrayOutputStream();
		
		factory = new TransformerFactory(
			templateConfiguration,
			crossRefDepositorEmail,
			pubBusService
		);

	}
	
	@After
	public void tearDown() throws IOException {
		outputStream.close();
	}

	@Test
	public void testTSVTransformer () {
		ITransformer transformer = factory.getTransformer(PubsConstants.MEDIA_TYPE_TSV_EXTENSION, outputStream, searchResults);
		assertThat(transformer, is(instanceOf(DelimitedTransformer.class)));
		DelimitedTransformer dt = (DelimitedTransformer)transformer;
		assertThat(dt.delimiter, is("\t"));
	}
	
	@Test
	public void testXLSXTransformer () {
		ITransformer transformer = factory.getTransformer(PubsConstants.MEDIA_TYPE_XLSX_EXTENSION, outputStream, searchResults);
		assertThat(transformer, is(instanceOf(XlsxTransformer.class)));
	}
	
	@Test
	public void testCSV() {
		ITransformer transformer = factory.getTransformer(PubsConstants.MEDIA_TYPE_CSV_EXTENSION, outputStream, searchResults);
		assertThat(transformer, is(instanceOf(DelimitedTransformer.class)));
		DelimitedTransformer dt = (DelimitedTransformer)transformer;
		assertThat(dt.delimiter, is(","));
	}
	
	@Test
	public void testCrossref() {
		ITransformer transformer = factory.getTransformer(PubsConstants.MEDIA_TYPE_CROSSREF_EXTENSION, outputStream, searchResults);
		assertThat(transformer, is(instanceOf(CrossrefTransformer.class)));
	}
	
	@Test
	public void testJsonTransformer() {
		ITransformer transformer = factory.getTransformer(PubsConstants.MEDIA_TYPE_JSON_EXTENSION, outputStream, searchResults);
		assertThat(transformer, is(instanceOf(JsonTransformer.class)));
	}
	
	@Test
	public void testDefaultToJsonTransformer() {
		ITransformer transformer = factory.getTransformer("", outputStream, searchResults);
		assertThat(transformer, is(instanceOf(JsonTransformer.class)));
	}

}
