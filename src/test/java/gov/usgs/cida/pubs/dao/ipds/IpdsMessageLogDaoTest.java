package gov.usgs.cida.pubs.dao.ipds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;
import gov.usgs.cida.pubs.domain.mp.MpPublication;

import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author drsteini
 *
 */
//TODO Why am I not rolling back tests?
@Ignore
public class IpdsMessageLogDaoTest extends BaseSpringTest {

    private static final List<String> IGNORE_PROPERTIES_SERIES = Arrays.asList("validationErrors", "psblDupFlag", "spatialMap", "notPublishedFlag");

    @Test
    public void addGetByIdUpdate() {
        IpdsMessageLog ipdsMessageLog = new IpdsMessageLog();
        ipdsMessageLog.setMessageText("<root><node>123</node></root>");
        ipdsMessageLog.setProcessType(ProcessType.SPN_PRODUCTION);
        IpdsMessageLog persisted = IpdsMessageLog.getDao().getById(IpdsMessageLog.getDao().add(ipdsMessageLog));
        assertEquals(ipdsMessageLog.getMessageText(), persisted.getMessageText());
        assertNotNull(persisted.getId());
        assertNotNull(persisted.getInsertDate());
        assertEquals(ProcessType.SPN_PRODUCTION, persisted.getProcessType());
        assertNull(persisted.getInsertUsername());
        assertNull(persisted.getProcessingDetails());
        assertNull(persisted.getProdId());
        assertNull(persisted.getUpdateDate());
        assertNull(persisted.getUpdateUsername());
        assertNull(persisted.getValidationErrors());

        ipdsMessageLog.setMessageText("<root><node>456</node></root>");
        persisted.setProcessingDetails("It worked!!");
        persisted.setProdId(123);
        IpdsMessageLog.getDao().update(persisted);
        IpdsMessageLog updated = IpdsMessageLog.getDao().getById(persisted.getId());
        assertEquals(persisted.getMessageText(), updated.getMessageText());
        assertEquals(persisted.getId(), updated.getId());
        assertEquals(persisted.getInsertDate(), updated.getInsertDate());
        assertNull(updated.getInsertUsername());
        assertEquals(persisted.getProcessingDetails(), updated.getProcessingDetails());
        assertEquals(persisted.getProdId(), updated.getProdId());
        assertNull(updated.getUpdateDate());
        assertNull(updated.getUpdateUsername());
        assertNull(updated.getValidationErrors());

        IpdsMessageLog.getDao().add(new IpdsMessageLog());
        List<IpdsMessageLog> logs = IpdsMessageLog.getDao().getByMap(null);
        assertNotNull(logs);
        assertEquals(2, logs.size());
    }

