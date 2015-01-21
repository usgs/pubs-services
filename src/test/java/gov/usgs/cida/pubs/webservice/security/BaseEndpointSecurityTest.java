package gov.usgs.cida.pubs.webservice.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import gov.usgs.cida.auth.client.IAuthClient;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.busservice.intfc.IMpListPublicationBusService;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.busservice.intfc.IPwPublicationBusService;
import gov.usgs.cida.pubs.domain.CorporateContributor;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.webservice.ContributorMvcService;
import gov.usgs.cida.pubs.webservice.LookupMvcService;
import gov.usgs.cida.pubs.webservice.VersionMvcService;
import gov.usgs.cida.pubs.webservice.mp.MpListMvcService;
import gov.usgs.cida.pubs.webservice.mp.MpListPublicationMvcService;
import gov.usgs.cida.pubs.webservice.mp.MpPublicationMvcService;
import gov.usgs.cida.pubs.webservice.pw.PwPublicationMvcService;
import gov.usgs.cida.pubs.webservice.pw.PwPublicationRssMvcService;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public abstract class BaseEndpointSecurityTest extends BaseSpringTest {

    @Autowired
	protected FilterChainProxy springSecurityFilter;

    @Mock
    protected IAuthClient mockAuthClient;
    @Mock
    protected AuthenticationService authenticationService;
    @Mock
    protected IMpPublicationBusService mpPubBusService;
	private AuthTokenService authTokenService;
    private MockMvc mockAuth;

    @Mock
    protected IBusService<Publication<?>> pubBusService;
	private MpPublicationMvcService mpPubMvc;
    private MockMvc mockMpPub;
    
    @Mock
	protected IPwPublicationBusService pwPubBusService;
	private PwPublicationMvcService pwPubMvc;
    private MockMvc mockPwPub;
    private PwPublicationRssMvcService pwPubRssMvc;
    private MockMvc mockPwPubRss;
    
    @Mock
    protected IBusService<CorporateContributor> corpBusService;
    @Mock
    protected IBusService<PersonContributor<?>> personBusService;
	private ContributorMvcService contribMvc;
    private MockMvc mockContrib;

	@Mock
	protected IBusService<MpList> listBusService;
    private MpListMvcService listMvc;
    private MockMvc mockList;

    @Mock
    protected IMpListPublicationBusService listPubBusService;
    private MpListPublicationMvcService listPubMvc;
    private MockMvc mockListPub;

    private MockMvc mockVersion;

    private MockMvc mockLookup;
    
    public void preSetup() {
    	MockitoAnnotations.initMocks(this);
    }

    public void postSetup() {
    	authTokenService = new AuthTokenService(authenticationService, mpPubBusService);
    	mockAuth = MockMvcBuilders.standaloneSetup(authTokenService).addFilters(springSecurityFilter).build();
    	
    	mpPubMvc = new MpPublicationMvcService(pubBusService, mpPubBusService);
    	mockMpPub = MockMvcBuilders.standaloneSetup(mpPubMvc).addFilters(springSecurityFilter).build();

    	pwPubMvc = new PwPublicationMvcService(pwPubBusService);
    	mockPwPub = MockMvcBuilders.standaloneSetup(pwPubMvc).addFilters(springSecurityFilter).build();
    	pwPubRssMvc = new PwPublicationRssMvcService(pwPubBusService);
    	mockPwPubRss = MockMvcBuilders.standaloneSetup(pwPubRssMvc).addFilters(springSecurityFilter).build();
    	
    	contribMvc = new ContributorMvcService(corpBusService, personBusService);
    	mockContrib = MockMvcBuilders.standaloneSetup(contribMvc).addFilters(springSecurityFilter).build();

    	listMvc = new MpListMvcService(listBusService);
    	mockList = MockMvcBuilders.standaloneSetup(listMvc).addFilters(springSecurityFilter).build();

    	listPubMvc = new MpListPublicationMvcService(listPubBusService);
    	mockListPub = MockMvcBuilders.standaloneSetup(listPubMvc).addFilters(springSecurityFilter).build();

    	mockVersion = MockMvcBuilders.standaloneSetup(new VersionMvcService()).addFilters(springSecurityFilter).build();

    	mockLookup = MockMvcBuilders.standaloneSetup(new LookupMvcService()).addFilters(springSecurityFilter).build();
    }

	public void publicTest(HttpHeaders httpHeaders, ResultMatcher expectedStatus) throws Exception {
    	//Lookups
		mockLookup.perform(get("/lookup/publicationtypes?text=b").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        	.andExpect(expectedStatus);
        
		mockLookup.perform(get("/lookup/publicationtype/4/publicationsubtypes?text=b").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        	.andExpect(expectedStatus);
        
		mockLookup.perform(get("/lookup/publicationsubtypes?text=b&publicationtypeid=4").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        	.andExpect(expectedStatus);
        
		mockLookup.perform(get("/lookup/publicationtype/"
            + PublicationType.REPORT + "/publicationsubtype/1/publicationseries?text=a").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
            .andExpect(expectedStatus);
        
		mockLookup.perform(get("/lookup/publicationseries?text=a&publicationsubtypeid=1").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        	.andExpect(expectedStatus);
        
		mockLookup.perform(get("/lookup/costcenters?mimetype=json").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        	.andExpect(expectedStatus);
        
		mockLookup.perform(get("/lookup/outsideaffiliates?mimetype=json").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        	.andExpect(expectedStatus);
        
		mockLookup.perform(get("/lookup/contributortypes?mimetype=json").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        	.andExpect(expectedStatus);
        
		mockLookup.perform(get("/lookup/linktypes?mimetype=json").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
            .andExpect(expectedStatus);
        
		mockLookup.perform(get("/lookup/linkfiletypes?mimetype=json").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
            .andExpect(expectedStatus);
        
		mockLookup.perform(get("/lookup/people?mimetype=json").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
            .andExpect(expectedStatus);
        
		mockLookup.perform(get("/lookup/corporations?mimetype=json").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
            .andExpect(expectedStatus);
		
		mockLookup.perform(get("/lookup/publishingServiceCenters?mimetype=json").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
            .andExpect(expectedStatus);
        
        //Version
        mockVersion.perform(get("/version?mimetype=json").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        	.andExpect(expectedStatus);
        
        //Auth
        mockAuth.perform(options("/auth/token").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
    		.andExpect(expectedStatus);

        mockAuth.perform(post("/auth/token").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
    		.andExpect(status().isOk());

        //Publication
        mockPwPub.perform(get("/publication?mimetype=json").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        	.andExpect(expectedStatus);
        
        mockPwPub.perform(get("/publication/1?mimetype=json").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        	.andExpect(expectedStatus);
        
        mockPwPubRss.perform(get("/publication/rss?orderby=dispPubDate").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType("text/xml")))
        	.andExpect(expectedStatus);
    }
    
    public void authenticatedTest(HttpHeaders httpHeaders, ResultMatcher expectedStatus) throws Exception {
    	
    	//Auth
    	mockAuth.perform(post("/auth/logout").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
    		.andExpect(expectedStatus);

    }

    public void pubsAuthorizedTestGetsDeletes(HttpHeaders httpHeaders, ResultMatcher expectedStatus, boolean fudge) throws Exception {
    	//Contributor
    	mockContrib.perform(get("/corporation/-1").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
    		.andExpect(expectedStatus);
    	
    	//These two endpoints are hard to mock, so we'll fudge it for now...
    	if (fudge) {
        	mockContrib.perform(get("/contributor/-1").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
    			.andExpect(status().isNotFound());
	
        	mockContrib.perform(get("/person/-1").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
    			.andExpect(status().isNotFound());
    	} else {
        	mockContrib.perform(get("/contributor/-1").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
    			.andExpect(expectedStatus);
	
        	mockContrib.perform(get("/person/-1").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
    			.andExpect(expectedStatus);
    	}

    	//MpList
    	mockList.perform(get("/lists?mimetype=json").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        	.andExpect(expectedStatus);
    	
    	//MpListPublication
    	mockListPub.perform(delete("/lists/66/pubs/12")
    		.secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
    		.andExpect(expectedStatus);
    	
    	//MpPublication
    	mockMpPub.perform(delete("/mppublications/1").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        	.andExpect(expectedStatus);
        
    	mockMpPub.perform(get("/mppublications/1?mimetype=json").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        	.andExpect(expectedStatus);
    	
    	mockMpPub.perform(get("/mppublications?mimetype=json").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
    		.andExpect(expectedStatus);
        
    	//These two are posts, but they return the same codes as a get when successful...
    	mockMpPub.perform(post("/mppublications/publish").content("{}").contentType(MediaType.APPLICATION_JSON)
    		.secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
    	    .andExpect(expectedStatus);
    	        
    	mockMpPub.perform(post("/mppublications/release").content("{}").contentType(MediaType.APPLICATION_JSON)
    		.secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
    		.andExpect(expectedStatus);
    }
    
    public void pubsAuthorizedTestPosts(HttpHeaders httpHeaders, ResultMatcher expectedStatus, boolean fudge) throws Exception {
    	//Contributor
    	mockContrib.perform(post("/corporation").content("{}").contentType(MediaType.APPLICATION_JSON)
    		.secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
    		.andExpect(expectedStatus);
    	
    	//These two endpoints are hard to mock, so we'll fudge it for now...
    	if (fudge) {
	    	mockContrib.perform(post("/outsidecontributor").content("{}").contentType(MediaType.APPLICATION_JSON)
	    		.secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
	    		.andExpect(status().isBadRequest());
	    			
	    	mockContrib.perform(post("/usgscontributor").content("{}").contentType(MediaType.APPLICATION_JSON)
	    		.secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
	    		.andExpect(status().isBadRequest());
    	} else {
        	mockContrib.perform(post("/outsidecontributor").content("{}").contentType(MediaType.APPLICATION_JSON)
        		.secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
            	.andExpect(expectedStatus);
            			
            mockContrib.perform(post("/usgscontributor").content("{}").contentType(MediaType.APPLICATION_JSON)
            	.secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
            	.andExpect(expectedStatus);
    	}

    	//MpListPublication
    	mockListPub.perform(post("/lists/66/pubs?publicationId=12")
    	    .secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
    	    .andExpect(expectedStatus);
    	
    	//MpPublication
    	mockMpPub.perform(post("/mppublications").content("{}").contentType(MediaType.APPLICATION_JSON)
        	.secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        	.andExpect(expectedStatus);
    }
    
    public void pubsAuthorizedTestPuts(HttpHeaders httpHeaders, ResultMatcher expectedStatus, boolean fudge) throws Exception {
    	//Contributor
    	mockContrib.perform(put("/corporation/1").content("{}").contentType(MediaType.APPLICATION_JSON)
    		.secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
    		.andExpect(expectedStatus);

    	//These two endpoints are hard to mock, so we'll fudge it for now...
    	if (fudge) {
	    	mockContrib.perform(put("/outsidecontributor/1").content("{}").contentType(MediaType.APPLICATION_JSON)
	    		.secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
	    		.andExpect(status().isBadRequest());
	
	    	mockContrib.perform(put("/usgscontributor/1").content("{}").contentType(MediaType.APPLICATION_JSON)
	    		.secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
	    		.andExpect(status().isBadRequest());
    	} else {
	    	mockContrib.perform(put("/outsidecontributor/1").content("{}").contentType(MediaType.APPLICATION_JSON)
	    		.secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
	    		.andExpect(expectedStatus);
		
	    	mockContrib.perform(put("/usgscontributor/1").content("{}").contentType(MediaType.APPLICATION_JSON)
	    		.secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
	    		.andExpect(expectedStatus);
    	}

    	//MpPublication
    	mockMpPub.perform(put("/mppublications/2").content("{}").contentType(MediaType.APPLICATION_JSON)
            .secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
            .andExpect(expectedStatus);
    }
    
    public void adAuthenticatedOrPubsAuthorizedTest(HttpHeaders httpHeaders, ResultMatcher expectedStatus) throws Exception {
    	mockMpPub.perform(get("/mppublications/1/preview").secure(true).headers(httpHeaders).accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
           	.andExpect(expectedStatus);
    }

}
