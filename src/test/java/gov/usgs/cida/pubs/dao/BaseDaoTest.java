package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.utility.PubsUtilities;

import org.junit.Test;

/**
 * @author drsteini
 *
 */
public class BaseDaoTest extends BaseSpringTest {

    @Test
    public void getClientId() {
        //no authentication
        String clientId = PublicationType.getDao().getClientId();
        assertNotNull("No Authentication", clientId);
        assertEquals("No Authentication", PubsUtilities.ANONYMOUS_USER, clientId);
        
        //TODO
      //TODO What is our real Authentication???				
//        //have authentication
//        PubsUtilitiesTest.buildTestAuthentication("dummy");
//        clientId = PublicationType.getDao().getClientId();
//        assertNotNull("Have Authentication", clientId);
//        assertEquals("Have Authentication", "dummy", clientId);
    }

}
