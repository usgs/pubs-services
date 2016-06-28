package gov.usgs.cida.pubs.utility;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * PUBSTWO-1183 - Do not parse input parameters into an array. Simply use the String as provided.
 * @author drsteini
 *
 */
@Component
public class CustomStringToArrayConverter implements Converter<String, String[]>{
	@Override
	public String[] convert(String source) {
		if (StringUtils.isNotBlank(source)) {
			return new String[]{source};
		} else {
			return null;
		}
	}

}
