package gov.usgs.cida.pubs.webservice.security;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.springframework.http.HttpHeaders;

import gov.usgs.cida.auth.model.AuthToken;
import gov.usgs.cida.pubs.dao.CorporateContributorDaoTest;
import gov.usgs.cida.pubs.dao.mp.MpListDaoTest;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDaoTest;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDaoTest;
import gov.usgs.cida.pubs.domain.CorporateContributor;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.webservice.mp.MpListMvcServiceTest;

public abstract class EndpointSecurityAuthTest extends BaseEndpointSecurityTest {

	protected HttpHeaders httpHeaders;
	protected AuthenticationService authService;

	private TokenSecurityFilter testFilter;

	public void setup() {
		preSetup();

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

	@SuppressWarnings("unchecked")
	public void mockSetup() throws Exception {
		AuthToken testToken = new AuthToken();
		testToken.setTokenId("a-token-string");
		testToken.setUsername("testyUser");

		when(pwPubBusService.getObjects(anyMap())).thenReturn(Arrays.asList(PwPublicationDaoTest.buildAPub(1)));
		when(pwPubBusService.getByIndexId(anyString())).thenReturn(PwPublicationDaoTest.buildAPub(1));

		when(mpPubBusService.getObject(anyInt())).thenReturn(MpPublicationDaoTest.buildAPub(1));
		when(mpPubBusService.checkAvailability(anyInt())).thenReturn(null);
		when(mpPubBusService.deleteObject(anyInt())).thenReturn(new ValidationResults());
		when(mpPubBusService.createObject((MpPublication) anyObject())).thenReturn(MpPublicationDaoTest.buildAPub(1));
		when(mpPubBusService.publish(anyInt())).thenReturn(new ValidationResults());
		when(mpPubBusService.getByIndexId(anyString())).thenReturn(MpPublicationDaoTest.buildAPub(1));
		when(mpPubBusService.updateObject((MpPublication) anyObject())).thenReturn(MpPublicationDaoTest.buildAPub(1));

		when(corpBusService.getObject(anyInt())).thenReturn(CorporateContributorDaoTest.buildACorp(1));
		when(corpBusService.createObject((CorporateContributor) anyObject())).thenReturn(CorporateContributorDaoTest.buildACorp(1));
		when(corpBusService.updateObject((CorporateContributor) anyObject())).thenReturn(CorporateContributorDaoTest.buildACorp(1));

		when(listBusService.updateObject(any(MpList.class))).thenReturn(MpListDaoTest.buildMpList(66));
		when(listBusService.getObjects(anyMap())).thenReturn(MpListMvcServiceTest.getListOfMpList());
		when(listBusService.createObject(any(MpList.class))).thenReturn(MpListDaoTest.buildMpList(66));
		when(listBusService.deleteObject(anyInt())).thenReturn(new ValidationResults());

		//This gets tricky because the TokeSecurityFilter's AuthenticationService and the
		//AuthTokenService's AuthenticationService end up not being the same object so we need to do the mocks at both levels...
		when(authenticationService.authenticate(anyString(), anyString())).thenReturn(testToken);
		when(mockAuthClient.getNewToken(anyString(), anyString())).thenReturn(testToken);
		when(mockAuthClient.getToken("a-token-string")).thenReturn(testToken);
		when(mockAuthClient.isValidToken("a-token-string")).thenReturn(true);

		httpHeaders = new HttpHeaders();
		httpHeaders.add(TokenSecurityFilter.AUTHORIZATION_HEADER, "Bearer a-token-string");
	}

}
