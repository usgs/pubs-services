package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDaoTest;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDaoTest;
import gov.usgs.cida.pubs.domain.Publication;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
//import gov.usgs.cida.pubs.domain.ProcessType;

/**
 * @author drsteini
 *
 */
public class PublicationDaoTest extends BaseDaoTest {

    @Test
    public void getByIdTest() {
        //From warehouse
        Publication<?> pub4 = Publication.getPublicationDao().getById(4);
        PwPublicationDaoTest.assertPwPub4(pub4);

        //From mypubs
        Publication<?> pub2 = Publication.getPublicationDao().getById(2);
        MpPublicationDaoTest.assertMpPub2(pub2);
    }

    @Test
    public void getByMapTest() {
        Map<String, Object> filters = new HashMap<>();
        filters.put("id", new int[] { 2 });
        Collection<Publication<?>> pubs = Publication.getPublicationDao().getByMap(filters);
        assertEquals(1, pubs.size());
        assertEquals(2, ((Publication<?>)pubs.toArray()[0]).getId().intValue());

        filters.clear();
        filters.put("indexId", new int[] { 4 });
        pubs = Publication.getPublicationDao().getByMap(filters);
        assertEquals(1, pubs.size());
        assertEquals(4, ((Publication<?>)pubs.toArray()[0]).getId().intValue());

        filters.clear();
        filters.put("ipdsId", new String[] {"IP-056327"});
        pubs = Publication.getPublicationDao().getByMap(filters);
        assertEquals(1, pubs.size());
        assertEquals(70116614, ((Publication<?>)pubs.toArray()[0]).getId().intValue());

        filters.clear();
        filters.put("id", new int[] { 4 });
        filters.put("indexId", new int[] { 4 });
        filters.put("ipdsId", new String[] {"ipds_id"});
        pubs = Publication.getPublicationDao().getByMap(filters);
        assertEquals(1, pubs.size());
        assertEquals(4, ((Publication<?>)pubs.toArray()[0]).getId().intValue());
    }

    @Test
    public void getObjectCountTest() {
        Map<String, Object> filters = new HashMap<>();
        Integer cnt = Publication.getPublicationDao().getObjectCount(null);
        assertEquals(4, cnt.intValue());

        filters.put("ipdsId", new String[] { "ipds_id" });
        cnt = Publication.getPublicationDao().getObjectCount(filters);
        assertEquals(1, cnt.intValue());
    }

}
