package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;

@Category(IntegrationTest.class)
@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
@DatabaseTearDown("classpath:/testCleanup/clearAll.xml")
public class PublicationSeriesDaoTest extends BaseSpringTest {

    public static final int pubSeriesCnt = 14;
    public static final int activePubSeriesCnt = 8;

    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSeries.xml")
    })
    public void getByIdInteger() {
        PublicationSeries pubSeries = PublicationSeries.getDao().getById(330);
        assertNotNull(pubSeries);
        assertEquals(330, pubSeries.getId().intValue());
        assertEquals("Open-File Report", pubSeries.getText());
        assertEquals("OFR", pubSeries.getCode());
        assertNull(pubSeries.getSeriesDoiName());
        assertEquals("0196-1497", pubSeries.getPrintIssn());
        assertEquals("2331-1258", pubSeries.getOnlineIssn());
        assertTrue(pubSeries.isActive());

        pubSeries = PublicationSeries.getDao().getById(341);
        assertNotNull(pubSeries);
        assertEquals(341, pubSeries.getId().intValue());
        assertEquals("Water Supply Paper", pubSeries.getText());
        assertEquals("WSP", pubSeries.getCode());
        assertNull(pubSeries.getSeriesDoiName());
        assertNull(pubSeries.getPrintIssn());
        assertNull(pubSeries.getOnlineIssn());
        assertFalse(pubSeries.isActive());
    }

    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSeries.xml")
    })
    public void getByIdString() {
        PublicationSeries pubSeries = PublicationSeries.getDao().getById("1");
        assertPubSeries1(pubSeries);
    }

    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSeries.xml")
    })
    public void getByMapAndCount() {
        List<PublicationSeries> pubSeries = PublicationSeries.getDao().getByMap(null);
        assertEquals(pubSeriesCnt, pubSeries.size());

        Map<String, Object> filters = new HashMap<>();
        filters.put("id", 133);
        pubSeries = PublicationSeries.getDao().getByMap(filters);
        assertNotNull(pubSeries);
        assertEquals(1, pubSeries.size());
        assertEquals(133, pubSeries.get(0).getId().intValue());
        assertEquals("Report", pubSeries.get(0).getText());
        assertNull(pubSeries.get(0).getCode());
        assertNull(pubSeries.get(0).getSeriesDoiName());
        assertNull(pubSeries.get(0).getPrintIssn());
        assertNull(pubSeries.get(0).getOnlineIssn());
        assertFalse(pubSeries.get(0).isActive());
        assertEquals(1, PublicationSeries.getDao().getObjectCount(filters).intValue());

        filters.clear();
        filters.put(PublicationSeriesDao.SUBTYPE_SEARCH, 5);
        pubSeries = PublicationSeries.getDao().getByMap(filters);
        assertNotNull(pubSeries);
        assertEquals(8, pubSeries.size());
        assertEquals(8, PublicationSeries.getDao().getObjectCount(filters).intValue());

        filters.put("text", "sc");
        pubSeries = PublicationSeries.getDao().getByMap(filters);
        assertEquals(2, pubSeries.size());
        assertEquals(2, PublicationSeries.getDao().getObjectCount(filters).intValue());

        filters.clear();
        filters.put("code", "MINERAL");
        pubSeries = PublicationSeries.getDao().getByMap(filters);
        assertEquals(1, pubSeries.size());
        assertEquals(323, pubSeries.get(0).getId().intValue());
        assertEquals("Mineral Commodities Summaries", pubSeries.get(0).getText());
        assertEquals("MINERAL", pubSeries.get(0).getCode());
        assertNull(pubSeries.get(0).getSeriesDoiName());
        assertEquals("0076-8952", pubSeries.get(0).getPrintIssn());
        assertNull(pubSeries.get(0).getOnlineIssn());
        assertTrue(pubSeries.get(0).isActive());
        assertEquals(1, PublicationSeries.getDao().getObjectCount(filters).intValue());

        filters.clear();
        filters.put("active", "Y");
        pubSeries = PublicationSeries.getDao().getByMap(filters);
        assertEquals(activePubSeriesCnt, pubSeries.size());
        assertEquals(activePubSeriesCnt, PublicationSeries.getDao().getObjectCount(filters).intValue());

    }

    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSeries.xml")
    })
	@ExpectedDatabase(value = "classpath:/testResult/publicationSeries/delete.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void deleteTest() {
       PublicationSeries.getDao().delete(new PublicationSeries());
       PublicationSeries one = new PublicationSeries();
       one.setId(1);
       PublicationSeries.getDao().delete(one);
       PublicationSeries.getDao().deleteById(333);
       PublicationSeries.getDao().deleteByParent(10);
    }

    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml")
    })
	@ExpectedDatabase(value = "classpath:/testResult/publicationSeries/add.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED, modifiers = IdModifier.class)
    public void addTest() {
    	PublicationSeries publicationSeries = buildAPubSeries(null);
        id = PublicationSeries.getDao().add(publicationSeries);
    }
    
    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSeries.xml")
    })
	@ExpectedDatabase(value = "classpath:/testResult/publicationSeries/update.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void updateTest() {
    	PublicationSeries publicationSeries = update330Properties();
    	PublicationSeries.getDao().update(publicationSeries);
    }
    
    public static void assertPubSeries1(PublicationSeries pubSeries) {
        assertNotNull(pubSeries);
        assertEquals(1, pubSeries.getId().intValue());
        assertEquals("Administrative Report", pubSeries.getText());
        assertNull(pubSeries.getCode());
        assertNull(pubSeries.getSeriesDoiName());
        assertNull(pubSeries.getPrintIssn());
        assertNull(pubSeries.getOnlineIssn());
        assertFalse(pubSeries.isActive());
    }

    public static PublicationSeries buildAPubSeries(Integer id) {
    	PublicationSeries pubSeries = new PublicationSeries();
    	pubSeries.setId(id);
     	PublicationSubtype publicationSubtype = new PublicationSubtype();
     	publicationSubtype.setId(29);
     	pubSeries.setPublicationSubtype(publicationSubtype);
     	pubSeries.setText("New Video");
 		pubSeries.setCode("XYZ");
 		pubSeries.setSeriesDoiName("doiname is here");
 		pubSeries.setPrintIssn("1234-4321");
 		pubSeries.setOnlineIssn("5678-8765");
 		pubSeries.setActive(true);
     	return pubSeries;
    }
    
    public static PublicationSeries update330Properties() {
    	PublicationSeries pubSeries = new PublicationSeries();
    	pubSeries.setId(330);
    	PublicationSubtype publicationSubtype = new PublicationSubtype();
    	publicationSubtype.setId(29);
    	pubSeries.setPublicationSubtype(publicationSubtype);
		pubSeries.setText("New Video");
		pubSeries.setCode("XYZ");
		pubSeries.setSeriesDoiName("doiname is here");
		pubSeries.setPrintIssn("1234-4321");
		pubSeries.setOnlineIssn("5678-8765");
    	pubSeries.setActive(false);
    	return pubSeries;
    }
}
