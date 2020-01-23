package com.nowellpoint.services.rest;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nowellpoint.api.service.IdentityProviderService;
import com.nowellpoint.services.rest.model.Token;

@Path("/oauth2")
@RequestScoped
public class TokenResource {
	
	@Inject
	IdentityProviderService identityProviderService;
	
	@POST
	@PermitAll
	@Path("/authorize")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
	public Response authorize(@FormParam("username") String username, @FormParam("password") String password) {
				
		Token token = identityProviderService.authenticate(username, password);
		
		CacheControl cacheControl = new CacheControl();
		cacheControl.setNoStore(Boolean.TRUE);
		
		return Response.ok(token)
				.cacheControl(cacheControl)
				.header("Pragma", "no-cache")
				.build();
	}
	
	@POST
	@Path("/refresh")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
	public Response refresh(@FormParam("refreshToken") String refreshToken) {
		
		Token token = identityProviderService.refreshToken(refreshToken); 
		
		CacheControl cacheControl = new CacheControl();
		cacheControl.setNoStore(Boolean.TRUE);
		
		return Response.ok(token)
				.cacheControl(cacheControl)
				.header("Pragma", "no-cache")
				.build();
	}
	
	@DELETE
	@Path("/revoke")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response revoke(@FormParam("token") String accessToken) {
		
		identityProviderService.revokeToken(accessToken);
		
		return Response.noContent().build();
	}
}