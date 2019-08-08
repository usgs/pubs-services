package gov.usgs.cida.pubs.springinit;

import java.time.LocalDateTime;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import gov.usgs.cida.pubs.busservice.sipp.DisseminationListService;

@Configuration
@EnableScheduling
public class DisseminationSchedulingConfig {
	private static final Logger LOG = LoggerFactory.getLogger(DisseminationSchedulingConfig.class);

	@Autowired
	private DisseminationListService disseminationListService;
	@Value("${sipp.dissemination.daysLastDisseminated}")
	private int daysLastDisseminated;

	@Bean
	public Executor getTaskExecutor() {
		return Executors.newScheduledThreadPool(10);
	}

	@Scheduled(cron = "${sipp.dissemination.schedule}")
	public void scheduleTaskWithCronExpression() {
		LOG.info("DisseminationListService.processDisseminationList :: Execution Start Time - {}", LocalDateTime.now());
		disseminationListService.processDisseminationList(daysLastDisseminated);
		LOG.info("DisseminationListService.processDisseminationList :: Execution End Time - {}", LocalDateTime.now());
	}

}
