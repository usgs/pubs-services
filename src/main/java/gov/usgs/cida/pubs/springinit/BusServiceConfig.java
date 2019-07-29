package gov.usgs.cida.pubs.springinit;

import java.io.IOException;

import org.apache.http.auth.NTCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import gov.usgs.cida.pubs.ConfigurationService;

@Configuration
public class BusServiceConfig {

	@Autowired
	ConfigurationService configurationService;

	@Value("classpath:templates/crossref.4.3.3/numberedSeries.xml")
	private Resource numberedSeriesResource;

	@Bean
	public String numberedSeriesXml() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(numberedSeriesResource.getInputStream()));
	}

	@Value("classpath:templates/crossref.4.3.3/unNumberedSeries.xml")
	private Resource unNumberedSeriesResource;

	@Bean
	public String unNumberedSeriesXml() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(unNumberedSeriesResource.getInputStream()));
	}

	@Value("classpath:templates/crossref.4.3.3/organizationName.xml")
	private Resource organizationNameResource;

	@Bean
	public String organizationNameXml() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(organizationNameResource.getInputStream()));
	}

	@Value("classpath:templates/crossref.4.3.3/personName.xml")
	private Resource personNameResource;

	@Bean
	public String personNameXml() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(personNameResource.getInputStream()));
	}

	@Value("classpath:templates/crossref.4.3.3/pages.xml")
	private Resource pagesResource;

	@Bean
	public String pagesXml() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(pagesResource.getInputStream()));
	}

	@Bean
	public NTCredentials nTCredentials() {
		return new NTCredentials(configurationService.getIpdsPubsWsUser(), configurationService.getIpdsPubsWsPwd(), "", "GS");
	}

}
