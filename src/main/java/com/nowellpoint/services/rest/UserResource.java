package com.nowellpoint.services.rest;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Validator;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;

import com.nowellpoint.api.service.UserService;
import com.nowellpoint.services.rest.model.User;

@Path("/users")
public class UserResource {
	
	@Inject
	UserService userService;
	
	@Inject
	Validator validator;
	
	@Inject
	@Claim(standard = Claims.groups)
	//@Claim("cognito:groups")
	String groups;
	
	@GET
	@Path("/{id}")
	@RolesAllowed("Administrator")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam("id") String id) {
		User user = userService.findById(id);
		if (user != null) {
			return Response.ok(user).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}
}