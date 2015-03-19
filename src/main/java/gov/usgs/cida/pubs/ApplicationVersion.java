package gov.usgs.cida.pubs;

import java.util.Properties;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;

public class ApplicationVersion implements ServletContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationVersion.class);

    private static ServletContext servletContext;

    public static String getVersion() {
        StringBuilder currentVersion = new StringBuilder("Application Version: ");
        try {
            String name = "/META-INF/MANIFEST.MF";
            Properties props = new Properties();
            props.load(servletContext.getResourceAsStream(name));
            String projectVersion = (String) props.get("Project-Version");
            currentVersion.append(projectVersion);
            if (projectVersion.endsWith("-SNAPSHOT")) {
            	currentVersion.append(" Built at: " + (String) props.get("BuildTime"));
            	currentVersion.append(" From commit: " + (String) props.get("Implementation-Build"));
            }
        } catch (Exception e) {
            LOG.info("unable to get application version", e);
            currentVersion.append("Unavailable");
        }
        return currentVersion.toString();
    }

    public void setServletContext(final ServletContext inServletContext) {
        servletContext = inServletContext;
    }

}
