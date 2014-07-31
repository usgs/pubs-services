package gov.usgs.cida.pubs.json;

import java.io.IOException;

import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author drsteini
 *
 */
public class PubsJsonLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
 
    @Override
    public void serialize(LocalDateTime dateTime, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
        gen.writeString(dateTime.toString());
    }

}
