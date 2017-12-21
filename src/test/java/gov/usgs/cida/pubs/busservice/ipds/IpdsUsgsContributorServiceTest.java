package gov.usgs.cida.pubs.busservice.ipds;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.dao.ContributorDaoTest;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;

@Category(IntegrationTest.class)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/ipdsPubsTypeConv.xml"),
	@DatabaseSetup("classpath:/testData/dataset.xml")
})
public class IpdsUsgsContributorServiceTest extends BaseIpdsTest {

	@Autowired
	public String usgsContributorXml;

	@Autowired
	@Qualifier("personContributorBusService")
	private IBusService<PersonContributor<?>> personContributorBusService;

	private IpdsUsgsContributorService ipdsUsgsContributorService;

	@Before
	public void setup() {
		ipdsUsgsContributorService = new IpdsUsgsContributorService(ipdsParser, ipdsWsRequester, personContributorBusService);
	}

	@Test
	public void getUsgsContributorTest() throws SAXException, IOException {
		Document d = ipdsParser.makeDocument("<root><d:AuthorNameId>3</d:AuthorNameId></root>");
		UsgsContributor contributor = ipdsUsgsContributorService.getContributor(d.getDocumentElement());
		ContributorDaoTest.assertContributor1(contributor);
	}

	@Test
	public void createUsgsContributorTest() throws SAXException, IOException {
		when(ipdsWsRequester.getContributor(anyString(), null)).thenReturn(usgsContributorXml);
		Document d = ipdsParser.makeDocument("<root><d:AuthorNameId>1</d:AuthorNameId></root>");
		UsgsContributor contributor = ipdsUsgsContributorService.createContributor(d.getDocumentElement(), null);
		assertNotNull(contributor);
		assertNotNull(contributor.getId());
		assertUsgsContributorData(contributor);
	}

	@Test
	public void bindContributorTest() throws SAXException, IOException, ParserConfigurationException {
		UsgsContributor contributor = ipdsUsgsContributorService.bindContributor(usgsContributorXml);
		assertNotNull(contributor);
		assertNull(contributor.getId());
		assertUsgsContributorData(contributor);
	}

	private void assertUsgsContributorData(UsgsContributor contributor) {
		assertEquals("Jane", contributor.getGiven());
		assertEquals("Doe", contributor.getFamily());
		assertEquals("jmdoe@usgs.gov", contributor.getEmail());
		assertEquals(123, contributor.getIpdsContributorId().intValue());
	}
}