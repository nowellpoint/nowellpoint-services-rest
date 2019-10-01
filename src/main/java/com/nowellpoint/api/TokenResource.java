package com.nowellpoint.api;

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nowellpoint.api.model.Token;
import com.nowellpoint.api.service.TokenService;
import com.nowellpoint.http.Status;

import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;

@Path("/oauth2")
public class TokenResource {
	
	@Inject
	TokenService tokenService;
	
	@POST
	@Path("/authorize")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
	public Response authorize(@FormParam("username") String username, 
			@FormParam("password") String password) {
		
		Token token = null;
				
		try {
			token = tokenService.authorize(username, password);
		} catch (NotAuthorizedException e) {
			throw new WebApplicationException(e.getErrorCode() + ": " + e.getErrorMessage(), Status.BAD_REQUEST);
		} catch (UnsupportedEncodingException e) {
			throw new WebApplicationException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
		}
		
		return Response.ok(token).build();
	}
}