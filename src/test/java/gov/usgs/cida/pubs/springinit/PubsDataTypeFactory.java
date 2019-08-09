package gov.usgs.cida.pubs.springinit;

import java.sql.Types;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PubsDataTypeFactory extends PostgresqlDataTypeFactory {
	private static final Logger logger = LoggerFactory.getLogger(PubsDataTypeFactory.class);

	public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {
		logger.debug("createDataType(sqlType={}, sqlTypeName={})",
				String.valueOf(sqlType), sqlTypeName);

		if (sqlType == Types.OTHER && ("json".equals(sqlTypeName) || "jsonb".equals(sqlTypeName))) {
			return new JsonType(); // support PostgreSQL json
		} else if (sqlType == Types.SQLXML) {
			return new XmlType(); // support PostgreSQL xml
		} else {
			return super.createDataType(sqlType, sqlTypeName);
		}
	}

}
