package gov.usgs.cida.pubs.busservice.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.validation.ValidationResults;

import java.util.Collection;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

@Category(IntegrationTest.class)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testData/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/dataset.xml")
})
public class MpListPublicationBusServiceTest extends BaseSpringTest {

	@Autowired
    public Validator validator;

    private MpListPublicationBusService busService;

    @Before
    public void initTest() throws Exception {
        MockitoAnnotations.initMocks(this);
        busService = new MpListPublicationBusService(validator);
    }

    @Test
    public void addPubToListTest() {
        busService.addPubToList(null, null);
        busService.addPubToList(1, null);
        busService.addPubToList(null, new String[0]);

        Collection<MpListPublication> mpListPublications = busService.addPubToList(3, new String[]{"1", "2"});
        assertNotNull(mpListPublications);
        assertEquals(2, mpListPublications.size());
        boolean gotOne = false;
        boolean gotTwo = false;
        for (MpListPublication listPub : mpListPublications) {
        	assertEquals(3, listPub.getMpList().getId().intValue());
        	if (1 == listPub.getMpPublication().getId()) {
        		gotOne = true;
        	} else if (2 == listPub.getMpPublication().getId()) {
        		gotTwo = true;
        	} else {
        		fail("unexpected publication ID:" + listPub.getMpPublication().getId());
        	}
        }
        assertTrue(gotOne);
        assertTrue(gotTwo);
    }

    @Test
    public void removePubFromListTest() {
    	ValidationResults res = busService.removePubFromList(null, null);
    	assertEquals(2, res.getValidationErrors().size());
    	res = busService.removePubFromList(-1, null);
    	assertEquals(1, res.getValidationErrors().size());
    	res = busService.removePubFromList(null, -1);
    	assertEquals(1, res.getValidationErrors().size());
    	
        res = busService.removePubFromList(9, 3);
        assertEquals(0, res.getValidationErrors().size());
        //This one matches the alternate key
        assertNull(MpListPublication.getDao().getById(2));
        //These two only match part of the alternate key
        assertNotNull(MpListPublication.getDao().getById(3));
        assertNotNull(MpListPublication.getDao().getById(4));
    }

}
