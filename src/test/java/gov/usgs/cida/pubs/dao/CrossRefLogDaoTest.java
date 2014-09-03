package gov.usgs.cida.pubs.dao;

import gov.usgs.cida.pubs.domain.CrossRefLog;

import org.junit.Test;

import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

public class CrossRefLogDaoTest extends BaseSpringDaoTest {

	@Test
	@ExpectedDatabase(assertionMode=DatabaseAssertionMode.NON_STRICT,
		table="CROSS_REF_LOG",
		query="select batch_id,prod_id,xmlserialize(content cross_ref_xml no indent) cross_ref_xml from cross_ref_log",
		value="classpath:/testData/expectedXrefLog.xml")
	public void testCreate() {
		CrossRefLog log = new CrossRefLog();
		log.setBatchId("123");
		log.setProdId("456");
		log.setCrossrefXml("<root/>");
		CrossRefLog.getDao().add(log);
		
		log = new CrossRefLog("abc", 666, "<root2/>");
		CrossRefLog.getDao().add(log);
	}

}
