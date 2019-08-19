package gov.usgs.cida.pubs.dao.sipp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.domain.sipp.InformationProduct;

@Repository
public class InformationProductDao {
	private static final Logger LOG = LoggerFactory.getLogger(InformationProductDao.class);
	private final ConfigurationService configurationService;
	private final RestTemplate restTemplate;

	public InformationProductDao(
			final ConfigurationService configurationService,
			final RestTemplate restTemplate
			) {
		this.configurationService = configurationService;
		this.restTemplate = restTemplate;
	}

	public InformationProduct getInformationProduct(String ipNumber) {
		InformationProduct informationProduct = null;
		String infoProductUrl = configurationService.getInfoProductUrl() + ipNumber;
		ResponseEntity<InformationProduct> response = restTemplate.getForEntity(infoProductUrl, InformationProduct.class);
		if (response.getStatusCode() == HttpStatus.OK
				&& response.hasBody()) {
			informationProduct = response.getBody();
		} else {
			LOG.info("Error getting InfoProduct:{} : {}", ipNumber, response.getStatusCodeValue());
		}
		return informationProduct;
	}
}
