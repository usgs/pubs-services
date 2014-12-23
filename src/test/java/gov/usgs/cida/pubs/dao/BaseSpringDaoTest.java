package gov.usgs.cida.pubs.dao;

import gov.usgs.cida.pubs.BaseSpringTest;

import java.io.File;
import java.sql.Connection;

import javax.annotation.Resource;
import javax.sql.DataSource;

import oracle.jdbc.OracleConnection;

import org.apache.tomcat.dbcp.dbcp.PoolableConnection;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Ignore;
import org.springframework.jdbc.datasource.DataSourceUtils;

/**
 * This is the base class used by test classes that need to add data to the database.
 * All tests that require data added to the database should extend this base class.
 *
 * @author drsteini
 *
 */
@Ignore
public class BaseSpringDaoTest extends BaseSpringTest {

    @Resource
    private DataSource dataSource;

    @Before public void setUp() throws Exception {
        //We are using a ReplacementDataSet to we can specify [NULL] for null values in the FlatXmlDataSet.
        //This allows us to specify all columns on each row, thus not getting caught by DBUnit only using the
        //columns specified in the first row (for the table).
        ReplacementDataSet dataSet = new ReplacementDataSet(new FlatXmlDataSetBuilder().build(new File(
                "src/test/resources/testData/dataset.xml"
                )));
        dataSet.addReplacementObject("[NULL]", null);

        Connection conn = DataSourceUtils.getConnection(dataSource).getMetaData().getConnection();
        if (conn instanceof PoolableConnection) {
            conn = ((PoolableConnection) conn).getInnermostDelegate();
        }
        if (conn instanceof org.apache.commons.dbcp.PoolableConnection) {
            conn = ((org.apache.commons.dbcp.PoolableConnection) conn).getInnermostDelegate();
        }

        OracleConnection oracleConn = (OracleConnection) conn;


        IDatabaseConnection connection;
        try {
            connection = new DatabaseConnection(oracleConn);

            DatabaseConfig config = connection.getConfig();
            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new OracleDataTypeFactory());
            config.setProperty(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, true);

            DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
        } catch (Exception err) {
            throw new RuntimeException(err);
        }

    }

}
