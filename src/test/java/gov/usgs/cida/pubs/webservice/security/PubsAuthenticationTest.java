package gov.usgs.cida.pubs.webservice.security;

import static org.junit.Assert.*;

import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;

import org.junit.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class PubsAuthenticationTest {

	@Test
	public void testAuthStatusAndConstructors() {
		assertFalse("Without pubs roles, authentication is considered failed", new PubsAuthentication(new ArrayList<SimpleGrantedAuthority>()).isAuthenticated());

		PubsAuthentication auth1 = new PubsAuthentication("username", new ArrayList<SimpleGrantedAuthority>());
		assertFalse("Without pubs roles, authentication is considered failed", auth1.isAuthenticated());
		assertTrue("getPrinciple returns User object", auth1.getPrincipal() instanceof User);
		assertEquals("getPrinciple returns User object with correct username", "username", ((User) auth1.getPrincipal()).getUsername());
		assertEquals("getPrinciple returns User object with password stripped", "******", ((User) auth1.getPrincipal()).getPassword());
		
		for(PubsRoles r : PubsRoles.values()) {
			ArrayList<String> rawRoleList = new ArrayList<>();
			rawRoleList.add(r.name());
			assertTrue("If role " + r.name() + " found, is authenticated", new PubsAuthentication("username", rawRoleList).isAuthenticated());
		}
		
		ArrayList<String> rawRoleList = new ArrayList<>();
		rawRoleList.add("RANDOM_ROLE");
		assertFalse("Having only RANDOM_ROLE not authenticated", new PubsAuthentication("username", rawRoleList).isAuthenticated());
	}
}
