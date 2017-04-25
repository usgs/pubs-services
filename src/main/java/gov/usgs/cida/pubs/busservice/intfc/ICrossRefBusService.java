package gov.usgs.cida.pubs.busservice.intfc;

import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import java.util.ArrayList;
import java.util.List;

public interface ICrossRefBusService {

	void submitCrossRef(MpPublication mpPublication);
	
	/**
	 * 
	 * @param pub
	 * @return String url of the index page
	 */
	public String getIndexPage(Publication<?> pub);

	/**
	 * 
	 * @param pub
	 * @return a list of authors, and editors in rank order.
	 * According to Crossref best practices, we want to 
	 * render all authors first, followed by all editors second.
	 * https://support.crossref.org/hc/en-us/articles/214567746-Authors-and-editors
	 */
	public List<PublicationContributor<?>> getContributors(Publication<?> pub);

}
