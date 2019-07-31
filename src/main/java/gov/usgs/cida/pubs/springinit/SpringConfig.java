package gov.usgs.cida.pubs.springinit;

import java.util.List;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.util.AntPathMatcher;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.SerializationFeature;

import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.utility.CustomStringToArrayConverter;
import gov.usgs.cida.pubs.utility.CustomStringToStringConverter;
import gov.usgs.cida.pubs.utility.StringArrayCleansingConverter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringConfig implements WebMvcConfigurer {

	@Autowired
	private CustomStringToArrayConverter customStringToArrayConverter;

	@Autowired
	private StringArrayCleansingConverter customStringListToArrayConverter;

	@Autowired
	private CustomStringToStringConverter customStringToStringConverter;

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(customStringToArrayConverter);
		registry.addConverter(customStringListToArrayConverter);
		registry.addConverter(customStringToStringConverter);
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer
			.favorParameter(true)
			.parameterName(PubsConstantsHelper.CONTENT_PARAMETER_NAME)
			.defaultContentType(MediaType.APPLICATION_JSON)
			.mediaType(PubsConstantsHelper.MEDIA_TYPE_CSV_EXTENSION, PubsConstantsHelper.MEDIA_TYPE_CSV)
			.mediaType(PubsConstantsHelper.MEDIA_TYPE_TSV_EXTENSION, PubsConstantsHelper.MEDIA_TYPE_TSV)
			.mediaType(PubsConstantsHelper.MEDIA_TYPE_XML_EXTENSION, MediaType.APPLICATION_XML)
			.mediaType(PubsConstantsHelper.MEDIA_TYPE_JSON_EXTENSION, MediaType.APPLICATION_JSON)
			.mediaType(PubsConstantsHelper.MEDIA_TYPE_XLSX_EXTENSION, PubsConstantsHelper.MEDIA_TYPE_XLSX)
			.mediaType(PubsConstantsHelper.MEDIA_TYPE_CROSSREF_EXTENSION, PubsConstantsHelper.MEDIA_TYPE_CROSSREF)
			;
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(jackson2HttpMessageConverter());
		converters.add(new MappingJackson2XmlHttpMessageConverter());
	}

	@Bean
	public MappingJackson2HttpMessageConverter jackson2HttpMessageConverter() {
		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
		builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return new MappingJackson2HttpMessageConverter(builder.build());
	}

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		//This should make the url case insensitive
		AntPathMatcher matcher = new AntPathMatcher();
		matcher.setCaseSensitive(false);
		configurer.setPathMatcher(matcher);
		//This will prevent Spring from expecting anything after a dot to be a file suffix.
		//It is needed because some index ID's contain a dot.
		configurer.setUseSuffixPatternMatch(false);
	}

	@Bean
	public ResourceBundleMessageInterpolator messageInterpolator() {
		return new ResourceBundleMessageInterpolator();
	}

	@Bean
	public LocalValidatorFactoryBean validator() {
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.setMessageInterpolator(messageInterpolator());
		return validator;
	}

}