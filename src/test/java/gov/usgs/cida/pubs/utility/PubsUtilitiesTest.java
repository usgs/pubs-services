package gov.usgs.cida.pubs.utility;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.utility.PubsUtilities;

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

}
