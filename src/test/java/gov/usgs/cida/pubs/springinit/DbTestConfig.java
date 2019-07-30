package gov.usgs.cida.pubs.springinit;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;

@TestConfiguration
@Import(MybatisConfig.class)
public class DbTestConfig {

	@Value("${spring.datasource.url}")
	private String datasourceUrl;

	@Value("${PUBS_OWNER_USERNAME}")
	private String datasourceOwnerUsername;

	@Value("${PUBS_OWNER_PASSWORD}")
	private String datasourceOwnerPassword;

	@Value("${PUBS_SCHEMA_NAME}")
	private String schemaName;

	@Bean
	public DataSource dbUnitDataSource() throws Exception {
		PGSimpleDataSource ds = new PGSimpleDataSource();
		ds.setUrl(datasourceUrl);
		ds.setUser(datasourceOwnerUsername);
		ds.setPassword(datasourceOwnerPassword);
		return ds;
	}

	//Bean to support DBunit for unit testing with PostgrSQL.
	@Bean
	public DatabaseConfigBean dbUnitDatabaseConfig() {
		DatabaseConfigBean dbUnitDbConfig = new DatabaseConfigBean();
		dbUnitDbConfig.setDatatypeFactory(new PubsDataTypeFactory());
		dbUnitDbConfig.setSkipOracleRecyclebinTables(true);
		dbUnitDbConfig.setQualifiedTableNames(false);
		return dbUnitDbConfig;
	}

	@Bean
	public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection() throws Exception {
		DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection = new DatabaseDataSourceConnectionFactoryBean();
		dbUnitDatabaseConnection.setDatabaseConfig(dbUnitDatabaseConfig());
		dbUnitDatabaseConnection.setDataSource(dbUnitDataSource());
		dbUnitDatabaseConnection.setSchema(schemaName);
		return dbUnitDatabaseConnection;
	}

}
