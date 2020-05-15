package gov.usgs.cida.pubs.utility;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

@Component
public class PubsStringDeserializer extends StringDeserializer {
	private static final long serialVersionUID = 451096428086616730L;

	@Override
	public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		return nullIfEmpty(super.deserialize(p, ctxt));
	}

	protected String nullIfEmpty(String value) {
		return value == null || value.trim().isEmpty() ? null : value.trim();
	}
}
