package gov.usgs.cida.pubs.domain;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;
import io.swagger.v3.oas.annotations.media.Schema;

@Component
public class PublishingServiceCenter extends BaseDomain<PublishingServiceCenter> implements ILookup, Serializable {

	private static final long serialVersionUID = -281712263572997816L;

	private static IDao<PublishingServiceCenter> pscDao;

	public static final Integer ARTICLE = 2;

	public static final Integer REPORT = 18;

	private String text;

	@Override
	public String getText() {
		return text;
	} 

	public void setText(final String inText) {
		text = inText;
	}

	public static IDao<PublishingServiceCenter> getDao() {
		return pscDao;
	}

	@Autowired
	@Qualifier("publishingServiceCenterDao")
	@Schema(hidden = true)
	public void setPublishingServiceCenterDao(final IDao<PublishingServiceCenter> inPscDao) {
		pscDao = inPscDao;
	}

}
