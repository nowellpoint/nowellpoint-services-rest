package com.nowellpoint.services.rest;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.jwt.JsonWebToken;

import com.nowellpoint.api.service.SubscriptionService;
import com.nowellpoint.api.service.ConnectionService;
import com.nowellpoint.services.rest.model.Connection;
import com.nowellpoint.services.rest.model.sforce.Subscription;

@Path("{connectionId}/subscriptions")
@RequestScoped
public class SubscriptionResource extends AbstractResource {
	
	@Inject
	SubscriptionService subscriptionService;
	
	@Inject
	ConnectionService connectionService;
	
	@Inject
	JsonWebToken jwt;
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getById(@PathParam("connectionId") String connectionId, @PathParam("id") String id) {		
		Optional<Connection> connection = connectionService.findById(getOrganizationId(jwt), connectionId);
		Optional<Subscription> subscription = subscriptionService.findById(connection.get(), id);
		if (subscription.isPresent()) {
			return Response.ok(subscription.get().asJsonObject()).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}
}