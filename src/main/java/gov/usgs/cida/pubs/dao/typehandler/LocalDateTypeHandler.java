package gov.usgs.cida.pubs.dao.typehandler;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import java.time.LocalDate;

/**
 * @author drsteini
 *
 */
public class LocalDateTypeHandler implements TypeHandler<LocalDate> {

    /** {@inheritDoc}
     * @see org.apache.ibatis.type.TypeHandler
     *             #setParameter(java.sql.PreparedStatement, int, java.lang.Object, org.apache.ibatis.type.JdbcType)
     */
    @Override
    public void setParameter(final PreparedStatement ps, final int i,
            final LocalDate parameter, final JdbcType jdbcType) throws SQLException {
        Date date = null; 
        if (parameter != null) { 
        	date = Date.valueOf(parameter);
        } 
        ps.setDate(i, date);
    }

    /** {@inheritDoc}
     * @see org.apache.ibatis.type.TypeHandler#getResult(java.sql.ResultSet, java.lang.String)
     */
    @Override
    public LocalDate getResult(final ResultSet rs, final String columnName) throws SQLException {
		Date date = rs.getDate(columnName);
		LocalDate ldt = null;
		if (date != null) {
			ldt = date.toLocalDate();
		}
		return ldt; 
    }

    /** {@inheritDoc}
     * @see org.apache.ibatis.type.TypeHandler#getResult(java.sql.CallableStatement, int)
     */
    @Override
    public LocalDate getResult(final CallableStatement cs, final int columnIndex) throws SQLException {
		Date date = cs.getDate(columnIndex);
		LocalDate ldt = null;
		if (date != null) {
			ldt = date.toLocalDate();
		}
		return ldt; 
    }

	@Override
	public LocalDate getResult(ResultSet rs, int columnIndex) throws SQLException {
		Date date = rs.getDate(columnIndex);
		LocalDate ldt = null;
		if (date != null) {
			ldt = date.toLocalDate();
		}
		return ldt; 
    }

}
