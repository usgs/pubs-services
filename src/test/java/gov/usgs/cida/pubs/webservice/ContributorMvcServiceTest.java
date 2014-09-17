package gov.usgs.cida.pubs.webservice;

import gov.usgs.cida.pubs.BaseSpringTest;

import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author dmsibley
 */
@Ignore
@WebAppConfiguration
public class ContributorMvcServiceTest extends BaseSpringTest {
	private static final Logger log = LoggerFactory.getLogger(ContributorMvcServiceTest.class);

	@Autowired
	private WebApplicationContext wac;
	private MockMvc mockMvc;
}
