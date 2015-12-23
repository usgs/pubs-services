package gov.usgs.cida.pubs.springinit;

import java.util.List;

import javax.sql.DataSource;

import org.apache.http.auth.NTCredentials;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.SerializationFeature;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.utility.CustomStringToArrayConverter;

@Configuration
@ComponentScan(basePackages={"gov.usgs.cida.pubs.webservice", "gov.usgs.cida.pubs.utility", "gov.usgs.cida.pubs.dao",
		"gov.usgs.cida.pubs.jms", "gov.usgs.cida.pubs.domain"})
@ImportResource("classpath:spring/applicationContext.xml")
@EnableWebMvc
@EnableTransactionManagement
@Import(BusServiceConfig.class)
public class SpringConfig extends WebMvcConfigurerAdapter {
	
	@Autowired
	CustomStringToArrayConverter customStringToArrayConverter;
	
	@Autowired
	DataSource dataSource;
	
	@Autowired
	@Qualifier("ipdsPubsWsUser")
	String ipdsPubsWsUser;
	
	@Autowired
	@Qualifier("ipdsPubsWsPwd")
	String ipdsPubsWsPwd;
	
	@Override
	public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(customStringToArrayConverter);
	}
    
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
        	.favorPathExtension(false)
        	.favorParameter(true)
        	.parameterName(PubsConstants.CONTENT_PARAMETER_NAME)
        	.defaultContentType(MediaType.APPLICATION_JSON)
        	.mediaType(PubsConstants.MEDIA_TYPE_CSV_EXTENSION, PubsConstants.MEDIA_TYPE_CSV)
        	.mediaType(PubsConstants.MEDIA_TYPE_TSV_EXTENSION, PubsConstants.MEDIA_TYPE_TSV)
         	.mediaType(PubsConstants.MEDIA_TYPE_XML_EXTENSION, MediaType.APPLICATION_XML)
        	.mediaType(PubsConstants.MEDIA_TYPE_JSON_EXTENSION, MediaType.APPLICATION_JSON)
        	.mediaType(PubsConstants.MEDIA_TYPE_XLSX_EXTENSION, PubsConstants.MEDIA_TYPE_XLSX)
        	;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(jackson2HttpMessageConverter());
    }
    
    @Bean
    public MappingJackson2HttpMessageConverter jackson2HttpMessageConverter() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new MappingJackson2HttpMessageConverter(builder.build());
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseSuffixPatternMatch(false);
    }
    
    @Bean
	public SqlSessionFactoryBean sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		Resource mybatisConfig = new ClassPathResource("mybatis/dataMapperConfig.xml");
		sqlSessionFactory.setConfigLocation(mybatisConfig);
		sqlSessionFactory.setDataSource(dataSource);
		Resource[] mappers = new PathMatchingResourcePatternResolver().getResources("mybatis/mappers/**/*.xml");
		sqlSessionFactory.setMapperLocations(mappers);
		return sqlSessionFactory;
	}

    @Bean
    public PlatformTransactionManager transactionManager() throws Exception {
    	DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
    	transactionManager.setDataSource(dataSource);
    	return transactionManager;
    }

    @Bean
    public NTCredentials nTCredentials() {
    	return new NTCredentials(ipdsPubsWsUser, ipdsPubsWsPwd, "", "GS");
    }

}