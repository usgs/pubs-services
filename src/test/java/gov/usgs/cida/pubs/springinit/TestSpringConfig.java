package gov.usgs.cida.pubs.springinit;

import java.sql.SQLException;

import javax.naming.NamingException;

import oracle.jdbc.pool.OracleDataSource;

import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;

@Configuration
@ImportResource("classpath:spring/testContext.xml")
@PropertySource(value = "classpath:test.properties")
public class TestSpringConfig extends SpringConfig {

	public TestSpringConfig() throws NamingException {
		super();
	}

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private Environment env;
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public SqlSessionFactoryBean sqlSessionFactory() {
		SqlSessionFactoryBean mybatis = new SqlSessionFactoryBean();
		Resource mybatisConfig = new ClassPathResource("mybatis/dataMapperConfig.xml");
		mybatis.setConfigLocation(mybatisConfig);
		try {
			mybatis.setDataSource(dataSource());
		} catch (SQLException e) {
			log.info("Issue creating testDataSource" + e.getLocalizedMessage());
			throw new RuntimeException(e);
		}
		return mybatis;
	}

	@Bean
	public OracleDataSource dataSource() throws SQLException {
		OracleDataSource ds = new OracleDataSource();
		ds.setURL(env.getProperty("jdbc.mypubs.url"));
		ds.setUser(env.getProperty("jdbc.mypubs.username"));
		ds.setPassword(env.getProperty("jdbc.mypubs.password"));
		return ds;
	}

  @Bean
  public PlatformTransactionManager transactionManager() throws Exception {
  	DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
  	transactionManager.setDataSource(dataSource());
  	return transactionManager;
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
		return "tcp://localhost:61616";
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
	public String authService() {
		return "localhost";
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

}
