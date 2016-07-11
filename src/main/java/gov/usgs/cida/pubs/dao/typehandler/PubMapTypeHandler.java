package gov.usgs.cida.pubs.dao.typehandler;

import gov.usgs.cida.pubs.PubMap;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.sql.CLOB;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PubMapTypeHandler implements TypeHandler<PubMap> {

	private static final Logger LOG = LoggerFactory.getLogger(PubMapTypeHandler.class);

	/** 
	 * {@inheritDoc}
	 * @see org.apache.ibatis.type.TypeHandler#setParameter(java.sql.PreparedStatement, int, java.lang.Object, org.apache.ibatis.type.JdbcType)
	 */
	@Override
	public void setParameter(PreparedStatement ps, int i, PubMap parameter, JdbcType jdbcType) throws SQLException {
		// Not necessary for Pubs so not implemented.
	}

	/** 
	 * {@inheritDoc}
	 * @see org.apache.ibatis.type.TypeHandler#getResult(java.sql.ResultSet, int)
	 */
	@Override
	public PubMap getResult(ResultSet rs, int columnIndex) throws SQLException {
		PubMap pubMap = new PubMap();
		for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
			pubMap.put(rs.getMetaData().getColumnName(i), nullCheckValue(rs.getObject(i)));
		}

		return pubMap;
	}

	/** 
	 * {@inheritDoc}
	 * @see org.apache.ibatis.type.TypeHandler#getResult(java.sql.CallableStatement, int)
	 */
	@Override
	public PubMap getResult(CallableStatement cs, int columnIndex) throws SQLException {
		//  Not necessary for Pubs so not implemented.
		return null;
	}

	/** 
	 * {@inheritDoc}
	 * @see org.apache.ibatis.type.TypeHandler#getResult(java.sql.ResultSet, java.lang.String)
	 */
	@Override
	public PubMap getResult(ResultSet rs, String columnName) throws SQLException {
		PubMap pubMap = new PubMap();
		for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
			pubMap.put(rs.getMetaData().getColumnName(i), nullCheckValue(rs.getObject(i)));
		}

		return pubMap;
	}

	private Object nullCheckValue(Object value) {
		Object rtn = "";
		if (null == value) {
			rtn = "";
		} else if (value instanceof CLOB) {
			try {
				rtn = ((CLOB) value).stringValue();
			} catch (SQLException e) {
				LOG.info("Null CLOB", e);
			}
		} else {
			rtn = value;
		}
		return rtn;
	}

}
