package gov.usgs.cida.pubs.dao.sipp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.domain.sipp.IpdsBureauApproval;

@Repository
public class IpdsBureauApprovalDao {
	private static final Logger LOG = LoggerFactory.getLogger(IpdsBureauApprovalDao.class);
	private final ConfigurationService configurationService;
	private final RestTemplate restTemplate;

	public IpdsBureauApprovalDao(
			final ConfigurationService configurationService,
			final RestTemplate restTemplate
			) {
		this.configurationService = configurationService;
		this.restTemplate = restTemplate;
	}

	public List<IpdsBureauApproval> getIpdsBureauApprovals(int daysLastDisseminated) {
		List<IpdsBureauApproval> ipdsBureauApprovals = null;
		String infoProductUrl = configurationService.getDisseminationListUrl() + daysLastDisseminated;
		ResponseEntity<List<IpdsBureauApproval>> response = restTemplate.exchange(
				infoProductUrl,
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<IpdsBureauApproval>>(){});
		if (response.getStatusCode() == HttpStatus.OK
				&& response.hasBody()) {
			ipdsBureauApprovals = response.getBody();
		} else {
			LOG.info("Error getting InfoProduct:{} : {}", daysLastDisseminated, response.getStatusCodeValue());
		}
		return ipdsBureauApprovals;
	}

}
