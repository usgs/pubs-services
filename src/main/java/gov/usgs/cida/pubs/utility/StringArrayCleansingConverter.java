package gov.usgs.cida.pubs.utility;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class StringArrayCleansingConverter implements Converter<String[], String[]>{
	@Override
	public String[] convert(String[] source) {
		List<String> filterValues = new ArrayList<>();
		if (null != source) {
			for(int i = 0; i < source.length; i++) {
				if (StringUtils.hasText(source[i])) {
					filterValues.add(source[i].trim());
				}
			}
		}
		return filterValues.toArray(new String[filterValues.size()]);
	}

}
