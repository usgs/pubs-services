package gov.usgs.cida.pubs.dao.sipp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.domain.sipp.IpdsPubTypeConv;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, IpdsPubTypeConvDao.class})
public class IpdsPubTypeConvDaoIT extends BaseIT {

	@Autowired
	IpdsPubTypeConvDao ipdsPubTypeConvDao;

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/ipdsPubsTypeConv.xml")
	})
	public void getByIpdsValueTest() {
		assertNull(ipdsPubTypeConvDao.getByIpdsValue(null));

		IpdsPubTypeConv conv = ipdsPubTypeConvDao.getByIpdsValue("Atlas");
		assertEquals(1, conv.getId().intValue());
		assertEquals(4, conv.getPublicationType().getId().intValue());
		assertNull(conv.getPublicationSubtype());

		conv = ipdsPubTypeConvDao.getByIpdsValue("USGS series");
		assertEquals(13, conv.getId().intValue());
		assertEquals(18, conv.getPublicationType().getId().intValue());
		assertEquals(5, conv.getPublicationSubtype().getId().intValue());
	}
}
