package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.aop.SetDbContextAspect;
import gov.usgs.cida.pubs.domain.PublicationType;

import java.io.File;
import java.sql.Connection;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.DataSourceUtils;

/**
 * @author drsteini
 *
 */
public class BaseDaoTest extends BaseSpringTest {

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

        Connection conn = DataSourceUtils.getConnection(dataSource);

        IDatabaseConnection connection;
        try {
            connection = new DatabaseConnection(conn);

            DatabaseConfig config = connection.getConfig();
            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new OracleDataTypeFactory());
            config.setProperty(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, true);

            DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
        } catch (Exception err) {
            throw new RuntimeException(err);
        }

    }

    @Test
    public void getClientId() {
        //no authentication
        String clientId = PublicationType.getDao().getClientId();
        assertNotNull("No Authentication", clientId);
        assertEquals("No Authentication", SetDbContextAspect.ANONYMOUS_USER, clientId);
    }

}
