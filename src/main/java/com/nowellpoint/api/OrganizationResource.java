package com.nowellpoint.api;

import java.io.InputStream;
import java.net.URI;
import java.util.Set;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nowellpoint.api.model.Organization;
import com.nowellpoint.api.model.UpdateOrganizationRequest;
import com.mongodb.MongoWriteException;
import com.nowellpoint.api.model.CreateOrganizationRequest;
import com.nowellpoint.api.model.CreateResponse;
import com.nowellpoint.api.service.OrganizationService;
import com.nowellpoint.api.util.JsonbUtil;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.http.Status;

@Path("/organizations")
public class OrganizationResource {
	
	@Inject
	OrganizationService orgnizationService;
	
	@Inject
	Validator validator;
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrganization(@PathParam("id") String id) {
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
    public Response createOrganization(InputStream requestBody) {
    	
    	CreateOrganizationRequest request = JsonbUtil.fromJson(requestBody, CreateOrganizationRequest.class);
    	
    	Set<ConstraintViolation<CreateOrganizationRequest>> violations = validator.validate(request);
    	
    	if (violations.isEmpty()) {
    		
    		Organization organization = null;
        	
        	try {
        		organization = orgnizationService.create(request);
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
    public Response updateOrganization(InputStream requestBody) {
    	
    	UpdateOrganizationRequest request = JsonbUtil.fromJson(requestBody, UpdateOrganizationRequest.class);
    	
    	Set<ConstraintViolation<UpdateOrganizationRequest>> violations = validator.validate(request);
    	
    	if (violations.isEmpty()) {
    		
    		Organization organization = null;
        	
        	try {
        		organization = orgnizationService.update(request);
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