package com.nowellpoint.api;

import java.io.InputStream;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.nowellpoint.api.model.Organization;
import com.nowellpoint.api.model.OrganizationRequest;
import com.nowellpoint.api.model.CreateOrganizationResult;
import com.nowellpoint.api.service.OrganizationService;
import com.nowellpoint.api.util.JsonbUtil;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.http.Status;

@Path("/organizations")
public class OrganizationResource {
	
	@Inject
	OrganizationService orgnizationService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrganization(InputStream requestBody) {
		
		OrganizationRequest request = JsonbUtil.fromJson(requestBody, OrganizationRequest.class);
    	
    	Organization connection = null;
    	
    	try {
    		connection = orgnizationService.create(request);
    	} catch (OauthException e) {
    		throw new WebApplicationException(e.getError() + ": " + e.getErrorDescription(), Status.FORBIDDEN);
    	}
    	
    	CreateOrganizationResult result = CreateOrganizationResult.builder()
    			.connection(connection)
    			.build();
    	
        return Response.created(UriBuilder.fromResource(OrganizationResource.class).build(connection.getId()))
        		.entity(result)
        		.build();
    }
}