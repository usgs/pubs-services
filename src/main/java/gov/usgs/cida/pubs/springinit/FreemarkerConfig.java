package gov.usgs.cida.pubs.springinit;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

//TODO Reactivate
//@Configuration
public class FreemarkerConfig {

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
