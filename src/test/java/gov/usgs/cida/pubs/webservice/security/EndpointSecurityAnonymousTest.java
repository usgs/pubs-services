package gov.usgs.cida.pubs.webservice.security;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import gov.usgs.cida.auth.model.AuthToken;
import gov.usgs.cida.pubs.domain.pw.PwPublicationTest;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

public class EndpointSecurityAnonymousTest extends BaseEndpointSecurityTest {

	private HttpHeaders httpHeaders;
	
    @Before
    public void setup() {
    	preSetup();
    	postSetup();
    }

    @SuppressWarnings("unchecked")
	@Test
    public void anonymousTest() throws Exception {
        when(authenticationService.authenticate(anyString(), anyString())).thenReturn(new AuthToken());
        when(pwPubBusService.getObjects(anyMap())).thenReturn(Arrays.asList(PwPublicationTest.buildAPub(1)));
        when(pwPubBusService.getByIndexId(anyString())).thenReturn(PwPublicationTest.buildAPub(1));
    	
    	httpHeaders = new HttpHeaders();
    	publicTest(httpHeaders, status().isOk());
    	authenticatedTest(httpHeaders, status().isUnauthorized());
    	pubsAuthorizedTestGetsDeletes(httpHeaders, status().isUnauthorized(), false);
    	pubsAuthorizedTestPosts(httpHeaders, status().isUnauthorized(), false);
    	pubsAuthorizedTestPuts(httpHeaders, status().isUnauthorized(), false);
    	adAuthenticatedOrPubsAuthorizedTest(httpHeaders, status().isUnauthorized());
    }
    
}
