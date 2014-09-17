package gov.usgs.cida.pubs.domain.mp;


import gov.usgs.cida.pubs.dao.intfc.IMpDao;
import gov.usgs.cida.pubs.domain.BaseDomain;
import gov.usgs.cida.pubs.validation.constraint.ParentExists;
import gov.usgs.cida.pubs.validation.constraint.UniqueKey;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author drsteini
 *
 */
@ParentExists
@UniqueKey
@JsonPropertyOrder({""})//TODO
public class MpListPublication extends BaseDomain<MpListPublication> {

	private static IMpDao<MpListPublication> mpListPublicationDao;

	private MpList mpList;

	private MpPublication mpPublication;

	public MpList getMpList() {
		return mpList;
	}

	public void setMpList(final MpList inMpList) {
		mpList = inMpList;
	}

	public MpPublication getMpPublication() {
		return mpPublication;
	}

	public void setMpPublication(final MpPublication inMpPublication) {
		mpPublication = inMpPublication;
	}

	public static IMpDao<MpListPublication> getDao() {
		return mpListPublicationDao;
	}

	public void setMpListPublicationDao(final IMpDao<MpListPublication> inMpListPublicationDao) {
		mpListPublicationDao = inMpListPublicationDao;
	}

}
