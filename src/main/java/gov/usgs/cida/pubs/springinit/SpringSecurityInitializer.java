package gov.usgs.cida.pubs.springinit;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import gov.usgs.cida.pubs.utility.CaseInsensitiveRequestFilter;

public class SpringSecurityInitializer extends AbstractSecurityWebApplicationInitializer {

	@Override
	public void beforeSpringSecurityFilterChain(ServletContext servletContext) {		
		FilterRegistration caseInsensitiveFilter = servletContext.addFilter("caseInsensitiveFilter", CaseInsensitiveRequestFilter.class);
		caseInsensitiveFilter.addMappingForUrlPatterns(null, false, "/*");
	}

}
