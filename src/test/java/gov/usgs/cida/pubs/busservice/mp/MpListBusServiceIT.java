package gov.usgs.cida.pubs.busservice.mp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.FullPubsDatabaseSetup;
import gov.usgs.cida.pubs.dao.mp.MpListDao;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.security.UserDetailTestService;
import gov.usgs.cida.pubs.springinit.DbTestConfig;
import gov.usgs.cida.pubs.utility.PubsUtilitiesTest;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, LocalValidatorFactoryBean.class, ConfigurationService.class,
			MpList.class, MpListDao.class, MpListBusService.class})
@FullPubsDatabaseSetup
public class MpListBusServiceIT extends BaseIT {

	@Autowired
	public MpListBusService busService;

	public static final int MP_LIST_CNT = 19;

	@Test
	public void getObjectsTest() {
		busService.getObjects(null);
		busService.getObjects(new HashMap<String, Object>());

		Map<String, Object> filters = new HashMap<>();
		filters.put(MpListDao.ID_SEARCH, -1);
		Collection<MpList> mpLists = busService.getObjects(filters);
		assertNotNull(mpLists);
		assertEquals(0, mpLists.size());

		filters.put(MpListDao.ID_SEARCH, 1);
		mpLists = busService.getObjects(filters);
		assertNotNull(mpLists);
		assertEquals(1, mpLists.size());

		filters.clear();
		filters.put(MpListDao.TEXT_SEARCH, "ipds");
		mpLists = busService.getObjects(filters);
		assertNotNull(mpLists);
		assertEquals(7, mpLists.size());

		filters.put(MpListDao.ID_SEARCH, 3);
		mpLists = busService.getObjects(filters);
		assertNotNull(mpLists);
		assertEquals(1, mpLists.size());

		//Get all Lists
		filters.clear();
		mpLists = busService.getObjects(filters);
		assertNotNull(mpLists);
		assertEquals(MP_LIST_CNT, mpLists.size());
	}

	@Test
	@WithMockUser(username=UserDetailTestService.SPN_USER, authorities={PubsUtilitiesTest.SPN_AUTHORITY})
	public void getSpnObjectsTest() {
		//Now we want just spn...
		Collection<MpList> mpLists = busService.getObjects(new HashMap<>());
		assertNotNull(mpLists);
		assertEquals(1, mpLists.size());
	}
}
