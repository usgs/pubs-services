package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.utility.PubsUtilitiesTest;

import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author drsteini
 *
 */
public class BaseDaoTest extends BaseSpringTest {

    @Test
    public void getClientIdNoAuth() {
        //no authentication
        String clientId = PublicationType.getDao().getClientId();
        assertNotNull("No Authentication", clientId);
        assertEquals("No Authentication", PubsConstants.ANONYMOUS_USER, clientId);
    }
        
    @Test
    public void getClientIdAuth() {
        //have authentication
        PubsUtilitiesTest.buildTestAuthentication("dummy");
        String clientId = PublicationType.getDao().getClientId();
        assertNotNull("Have Authentication", clientId);
        assertEquals("Have Authentication", "dummy", clientId);
    }

    @Test
    public void getClientIdNoAuthAgain() {
        //no authentication again
    	SecurityContextHolder.clearContext();
        String clientId = PublicationType.getDao().getClientId();
        assertNotNull("No Authentication", clientId);
        assertEquals("No Authentication", PubsConstants.ANONYMOUS_USER, clientId);
    }

}
