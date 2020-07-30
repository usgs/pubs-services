package gov.usgs.cida.pubs.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.dao.intfc.ICrossRefLogDao;
import gov.usgs.cida.pubs.domain.CrossRefLog;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, CrossRefLogDao.class})
@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
public class CrossRefLogDaoIT extends BaseIT {

	@Autowired
	protected ICrossRefLogDao crossRefLogDao;

	@Test
	@ExpectedDatabase(assertionMode=DatabaseAssertionMode.NON_STRICT,
		table="CROSS_REF_LOG",
		query="select batch_id,prod_id,xmlserialize(content cross_ref_xml as text) cross_ref_xml from cross_ref_log order by 1",
		value="classpath:/testResult/xrefLog.xml")
	public void testCreate() {
		CrossRefLog log = new CrossRefLog();
		log.setBatchId("123");
		log.setProdId("456");
		log.setCrossrefXml("<root/>");
		crossRefLogDao.add(log);

		log = new CrossRefLog("abc", 666, "<root2/>");
		crossRefLogDao.add(log);
	}

}
