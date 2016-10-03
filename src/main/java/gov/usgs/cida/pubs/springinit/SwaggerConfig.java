package gov.usgs.cida.pubs.springinit;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import springfox.documentation.PathProvider;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.AbstractPathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
public class SwaggerConfig {

	@Autowired
	@Qualifier("displayProtocol")
	String displayProtocol;

	@Autowired
	@Qualifier("displayHost")
	String displayHost;

	@Autowired
	@Qualifier("displayPath")
	String displayPath;

	@Bean
	public Docket nldiServicesApi() {
		Set<String> protocols = new HashSet<>();
		protocols.add(displayProtocol);
		return new Docket(DocumentationType.SWAGGER_2)
				.protocols(protocols)
				.host(displayHost)
				.pathProvider(pathProvider())
				.useDefaultResponseMessages(false);
	}

	@Bean
	public PathProvider pathProvider() {
		PathProvider rtn = new ProxyPathProvider();
		return rtn;
	}

	public class ProxyPathProvider extends AbstractPathProvider {
		@Override
		protected String applicationPath() {
			return displayPath;
		}
	
		@Override
		protected String getDocumentationPath() {
			return displayPath;
		}
	}
}
