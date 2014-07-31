package gov.usgs.cida.pubs.json;

import java.io.IOException;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * @author drsteini
 *
 */
public class PubsJsonLocalDateDeSerializer extends JsonDeserializer<LocalDate> {

    /** {@inheritDoc}
     * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)
     */
    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String date = jp.getValueAsString();
        LocalDate ldt = null;
        if (date != null && date.length() > 0) { 
            ldt = new LocalDate(new LocalDate(date.split("T")[0]));
        }
        return ldt; 
    }

}
