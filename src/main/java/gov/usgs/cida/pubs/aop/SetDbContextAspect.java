package gov.usgs.cida.pubs.aop;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.ibatis.session.SqlSessionFactory;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.utility.PubsUtilities;
import oracle.jdbc.OracleConnection;

/**
 * @author drsteini
 *
 */
@Aspect
@Component
public class SetDbContextAspect extends SqlSessionDaoSupport {

	/** The clientId/username. */
	private String clientId;

	@Pointcut("@annotation(gov.usgs.cida.pubs.aop.ISetDbContext)")
	private void dbAccess() {
	}

	@Autowired
	public SetDbContextAspect(SqlSessionFactory sqlSessionFactory) {
		setSqlSessionFactory(sqlSessionFactory);
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
			OracleConnection oracleConn = getAsOracleConnection(conn);
			oracleConn.setEndToEndMetrics(metrics, (short) 0);

		} catch (SQLException e) {
			throw new RuntimeException("Problem setting client ID", e);
		}
	}

	/**
	 * Attempts to get the underlying instance of OracleConnection.
	 * @param conn the current connection object
	 * @return the OracleConnection implementation
	 * @throws SQLException
	 */
	private OracleConnection getAsOracleConnection(final Connection conn) throws SQLException {
		OracleConnection c = null;
		if (OracleConnection.class.isAssignableFrom(conn.getClass())) {
			c = (OracleConnection)conn;
		} else if (conn.isWrapperFor(OracleConnection.class)) {
			// Apache Commons DBCP 1.4, when used directly (which we do for unit tests) returns the connection
			// wrapped in a PoolableConnection, which needs to be unwrapped
			c = conn.unwrap(OracleConnection.class);
		} else {
			throw new RuntimeException("Unknown connection class returned from pool: " + conn.getClass());
		}
		return c;
	}

}
