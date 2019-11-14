package com.nowellpoint.services.rest;

import java.io.UnsupportedEncodingException;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import com.nowellpoint.api.service.IdentityProviderService;
import com.nowellpoint.http.Status;
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
	public Response authorize( 
			@FormParam("username") String username, 
			@FormParam("password") String password) {
		
		Token token = null;
				
		try {
			token = identityProviderService.authenticate(username, password);
		} catch (NotAuthorizedException e) {
			throw new WebApplicationException(e.getErrorCode() + ": " + e.getErrorMessage(), Status.BAD_REQUEST);
		} catch (UnsupportedEncodingException e) {
			throw new WebApplicationException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
		}
		
		return Response.ok(token).build();
	}
	
	@POST
	@Path("/refresh")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
	public Response refresh(@FormParam("refreshToken") String refreshToken) {
		
		Token token = null;
				
		try {
			token = identityProviderService.refreshToken(refreshToken);
		} catch (NotAuthorizedException e) {
			throw new WebApplicationException(e.getErrorCode() + ": " + e.getErrorMessage(), Status.BAD_REQUEST);
		} catch (UnsupportedEncodingException e) {
			throw new WebApplicationException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
		}
		
		return Response.ok(token).build();
	}
	
	@DELETE
	@Path("/revoke")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response revoke(@FormParam("token") String accessToken) {
		identityProviderService.revokeToken(accessToken);
		return Response.noContent().build();
	}
}