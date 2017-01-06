package gov.usgs.cida.pubs.busservice.ipds;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.xml.sax.SAXException;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.dao.AffiliationDaoTest;
import gov.usgs.cida.pubs.domain.CostCenter;

@Category(IntegrationTest.class)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/ipdsPubsTypeConv.xml"),
	@DatabaseSetup("classpath:/testData/dataset.xml")
})
public class IpdsCostCenterServiceTest extends BaseIpdsTest {

	@Autowired
	@Qualifier("costCenterBusService")
	private IBusService<CostCenter> costCenterBusService;
	
	private IpdsCostCenterService ipdsCostCenterService;

	@Before
	public void setup() {
		ipdsCostCenterService = new IpdsCostCenterService(ipdsParser, ipdsWsRequester, costCenterBusService);
	}

	@Test
	public void testGetCostCenter() throws SAXException, IOException {
		when(ipdsWsRequester.getCostCenter(anyString(), anyString())).thenReturn(costCenterXml);
		
		CostCenter affiliation = ipdsCostCenterService.getCostCenter("4");
		AffiliationDaoTest.assertAffiliation1(affiliation);
	}

	@Test
	public void createCostCenterTest() throws SAXException, IOException {
		when(ipdsWsRequester.getCostCenter(anyString(), anyString())).thenReturn(costCenterXml);
		
		String ipdsId = String.valueOf(randomPositiveInt());
		CostCenter costCenter = ipdsCostCenterService.createCostCenter(ipdsId);

		assertNotNull(costCenter.getId());
		assertEquals("CostCenter Test", costCenter.getText());
		assertTrue(costCenter.isActive());
		assertTrue(costCenter.isUsgs());
		assertEquals(ipdsId, costCenter.getIpdsId().toString());
	}
}