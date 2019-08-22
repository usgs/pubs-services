package gov.usgs.cida.pubs.springinit;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import gov.usgs.cida.pubs.dao.sipp.RequestResponseLoggingInterceptor;

@Configuration
public class RestTemplateConfig {

	@Bean
	public ClientHttpRequestFactory clientHttpRequestFactory() {
		return new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
	}

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
		restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
		return restTemplate;
	}

}
