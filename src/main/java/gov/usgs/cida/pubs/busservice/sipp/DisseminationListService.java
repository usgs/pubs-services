package gov.usgs.cida.pubs.busservice.sipp;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.busservice.intfc.ISippProcess;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;

@Service
public class DisseminationListService {
	private static final Logger LOG = LoggerFactory.getLogger(DisseminationListService.class);
	private static ThreadLocal<StringBuilder> stringBuilder = new ThreadLocal<>();
	private static ThreadLocal<Integer> additions = new ThreadLocal<>();
	private static ThreadLocal<Integer> errors = new ThreadLocal<>();

	private final ConfigurationService configurationService;
	private final ISippProcess sippProcess;
	private final RestTemplate restTemplate;

	@Autowired
	DisseminationListService(
			final ConfigurationService configurationService,
			final ISippProcess sippProcess,
			final RestTemplate restTemplate
			) {
		this.configurationService = configurationService;
		this.sippProcess = sippProcess;
		this.restTemplate = restTemplate;
	}

	@Transactional
	public void processDisseminationList(final int daysLastDisseminated) {
		String messageText = getIpdsProductXml(daysLastDisseminated);

		IpdsMessageLog newMessage = new IpdsMessageLog();
		newMessage.setProcessType(ProcessType.DISSEMINATION);
		newMessage.setMessageText(messageText);
		IpdsMessageLog msg = IpdsMessageLog.getDao().getById(IpdsMessageLog.getDao().add(newMessage));

		String processingDetails = processLog(ProcessType.DISSEMINATION, msg.getId());
		msg.setProcessingDetails(processingDetails);
		IpdsMessageLog.getDao().update(msg);
	}

	protected String getIpdsProductXml(int daysLastDisseminated) {
		String productList = null;

		String publicationListUrl = configurationService.getDisseminationListUrl() + daysLastDisseminated;
		ResponseEntity<String> response = restTemplate.getForEntity(publicationListUrl, String.class);
		if (response.getStatusCode() == HttpStatus.OK
				&& response.hasBody()) {
			productList = response.getBody();
		} else {
			LOG.info("Error getting Dissemination List: {}", response.getStatusCodeValue());
		}
		return productList;
	}

	protected String processLog(final ProcessType inProcessType, final int logId) {
		setStringBuilder(new StringBuilder());
		setAdditions(0);
		setErrors(0);

		List<Map<String, Object>> ipdsPubs = IpdsMessageLog.getDao().getFromSipp(logId);

		for (Map<String, Object> ipdsPub : ipdsPubs) {
			ProcessSummary processSummary = sippProcess.processPublication(inProcessType, (String) ipdsPub.get(IpdsMessageLog.IPNUMBER));
			getStringBuilder().append(processSummary.getProcessingDetails());
			setAdditions(getAdditions() + processSummary.getAdditions());
			setErrors(getErrors() + processSummary.getErrors());
		}

		String counts = "Summary:\n\tTotal Entries: " + ipdsPubs.size() + "\n\tPublications Added: " + getAdditions() + "\n\tErrors Encountered: " + getErrors() + "\n\n";

		getStringBuilder().insert(0, counts);
		
		getStringBuilder().append("Log: " + logId + "\n");

		return getStringBuilder().toString();
	}

	public static StringBuilder getStringBuilder() {
		return stringBuilder.get();
	}

	public static void setStringBuilder(StringBuilder inStringBuilder) {
		stringBuilder.set(inStringBuilder);
	}

	public static Integer getAdditions() {
		return additions.get();
	}

	public static void setAdditions(Integer inAdditions) {
		additions.set(inAdditions);
	}

	public static Integer getErrors() {
		return errors.get();
	}

	public static void setErrors(Integer inErrors) {
		errors.set(inErrors);
	}

}
