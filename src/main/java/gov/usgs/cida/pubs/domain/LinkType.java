package gov.usgs.cida.pubs.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;

@Component
public class LinkType extends BaseDomain<LinkType> implements ILookup {

	private static IDao<LinkType> linkTypeDao;

	public static final Integer INDEX_PAGE = 15;

	public static final Integer THUMBNAIL = 24;

	private String text;

	@Override
	public String getText() {
		return text;
	}

	public void setText(final String inText) {
		text = inText;
	}

	public static IDao<LinkType> getDao() {
		return linkTypeDao;
	}

	@Autowired
	@Qualifier("linkTypeDao")
	public void setLinkTypeDao(final IDao<LinkType> inLinkTypeDao) {
		linkTypeDao = inLinkTypeDao;
	}

}
