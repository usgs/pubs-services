package gov.usgs.cida.pubs.transform;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import gov.usgs.cida.pubs.domain.SearchResults;
import gov.usgs.cida.pubs.json.View;

public class JsonTransformer extends Transformer {
	private static final Logger LOG = LoggerFactory.getLogger(JsonTransformer.class);

	protected JsonFactory f;
	protected JsonGenerator g;
	protected ObjectMapper mapper;
	protected ObjectWriter writer;
	protected SearchResults searchResults;

	public JsonTransformer(OutputStream target, SearchResults searchResults) {
		super(target, null);
		this.searchResults = searchResults;
		init();
	}

	@Override
	protected void init() {
		f = new JsonFactory();
		try {
			g = f.createGenerator(target);
		} catch (IOException e) {
			throw new RuntimeException("Error initializing json document", e);
		}
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
		writer = mapper.writerWithView(View.PW.class);
		LOG.trace("DEFAULT_VIEW_INCLUSION:" + mapper.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION));
		writeHeader(null);
	}

	protected void writeHeader(Map<?,?> resultMap) {
		try {
			g.writeStartObject();
			g.writeStringField("pageNumber", searchResults.getPageNumber());
			g.writeStringField("pageRowStart", searchResults.getPageRowStart());
			g.writeStringField("pageSize", searchResults.getPageSize());
			g.writeNumberField("recordCount", searchResults.getRecordCount());

			g.writeFieldName("records");
			g.writeStartArray();
		} catch (IOException e) {
			throw new RuntimeException("Error starting json document", e);
		}
	}

	protected void writeData(Map<?, ?> result) throws IOException {
		//This transformer works on Pojo's, not a map.
	}

	@Override
	public void write(Object result) throws IOException {
		try {
			writer.writeValue(g, result);
			g.writeRaw('\n');
		} catch (IOException e) {
			throw new RuntimeException("Error writing json", e);
		}
	}

	/** output the closing tags and close stuff as appropriate. */
	@Override
	public void end() {
		try {
			g.writeEndArray();
			g.writeEndObject();
			g.close();
		} catch (IOException e) {
			throw new RuntimeException("Error ending json document", e);
		}
	}

	protected String getValue(Map<String, Object> resultMap, String key) {
		if (resultMap.containsKey(key) && null != resultMap.get(key)) {
			return resultMap.get(key).toString();
		} else {
			return "";
		}
	}

}