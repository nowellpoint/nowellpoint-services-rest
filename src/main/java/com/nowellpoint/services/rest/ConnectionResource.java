package com.nowellpoint.services.rest;

import java.net.URI;
import java.util.Optional;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
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
import com.nowellpoint.api.service.ConnectionService;
import com.nowellpoint.services.rest.model.Connection;
import com.nowellpoint.services.rest.model.ConnectionRequest;
import com.nowellpoint.services.rest.model.ResponseEntity;
import com.nowellpoint.services.rest.model.ServiceException;

@Path("/connections")
@RequestScoped
public class ConnectionResource {
	
	@Inject
	ConnectionService connectionService;
	
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
	public Response getConnection(@Context SecurityContext context, @PathParam("id") String id) {		
		var connection = connectionService.findById("00DR0000001vySjMAI", id);
		if (connection.isPresent()) {
			return Response.ok(connection.get()).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createConnection(ConnectionRequest connectionRequest) {
    	var violations = validator.validate(connectionRequest);
    	if (violations.isEmpty()) {
    		Connection connection = null;
        	try {
        		connection = connectionService.create("00DR0000001vySjMAI", connectionRequest);
        	} catch (ServiceException e) {
        		throw new WebApplicationException(e.getMessage(), Status.FORBIDDEN);
        	}
        	
            return Response.created(URI.create(connection.getAttributes().getHref()))
            		.entity(ResponseEntity.of(connection))
            		.build();
    	} else {
    		return Response.status(Status.BAD_REQUEST)
            		.entity(ResponseEntity.of(violations))
            		.build();
    	}
    }
    
    @POST
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateConnection(@PathParam("id") String id, ConnectionRequest connectionRequest) {
    	var violations = validator.validate(connectionRequest);
    	if (violations.isEmpty()) {
    		Connection connection = null;
        	try {
        		connection = connectionService.update("00DR0000001vySjMAI", id, connectionRequest);
        	} catch (ServiceException e) {
        		throw new WebApplicationException(e.getMessage(), Status.FORBIDDEN);
        	} catch (MongoWriteException e) {
        		throw new WebApplicationException(e, Status.BAD_REQUEST);
        	}
        	
            return Response.ok()
            		.entity(ResponseEntity.of(connection))
            		.build();
    	} else {
    		return Response.status(Response.Status.BAD_REQUEST)
            		.entity(ResponseEntity.of(violations))
            		.build();
    	}
    }
}