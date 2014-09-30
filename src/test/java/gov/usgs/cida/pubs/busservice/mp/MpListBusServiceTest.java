package gov.usgs.cida.pubs.busservice.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import gov.usgs.cida.pubs.dao.BaseSpringDaoTest;
import gov.usgs.cida.pubs.dao.mp.MpListDaoTest;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.validation.ValidationResults;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

public class MpListBusServiceTest extends BaseSpringDaoTest {

	@Autowired
    public Validator validator;

    private MpListBusService busService;

    @Before
    public void initTest() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        busService = new MpListBusService(validator);
    }

    @Test
    public void getObjectsTest() {
        busService.getObjects(null);
        busService.getObjects(new HashMap<String, Object>());

        Map<String, Object> filters = new HashMap<>();
        filters.put("id", -1);
        Collection<MpList> mpLists = busService.getObjects(filters);
        assertNotNull(mpLists);
        assertEquals(0, mpLists.size());

        filters.put("id", 1);
        mpLists = busService.getObjects(filters);
        assertNotNull(mpLists);
        assertEquals(1, mpLists.size());

        filters.clear();
        filters.put("text", "ipds");
        mpLists = busService.getObjects(filters);
        assertNotNull(mpLists);
        assertEquals(5, mpLists.size());

        filters.put("id", 3);
        mpLists = busService.getObjects(filters);
        assertNotNull(mpLists);
        assertEquals(1, mpLists.size());
    }

    @Test
    public void createObjectTest() {
        busService.createObject(null);

        MpList mpList = busService.createObject(new MpList());
        assertNotNull(mpList.getId());
        
        mpList = busService.createObject(MpListDaoTest.buildMpList(999999999));
        MpListDaoTest.assertMpList("999999999", mpList);
    }

    @Test
    public void updateObjectTest() {
        busService.updateObject(null);
        busService.updateObject(new MpList());

        MpList mpList = busService.createObject(MpListDaoTest.buildMpList(66));
        mpList.setText(mpList.getText() + "b");
        mpList.setDescription(mpList.getDescription() + "b");
        mpList.setType(mpList.getType() + "b");
        MpList after = busService.updateObject(mpList);
        MpListDaoTest.assertMpList("66b", after);
    }

    @Test
    public void deleteObjectTest() {
        busService.deleteObject(null);
        busService.deleteObject(-1);
        
        //Cannot delete this one!
        ValidationResults res = busService.deleteObject(3);
        assertEquals(1, res.getValidationErrors().size());
        assertNotNull(MpList.getDao().getById(3));

        //Can delete this one!
        busService.deleteObject(281);
        assertNull(MpList.getDao().getById(281));
    }

}
