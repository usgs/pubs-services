package gov.usgs.cida.pubs.webservice.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.springframework.http.HttpHeaders;

import gov.usgs.cida.auth.model.AuthToken;
import gov.usgs.cida.pubs.dao.CorporateContributorDaoIT;
import gov.usgs.cida.pubs.dao.mp.MpListDaoIT;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDaoIT;
import gov.usgs.cida.pubs.domain.CorporateContributor;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublicationTest;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.webservice.mp.MpListMvcServiceTest;

public abstract class EndpointSecurityAuthTest extends BaseEndpointSecurityIT {

	protected HttpHeaders httpHeaders;
	protected AuthenticationService authService;

	private TokenSecurityFilter testFilter;

	public void setup() {
		//We want to override the TokenSecurityFilter with one that will have the authenticationService mocked to our needs
		//This is relying on that filter being in the position hard-coded below...
		authService = new AuthenticationService(mockAuthClient);
		testFilter = new TokenSecurityFilter(authService);
		for (int i=0; i<springSecurityFilter.getFilterChains().get(0).getFilters().size(); i++) {
			if (springSecurityFilter.getFilterChains().get(0).getFilters().get(i) instanceof TokenSecurityFilter) {
				springSecurityFilter.getFilterChains().get(0).getFilters().set(i, testFilter);
			}
		}

		postSetup();
	}

	public void mockSetup() throws Exception {
		AuthToken testToken = new AuthToken();
		testToken.setTokenId("a-token-string");
		testToken.setUsername("testyUser");

		when(pwPubBusService.getObjects(anyMap())).thenReturn(Arrays.asList(PwPublicationTest.buildAPub(1)));
		when(pwPubBusService.getByIndexId(anyString())).thenReturn(PwPublicationTest.buildAPub(1));

		when(mpPubBusService.getObject(anyInt())).thenReturn(MpPublicationDaoIT.buildAPub(1));
		when(mpPubBusService.checkAvailability(anyInt())).thenReturn(null);
		when(mpPubBusService.deleteObject(anyInt())).thenReturn(new ValidationResults());
		when(mpPubBusService.createObject(any(MpPublication.class))).thenReturn(MpPublicationDaoIT.buildAPub(1));
		when(mpPubBusService.publish(anyInt())).thenReturn(new ValidationResults());
		when(mpPubBusService.getByIndexId(anyString())).thenReturn(MpPublicationDaoIT.buildAPub(1));
		when(mpPubBusService.updateObject(any(MpPublication.class))).thenReturn(MpPublicationDaoIT.buildAPub(1));

		when(corpBusService.getObject(anyInt())).thenReturn(CorporateContributorDaoIT.buildACorp(1));
		when(corpBusService.createObject(any(CorporateContributor.class))).thenReturn(CorporateContributorDaoIT.buildACorp(1));
		when(corpBusService.updateObject(any(CorporateContributor.class))).thenReturn(CorporateContributorDaoIT.buildACorp(1));

		when(listBusService.updateObject(any(MpList.class))).thenReturn(MpListDaoIT.buildMpList(66));
		when(listBusService.getObjects(anyMap())).thenReturn(MpListMvcServiceTest.getListOfMpList());
		when(listBusService.createObject(any(MpList.class))).thenReturn(MpListDaoIT.buildMpList(66));
		when(listBusService.deleteObject(anyInt())).thenReturn(new ValidationResults());

		//This gets tricky because the TokeSecurityFilter's AuthenticationService and the
		//AuthTokenService's AuthenticationService end up not being the same object so we need to do the mocks at both levels...
		when(authenticationService.authenticate(null, null)).thenReturn(testToken);
		when(mockAuthClient.getNewToken(anyString(), anyString())).thenReturn(testToken);
		when(mockAuthClient.getToken("a-token-string")).thenReturn(testToken);
		when(mockAuthClient.isValidToken("a-token-string")).thenReturn(true);

		httpHeaders = new HttpHeaders();
		httpHeaders.add(TokenSecurityFilter.AUTHORIZATION_HEADER, "Bearer a-token-string");
	}

}
