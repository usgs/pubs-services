package gov.usgs.cida.pubs.dao.ipds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.PubMap;
import gov.usgs.cida.pubs.dao.BaseSpringDaoTest;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;
import gov.usgs.cida.pubs.domain.ipds.IpdsPubTypeConv;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

/**
 * @author drsteini
 *
 */
public class IpdsMessageLogDaoTest extends BaseSpringDaoTest {

    @Resource(name="feedXml")
    public String feedXml;

    private static final List<String> IGNORE_PROPERTIES = Arrays.asList("validationErrors");

    @Test
    public void addGetByIdUpdate() {
        IpdsMessageLog ipdsMessageLog = new IpdsMessageLog();
        ipdsMessageLog.setMessageText("<root><node>123</node></root>");
        ipdsMessageLog.setProcessType(ProcessType.SPN_PRODUCTION);
        IpdsMessageLog persisted = IpdsMessageLog.getDao().getById(IpdsMessageLog.getDao().add(ipdsMessageLog));
        assertEquals(ipdsMessageLog.getMessageText(), persisted.getMessageText());
        assertNotNull(persisted.getId());
        assertEquals(ProcessType.SPN_PRODUCTION, persisted.getProcessType());
        assertNull(persisted.getProcessingDetails());
        assertNull(persisted.getProdId());

        ipdsMessageLog.setMessageText("<root><node>456</node></root>");
        persisted.setProcessingDetails("It worked!!");
        persisted.setProdId(123);
        IpdsMessageLog.getDao().update(persisted);
        IpdsMessageLog updated = IpdsMessageLog.getDao().getById(persisted.getId());
        assertDaoTestResults(IpdsMessageLog.class, updated, persisted, IGNORE_PROPERTIES, true, true);

        IpdsMessageLog.getDao().add(new IpdsMessageLog());
        List<IpdsMessageLog> logs = IpdsMessageLog.getDao().getByMap(null);
        assertNotNull(logs);
        assertEquals(2, logs.size());
    }

    @Test
    public void getFromIpds() {
        IpdsMessageLog ipdsMessageLogSeries = new IpdsMessageLog();
        String ipdsMessage = feedXml;
        ipdsMessageLogSeries.setMessageText(ipdsMessage);
        List<PubMap> pubMaps = IpdsMessageLog.getDao().getFromIpds(IpdsMessageLog.getDao().add(ipdsMessageLogSeries));
        assertNotNull(pubMaps);
        assertEquals(37, pubMaps.size());
        assertEquals(78, pubMaps.get(0).keySet().size());
        for (String property : IpdsMessageLog.IPDS_LOG_PROPERTIES) {
            assertTrue(pubMaps.get(0).containsKey(property));
        }
    }

    public static PubMap createPubMap1() {
        PubMap pubMap = new PubMap();
        pubMap.put(IpdsMessageLog.PRODUCTTYPEVALUE, "USGS series");
        pubMap.put(IpdsMessageLog.USGSSERIESVALUE, "Open-File Report");//"Journal of Crustacean Biology")480;
        pubMap.put(IpdsMessageLog.USGSSERIESNUMBER, "12.1");
        pubMap.put(IpdsMessageLog.USGSSERIESLETTER, "a");
        pubMap.put(IpdsMessageLog.FINALTITLE, "My Final Title");
        pubMap.put(IpdsMessageLog.WORKINGTITLE, "My Working Title");
        pubMap.put(IpdsMessageLog.ABSTRACT, "My Abstract");
        pubMap.put(IpdsMessageLog.NONUSGSPUBLISHER, "Not one of those USGS Publishers");
        pubMap.put(IpdsMessageLog.DIGITALOBJECTIDENTIFIER, "doi");
        pubMap.put(IpdsMessageLog.ISBN, "isbn234");
        pubMap.put(IpdsMessageLog.COOPERATORS, "I really want to cooperate");
        pubMap.put(IpdsMessageLog.CITATION, "A short citation");
        pubMap.put(IpdsMessageLog.PHYSICALDESCRIPTION, "physical desc");
        pubMap.put(IpdsMessageLog.PAGERANGE, "pages 1-5");
        pubMap.put(IpdsMessageLog.PRODUCTSUMMARY, "what a summary");
        pubMap.put(IpdsMessageLog.IPNUMBER, "IP1234");
        pubMap.put(IpdsMessageLog.IPDSREVIEWPROCESSSTATEVALUE, ProcessType.SPN_PRODUCTION.getIpdsValue());
        pubMap.put(IpdsMessageLog.IPDS_INTERNAL_ID, 453228);
        pubMap.put(IpdsMessageLog.JOURNALTITLE, "A Journal");
        pubMap.put(IpdsMessageLog.DISEMINATIONDATE, "2014-10-10");
        return pubMap;
    }

