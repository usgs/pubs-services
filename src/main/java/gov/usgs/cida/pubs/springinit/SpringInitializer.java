package gov.usgs.cida.pubs.springinit;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

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