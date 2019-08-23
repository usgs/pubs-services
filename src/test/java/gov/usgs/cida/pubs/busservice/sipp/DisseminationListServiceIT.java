package gov.usgs.cida.pubs.busservice.sipp;

import static org.mockito.Mockito.when;

import java.util.List;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.busservice.ext.ExtPublicationService;
import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.busservice.intfc.IListBusService;
import gov.usgs.cida.pubs.busservice.mp.MpPublicationBusService;
import gov.usgs.cida.pubs.dao.sipp.IpdsBureauApprovalDao;
import gov.usgs.cida.pubs.dao.sipp.SippProcessLogDao;
import gov.usgs.cida.pubs.domain.IpdsBureauApprovalHelper;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.domain.sipp.IpdsBureauApproval;
import gov.usgs.cida.pubs.domain.sipp.SippProcessLog;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, SippProcess.class, IpdsBureauApproval.class,
			SippProcessLog.class, SippProcessLogDao.class, SippConversionService.class,
			MpPublicationBusService.class})
@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
public class DisseminationListServiceIT extends BaseIT {

	public static final String MOCK_SIPP_URL = "https://localhost/mock?a=";

	@MockBean(name="configurationService")
	protected ConfigurationService configurationService;
	@MockBean(name="extPublicationBusService")
	protected ExtPublicationService extPublicationBusService;
	@MockBean(name="validator")
	protected Validator validator;
	@MockBean(name="crossRefBusService")
	protected ICrossRefBusService crossRefBusService;
	@MockBean(name="mpPublicationCostCenterBusService")
	protected IListBusService<PublicationCostCenter<MpPublicationCostCenter>> costCenterBusService;
	@MockBean(name="mpPublicationLinkBusService")
	protected IListBusService<PublicationLink<MpPublicationLink>> linkBusService;
	@MockBean(name="mpPublicationContributorBusService")
	protected IListBusService<PublicationContributor<MpPublicationContributor>> contributorBusService;
	@MockBean(name="ipdsBureauApprovalDao")
	protected IpdsBureauApprovalDao ipdsBureauApprovalDao;
	@Autowired
	protected SippProcess sippProcess;

	protected DisseminationListService service;

	@Before
	public void setUp() {
		service = new DisseminationListService(sippProcess);
	}

	@Test
	@ExpectedDatabase(
			value="classpath:/testResult/sipp/dissemination/csv/",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			table="sipp_process_log",
			query="select process_type, total_entries,publications_added,errors_encountered,processing_details from sipp_process_log")
	public void processDisseminationListTest() {
		when(ipdsBureauApprovalDao.getIpdsBureauApprovals(1))
			.thenReturn(List.of(IpdsBureauApprovalHelper.getIpdsBureauApproval("IP-1234"), IpdsBureauApprovalHelper.getIpdsBureauApproval("IP-5678")));
		service.processDisseminationList(1);
	}

}
