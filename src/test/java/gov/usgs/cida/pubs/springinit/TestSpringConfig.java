package gov.usgs.cida.pubs.springinit;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

@Configuration
public class TestSpringConfig {

	@Value("classpath:testData/emptyStringPub.json")
	private Resource emptyStringPub;
	@Bean
	public String emptyStringPub() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(emptyStringPub.getInputStream()));
	}

	@Value("classpath:testResult/getMpPub1.json")
	private Resource mpPub1;
	@Bean
	public String expectedGetMpPub1() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(mpPub1.getInputStream()));
	}

	@Value("classpath:testResult/expectedGetPubsDefault.json")
	private Resource mpExpectedGetPubsDefault;
	@Bean
	public String expectedGetPubsDefault() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(mpExpectedGetPubsDefault.getInputStream()));
	}

	@Value("classpath:testResult/getRssPub.xml")
	private Resource rssPub;
	@Bean
	public String expectedGetRssPub() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(rssPub.getInputStream()));
	}

	@Value("classpath:testResult/testOneUnNumberedSeriesPub.xml")
	private Resource testOneUnNumberedSeries;
	@Bean
	public String testOneUnNumberedSeriesPubXml() throws IOException{
		return new String(FileCopyUtils.copyToByteArray(testOneUnNumberedSeries.getInputStream()));
	}

	@Value("classpath:testResult/testOneUnNumberedSeriesPub-min.xml")
	private Resource testOneUnNumberedSeriesMin;
	@Bean
	public String testOneUnNumberedSeriesPubXmlMin() throws IOException{
		return new String(FileCopyUtils.copyToByteArray(testOneUnNumberedSeriesMin.getInputStream()));
	}

	@Value("classpath:testResult/testOneNumberedSeriesPub.xml")
	private Resource testOneNumberedSeries;
	@Bean
	public String testOneNumberedSeriesPubXml() throws IOException{
		return new String(FileCopyUtils.copyToByteArray(testOneNumberedSeries.getInputStream()));
	}

	@Value("classpath:testResult/testOneNumberedSeriesPub-min.xml")
	private Resource testOneNumberedSeriesMin;
	@Bean
	public String testOneNumberedSeriesPubXmlMin() throws IOException{
		return new String(FileCopyUtils.copyToByteArray(testOneNumberedSeriesMin.getInputStream()));
	}
}
