package gov.usgs.cida.pubs.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;

@Component
public class ContributorType extends BaseDomain<ContributorType> implements ILookup {

	public static final Integer AUTHORS = 1;
	public static final Integer EDITORS = 2;
	public static final Integer COMPILERS = 3;
	public static String AUTHOR_KEY;
	public static String EDITOR_KEY;

	private static IDao<ContributorType> contributorTypeDao;

	private String text;

	@Override
	public String getText() {
		return text;
	}

	public void setText(final String inText) {
		text = inText;
	}

	/**
	 * @return the contributorTypeDao
	 */
	public static IDao<ContributorType> getDao() {
		return contributorTypeDao;
	}

	/**
	 * The setter for contributorTypeDao.
	 * @param inContributorTypeDao the contributorTypeDao to set
	 */
	@Autowired
	@Qualifier("contributorTypeDao")
	public void setContributorTypeDao(final IDao<ContributorType> inContributorTypeDao) {
		contributorTypeDao = inContributorTypeDao;
	}

	@EventListener
	public void onApplicationEvent(ContextRefreshedEvent event) {
		//This code will not be executed until the entire Spring Context is loaded.
		AUTHOR_KEY = contributorTypeDao.getById(AUTHORS).getText().toLowerCase();
		EDITOR_KEY = contributorTypeDao.getById(EDITORS).getText().toLowerCase();
	}
}
