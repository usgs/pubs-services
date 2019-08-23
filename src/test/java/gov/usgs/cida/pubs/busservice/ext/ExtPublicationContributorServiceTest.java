package gov.usgs.cida.pubs.busservice.ext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.dao.intfc.IPersonContributorDao;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.CostCenterHelper;
import gov.usgs.cida.pubs.domain.OutsideAffiliationHelper;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.OutsideContributorHelper;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationContributorHelper;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.domain.UsgsContributorHelper;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes= {OutsideContributor.class})
public class ExtPublicationContributorServiceTest extends BaseTest {

	@MockBean(name="contributorDao")
	protected IDao<Contributor<?>> contributorDao;
	@MockBean(name="extAffiliationBusService")
	protected ExtAffiliationBusService extAffiliationBusService;
	@MockBean(name="personContributorDao")
	protected IPersonContributorDao personContributorDao;
	@MockBean(name="personContributorBusService")
	protected IBusService<PersonContributor<?>> personContributorBusService;

	protected ExtPublicationContributorService extPublicationContributorService;
	protected List<Contributor<?>> emptyContributorList = new ArrayList<>();
	protected PersonContributor<?> personContributor = new PersonContributor<>();

	@Before
	public void setUp() throws Exception {
		extPublicationContributorService = new ExtPublicationContributorService(extAffiliationBusService, personContributorBusService);
		reset(contributorDao, extAffiliationBusService, personContributorBusService, personContributorDao);
	}

	@Test
	public void processPublicationContributorsTest() {
		when(personContributorDao.getByPreferred(any(UsgsContributor.class))).thenReturn(List.of(UsgsContributorHelper.JANE_N_DOE));
		when(personContributorDao.getByMap(anyMap())).thenReturn(List.of(OutsideContributorHelper.JANE_M_DOE));
		Collection<PublicationContributor<?>> publicationContributors = new ArrayList<>();

		//empty list
		extPublicationContributorService.processPublicationContributors(publicationContributors);
		assertTrue(publicationContributors.isEmpty());
		verify(personContributorDao, never()).getByPreferred(any(UsgsContributor.class));
		verify(personContributorDao, never()).getByMap(anyMap());

		//all gouda
		publicationContributors.add(PublicationContributorHelper.OUTSIDE_AUTHOR);
		publicationContributors.add(PublicationContributorHelper.USGS_AUTHOR);
		extPublicationContributorService.processPublicationContributors(publicationContributors);
		assertEquals(2, publicationContributors.size());
		verify(personContributorDao).getByPreferred(any(UsgsContributor.class));
		verify(personContributorDao).getByMap(anyMap());
	}

