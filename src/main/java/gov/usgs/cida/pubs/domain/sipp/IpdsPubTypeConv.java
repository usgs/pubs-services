package gov.usgs.cida.pubs.domain.sipp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.usgs.cida.pubs.dao.intfc.IIpdsPubTypeConvDao;
import gov.usgs.cida.pubs.domain.BaseDomain;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;

@Component
public class IpdsPubTypeConv extends BaseDomain <IpdsPubTypeConv> {

	private static IIpdsPubTypeConvDao ipdsPubTypeConvDao;

	public static final int USGS_PERIODICAL = 12;

	private String ipdsValue;

	private PublicationType publicationType;

	private PublicationSubtype publicationSubtype;

	public String getIpdsValue() {
		return ipdsValue;
	}

	public void setIpdsValue(final String inIpdsValue) {
		ipdsValue = inIpdsValue;
	}

	public PublicationType getPublicationType() {
		return publicationType;
	}

	public void setPublicationType(final PublicationType inPublicationType) {
		publicationType = inPublicationType;
	}

	public PublicationSubtype getPublicationSubtype() {
		return publicationSubtype;
	}

	public void setPublicationSubtype(final PublicationSubtype inPublicationSubtype) {
		publicationSubtype = inPublicationSubtype;
	}

	public static IIpdsPubTypeConvDao getDao() {
		return ipdsPubTypeConvDao;
	}

	@Autowired
	public void setIpdsPubTypeConvDao(final IIpdsPubTypeConvDao inIpdsPubTypeConvDao) {
		ipdsPubTypeConvDao = inIpdsPubTypeConvDao;
	}

}
