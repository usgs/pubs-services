package gov.usgs.cida.pubs.busservice.ipds;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;

@Category(IntegrationTest.class)
public abstract class BaseIpdsTest extends BaseSpringTest {

	@Autowired
	public String costCenterXml;

	@Mock
	protected IpdsWsRequester ipdsWsRequester;
	
	@Autowired
	protected IpdsParserService ipdsParser;
	
	@Before
	public void baseIdpsSetup() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
}