	@Test
	public void processPublicationContributorTest() {
		when(personContributorDao.getByPreferred(any(UsgsContributor.class))).thenReturn(List.of(UsgsContributorHelper.JANE_N_DOE));
		when(personContributorDao.getByMap(anyMap())).thenReturn(List.of(OutsideContributorHelper.JANE_M_DOE));
		PublicationContributor<?> publicationContributor = new PublicationContributor<>();

		//null contributor
		assertNull(extPublicationContributorService.processPublicationContributor(publicationContributor).getContributor());
		verify(personContributorDao, never()).getByPreferred(any(UsgsContributor.class));
		verify(personContributorDao, never()).getByMap(anyMap());

		//usgs contributor
		publicationContributor.setContributor(UsgsContributorHelper.JANE_N_DOE);
		assertEquals(UsgsContributorHelper.JANE_N_DOE, extPublicationContributorService.processPublicationContributor(publicationContributor).getContributor());
		verify(personContributorDao).getByPreferred(any(UsgsContributor.class));
		verify(personContributorDao, never()).getByMap(anyMap());

		//outside contributor
		publicationContributor.setContributor(OutsideContributorHelper.JANE_M_DOE);
		assertEquals(OutsideContributorHelper.JANE_M_DOE, extPublicationContributorService.processPublicationContributor(publicationContributor).getContributor());
		verify(personContributorDao).getByPreferred(any(UsgsContributor.class));
		verify(personContributorDao).getByMap(anyMap());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void processUsgsContributorTest() {
		when(personContributorDao.getByPreferred(any(UsgsContributor.class))).thenReturn(
				emptyContributorList,
				List.of(UsgsContributorHelper.JANE_N_DOE)
				);
		doReturn(UsgsContributorHelper.JANE_DOE).when(personContributorBusService).createObject(any(PersonContributor.class));
		UsgsContributor contributor = new UsgsContributor();

		//orcid null
		assertEquals(UsgsContributorHelper.JANE_DOE, extPublicationContributorService.processUsgsContributor(contributor));
		verify(personContributorDao, never()).getByPreferred(any(UsgsContributor.class));
		verify(personContributorBusService).createObject(any(PersonContributor.class));

		//not found
		contributor.setOrcid("0000-0000-0000-0001");
		assertEquals(UsgsContributorHelper.JANE_DOE, extPublicationContributorService.processUsgsContributor(contributor));
		verify(personContributorDao).getByPreferred(any(UsgsContributor.class));
		verify(personContributorBusService, times(2)).createObject(any(PersonContributor.class));

		//found
		contributor.setOrcid("0000-0000-0000-0001");
		assertEquals(UsgsContributorHelper.JANE_N_DOE, extPublicationContributorService.processUsgsContributor(contributor));
		verify(personContributorDao, times(2)).getByPreferred(any(UsgsContributor.class));
		verify(personContributorBusService, times(2)).createObject(any(PersonContributor.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getUsgsContributorByOrcidTest() {
		when(personContributorDao.getByPreferred(any(UsgsContributor.class))).thenReturn(
				emptyContributorList,
				List.of(UsgsContributorHelper.JANE_DOE),
				List.of(UsgsContributorHelper.JANE_N_DOE, UsgsContributorHelper.JANE_DOE)
				);

		//Not found
		assertNull(extPublicationContributorService.getUsgsContributorByOrcid("0000-0000-0000-0001"));
		verify(personContributorDao).getByPreferred(any(UsgsContributor.class));

		//One Hit
		assertEquals(UsgsContributorHelper.JANE_DOE, extPublicationContributorService.getUsgsContributorByOrcid("0000-0000-0000-0001"));
		verify(personContributorDao, times(2)).getByPreferred(any(UsgsContributor.class));

		//Multiple hits
		assertEquals(UsgsContributorHelper.JANE_N_DOE, extPublicationContributorService.getUsgsContributorByOrcid("0000-0000-0000-0001"));
		verify(personContributorDao, times(3)).getByPreferred(any(UsgsContributor.class));
	}

	@Test
	public void createUsgsContributorTest() {
		//This test is showing that the contributor's collection of affiliations has been replaced with the
		//set created by calling extAffiliationBusService.processAffiliations - ie the set of valid affiliations
		//with appropriate id's
		ArgumentCaptor<UsgsContributor> valueCapture = ArgumentCaptor.forClass(UsgsContributor.class);
		when(extAffiliationBusService.processAffiliations(anyCollection())).thenReturn(Set.of(CostCenterHelper.VOLCANO_HAZARDS_PROGRAM));
		when(personContributorBusService.createObject(valueCapture.capture())).thenReturn(null);
		doReturn(UsgsContributorHelper.JANE_DOE).when(personContributorBusService).createObject(valueCapture.capture());

		assertEquals(UsgsContributorHelper.JANE_DOE, extPublicationContributorService.createUsgsContributor(UsgsContributorHelper.JANE_DOE));
		assertEquals(1, valueCapture.getValue().getAffiliations().size());
		assertTrue(valueCapture.getValue().getAffiliations().contains(CostCenterHelper.VOLCANO_HAZARDS_PROGRAM));
		verify(extAffiliationBusService).processAffiliations(anyCollection());
		verify(personContributorBusService).createObject(any(PersonContributor.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void processOutsideContributorTest() {
		when(personContributorDao.getByMap(anyMap())).thenReturn(
				emptyContributorList,								//orcid null - not found by name
				List.of(OutsideContributorHelper.JANE_Q_ODOUL),		//orcid null - found by name
				List.of(OutsideContributorHelper.JANE_M_DOE),		//found by orcid
				emptyContributorList,								//not found by orcid - found by name (orcid call)
				List.of(OutsideContributorHelper.JANE_Q_ODOUL)		//not found by orcid - found by name (name call)
				);
		doReturn(OutsideContributorHelper.JANE_M_DOE).when(personContributorBusService).createObject(any(PersonContributor.class));
		OutsideContributor contributor = new OutsideContributor();

		//all null
		assertEquals(OutsideContributorHelper.JANE_M_DOE, extPublicationContributorService.processOutsideContributor(contributor));
		verify(personContributorDao, never()).getByMap(anyMap());
		verify(personContributorBusService).createObject(any(PersonContributor.class));

		//orcid null - family null
		contributor.setGiven("given");
		assertEquals(OutsideContributorHelper.JANE_M_DOE, extPublicationContributorService.processOutsideContributor(contributor));
		verify(personContributorDao, never()).getByMap(anyMap());
		verify(personContributorBusService, times(2)).createObject(any(PersonContributor.class));

		//orcid null - given null
		contributor.setFamily("family");
		contributor.setGiven(null);
		assertEquals(OutsideContributorHelper.JANE_M_DOE, extPublicationContributorService.processOutsideContributor(contributor));
		verify(personContributorDao, never()).getByMap(anyMap());
		verify(personContributorBusService, times(3)).createObject(any(PersonContributor.class));

		//orcid null - not found by name
		contributor.setGiven("given");
		assertEquals(OutsideContributorHelper.JANE_M_DOE, extPublicationContributorService.processOutsideContributor(contributor));
		verify(personContributorDao).getByMap(anyMap());
		verify(personContributorBusService, times(4)).createObject(any(PersonContributor.class));

		//orcid null - found by name
		assertEquals(OutsideContributorHelper.JANE_Q_ODOUL, extPublicationContributorService.processOutsideContributor(contributor));
		verify(personContributorDao, times(2)).getByMap(anyMap());
		verify(personContributorBusService, times(4)).createObject(any(PersonContributor.class));

		//found by orcid
		contributor.setOrcid("0000-0000-0000-0001");
		assertEquals(OutsideContributorHelper.JANE_M_DOE, extPublicationContributorService.processOutsideContributor(contributor));
		verify(personContributorDao, times(3)).getByMap(anyMap());
		verify(personContributorBusService, times(4)).createObject(any(PersonContributor.class));

		//not found by orcid - found by name
		assertEquals(OutsideContributorHelper.JANE_Q_ODOUL, extPublicationContributorService.processOutsideContributor(contributor));
		verify(personContributorDao, times(5)).getByMap(anyMap());
		verify(personContributorBusService, times(4)).createObject(any(PersonContributor.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getOutsideContributorByOrcidTest() {
		when(personContributorDao.getByMap(anyMap())).thenReturn(
				emptyContributorList,
				List.of(OutsideContributorHelper.JANE_M_DOE),
				List.of(OutsideContributorHelper.JANE_Q_ODOUL, OutsideContributorHelper.JANE_M_DOE)
				);

		//Not found
		assertNull(extPublicationContributorService.getOutsideContributorByOrcid("0000-0000-0000-0001"));
		verify(personContributorDao).getByMap(anyMap());

		//One Hit
		assertEquals(OutsideContributorHelper.JANE_M_DOE, extPublicationContributorService.getOutsideContributorByOrcid("0000-0000-0000-0001"));
		verify(personContributorDao, times(2)).getByMap(anyMap());

		//Multiple hits
		assertEquals(OutsideContributorHelper.JANE_Q_ODOUL, extPublicationContributorService.getOutsideContributorByOrcid("0000-0000-0000-0001"));
		verify(personContributorDao, times(3)).getByMap(anyMap());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getByNameTest() {
		when(personContributorDao.getByMap(anyMap())).thenReturn(
				emptyContributorList,
				List.of(OutsideContributorHelper.JANE_M_DOE),
				List.of(OutsideContributorHelper.JANE_Q_ODOUL, OutsideContributorHelper.JANE_M_DOE)
				);

		//Not found
		assertNull(extPublicationContributorService.getByName("family", "given"));
		verify(personContributorDao).getByMap(anyMap());

		//One Hit
		assertEquals(OutsideContributorHelper.JANE_M_DOE, extPublicationContributorService.getByName("family", "given"));
		verify(personContributorDao, times(2)).getByMap(anyMap());

		//Multiple hits
		assertEquals(OutsideContributorHelper.JANE_Q_ODOUL, extPublicationContributorService.getByName("family", "given"));
		verify(personContributorDao, times(3)).getByMap(anyMap());
	}

	@Test
	public void createOutsideContributorTest() {
		//This test is showing that the contributor's collection of affiliations has been replaced with the
		//set created by calling extAffiliationBusService.processAffiliations - ie the set of valid affiliations
		//with appropriate id's
		ArgumentCaptor<OutsideContributor> valueCapture = ArgumentCaptor.forClass(OutsideContributor.class);
		when(extAffiliationBusService.processAffiliations(anyCollection())).thenReturn(Set.of(OutsideAffiliationHelper.UNIVERSITY_HAWAII_HILO));
		doReturn(OutsideContributorHelper.JANE_M_DOE).when(personContributorBusService).createObject(valueCapture.capture());

		assertEquals(OutsideContributorHelper.JANE_M_DOE, extPublicationContributorService.createOutsideContributor(OutsideContributorHelper.JANE_M_DOE));
		assertEquals(1, valueCapture.getValue().getAffiliations().size());
		assertTrue(valueCapture.getValue().getAffiliations().contains(OutsideAffiliationHelper.UNIVERSITY_HAWAII_HILO));
		verify(extAffiliationBusService).processAffiliations(anyCollection());
		verify(personContributorBusService).createObject(any(PersonContributor.class));
	}
}