    public static PubMap createPubMap2() {
        PubMap pubMap = new PubMap();
        pubMap.put(IpdsMessageLog.PRODUCTTYPEVALUE, "New");
        pubMap.put(IpdsMessageLog.USGSSERIESVALUE, "Series Value");//"Journal of Crustacean Biology")480;
        pubMap.put(IpdsMessageLog.USGSSERIESNUMBER, ".");
        pubMap.put(IpdsMessageLog.USGSSERIESLETTER, "a");
        pubMap.put(IpdsMessageLog.WORKINGTITLE, "My Working Title");
        pubMap.put(IpdsMessageLog.ABSTRACT, "My Abstract");
        pubMap.put(IpdsMessageLog.NONUSGSPUBLISHER, "Not one of those USGS Publishers");
        pubMap.put(IpdsMessageLog.DIGITALOBJECTIDENTIFIER, "doi");
        pubMap.put(IpdsMessageLog.ISBN, "isbn234");
        pubMap.put(IpdsMessageLog.COOPERATORS, "I really want to cooperate");
        pubMap.put(IpdsMessageLog.CITATION, "A short citation");
        pubMap.put(IpdsMessageLog.PHYSICALDESCRIPTION, "physical desc");
        pubMap.put(IpdsMessageLog.PAGERANGE, "pages 1-5");
        pubMap.put(IpdsMessageLog.PRODUCTSUMMARY, "what a summary");
        pubMap.put(IpdsMessageLog.IPNUMBER, "IP1234");
        pubMap.put(IpdsMessageLog.IPDSREVIEWPROCESSSTATEVALUE, ProcessType.SPN_PRODUCTION.getIpdsValue());
        pubMap.put(IpdsMessageLog.IPDS_INTERNAL_ID, 453228);
        pubMap.put(IpdsMessageLog.JOURNALTITLE, "A Journal");
        pubMap.put(IpdsMessageLog.DISEMINATIONDATE, "2014-10-10");
        return pubMap;
    }

    public static PubMap createPubMap3() {
        PubMap pubMap = new PubMap();
        pubMap.put(IpdsMessageLog.PRODUCTTYPEVALUE, "USGS periodical");
        pubMap.put(IpdsMessageLog.USGSSERIESVALUE, "Open-File Report");
        pubMap.put(IpdsMessageLog.USGSSERIESLETTER, "a");
        pubMap.put(IpdsMessageLog.FINALTITLE, "My Final Title");
        pubMap.put(IpdsMessageLog.WORKINGTITLE, "My Working Title");
        pubMap.put(IpdsMessageLog.ABSTRACT, "My Abstract");
        pubMap.put(IpdsMessageLog.NONUSGSPUBLISHER, "Not one of those USGS Publishers");
        pubMap.put(IpdsMessageLog.DIGITALOBJECTIDENTIFIER, "doi");
        pubMap.put(IpdsMessageLog.ISBN, "isbn234");
        pubMap.put(IpdsMessageLog.COOPERATORS, "I really want to cooperate");
        pubMap.put(IpdsMessageLog.CITATION, "A short citation");
        pubMap.put(IpdsMessageLog.PHYSICALDESCRIPTION, "physical desc");
        pubMap.put(IpdsMessageLog.PAGERANGE, "pages 1-5");
        pubMap.put(IpdsMessageLog.PRODUCTSUMMARY, "what a summary");
        pubMap.put(IpdsMessageLog.IPNUMBER, "IP1234");
        pubMap.put(IpdsMessageLog.IPDSREVIEWPROCESSSTATEVALUE, ProcessType.SPN_PRODUCTION.getIpdsValue());
        pubMap.put(IpdsMessageLog.IPDS_INTERNAL_ID, 453228);
        pubMap.put(IpdsMessageLog.JOURNALTITLE, "A Journal");
        pubMap.put(IpdsMessageLog.DISEMINATIONDATE, "2014-10-10");
        return pubMap;
    }

    public static PubMap createPubMap4() {
        PubMap pubMap = new PubMap();
        pubMap.put(IpdsMessageLog.PRODUCTTYPEVALUE, "Thesis");
        pubMap.put(IpdsMessageLog.USGSSERIESVALUE, "Open-File Report");
        pubMap.put(IpdsMessageLog.USGSSERIESLETTER, "a");
        pubMap.put(IpdsMessageLog.FINALTITLE, "My Final Title");
        pubMap.put(IpdsMessageLog.WORKINGTITLE, "My Working Title");
        pubMap.put(IpdsMessageLog.ABSTRACT, "My Abstract");
        pubMap.put(IpdsMessageLog.NONUSGSPUBLISHER, "Not one of those USGS Publishers");
        pubMap.put(IpdsMessageLog.DIGITALOBJECTIDENTIFIER, "doi");
        pubMap.put(IpdsMessageLog.ISBN, "isbn234");
        pubMap.put(IpdsMessageLog.COOPERATORS, "I really want to cooperate");
        pubMap.put(IpdsMessageLog.CITATION, "A short citation");
        pubMap.put(IpdsMessageLog.PHYSICALDESCRIPTION, "physical desc");
        pubMap.put(IpdsMessageLog.PAGERANGE, "pages 1-5");
        pubMap.put(IpdsMessageLog.PRODUCTSUMMARY, "what a summary");
        pubMap.put(IpdsMessageLog.IPNUMBER, "IP1234");
        pubMap.put(IpdsMessageLog.IPDSREVIEWPROCESSSTATEVALUE, ProcessType.SPN_PRODUCTION.getIpdsValue());
        pubMap.put(IpdsMessageLog.IPDS_INTERNAL_ID, 453228);
        pubMap.put(IpdsMessageLog.JOURNALTITLE, "A Journal");
        pubMap.put(IpdsMessageLog.DISEMINATIONDATE, "2014-10-10");
        return pubMap;
    }

}
