package gov.usgs.cida.pubs.json;

import java.io.IOException;

import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * @author drsteini
 *
 */
public class PubsJsonLocalDateTimeDeSerializer extends JsonDeserializer<LocalDateTime> {

    /** {@inheritDoc}
     * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)
     */
    @Override
    public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String date = jp.getValueAsString();
        LocalDateTime ldt = null;
        if (date != null && date.length() > 0) { 
            ldt = new LocalDateTime(new LocalDateTime(date));
        }
        return ldt; 
    }

}
