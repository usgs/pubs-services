package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.domain.PublicationIndex;
import gov.usgs.cida.pubs.domain.mp.MpPublication;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

@Category(IntegrationTest.class)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/dataset.xml")
})
public class PublicationIndexDaoTest extends BaseSpringTest {
	
    @Test
    public void getByIdTest() {
    	PublicationIndex publicationIndex = PublicationIndex.getDao().getById(5);
        assertEquals(5, publicationIndex.getId().intValue());
        assertEquals("future title 9 ConFamily, ConGiven, ConSuffix;US Geological Survey Ice Survey Team; US Geological Survey Ice Survey Team; Affiliation Cost Center 2", publicationIndex.getQ());
    }

    @Test
    public void updateTest() {
    	//setup a pub
    	MpPublication pub = new MpPublication();
    	pub.setId(MpPublication.getDao().getNewProdId());
    	pub.setTitle("test");
    	MpPublication.getDao().add(pub);
    	MpPublication.getDao().publishToPw(pub.getId());
    	
    	//update the index table
    	PublicationIndex.getDao().publish(pub.getId());
    	
    	//verify the update
    	PublicationIndex publicationIndex = PublicationIndex.getDao().getById(pub.getId());
        assertEquals(pub.getId(), publicationIndex.getId());
        assertEquals("test    " + pub.getId(), publicationIndex.getQ().trim());
    }

}
