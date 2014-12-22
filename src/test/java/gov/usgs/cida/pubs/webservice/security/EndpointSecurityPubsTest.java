package gov.usgs.cida.pubs.webservice.security;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class EndpointSecurityPubsTest extends EndpointSecurityAuthTest {

    @Before
    public void setup() {
    	super.setup();
    }

	@Test
    public void adAuthenticatedTest() throws Exception {
		mockSetup();
		when(mockAuthClient.getRolesByToken("a-token-string")).thenReturn(new ArrayList<>(Arrays.asList(PubsRoles.PUBS_ADMIN.name())));
    	publicTest(httpHeaders, status().isOk());
    	authenticatedTest(httpHeaders, status().isOk());
    	pubsAuthorizedTestGetsDeletes(httpHeaders, status().isOk(), true);
    	pubsAuthorizedTestPosts(httpHeaders, status().isCreated(), true);
    	pubsAuthorizedTestPuts(httpHeaders, status().isOk(), true);
    	adAuthenticatedOrPubsAuthorizedTest(httpHeaders, status().isOk());
    }

}
