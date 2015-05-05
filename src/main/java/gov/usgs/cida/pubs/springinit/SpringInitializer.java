package gov.usgs.cida.pubs.springinit;

import gov.usgs.cida.pubs.filter.CORSFilter;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class SpringInitializer extends AbstractSecurityWebApplicationInitializer {
	
	public SpringInitializer() {
		super(SecurityConfig.class, SpringConfig.class);
//	}
//	
//	/**
//	 *  gets invoked automatically when application context loads
//	 */
//	public void onStartup(ServletContext servletContext) throws ServletException {		
//		AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
//		ctx.register(SpringConfig.class);
		
//		FilterRegistration corsFilter = servletContext.addFilter("corsFilter", CORSFilter.class);
//		corsFilter.addMappingForUrlPatterns(null, true, "/*");
//
//		Dynamic servlet = servletContext.addServlet("springDispatcher", new DispatcherServlet(ctx));
//		servlet.addMapping("/");
//		servlet.setAsyncSupported(true);
//		servlet.setLoadOnStartup(1);
	}
	
}