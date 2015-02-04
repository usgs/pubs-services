package gov.usgs.cida.pubs.json;

import java.io.IOException;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author drsteini
 *
 */
public class PubsJsonLocalDateSerializer extends JsonSerializer<LocalDate> {
 
    @Override
    public void serialize(LocalDate date, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(date.toString());
    }

}
