package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.aop.SetDbContextAspect;
import gov.usgs.cida.pubs.domain.PublicationType;

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
        assertEquals("No Authentication", SetDbContextAspect.ANONYMOUS_USER, clientId);
    }

}
