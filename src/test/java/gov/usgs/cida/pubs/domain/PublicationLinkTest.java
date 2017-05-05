package gov.usgs.cida.pubs.domain;


public class PublicationLinkTest {
	public static PublicationLink<?> buildIndexLink(){
		PublicationLink<?> link = new PublicationLink<>();
		LinkType linkType = new LinkType();
		linkType.setId(LinkType.INDEX_PAGE);
		link.setLinkType(linkType);
		link.setUrl("http://pubs.usgs.gov/of/2013/1259/");
		return link;
	}
}
