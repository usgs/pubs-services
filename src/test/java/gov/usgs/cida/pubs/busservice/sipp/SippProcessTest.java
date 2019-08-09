package gov.usgs.cida.pubs.busservice.sipp;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.domain.ProcessType;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes= {SippProcess.class})
public class SippProcessTest extends BaseTest {

	protected SippProcess sippProcess;

	@Before
	public void setUp() throws Exception {
		sippProcess = new SippProcess();
	}

	@Test
	public void processPublicationTest() {
		ProcessSummary processSummary = sippProcess.processPublication(ProcessType.DISSEMINATION, "IP-12344354");
		assertEquals(0, processSummary.getAdditions());
		assertEquals(0, processSummary.getErrors());
		assertEquals("IP-12344354:\n\n", processSummary.getProcessingDetails());
	}
}
