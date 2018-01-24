package gov.usgs.cida.pubs.springinit;

import java.io.IOException;
import java.sql.SQLException;

import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;

import oracle.jdbc.pool.OracleDataSource;

@PropertySource(value = "classpath:test.properties")
public class TestSpringConfig {

	@Autowired
	private Environment env;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public OracleDataSource dataSource() throws SQLException {
		OracleDataSource ds = new OracleDataSource();
		ds.setURL(env.getProperty("jdbc.mypubs.url"));
		ds.setUser(env.getProperty("jdbc.mypubs.username"));
		ds.setPassword(env.getProperty("jdbc.mypubs.password"));
		return ds;
	}

	//Beans to support DBunit for unit testing with Oracle.
	@Bean
	public DatabaseConfigBean dbUnitDatabaseConfig() {
		DatabaseConfigBean dbUnitDbConfig = new DatabaseConfigBean();
		dbUnitDbConfig.setDatatypeFactory(new OracleDataTypeFactory());
		dbUnitDbConfig.setSkipOracleRecyclebinTables(true);
		dbUnitDbConfig.setQualifiedTableNames(false);
		return dbUnitDbConfig;
	}

	@Bean
	public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection() throws SQLException {
		DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection = new DatabaseDataSourceConnectionFactoryBean();
		dbUnitDatabaseConnection.setDatabaseConfig(dbUnitDatabaseConfig());
		dbUnitDatabaseConnection.setDataSource(dataSource());
		dbUnitDatabaseConnection.setSchema("PUBS");
		return dbUnitDatabaseConnection;
	}

	@Bean
	public String ipdsPubsWsPwd() {
		return env.getProperty("pubs.ipdsPubsWsPwd");
	}

	@Bean
	public String ipdsPubsWsUser() {
		return env.getProperty("pubs.ipdsPubsWsUser");
	}

	@Bean
	public String brokerURL() {
		return "vm://localhost?broker.persistent=false";
	}

	@Bean
	public String queueName() {
		return "dummy";
	}

	@Bean
	public String costCenterQueueName() {
		return "dummy";
	}

	@Bean
	public String ipdsEndpoint() {
		return "bad.usgs.gov";//"ipdsv2test.usgs.gov";
	}

	@Bean
	public String crossRefProtocol() {
		return "https";
	}

	@Bean
	public String crossRefHost() {
		return "test.crossref.org";
	}

	@Bean
	public String crossRefUrl() {
		return "/servlet/deposit";
	}

	@Bean
	public Integer crossRefPort() {
		return 443;
	}

	@Bean
	public String crossRefUser() {
		return env.getProperty("crossref.username");
	}

	@Bean
	public String crossRefPwd() {
		return env.getProperty("crossref.password");
	}

	@Bean
	public String crossRefSchemaUrl() {
		return "http://www.crossref.org/schema/deposit/crossref4.4.0.xsd";
	}
	
	@Bean
	public String displayHost() {
		return "localhost";
	}
	
	@Bean
	public String pubsEmailList() {
		return env.getProperty("pubs.emailList");
	}

	@Bean
	public String mailHost() {
		return "gsvaresh01.er.usgs.gov";
	}

	@Bean
	public Integer lockTimeoutHours() {
		return 3;
	}

	@Bean
	public String crossRefDepositorEmail() {
		return "drsteini@usgs.gov";
	}

	@Bean
	public String warehouseEndpoint() {
		return "http://pubs.er.usgs.gov";
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
	

}
