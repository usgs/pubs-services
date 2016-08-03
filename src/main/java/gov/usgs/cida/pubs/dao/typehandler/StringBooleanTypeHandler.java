package gov.usgs.cida.pubs.dao.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

public class StringBooleanTypeHandler implements TypeHandler<Boolean> {

	public static final String TRUE = "Y";
	public static final String FALSE = "N";

	@Override
	public Boolean getResult(ResultSet arg0, String arg1) throws SQLException {
		return TRUE.equalsIgnoreCase(arg0.getString(arg1));
	}

	@Override
	public Boolean getResult(ResultSet arg0, int arg1) throws SQLException {
		return TRUE.equalsIgnoreCase(arg0.getString(arg1));
	}

	@Override
	public Boolean getResult(CallableStatement arg0, int arg1) throws SQLException {
		return TRUE.equalsIgnoreCase(arg0.getString(arg1));
	}

	@Override
	public void setParameter(PreparedStatement arg0, int arg1, Boolean arg2, JdbcType arg3) throws SQLException {
		arg0.setString(arg1, null != arg2 && arg2.booleanValue() ? TRUE : FALSE);
	}

}