    @Test
    public void getFromIpds() {
        //Test USGS Series
        IpdsMessageLog ipdsMessageLogSeries = new IpdsMessageLog();
        String ipdsMessage = getStringFromFile("/testData/feed.xml");
        ipdsMessageLogSeries.setMessageText(ipdsMessage);
        List<MpPublication> newpubSeries = IpdsMessageLog.getDao().getFromIpds(IpdsMessageLog.getDao().add(ipdsMessageLogSeries));
        assertNotNull(newpubSeries);
        assertEquals(37, newpubSeries.size());

        assertDaoTestResults(MpPublication.class, buildASeries(), newpubSeries.get(0), IGNORE_PROPERTIES_SERIES, true, true, true);

//        //Test Journal or periodical article
//        IpdsMessageLog ipdsMessageLogJourn = new IpdsMessageLog();
//        ipdsMessageLogJourn.setMessageText(ipdsMessage.replace("<genre>USGS series</genre>", "<genre>Journal or periodical article</genre>"));
//        List<MpPublication> newpubJourn = IpdsMessageLog.getDao().getFromIpds(IpdsMessageLog.getDao().add(ipdsMessageLogJourn));
//        assertNotNull(newpubJourn);
//        
//        assertDaoTestResults(MpPublication.class, buildAJourn(), newpubJourn, IGNORE_PROPERTIES, true, true, true);
//        
//        //Test other
//        IpdsMessageLog ipdsMessageLogOther = new IpdsMessageLog();
//        ipdsMessageLogOther.setMessageText(ipdsMessage.replace("<genre>USGS series</genre>", "<genre>Poster, exhibit, newsletter, pre</genre>"));
//        List<MpPublication> newpubOther = IpdsMessageLog.getDao().getFromIpds(IpdsMessageLog.getDao().add(ipdsMessageLogOther));
//        assertNotNull(newpubOther);
//        
////        MpPublication newpubSeries = IpdsMessageLog.getDao().getFromIpds(IpdsMessageLog.getDao().add(ipdsMessageLogSeries));
////        assertNotNull(newpubSeries);
////        
////        assertDaoTestResults(MpPublication.class, buildASeries(), newpubSeries, IGNORE_PROPERTIES_SERIES, true, true, true);
////        
////        //Test Journal or periodical article
////        IpdsMessageLog ipdsMessageLogJourn = new IpdsMessageLog();
////        ipdsMessageLogJourn.setMessageText(ipdsMessage.replace("<genre>USGS series</genre>", "<genre>Journal or periodical article</genre>"));
////        MpPublication newpubJourn = IpdsMessageLog.getDao().getFromIpds(IpdsMessageLog.getDao().add(ipdsMessageLogJourn));
////        assertNotNull(newpubJourn);
////        
////        assertDaoTestResults(MpPublication.class, buildAJourn(), newpubJourn, IGNORE_PROPERTIES, true, true, true);
////        
////        //Test other
////        IpdsMessageLog ipdsMessageLogOther = new IpdsMessageLog();
////        ipdsMessageLogOther.setMessageText(ipdsMessage.replace("<genre>USGS series</genre>", "<genre>Poster, exhibit, newsletter, pre</genre>"));
////        MpPublication newpubOther = IpdsMessageLog.getDao().getFromIpds(IpdsMessageLog.getDao().add(ipdsMessageLogOther));
////        assertNotNull(newpubOther);
////        
////        assertDaoTestResults(MpPublication.class, buildAnOther(), newpubOther, IGNORE_PROPERTIES, true, true, true);
    }

    private MpPublication buildASeries() {
        MpPublication newpub = new MpPublication();
        PublicationType pubType = new PublicationType();
        pubType.setId(PublicationType.REPORT);
        newpub.setPublicationType(pubType);
        PublicationSubtype pubSubtype = new PublicationSubtype();
        pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
        newpub.setPublicationSubtype(pubSubtype);
        PublicationSeries pubSeries = new PublicationSeries();
        pubSeries.setId(PublicationSeries.SIR);
        newpub.setPublicationSeries(pubSeries);
        newpub.setTitle("Pesticides in Wyoming Groundwater, 2008–10");
        newpub.setSeriesNumber("2013-5064");
        newpub.setLanguage("English");
        newpub.setPublisher("U.S. Geological Survey");
        newpub.setPublisherLocation("place location");
        newpub.setDocAbstract("My Abstract");
        newpub.setTemporalStart(new LocalDate(2012,12,12));
        newpub.setTemporalEnd(new LocalDate(2012,12,13));
        newpub.setProductDescription("Physical Description");
        newpub.setNumberOfPages("12");
//        newpub.setNumberOversizedSheets("0");
        newpub.setIpdsId("IP-035570");
        newpub.setUsgsCitation("A citation");
        newpub.setIpdsReviewProcessState(ProcessType.DISSEMINATION.getIpdsValue());
        newpub.setNotes("A synthesis of pesticides in groundwater data collected from 1995 through 2010 in Wyoming, examining changes in use and trends in concentrations");
        newpub.setDoiName("XXX");
        newpub.setIpdsInternalId("81817");
        return newpub;
    }

