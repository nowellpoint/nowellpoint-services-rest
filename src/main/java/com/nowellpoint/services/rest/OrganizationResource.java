package com.nowellpoint.services.rest;

import java.net.URI;
import java.security.Principal;
import java.util.Optional;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.JsonWebToken;

import com.mongodb.MongoWriteException;
import com.nowellpoint.api.service.OrganizationService;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.http.Status;
import com.nowellpoint.services.rest.model.ConnectionRequest;
import com.nowellpoint.services.rest.model.CreateResponse;
import com.nowellpoint.services.rest.model.Organization;

@Path("/organizations")
@RequestScoped
public class OrganizationResource {
	
	@Inject
	OrganizationService orgnizationService;
	
	@Inject
	Validator validator;
	
	@Inject
	JsonWebToken jwt;
	
	@Inject
	//@Claim(standard = Claims.groups)
	@Claim("cognito:groups")
	String groups;
	
	@GET
	@RolesAllowed("Administrator")
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrganization(@Context SecurityContext context, @PathParam("id") String id) {
		Principal caller =  context.getUserPrincipal(); 
		String name = caller == null ? "anonymous" : caller.getName();
		
		System.out.println(name);
		System.out.println(context.isSecure());
		System.out.println(context.getAuthenticationScheme());
		System.out.println(groups);
		
		Organization organization = orgnizationService.findById(id);
		if (organization != null) {
			return Response.ok(organization).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrganization(ConnectionRequest request) {
    	
    	Set<ConstraintViolation<ConnectionRequest>> violations = validator.validate(request);
    	
    	if (violations.isEmpty()) {
    		
    		Organization organization = null;
        	
        	try {
        		organization = orgnizationService.build(request);
        	} catch (OauthException e) {
        		throw new WebApplicationException(e.getError() + ": " + e.getErrorDescription(), Status.FORBIDDEN);
        	}
        	
            return Response.created(URI.create(organization.getAttributes().getHref()))
            		.entity(organization)
            		.build();
    	} else {
    		
    		return Response.status(Status.BAD_REQUEST)
            		.entity(new CreateResponse(violations))
            		.build();
    	}
    }
    
    @POST
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateOrganization(ConnectionRequest request) {
    	
    	Set<ConstraintViolation<ConnectionRequest>> violations = validator.validate(request);
    	
    	if (violations.isEmpty()) {
    		
    		Organization organization = null;
        	
        	try {
        		organization = orgnizationService.build(request);
        	} catch (OauthException e) {
        		throw new WebApplicationException(e.getError() + ": " + e.getErrorDescription(), Status.FORBIDDEN);
        	} catch (MongoWriteException e) {
        		throw new WebApplicationException(e, Status.BAD_REQUEST);
        	}
        	
            return Response.ok()
            		.entity(organization)
            		.build();
    	} else {
    		
    		return Response.status(Status.BAD_REQUEST)
            		.entity(new CreateResponse(violations))
            		.build();
    	}
    }
}