package gov.usgs.cida.pubs.dao.intfc;

import gov.usgs.cida.pubs.domain.sipp.IpdsPubTypeConv;

public interface IIpdsPubTypeConvDao {

	IpdsPubTypeConv getByIpdsValue(String ipdsValue);

}
