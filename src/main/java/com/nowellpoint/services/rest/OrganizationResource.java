package com.nowellpoint.services.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

import com.mongodb.MongoWriteException;
import com.nowellpoint.api.service.OrganizationService;
import com.nowellpoint.services.rest.model.Organization;
import com.nowellpoint.services.rest.model.OrganizationRequest;
import com.nowellpoint.services.rest.model.ResponseEntity;
import com.nowellpoint.services.rest.model.ServiceException;

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
	@Claim(standard = Claims.groups)
	String groups;
	
	@GET
	@Path("/{id}")
	//@RolesAllowed("Administrator")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrganization(@Context SecurityContext context, @PathParam("id") String id) {		
		var organization = orgnizationService.findById(id);
		if (organization != null) {
			return Response.ok(organization).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}
    
    @POST
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateOrganization(@Context SecurityContext context, @PathParam("id") String id, OrganizationRequest request) {
    	var violations = validator.validate(request);
    	if (violations.isEmpty()) {
    		Organization organization = null;
        	try {
        		organization = orgnizationService.update(id, request);
        	} catch (ServiceException e) {
        		throw new WebApplicationException(e.getMessage(), Status.FORBIDDEN);
        	} catch (MongoWriteException e) {
        		throw new WebApplicationException(e, Status.BAD_REQUEST);
        	}
        	
            return Response.ok()
            		.entity(ResponseEntity.of(organization))
            		.build();
    	} else {
    		return Response.status(Response.Status.BAD_REQUEST)
            		.entity(ResponseEntity.of(violations))
            		.build();
    	}
    }
}