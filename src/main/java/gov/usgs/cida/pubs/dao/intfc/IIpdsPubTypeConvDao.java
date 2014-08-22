package gov.usgs.cida.pubs.dao.intfc;

import gov.usgs.cida.pubs.domain.ipds.IpdsPubTypeConv;

public interface IIpdsPubTypeConvDao {

    IpdsPubTypeConv getByIpdsValue(String ipdsValue);

}
