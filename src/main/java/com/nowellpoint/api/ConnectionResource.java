package com.nowellpoint.api;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.nowellpoint.api.model.Connection;
import com.nowellpoint.api.model.ConnectionRequest;
import com.nowellpoint.api.model.CreateConnectionResult;
import com.nowellpoint.api.service.ConnectionService;

@Path("/connections")
public class ConnectionResource {
	
	@Inject
	ConnectionService connectionService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createConnection(ConnectionRequest request) {
    	
    	Connection connection = connectionService.create(request);
    	
    	CreateConnectionResult result = CreateConnectionResult.builder()
    			.connection(connection)
    			.build();
    	
        return Response.created(UriBuilder.fromResource(ConnectionResource.class).build(connection.getId()))
        		.entity(result)
        		.build();
    }
}