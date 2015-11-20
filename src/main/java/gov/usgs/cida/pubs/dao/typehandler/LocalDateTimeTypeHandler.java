package gov.usgs.cida.pubs.dao.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/**
 * @author drsteini
 *
 */
public class LocalDateTimeTypeHandler implements TypeHandler<LocalDateTime> {

    /** {@inheritDoc}
     * @see org.apache.ibatis.type.TypeHandler
     *             #setParameter(java.sql.PreparedStatement, int, java.lang.Object, org.apache.ibatis.type.JdbcType)
     */
    @Override
    public void setParameter(final PreparedStatement ps, final int i,
            final LocalDateTime parameter, final JdbcType jdbcType) throws SQLException {
        Timestamp date = null;
        if (parameter != null) {
            date = Timestamp.valueOf(parameter);
        }
        ps.setTimestamp(i, date);
    }

    /** {@inheritDoc}
     * @see org.apache.ibatis.type.TypeHandler#getResult(java.sql.ResultSet, java.lang.String)
     */
    @Override
    public LocalDateTime getResult(final ResultSet rs, final String columnName) throws SQLException {
        Timestamp date = rs.getTimestamp(columnName);
        LocalDateTime ldt = null;
        if (date != null) {
        	ldt = date.toLocalDateTime();
        }
        return ldt; 
    }

    /** {@inheritDoc}
     * @see org.apache.ibatis.type.TypeHandler#getResult(java.sql.CallableStatement, int)
     */
    @Override
    public LocalDateTime getResult(final CallableStatement cs, final int columnIndex) throws SQLException {
        Timestamp date = cs.getTimestamp(columnIndex);
        LocalDateTime ldt = null;
        if (date != null) {
        	ldt = date.toLocalDateTime();
        }
        return ldt; 
    }

    /** {@inheritDoc}
     * @see org.apache.ibatis.type.TypeHandler#getResult(java.sql.ResultSet, int)
     */
    @Override
    public LocalDateTime getResult(ResultSet rs, int columnIndex) throws SQLException {
    	Timestamp date = rs.getTimestamp(columnIndex);
    	LocalDateTime ldt = null;
		if (date != null) {
			ldt = date.toLocalDateTime();
		}
		return ldt; 
    }

}
