package gov.usgs.cida.pubs.webservice;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.domain.CorporateContributor;
import gov.usgs.cida.pubs.domain.PersonContributor;

import org.junit.Before;
import org.junit.Ignore;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@Ignore
public class ContributorMvcServiceTest extends BaseSpringTest {

	@Mock
	private IBusService<CorporateContributor> corporateContributorBusService;
	@Mock
	private IBusService<PersonContributor<?>> personContributorBusService;

//    private MockMvc mockMvc;

//    private ContributorMvcService mvcService;
	
    @Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
//    	mvcService = new ContributorMvcService(corporateContributorBusService, personContributorBusService);
//    	mockMvc = MockMvcBuilders.standaloneSetup(mvcService).setMessageConverters(jackson2HttpMessageConverter).build();
    }

//  @RequestMapping(value={"/contributor/{contributorId}"}, method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
//	@Test
//    public void getPreviewTest() throws Exception {
//    	//Happy Path
//        when(busService.getByIndexId(anyString())).thenReturn(buildAPub(1));
//    	
//        MvcResult rtn = mockMvc.perform(get("/mppublications/" + MpPublicationDaoTest.MPPUB1_INDEXID
//        		+ "/preview").accept(MediaType.APPLICATION_JSON))
//        .andExpect(status().isOk())
//        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
//        .andReturn();
//
//        assertThat(getRtnAsJSONObject(rtn),
//                sameJSONObjectAs(new JSONObject(expectedGetMpPub1)));
//    }
//	@RequestMapping(value={"/person/{contributorId}"}, method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
//	@RequestMapping(value={"/usgscontributor"}, method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
//	@RequestMapping(value="/usgscontributor/{id}", method=RequestMethod.PUT, produces=MediaType.APPLICATION_JSON_VALUE)
//	@RequestMapping(value={"/outsidecontributor"}, method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
//	@RequestMapping(value="/outsidecontributor/{id}", method=RequestMethod.PUT, produces=MediaType.APPLICATION_JSON_VALUE)
//	@RequestMapping(value={"/corporation/{contributorId}"}, method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
//	@RequestMapping(value={"/corporation"}, method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
//	@RequestMapping(value="/corporation/{id}", method=RequestMethod.PUT, produces=MediaType.APPLICATION_JSON_VALUE)

}
