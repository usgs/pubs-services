package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.intfc.IPublicationIndexDao;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PublicationIndex extends BaseDomain<PublicationIndex> implements Serializable {
	
	private static final long serialVersionUID = -3070732718809243589L;

	private static IPublicationIndexDao publicationIndexDao;

	private String q;

	public String getQ() {
		return q;
	}

	public void setQ(final String inQ) {
		q = inQ;
	}

	public static IPublicationIndexDao getDao() {
		return publicationIndexDao;
	}

	@Autowired
	public void setPublicationIndexDao(final IPublicationIndexDao inPublicationIndexDao) {
		publicationIndexDao = inPublicationIndexDao;
	}

}
