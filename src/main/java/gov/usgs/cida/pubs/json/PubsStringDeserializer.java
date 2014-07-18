package gov.usgs.cida.pubs.json;

import gov.usgs.cida.pubs.utility.PubsUtilities;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * @author drsteini
 *
 */
public class PubsStringDeserializer extends JsonDeserializer<String> {

    /** {@inheritDoc}
     * @see com.fasterxml.jackson.map.JsonDeserializer#deserialize(com.fasterxml.jackson.JsonParser, com.fasterxml.jackson.map.DeserializationContext)
     */
    @Override
    public String deserialize(JsonParser parser, DeserializationContext context)
            throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper(); 
        TypeReference<String> typeRef = new TypeReference<String>() {};
        String rawString = mapper.readValue(parser, typeRef);
        return PubsUtilities.parseString(rawString);
    }

}
