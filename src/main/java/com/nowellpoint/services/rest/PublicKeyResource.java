package com.nowellpoint.services.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.nowellpoint.api.service.CryptographyService;

@Path("/.well-known")
@RequestScoped
public class PublicKeyResource {
	
	@Inject
	CryptographyService cryptographyService;
	
	@GET
	@Path("/jwks.json")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPublicKeys() {
		try {
			String jwks = cryptographyService.getPublicKeys();
			return Response.ok(jwks).build();    
		} catch (RuntimeException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
}