package gov.usgs.cida.pubs.busservice.ipds;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.xml.sax.SAXException;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.busservice.CostCenterBusService;
import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.dao.AffiliationDao;
import gov.usgs.cida.pubs.dao.AffiliationDaoIT;
import gov.usgs.cida.pubs.dao.CostCenterDao;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.springinit.DbTestConfig;
import gov.usgs.cida.pubs.springinit.TestSpringConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, CostCenterBusService.class, LocalValidatorFactoryBean.class,
			TestSpringConfig.class, IpdsParserService.class, Affiliation.class,
			AffiliationDao.class, CostCenter.class, CostCenterDao.class})
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/ipdsPubsTypeConv.xml"),
	@DatabaseSetup("classpath:/testData/dataset.xml")
})
public class IpdsCostCenterServiceIT extends BaseIpdsTest {

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
		when(ipdsWsRequester.getCostCenter(anyInt(), anyInt())).thenReturn(costCenterXml);
		
		CostCenter affiliation = ipdsCostCenterService.getCostCenter(4);
		AffiliationDaoIT.assertAffiliation1(affiliation);
	}

	@Test
	public void createCostCenterTest() throws SAXException, IOException {
		when(ipdsWsRequester.getCostCenter(anyInt(), anyInt())).thenReturn(costCenterXml);

		Integer ipdsId = randomPositiveInt();
		CostCenter costCenter = ipdsCostCenterService.createCostCenter(ipdsId);

		assertNotNull(costCenter.getId());
		assertEquals("CostCenter Test", costCenter.getText());
		assertTrue(costCenter.isActive());
		assertTrue(costCenter.isUsgs());
		assertEquals(ipdsId, costCenter.getIpdsId());
	}
}