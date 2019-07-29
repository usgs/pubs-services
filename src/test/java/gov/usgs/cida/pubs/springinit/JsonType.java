package gov.usgs.cida.pubs.springinit;

import org.dbunit.dataset.datatype.AbstractDataType;
import org.dbunit.dataset.datatype.TypeCastException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * support PostgreSQL json
 * Created by HieuNV on 4/21/2015.
 */
public class JsonType extends AbstractDataType {
	private static final Logger LOG = LoggerFactory.getLogger(JsonType.class);

	public JsonType() {
		super("json", Types.OTHER, String.class, false);
	}

	@Override
	public Object typeCast(Object value) throws TypeCastException {
		return value.toString();
	}

	@Override
	public Object getSqlValue(int column, ResultSet resultSet) throws SQLException {
		LOG.trace("getSqlValue({}, {})", column, resultSet);
		if (resultSet.wasNull()) {
			return null;
		}
		return resultSet.getString(column);
	}

	@Override
	public void setSqlValue(Object value, int column, PreparedStatement statement) throws SQLException {
		if (value == null) {
			statement.setNull(column, Types.OTHER);
		}
		statement.setObject(column, value.toString(), Types.OTHER);
	}

}
