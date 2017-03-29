package gov.usgs.cida.pubs.busservice.ipds;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.util.FileCopyUtils;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.PubMap;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;

@Category(IntegrationTest.class)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/ipdsPubsTypeConv.xml")
})
public class IpdsProcessTxnTest extends BaseSpringTest {

	@Autowired
	protected IpdsProcess ipdsProcess;
//	@Value("classpath:testData/ipdsTxn.xml")
	@Value("classpath:testData/feedip.xml")
	private org.springframework.core.io.Resource feed;
	public String feedXml() throws IOException {
		return new String(FileCopyUtils.copyToByteArray(feed.getInputStream()));
	}

	@Test
	public void processLogTest() throws IOException {
		IpdsMessageLog ipdsMessageLogSeries = new IpdsMessageLog();
		String ipdsMessage = feedXml();
		ipdsMessageLogSeries.setMessageText(ipdsMessage);
		Integer id = IpdsMessageLog.getDao().add(ipdsMessageLogSeries);
//		for (int i=2; i<32; i++) {
			LOG.info(ipdsProcess.processLog(ProcessType.DISSEMINATION, id));
//		}
//		for (int i=58; i<99; i++) {
//			LOG.info(ipdsProcess.processLog(ProcessType.DISSEMINATION, i));
//		}
	}


}
