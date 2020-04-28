package gov.usgs.cida.pubs.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;
import io.swagger.v3.oas.annotations.media.Schema;

@Component
public class LinkFileType extends BaseDomain<LinkFileType> implements ILookup {

	private static IDao<LinkFileType> linkFileTypeDao;

	private String text;

	@Override
	public String getText() {
		return text;
	}

	public void setText(final String inText) {
		text = inText;
	}

	public static IDao<LinkFileType> getDao() {
		return linkFileTypeDao;
	}

	@Autowired
	@Qualifier("linkFileTypeDao")
	@Schema(hidden = true)
	public void setLinkFileTypeDao(final IDao<LinkFileType> inLinkFileTypeDao) {
		linkFileTypeDao = inLinkFileTypeDao;
	}

}
