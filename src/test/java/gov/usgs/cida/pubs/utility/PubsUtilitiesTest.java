package gov.usgs.cida.pubs.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;

import java.util.Arrays;

import org.junit.Test;

public class PubsUtilitiesTest extends BaseSpringTest {

    @Test
    public void isUsgsNumberedSeriesTest() {
        assertFalse(PubsUtilities.isUsgsNumberedSeries(null));
        PublicationSubtype pubSubtype = new PublicationSubtype();
        assertFalse(PubsUtilities.isUsgsNumberedSeries(pubSubtype));
        pubSubtype.setId(1);
        assertFalse(PubsUtilities.isUsgsNumberedSeries(pubSubtype));
        pubSubtype.setId(PublicationSubtype.USGS_UNNUMBERED_SERIES);
        assertFalse(PubsUtilities.isUsgsNumberedSeries(pubSubtype));
        pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
        assertTrue(PubsUtilities.isUsgsNumberedSeries(pubSubtype));
    }

    @Test
    public void isUsgsUnnumberedSeriesTest() {
        assertFalse(PubsUtilities.isUsgsUnnumberedSeries(null));
        PublicationSubtype pubSubtype = new PublicationSubtype();
        assertFalse(PubsUtilities.isUsgsUnnumberedSeries(pubSubtype));
        pubSubtype.setId(1);
        assertFalse(PubsUtilities.isUsgsUnnumberedSeries(pubSubtype));
        pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
        assertFalse(PubsUtilities.isUsgsUnnumberedSeries(pubSubtype));
        pubSubtype.setId(PublicationSubtype.USGS_UNNUMBERED_SERIES);
        assertTrue(PubsUtilities.isUsgsUnnumberedSeries(pubSubtype));
    }

    @Test
    public void isPublicationTypeArticleTest() {
        assertFalse(PubsUtilities.isPublicationTypeArticle(null));
        PublicationType pubType = new PublicationType();
        assertFalse(PubsUtilities.isPublicationTypeArticle(pubType));
        pubType.setId(1);
        assertFalse(PubsUtilities.isPublicationTypeArticle(pubType));
        pubType.setId(PublicationType.REPORT);
        assertFalse(PubsUtilities.isPublicationTypeArticle(pubType));
        pubType.setId(PublicationType.ARTICLE);
        assertTrue(PubsUtilities.isPublicationTypeArticle(pubType));
    }

    @Test
    public void getUsernameTest() {
    	assertEquals("Not Authenticated", PubsUtilities.ANONYMOUS_USER, PubsUtilities.getUsername());
    	
    	//TODO What is our real Authentication???				
//    	buildTestAuthentication("dummy");
//    	assertEquals("Is Authenticated", "dummy", PubsUtilities.getUsername());
//    }
//
//    public static void buildTestAuthentication(String username) {
//    	SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, "password"));
    }

    @Test
    public void buildErrorMsgTest() {
    	Object[] messageArguments = Arrays.asList(new String[]{"abc", "def"}).toArray();
    	assertNull(PubsUtilities.buildErrorMsg(null, null));
    	assertEquals("", PubsUtilities.buildErrorMsg("", null));
    	assertEquals("{0} is already in use on Prod Id {1}.", PubsUtilities.buildErrorMsg("{publication.indexid.duplicate}", null));
    	assertEquals("abc is already in use on Prod Id def.", PubsUtilities.buildErrorMsg("{publication.indexid.duplicate}", messageArguments));
    	assertEquals("is {0} from {1}", PubsUtilities.buildErrorMsg("is {0} from {1}", null));
    	assertEquals("is abc from def", PubsUtilities.buildErrorMsg("is {0} from {1}", messageArguments));
    	assertEquals("abc from def not", PubsUtilities.buildErrorMsg("{0} from {1} not", messageArguments));
    }

}
