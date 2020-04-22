package gov.usgs.cida.pubs.busservice.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.CostCenterHelper;
import gov.usgs.cida.pubs.domain.OutsideAffiliation;
import gov.usgs.cida.pubs.domain.OutsideAffiliationHelper;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes= {CostCenterHelper.class})
public class ExtAffiliationBusServiceTest extends BaseTest {

	@MockBean(name="costCenterBusService")
	protected IBusService<CostCenter> costCenterBusService;
	@MockBean(name="outsideAffiliationBusService")
	protected IBusService<OutsideAffiliation> outsideAffiliationBusService;

	protected ExtAffiliationBusService extAffiliationBusService;
	protected List<CostCenter> emptyCostCenterList = new ArrayList<>();
	protected List<OutsideAffiliation> emptyOutsideAffiliationList = new ArrayList<>();

	@BeforeEach
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		extAffiliationBusService = new ExtAffiliationBusService(costCenterBusService, outsideAffiliationBusService);
		reset(costCenterBusService, outsideAffiliationBusService);
	}

	@Test
	public void createCostCenterTest() {
		when(costCenterBusService.createObject(any(CostCenter.class))).thenReturn(CostCenterHelper.UPPER_MIDWEST_WSC);
		CostCenter costCenter = new CostCenter();

		assertNull(extAffiliationBusService.createCostCenter(costCenter));
		verify(costCenterBusService, never()).createObject(any(CostCenter.class));

		costCenter.setText(CostCenterHelper.UPPER_MIDWEST_WSC.getText());
		assertEquals(CostCenterHelper.UPPER_MIDWEST_WSC, extAffiliationBusService.createCostCenter(costCenter));
		verify(costCenterBusService).createObject(any(CostCenter.class));
	}

	@Test
	public void createOutsideAffiliationTest() {
		when(outsideAffiliationBusService.createObject(any(OutsideAffiliation.class))).thenReturn(OutsideAffiliationHelper.UNIVERSITY_HAWAII_HILO);
		OutsideAffiliation outsideAffiliation = new OutsideAffiliation();

		assertNull(extAffiliationBusService.createOutsideAffiliation(outsideAffiliation));
		verify(outsideAffiliationBusService, never()).createObject(any(OutsideAffiliation.class));

		outsideAffiliation.setText(OutsideAffiliationHelper.UNIVERSITY_HAWAII_HILO.getText());
		assertEquals(OutsideAffiliationHelper.UNIVERSITY_HAWAII_HILO, extAffiliationBusService.createOutsideAffiliation(outsideAffiliation));
		verify(outsideAffiliationBusService).createObject(any(OutsideAffiliation.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getCostCenterTest() {
		when(costCenterBusService.getObjects(anyMap())).thenReturn(
				emptyCostCenterList,
				List.of(CostCenterHelper.UPPER_MIDWEST_WSC),
				List.of(CostCenterHelper.VOLCANO_HAZARDS_PROGRAM, CostCenterHelper.UPPER_MIDWEST_WSC)
				);
		CostCenter costCenter = new CostCenter();

		assertNull(extAffiliationBusService.getCostCenter(costCenter));
		verify(costCenterBusService, never()).getObjects(anyMap());

		costCenter.setText(CostCenterHelper.UPPER_MIDWEST_WSC.getText());
		assertNull(extAffiliationBusService.getCostCenter(costCenter));
		verify(costCenterBusService).getObjects(anyMap());

		assertEquals(CostCenterHelper.UPPER_MIDWEST_WSC, extAffiliationBusService.getCostCenter(costCenter));
		verify(costCenterBusService, times(2)).getObjects(anyMap());

		assertEquals(CostCenterHelper.VOLCANO_HAZARDS_PROGRAM, extAffiliationBusService.getCostCenter(costCenter));
		verify(costCenterBusService, times(3)).getObjects(anyMap());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getOutsideAffiliationTest() {
		when(outsideAffiliationBusService.getObjects(anyMap())).thenReturn(
				emptyOutsideAffiliationList,
				List.of(OutsideAffiliationHelper.UNIVERSITY_HAWAII_HILO),
				List.of(OutsideAffiliationHelper.UNIVERSITY_WISCONSIN, OutsideAffiliationHelper.UNIVERSITY_HAWAII_HILO)
				);
		OutsideAffiliation outsideAffiliation = new OutsideAffiliation();

		assertNull(extAffiliationBusService.getOutsideAffiliation(outsideAffiliation));
		verify(outsideAffiliationBusService, never()).getObjects(anyMap());

		outsideAffiliation.setText(OutsideAffiliationHelper.UNIVERSITY_HAWAII_HILO.getText());
		assertNull(extAffiliationBusService.getOutsideAffiliation(outsideAffiliation));
		verify(outsideAffiliationBusService).getObjects(anyMap());

		assertEquals(OutsideAffiliationHelper.UNIVERSITY_HAWAII_HILO, extAffiliationBusService.getOutsideAffiliation(outsideAffiliation));
		verify(outsideAffiliationBusService, times(2)).getObjects(anyMap());

		assertEquals(OutsideAffiliationHelper.UNIVERSITY_WISCONSIN, extAffiliationBusService.getOutsideAffiliation(outsideAffiliation));
		verify(outsideAffiliationBusService, times(3)).getObjects(anyMap());
	}

	@Test
	public void processAffiliationsTest() {
		when(outsideAffiliationBusService.getObjects(anyMap())).thenReturn(emptyOutsideAffiliationList);
		when(outsideAffiliationBusService.createObject(any(OutsideAffiliation.class))).thenReturn(
				OutsideAffiliationHelper.UNIVERSITY_HAWAII_HILO,
				OutsideAffiliationHelper.UNIVERSITY_WISCONSIN
				);
		when(costCenterBusService.getObjects(anyMap())).thenReturn(emptyCostCenterList);
		when(costCenterBusService.createObject(any(CostCenter.class))).thenReturn(
				CostCenterHelper.UPPER_MIDWEST_WSC,
				CostCenterHelper.VOLCANO_HAZARDS_PROGRAM,
				null
				);

		Collection<Affiliation<? extends Affiliation<?>>> affiliations = 
				List.of(OutsideAffiliationHelper.UNIVERSITY_HAWAII_HILO, CostCenterHelper.UPPER_MIDWEST_WSC,
						OutsideAffiliationHelper.UNIVERSITY_WISCONSIN, CostCenterHelper.VOLCANO_HAZARDS_PROGRAM,
						new CostCenter());

		//nothing here
		assertNull(extAffiliationBusService.processAffiliations(new ArrayList<>()));

		//aha
		Set<Affiliation<? extends Affiliation<?>>> processed = extAffiliationBusService.processAffiliations(affiliations);
		assertEquals(4, processed.size());
		assertTrue(processed.contains(OutsideAffiliationHelper.UNIVERSITY_HAWAII_HILO));
		assertTrue(processed.contains(OutsideAffiliationHelper.UNIVERSITY_WISCONSIN));
		assertTrue(processed.contains(CostCenterHelper.UPPER_MIDWEST_WSC));
		assertTrue(processed.contains(CostCenterHelper.VOLCANO_HAZARDS_PROGRAM));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void processCostCenterTest() {
		when(costCenterBusService.getObjects(anyMap())).thenReturn(
				List.of(CostCenterHelper.UPPER_MIDWEST_WSC),
				emptyCostCenterList
				);
		when(costCenterBusService.createObject(any(CostCenter.class))).thenReturn(CostCenterHelper.UPPER_MIDWEST_WSC);

		//nothing here
		assertNull(extAffiliationBusService.processCostCenter(null));
		verify(costCenterBusService, never()).getObjects(anyMap());
		verify(costCenterBusService, never()).createObject(any(CostCenter.class));

		//find it
		assertEquals(CostCenterHelper.UPPER_MIDWEST_WSC, extAffiliationBusService.processCostCenter(CostCenterHelper.UPPER_MIDWEST_WSC));
		verify(costCenterBusService).getObjects(anyMap());
		verify(costCenterBusService, never()).createObject(any(CostCenter.class));

		//create it
		assertEquals(CostCenterHelper.UPPER_MIDWEST_WSC, extAffiliationBusService.processCostCenter(CostCenterHelper.UPPER_MIDWEST_WSC));
		verify(costCenterBusService, times(2)).getObjects(anyMap());
		verify(costCenterBusService).createObject(any(CostCenter.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void processOutsideAffiliationTest() {
		when(outsideAffiliationBusService.getObjects(anyMap())).thenReturn(
				List.of(OutsideAffiliationHelper.UNIVERSITY_HAWAII_HILO),
				emptyOutsideAffiliationList
				);
		when(outsideAffiliationBusService.createObject(any(OutsideAffiliation.class))).thenReturn(OutsideAffiliationHelper.UNIVERSITY_HAWAII_HILO);

		//nothing here
		assertNull(extAffiliationBusService.processOutsideAffiliation(null));
		verify(outsideAffiliationBusService, never()).getObjects(anyMap());
		verify(outsideAffiliationBusService, never()).createObject(any(OutsideAffiliation.class));

		//find it
		assertEquals(OutsideAffiliationHelper.UNIVERSITY_HAWAII_HILO, extAffiliationBusService.processOutsideAffiliation(OutsideAffiliationHelper.UNIVERSITY_HAWAII_HILO));
		verify(outsideAffiliationBusService).getObjects(anyMap());
		verify(outsideAffiliationBusService, never()).createObject(any(OutsideAffiliation.class));

		//create it
		assertEquals(OutsideAffiliationHelper.UNIVERSITY_HAWAII_HILO, extAffiliationBusService.processOutsideAffiliation(OutsideAffiliationHelper.UNIVERSITY_HAWAII_HILO));
		verify(outsideAffiliationBusService, times(2)).getObjects(anyMap());
		verify(outsideAffiliationBusService).createObject(any(OutsideAffiliation.class));
	}
}