    private MpPublication buildAJourn() {
        MpPublication newpub = new MpPublication();
//        newpub.setPublicationType("Article");
//        newpub.setPublicationTypeId("62");
        newpub.setTitle("This is my test title");
//        newpub.setPublicationYear("2013");
//        newpub.setLargerWorkId("121");
//        newpub.setLargerWorkType("Journal");
//        newpub.setLargerWorkTitle("Publisher line 1");
//        newpub.setSeries("");
//        newpub.setSeriesCd("");
//        newpub.setSeriesNumber("");
//        newpub.setVolume("volume");
//        newpub.setIssue("issue");
//        newpub.setEdition("");
        newpub.setLanguage("English");
//        newpub.setPublisher("");
        newpub.setPublisherLocation("place location");
//        newpub.setContributingOffice("place cost center");
//        newpub.setSource("IPDS Stub Record");
        newpub.setDocAbstract("My Abstract");
        newpub.setTemporalStart(new LocalDate(2012,12,12));
        newpub.setTemporalEnd(new LocalDate(2012,12,13));
//        newpub.setProdDescription("pd1;pd2;pd3;pd4");
//        newpub.setNumberOfPages();
//        newpub.setNumberOversizedSheets();
//        newpub.setAuthorDisplay("First M Last;First M X Last;Another Name Format");
        newpub.setDoiName("2012/1158/");
//        newpub.setPublicationHandle("2012-1158");
//        newpub.setCombinedTerminologyDisplay("topic 1;topic 2;topic 3");
//        newpub.setLatN("95");
//        newpub.setLatS("90");
//        newpub.setLonW("-120");
//        newpub.setLonE("-100");
//        newpub.setCountry("UNITED STATES");
//        newpub.setState("WI");
//        newpub.setCity("Columbus");
//        newpub.setCounty("Dodge");
        newpub.setIpdsId("IP-038967");
//        newpub.setIpdsCitation("A citation");
        return newpub;
    }

    private MpPublication buildAnOther() {
        MpPublication newpub = new MpPublication();
//        newpub.setPublicationType("Poster");
//        newpub.setPublicationTypeId("63");
        newpub.setTitle("This is my test title");
//        newpub.setPublicationYear("2013");
//        newpub.setLargerWorkId();
//        newpub.setLargerWorkType("");
//        newpub.setLargerWorkTitle("");
//        newpub.setSeries("");
//        newpub.setSeriesCd("");
//        newpub.setSeriesNumber("");
//        newpub.setVolume("volume");
//        newpub.setIssue("issue");
//        newpub.setEdition("");
//        newpub.setLanguage("");
        newpub.setPublisher("Publisher line 1");
        newpub.setPublisherLocation("place location");
//        newpub.setContributingOffice("place cost center");
//        newpub.setSource("IPDS Stub Record");
        newpub.setDocAbstract("My Abstract");
        newpub.setTemporalStart(new LocalDate(2012,12,12));
        newpub.setTemporalEnd(new LocalDate(2012,12,13));
//        newpub.setProdDescription("");
//        newpub.setNumberOfPages();
//        newpub.setNumberOversizedSheets("8");
//        newpub.setAuthorDisplay("First M Last;First M X Last;Another Name Format");
        newpub.setDoiName("2012/1158/");
//        newpub.setPublicationHandle("2012-1158");
//        newpub.setCombinedTerminologyDisplay("topic 1;topic 2;topic 3");
//        newpub.setLatN("95");
//        newpub.setLatS("90");
//        newpub.setLonW("-120");
//        newpub.setLonE("-100");
//        newpub.setCountry("UNITED STATES");
//        newpub.setState("WI");
//        newpub.setCity("Columbus");
//        newpub.setCounty("Dodge");
        newpub.setIpdsId("IP-038967");
//        newpub.setIpdsCitation("A citation");
        return newpub;
    }
}
