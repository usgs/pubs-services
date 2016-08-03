package gov.usgs.cida.pubs.dao.ipds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.domain.ipds.IpdsPubTypeConv;

@Category(IntegrationTest.class)
@DatabaseTearDown("classpath:/testCleanup/clearAll.xml")
public class IpdsPubTypeConvDaoTest extends BaseSpringTest {

	@Test
	@DatabaseSetup("classpath:/testData/ipdsPubsTypeConv.xml")
	public void getByIpdsValueTest() {
		assertNull(IpdsPubTypeConv.getDao().getByIpdsValue(null));

		IpdsPubTypeConv conv = IpdsPubTypeConv.getDao().getByIpdsValue("Atlas");
		assertEquals(1, conv.getId().intValue());
		assertEquals(4, conv.getPublicationType().getId().intValue());
		assertNull(conv.getPublicationSubtype());

		conv = IpdsPubTypeConv.getDao().getByIpdsValue("USGS series");
		assertEquals(13, conv.getId().intValue());
		assertEquals(18, conv.getPublicationType().getId().intValue());
		assertEquals(5, conv.getPublicationSubtype().getId().intValue());
	}
}
