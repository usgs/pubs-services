package gov.usgs.cida.pubs.springinit;

import java.util.List;

import org.apache.http.auth.NTCredentials;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.AntPathMatcher;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.SerializationFeature;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.utility.CustomStringToArrayConverter;
import gov.usgs.cida.pubs.utility.CustomStringToStringConverter;
import gov.usgs.cida.pubs.utility.StringArrayCleansingConverter;
import java.io.IOException;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

@Configuration
@ComponentScan(basePackages={"gov.usgs.cida.pubs.webservice", "gov.usgs.cida.pubs.utility", "gov.usgs.cida.pubs.dao",
		"gov.usgs.cida.pubs.jms", "gov.usgs.cida.pubs.domain", "gov.usgs.cida.pubs.aop", "gov.usgs.cida.pubs.busservice", "gov.usgs.cida.pubs.transform"})
@EnableWebMvc
@EnableTransactionManagement
@EnableAspectJAutoProxy
@Import({BusServiceConfig.class, MybatisConfig.class})
public class SpringConfig extends WebMvcConfigurerAdapter {

	@Autowired
	CustomStringToArrayConverter customStringToArrayConverter;

	@Autowired
	StringArrayCleansingConverter customStringListToArrayConverter;

	@Autowired
	CustomStringToStringConverter customStringToStringConverter;

	@Autowired
	@Qualifier("ipdsPubsWsUser")
	String ipdsPubsWsUser;

	@Autowired
	@Qualifier("ipdsPubsWsPwd")
	String ipdsPubsWsPwd;

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
			.parameterName(PubsConstants.CONTENT_PARAMETER_NAME)
			.defaultContentType(MediaType.APPLICATION_JSON)
			.mediaType(PubsConstants.MEDIA_TYPE_CSV_EXTENSION, PubsConstants.MEDIA_TYPE_CSV)
			.mediaType(PubsConstants.MEDIA_TYPE_TSV_EXTENSION, PubsConstants.MEDIA_TYPE_TSV)
			.mediaType(PubsConstants.MEDIA_TYPE_XML_EXTENSION, MediaType.APPLICATION_XML)
			.mediaType(PubsConstants.MEDIA_TYPE_JSON_EXTENSION, MediaType.APPLICATION_JSON)
			.mediaType(PubsConstants.MEDIA_TYPE_XLSX_EXTENSION, PubsConstants.MEDIA_TYPE_XLSX)
			.mediaType(PubsConstants.MEDIA_TYPE_CROSSREF_EXTENSION, PubsConstants.MEDIA_TYPE_CROSSREF)
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
	public NTCredentials nTCredentials() {
		return new NTCredentials(ipdsPubsWsUser, ipdsPubsWsPwd, "", "GS");
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

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html")
			.addResourceLocations("classpath:/META-INF/resources/");

		registry.addResourceHandler("webjars/**")
			.addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	
	@Bean
	public FreeMarkerConfigurationFactory freeMarkerConfigurationFactory() {
		FreeMarkerConfigurationFactory factory = new FreeMarkerConfigurationFactory();
		factory.setPreferFileSystemAccess(false);
		return factory;
	}
	
	@Bean
	public freemarker.template.Configuration freeMarkerConfiguration() {
		freemarker.template.Configuration templateConfig;
		try {
			templateConfig = freeMarkerConfigurationFactory().createConfiguration();
			templateConfig.setTemplateLoader(
				new ClassTemplateLoader(this.getClass().getClassLoader(), "templates")
			);
			//we will choose whether TemplateExceptions should halt the program
			templateConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
			
			//Associate ".ftlx" file extensions with XML auto-escaping,
			//and ".ftlh" extensions with HTML auto-escaping
			templateConfig.setRecognizeStandardFileExtensions(true);
			//we will choose whether TemplateExceptions should be logged
			templateConfig.setLogTemplateExceptions(false);
		} catch (IOException | TemplateException ex) {
			throw new RuntimeException("could not create freemarker template configuration", ex);
		}
		return templateConfig;
	}
}