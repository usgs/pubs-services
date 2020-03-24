package gov.usgs.cida.pubs.busservice.ext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDaoIT;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.CostCenterHelper;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.PublicationCostCenterHelper;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.ValidationResults;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={CostCenterHelper.class})
public class ExtPublicationServiceTest extends BaseTest {

	@MockBean(name="extAffiliationBusService")
	protected ExtAffiliationBusService extAffiliationBusService;
	@MockBean(name="extPublicationContributorBusService")
	protected ExtPublicationContributorService extPublicationContributorBusService;
	@MockBean(name="pubBusService")
	protected IMpPublicationBusService pubBusService;

	protected ExtPublicationService extPublicationService;
	protected List<Contributor<?>> emptyContributorList = new ArrayList<>();
	protected PersonContributor<?> personContributor = new PersonContributor<>();

	@Before
	public void setUp() throws Exception {
		extPublicationService = new ExtPublicationService(extAffiliationBusService, extPublicationContributorBusService, pubBusService);
		reset(extAffiliationBusService, extPublicationContributorBusService, pubBusService);
	}

	@Test
	public void createTest() {
		when(extPublicationContributorBusService.processPublicationContributors(anyCollection())).thenReturn(new ValidationResults());
		when(extAffiliationBusService.processCostCenter(any(CostCenter.class))).thenReturn(new CostCenter());
		when(pubBusService.createObject(any(MpPublication.class))).thenReturn(new MpPublication());
		extPublicationService.create(MpPublicationDaoIT.buildAPub(12));
		verify(extPublicationContributorBusService).processPublicationContributors(anyCollection());
		verify(extAffiliationBusService).processCostCenter(any(CostCenter.class));
		verify(pubBusService).createObject(any(MpPublication.class));
	}

	@Test
	public void processCostCentersTest() {
		when(extAffiliationBusService.processCostCenter(any(CostCenter.class))).thenReturn(
				CostCenterHelper.UPPER_MIDWEST_WSC,
				CostCenterHelper.VOLCANO_HAZARDS_PROGRAM);
		Collection<PublicationCostCenter<?>> publicationCostCenters = new LinkedList<>();
		extPublicationService.processCostCenters(publicationCostCenters);
		assertTrue(publicationCostCenters.isEmpty());

		publicationCostCenters.add(PublicationCostCenterHelper.PSC_VHP);
		publicationCostCenters.add(PublicationCostCenterHelper.PSC_UMW);
		extPublicationService.processCostCenters(publicationCostCenters);
		//They flipped in this test because of the mock return values - would not in real life
		assertEquals(CostCenterHelper.UPPER_MIDWEST_WSC, publicationCostCenters.toArray(new PublicationCostCenter[2])[0].getCostCenter());
		assertEquals(CostCenterHelper.VOLCANO_HAZARDS_PROGRAM, publicationCostCenters.toArray(new PublicationCostCenter[2])[1].getCostCenter());
		verify(extAffiliationBusService, times(2)).processCostCenter(any(CostCenter.class));
	}

	@Test
	public void processPublicationCostCenterTest() {
		when(extAffiliationBusService.processCostCenter(any(CostCenter.class))).thenReturn(CostCenterHelper.VOLCANO_HAZARDS_PROGRAM);
		PublicationCostCenter<?> publicationCostCenter = new PublicationCostCenter<>();
		publicationCostCenter.setCostCenter(new CostCenter());
		extPublicationService.processPublicationCostCenter(publicationCostCenter);
		assertEquals(CostCenterHelper.VOLCANO_HAZARDS_PROGRAM, publicationCostCenter.getCostCenter());
		verify(extAffiliationBusService).processCostCenter(any(CostCenter.class));
	}
}
