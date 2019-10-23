package gov.usgs.cida.pubs.springinit;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

@Configuration
public class BusServiceConfig {

	@Value("classpath:templates/crossref.4.3.3/unNumberedSeries.xml")
	private Resource unNumberedSeriesResource;

	@Value("classpath:templates/crossref.4.3.3/organizationName.xml")
	private Resource organizationNameResource;

	@Value("classpath:templates/crossref.4.3.3/personName.xml")
	private Resource personNameResource;

	@Value("classpath:templates/crossref.4.3.3/pages.xml")
	private Resource pagesResource;

	@Bean
	public String unNumberedSeriesXml() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(unNumberedSeriesResource.getInputStream()));
	}

	@Bean
	public String organizationNameXml() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(organizationNameResource.getInputStream()));
	}

	@Bean
	public String personNameXml() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(personNameResource.getInputStream()));
	}

	@Bean
	public String pagesXml() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(pagesResource.getInputStream()));
	}
}
