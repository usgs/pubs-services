package gov.usgs.cida.pubs.busservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.core.BaseDomainTest;
import gov.usgs.cida.pubs.core.busservice.mocks.CrossRefBusServiceMock;
import gov.usgs.cida.pubs.core.domain.LinkClass;
import gov.usgs.cida.pubs.core.domain.MpLinkDim;
import gov.usgs.cida.pubs.core.domain.MpPublication;
import gov.usgs.cida.pubs.core.domain.PublicationType;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CrossRefBusServiceTest extends BaseSpringTest {
	
	@Autowired
	private CrossRefBusServiceMock busService;
	@Autowired
	private String ofr20131259Xml;
	@Autowired
	private String simpleAuthorsXml;
	@Autowired
	private String testPagesXml;
	@Autowired
	private String testUnNumberedSeriesXml;
	@Autowired
	private String numberedSeriesXml;
	@Autowired
	private String unNumberedSeriesXml;

	@Test
	public void buildNumberedSeriesXmlTest() {
		MpPublication pub = buildPub();
		pub.setPublicationTypeId(PublicationType.USGS_NUMBERED_SERIES);
		pub.setSeriesCd("OFR");
		pub.setPublicationMonth("10");
		pub.setPublicationDay("24");
		pub.setPublicationYear("2013");
		pub.setTitle("Postwildfire debris-flow hazard assessment of the area burned by the 2013 West Fork Fire Complex, southwestern Colorado");
		pub.setSeriesNumber("2013-1259");
		pub.setDoiName("10.3133/ofr20131259");
		pub.setAuthorDisplay("Verdin, Kristine L.; Dupree, Jean A.; Stevens, Michael R.");

		pub.setStartPage("52");
		pub.setEndPage("56");

		String xml = busService.buildBaseXml(pub, buildIndex(pub), numberedSeriesXml);
		assertNotNull(xml);
		assertFalse(xml.contains("{doi_batch_id}"));
		assertFalse(xml.contains("{submission_timestamp}"));
		assertFalse(xml.contains("{series_name}"));
		assertFalse(xml.contains("{online_issn}"));
		assertFalse(xml.contains("{dissemination_month}"));
		assertFalse(xml.contains("{dissemination_day}"));
		assertFalse(xml.contains("{dissemination_year}"));
		assertFalse(xml.contains("{contributers}"));
		assertFalse(xml.contains("{title}"));
		assertFalse(xml.contains("{series_number}"));
		assertFalse(xml.contains("{pages}"));
		assertFalse(xml.contains("{doi_name}"));
		assertFalse(xml.contains("{index_page}"));
		String modCompare = replaceDoiBatchId(ofr20131259Xml, xml);
		modCompare = replaceTimestamp(modCompare, xml);
		assertEquals(stripCrLf(modCompare), stripCrLf(xml));
	}

	@Test
	public void buildUnNumberedSeriesXmlTest() {
		MpPublication pub = buildPub();
		pub.setPublicationTypeId(PublicationType.USGS_UNNUMBERED_SERIES);
		pub.setSeriesCd("GIP");
		pub.setPublicationMonth("10");
		pub.setPublicationDay("24");
		pub.setPublicationYear("2013");
		pub.setTitle("Postwildfire debris-flow hazard assessment of the area burned by the 2013 West Fork Fire Complex, southwestern Colorado");
		pub.setDoiName("10.3133/ofr20131259");
		pub.setAuthorDisplay("Verdin, Kristine L.; Dupree, Jean A.; Stevens, Michael R.");

		pub.setStartPage("52");
		pub.setEndPage("56");

		String xml = busService.buildBaseXml(pub, buildIndex(pub), unNumberedSeriesXml);
		assertNotNull(xml);
		assertFalse(xml.contains("{doi_batch_id}"));
		assertFalse(xml.contains("{submission_timestamp}"));
		assertFalse(xml.contains("{series_name}"));
		assertFalse(xml.contains("{online_issn}"));
		assertFalse(xml.contains("{dissemination_month}"));
		assertFalse(xml.contains("{dissemination_day}"));
		assertFalse(xml.contains("{dissemination_year}"));
		assertFalse(xml.contains("{contributers}"));
		assertFalse(xml.contains("{title}"));
		assertFalse(xml.contains("{series_number}"));
		assertFalse(xml.contains("{pages}"));
		assertFalse(xml.contains("{doi_name}"));
		assertFalse(xml.contains("{index_page}"));
		String modCompare = replaceDoiBatchId(testUnNumberedSeriesXml, xml);
		modCompare = replaceTimestamp(modCompare, xml);
		assertEquals(stripCrLf(modCompare), stripCrLf(xml));
	}
	
	@Test
	public void getAuthorsTest() {
		MpPublication pub = new MpPublication();
		String xml = busService.getAuthors(pub);
		assertEquals("", xml);

		pub.setAuthorDisplay("Verdin, Kristine L., sr; Dupree, Jean A.; Stevens, Michael R.");
		xml = busService.getAuthors(pub);
		assertEquals(stripCrLf(simpleAuthorsXml), stripCrLf(xml));
	}
	
	@Test
	public void getPagesTest() {
		MpPublication pub = new MpPublication();
		String xml = busService.getPages(pub);
		assertEquals("", xml);

		pub.setStartPage("");
		xml = busService.getPages(pub);
		assertEquals("", xml);
		
		pub.setEndPage("");
		xml = busService.getPages(pub);
		assertEquals("", xml);
		
		pub.setStartPage("52");
		xml = busService.getPages(pub);
		assertEquals("", xml);
		
		pub.setEndPage("56");
		xml = busService.getPages(pub);
		assertEquals(testPagesXml, xml);
		
	}

	@Test
	public void getIndexPageTest() {
		MpPublication pub = buildPub();
		
		String index = busService.getIndexPage(pub);
		assertEquals("", index);
		
		buildIndex(pub);
		
		index = busService.getIndexPage(pub);
		assertEquals("http://pubs.usgs.gov/of/2013/1259/", index);
	}

//	@Test
	public void submitCrossRefTest() {
		MpPublication pub = buildPub();
		buildIndex(pub);
		pub.setPublicationTypeId(PublicationType.USGS_NUMBERED_SERIES);
		pub.setSeriesCd("OFR");
		pub.setPublicationMonth("10");
		pub.setPublicationDay("24");
		pub.setPublicationYear("2013");
		pub.setTitle("Postwildfire debris-flow hazard assessment of the area burned by the 2013 West Fork Fire Complex, southwestern Colorado");
		pub.setSeriesNumber("2013-1259");
		pub.setDoiName("10.3133/ofr20131259");
		pub.setAuthorDisplay("Verdin, Kristine L.; Dupree, Jean A.; Stevens, Michael R.");
		pub.setStartPage("52");
		pub.setEndPage("56");
		pub.setIndexId("ofr20131259");
		busService.submitCrossRef(pub);
	}
	//	public void submitCrossRef(MpPublication mpPublication) {}
//	protected String buildXml(final MpPublication mpPublication) {}
	
	private String replaceDoiBatchId(String referenceXml, String compareXml) {
		String doiBatchId = compareXml.substring(compareXml.indexOf("<doi_batch_id>") + 14, compareXml.indexOf("</doi_batch_id>"));
		return referenceXml.replace("4554651", doiBatchId);
	}
	
	private String replaceTimestamp(String referenceXml, String compareXml) {
		String timestamp = compareXml.substring(compareXml.indexOf("<timestamp>") + 11, compareXml.indexOf("</timestamp>"));
		return referenceXml.replace("6546546132", timestamp);
	}

	private MpPublication buildPub() {
		Integer pubId = MpPublication.getDao().getNewProdId();
		MpPublication pub = new MpPublication();
		pub.setId(pubId);
		MpPublication.getDao().add(pub);
		return pub;
	}
	
	private String buildIndex(MpPublication pub) {
		MpLinkDim indexPage = new MpLinkDim();
		indexPage.setProdId(String.valueOf(pub.getId()));
		indexPage.setLinkClass(LinkClass.INDEX_PAGE.getValue());
		indexPage.setLink("http://pubs.usgs.gov/of/2013/1259/");
		MpLinkDim.getDao().add(indexPage);
		return indexPage.getLink();
	}
}
