package gov.usgs.cida.pubs.busservice.ipds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;

import gov.usgs.cida.pubs.BaseIT;

public abstract class BaseIpdsTest extends BaseIT {

	@Autowired
	@Qualifier("costCenterXml")
	public String costCenterXml;

	@MockBean
	protected IpdsWsRequester ipdsWsRequester;

	@Autowired
	protected IpdsParserService ipdsParser;

}
