package gov.usgs.cida.pubs.aop;

import gov.usgs.cida.pubs.utility.PubsUtilities;

import java.sql.Connection;
import java.sql.SQLException;

import oracle.jdbc.OracleConnection;

import org.apache.tomcat.dbcp.dbcp.PoolableConnection;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author drsteini
 *
 */
@Aspect
public class SetDbContextAspect extends SqlSessionDaoSupport {

    /** The clientId/username. */
    private String clientId;

    @Pointcut("@annotation(gov.usgs.cida.pubs.aop.ISetDbContext)")
    private void dbAccess() {
    }

    /** Set the client ID in the database. */
    @Before(value="dbAccess()")
    @Transactional(readOnly = true)
    protected void registerContextValues() {
        try {
            clientId = PubsUtilities.getUsername();

            String[] metrics = new String[OracleConnection.END_TO_END_STATE_INDEX_MAX];
            metrics[OracleConnection.END_TO_END_CLIENTID_INDEX] = clientId;
            metrics[OracleConnection.END_TO_END_ACTION_INDEX]   = "x";
            metrics[OracleConnection.END_TO_END_ECID_INDEX]     = "y";
            metrics[OracleConnection.END_TO_END_MODULE_INDEX]   = "z";

            Connection conn = getSqlSession().getConnection().getMetaData().getConnection();
            if (conn instanceof PoolableConnection) {
                conn = ((PoolableConnection) conn).getInnermostDelegate();
            }
            if (conn instanceof org.apache.commons.dbcp.PoolableConnection) {
                conn = ((org.apache.commons.dbcp.PoolableConnection) conn).getInnermostDelegate();
            }

            OracleConnection oracleConn = (OracleConnection) conn;
            oracleConn.setEndToEndMetrics(metrics, (short) 0);

        } catch (SQLException e) {
            throw new RuntimeException("Problem setting client ID", e);
        }
    }

}
