package gov.usgs.cida.pubs.dao.sipp;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.domain.InformationProductHelper;
import gov.usgs.cida.pubs.domain.sipp.InformationProduct;

public class InformationProductDaoTest extends BaseTest {
	public static final String MOCK_SIPP_URL = "https://localhost/mock?a=";

	@Mock
	protected RestTemplate restTemplate;
	@Mock
	protected ConfigurationService configurationService;

	private InformationProductDao dao;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		dao = new InformationProductDao(configurationService, restTemplate);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getInformationProductTest() throws Exception {
		when(configurationService.getInfoProductUrl()).thenReturn(MOCK_SIPP_URL);
		when(restTemplate.getForEntity(MOCK_SIPP_URL+"1", InformationProduct.class))
			.thenReturn(new ResponseEntity<InformationProduct>(getDisseminationFromXml(), HttpStatus.OK),
				new ResponseEntity<InformationProduct>(getDisseminationFromXml(), HttpStatus.BAD_REQUEST));

		//Found it
		assertDaoTestResults(
				InformationProduct.class,
				InformationProductHelper.getInformationProduct108541(),
				dao.getInformationProduct("1"),
				null, false, false, true
				);

		//Bad response
		assertNull(dao.getInformationProduct("1"));
	}

	public InformationProduct getDisseminationFromXml() throws Exception {
		XmlMapper xmlMapper = new XmlMapper();
		return xmlMapper.readValue(getFile("testData/sipp/dissemination.xml"), InformationProduct.class);
	}

}
