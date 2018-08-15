package gov.usgs.cida.pubs.springinit;

import java.io.IOException;

import org.apache.http.auth.NTCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import gov.usgs.cida.pubs.dao.ContributorTypeDaoIT;

@Configuration
public class TestSpringConfig {

	@Bean
	public NTCredentials nTCredentials() {
		return new NTCredentials("test", "test", "", "GS");
	}

	@Value("classpath:testResult/getMpPub1.json")
	private Resource mpPub1;
	@Bean
	public String expectedGetMpPub1() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(mpPub1.getInputStream()));
	}

	@Value("classpath:testResult/getRssPub.xml")
	private Resource rssPub;
	@Bean
	public String expectedGetRssPub() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(rssPub.getInputStream()));
	}

	@Value("classpath:testData/ipds/contributors.xml")
	private Resource contributors;
	@Bean
	public String contributorsXml() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(contributors.getInputStream()));
	}

	@Value("classpath:testData/ipds/usgsContributor.xml")
	private Resource usgsContributor;
	@Bean
	public String usgsContributorXml() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(usgsContributor.getInputStream()));
	}

	@Value("classpath:testData/ipds/newUsgsAuthor.xml")
	private Resource newUsgsAuthor;
	@Bean
	public String newUsgsAuthor() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(newUsgsAuthor.getInputStream()));
	}

	@Value("classpath:testData/ipds/newUsgsEditor.xml")
	private Resource newUsgsEditor;
	@Bean
	public String newUsgsEditor() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(newUsgsEditor.getInputStream()));
	}

	@Value("classpath:testData/ipds/newOutsideAuthor.xml")
	private Resource newOutsideAuthor;
	@Bean
	public String newOutsideAuthor() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(newOutsideAuthor.getInputStream()));
	}

	@Value("classpath:testData/ipds/newOutsideEditor.xml")
	private Resource newOutsideEditor;
	@Bean
	public String newOutsideEditor() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(newOutsideEditor.getInputStream()));
	}

	@Value("classpath:testData/ipds/newOutsideContributorUsgsAffiliation.xml")
	private Resource newOutsideContributorUsgsAffiliation;
	@Bean
	public String newOutsideContributorUsgsAffiliationXml() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(newOutsideContributorUsgsAffiliation.getInputStream()));
	}

	@Value("classpath:testData/ipds/existingOutsideAuthor.xml")
	private Resource existingOutsideAuthor;
	@Bean
	public String existingOutsideAuthor() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(existingOutsideAuthor.getInputStream()));
	}

	@Value("classpath:testData/ipds/costCenter.xml")
	private Resource costCenter;
	@Bean
	public String costCenterXml() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(costCenter.getInputStream()));
	}

	@Value("classpath:testData/ipds/feed.xml")
	private Resource feed;
	@Bean
	public String feedXml() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(feed.getInputStream()));
	}

	@Value("classpath:testData/ipds/notes.xml")
	private Resource notes;
	@Bean
	public String notesXml() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(notes.getInputStream()));
	}

	@Value("classpath:testResult/ofr20131259.xml")
	private Resource ofr20131259;
	@Bean
	public String ofr20131259Xml() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(ofr20131259.getInputStream()));
	}

	@Value("classpath:testResult/testUnNumberedSeries.xml")
	private Resource testUnNumberedSeries;
	@Bean
	public String testUnNumberedSeriesXml() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(testUnNumberedSeries.getInputStream()));
	}

	@Value("classpath:testResult/testOneUnNumberedSeriesPub.xml")
	private Resource testOneUnNumberedSeries;
	@Bean
	public String testOneUnNumberedSeriesPubXml() throws IOException{
		return new String(FileCopyUtils.copyToByteArray(testOneUnNumberedSeries.getInputStream()));
	}

	@Value("classpath:testResult/testOneNumberedSeriesPub.xml")
	private Resource testOneNumberedSeries;
	@Bean
	public String testOneNumberedSeriesPubXml() throws IOException{
		return new String(FileCopyUtils.copyToByteArray(testOneNumberedSeries.getInputStream()));
	}

	@Bean
	public String authorKey() {
		return ContributorTypeDaoIT.AUTHOR_KEY;
	}

	@Bean
	public String editorKey() {
		return ContributorTypeDaoIT.EDITOR_KEY;
	}
}
