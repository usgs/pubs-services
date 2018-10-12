package gov.usgs.cida.pubs.springinit;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.PubsConstants;
import springfox.documentation.PathProvider;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.AbstractPathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Profile("swagger")
public class SwaggerConfig {

	@Autowired
	ConfigurationService configurationService;

	@Bean
	public Docket nldiServicesApi() {
		Set<String> protocols = new HashSet<>();
		protocols.add(configurationService.getDisplayProtocol());
		Docket docket = new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build()
				.protocols(protocols)
				.host(configurationService.getDisplayHost())
				.pathProvider(pathProvider())
				.useDefaultResponseMessages(false);

		docket.apiInfo(apiInfo());
		docket.securitySchemes(Collections.singletonList(apiKey()));
		return docket;
	}

	@Bean
	public PathProvider pathProvider() {
		PathProvider rtn = new ProxyPathProvider();
		return rtn;
	}

	public class ProxyPathProvider extends AbstractPathProvider {
		@Override
		protected String applicationPath() {
			return configurationService.getDisplayPath();
		}
	
		@Override
		protected String getDocumentationPath() {
			return configurationService.getDisplayPath();
		}
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("REST API Document")
				.description("description for api")
				.termsOfServiceUrl("localhost")
				.version("1.0")
				.build();
	}

	private ApiKey apiKey() {
		return new ApiKey(PubsConstants.API_KEY_NAME, "Authorization", "header");
	}

}
