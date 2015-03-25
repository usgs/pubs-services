package gov.usgs.cida.pubs.utility;
import org.springframework.core.convert.converter.Converter;

/**
 * PUBSTWO-1183 - Do not parse input parameters into an array. Simply use the String as provided.
 * @author drsteini
 *
 */
public class CustomStringToArrayConverter implements Converter<String, String[]>{
	@Override
	public String[] convert(String source) {
		return new String[]{source};
	}

}
