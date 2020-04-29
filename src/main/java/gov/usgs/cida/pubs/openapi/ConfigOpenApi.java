package gov.usgs.cida.pubs.openapi;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.springinit.SecurityConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class ConfigOpenApi {

	@Autowired
	private ConfigurationService configurationService;

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.addServersItem(new Server().url(configurationService.getSwaggerUrl()))
				.info(new Info().title("USGS Publications Warehouse Web Services API").version(configurationService.getAppVersion()));
	}

	@Bean
	public GroupedOpenApi adminOpenAPI() {
		return GroupedOpenApi.builder().setGroup("admin").pathsToMatch(SecurityConfig.ADMIN_PATHS).build();
	}

	@Bean
	public GroupedOpenApi authorizedOpenAPI() {
		return GroupedOpenApi.builder().setGroup("authorized").pathsToMatch(SecurityConfig.AUTHORIZED_PATHS).build();
	}

	@Bean
	public GroupedOpenApi publicOpenAPI() {
		return GroupedOpenApi.builder().setGroup("public").pathsToMatch(SecurityConfig.PUBLIC_PATHS).build();
	}

	@Bean
	public GroupedOpenApi spnOpenAPI() {
		return GroupedOpenApi.builder().setGroup("spn").packagesToScan(SecurityConfig.SPN_PATHS).build();
	}
}
