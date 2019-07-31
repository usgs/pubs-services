package gov.usgs.cida.pubs.utility;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PubsEscapeXML10Test {

	@Test
	public void cleanseXmlTest() {
		assertEquals("<a><b>c</b></a>", PubsEscapeXML10Utils.cleanseXml("<a><b>c</b></a>"));
		assertEquals(PubsEscapeXML10Utils.ERROR_CLEANSING, PubsEscapeXML10Utils.cleanseXml("ï»¿<a><b>c</b></a>"));
		assertEquals(PubsEscapeXML10Utils.ERROR_CLEANSING, PubsEscapeXML10Utils.cleanseXml("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + 
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html/>"));
	}

}
