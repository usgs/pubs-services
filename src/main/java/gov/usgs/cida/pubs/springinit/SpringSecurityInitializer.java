package gov.usgs.cida.pubs.springinit;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SpringSecurityInitializer extends AbstractSecurityWebApplicationInitializer {
	
	public SpringSecurityInitializer() {
		super(SecurityConfig.class);
	}

}
