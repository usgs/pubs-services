package gov.usgs.cida.pubs.springinit;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

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
		return "ipdsdev.usgs.gov";
	}

	@Bean
	public String crossRefProtocol() {
		return "http";
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
		return 80;
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
	public String pubsEmailList() {
		return "drsteini@usgs.gov";
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
		try (InputStream is = mpPub1.getInputStream()) {
			return IOUtils.toString(is);
		}
	}

	@Value("classpath:testResult/getRssPub.xml")
	private Resource rssPub;
	@Bean
	public String expectedGetRssPub() throws IOException {
		try (InputStream is = rssPub.getInputStream()) {
			return IOUtils.toString(is);
		}
	}

	@Value("classpath:testData/contributors.xml")
	private Resource contributors;
	@Bean
	public String contributorsXml() throws IOException {
		try (InputStream is = contributors.getInputStream()) {
			return IOUtils.toString(is);
		}
	}

	@Value("classpath:testData/usgsContributor.xml")
	private Resource usgsContributor;
	@Bean
	public String usgsContributorXml() throws IOException {
		try (InputStream is = usgsContributor.getInputStream()) {
			return IOUtils.toString(is);
		}
	}

	@Value("classpath:testData/newOutsideContributor.xml")
	private Resource newOutsideContributor;
	@Bean
	public String newOutsideContributorXml() throws IOException {
		try (InputStream is = newOutsideContributor.getInputStream()) {
			return IOUtils.toString(is);
		}
	}
	
	@Value("classpath:testData/existingOutsideContributor.xml")
	private Resource existingOutsideContributor;
	@Bean
	public String existingOutsideContributorXml() throws IOException {
		try (InputStream is = existingOutsideContributor.getInputStream()) {
			return IOUtils.toString(is);
		}
	}

	@Value("classpath:testData/costCenter.xml")
	private Resource costCenter;
	@Bean
	public String costCenterXml() throws IOException {
		try (InputStream is = costCenter.getInputStream()) {
			return IOUtils.toString(is);
		}
	}

	@Value("classpath:testData/feed.xml")
	private Resource feed;
	@Bean
	public String feedXml() throws IOException {
		try (InputStream is = feed.getInputStream()) {
			return IOUtils.toString(is);
		}
	}

	@Value("classpath:testData/bad.xml")
	private Resource bad;
	@Bean
	public String badXml() throws IOException {
		try (InputStream is = bad.getInputStream()) {
			return IOUtils.toString(is);
		}
	}

	@Value("classpath:testData/notes.xml")
	private Resource notes;
	@Bean
	public String notesXml() throws IOException {
		try (InputStream is = notes.getInputStream()) {
			return IOUtils.toString(is);
		}
	}

	@Value("classpath:testResult/ofr20131259.xml")
	private Resource ofr20131259;
	@Bean
	public String ofr20131259Xml() throws IOException {
		try (InputStream is = ofr20131259.getInputStream()) {
			return IOUtils.toString(is);
		}
	}

	@Value("classpath:testResult/testUnNumberedSeries.xml")
	private Resource testUnNumberedSeries;
	@Bean
	public String testUnNumberedSeriesXml() throws IOException {
		try (InputStream is = testUnNumberedSeries.getInputStream()) {
			return IOUtils.toString(is);
		}
	}

}
