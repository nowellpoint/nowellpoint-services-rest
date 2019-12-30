package com.nowellpoint.services.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import com.nowellpoint.api.service.UserService;
import com.nowellpoint.services.rest.model.Identity;

@Path("/userinfo")
@RequestScoped
public class IdentityResource {
	
	@Inject
	UserService userService;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrganization(@Context SecurityContext context) {
		var user = userService.findById(context.getUserPrincipal().getName());
		
		if (user != null) {
			return Response.ok(Identity.builder()
					.id(user.getId())
					.email(user.getEmail())
					.emailVerified(user.getEmailVerified())
					.name(user.getName())
					.givenName(user.getFirstName())
					.familyName(user.getLastName())
					.preferredUsername(user.getUsername())
					.locale(user.getLocale().toString())
					.updatedAt(user.getUpdatedOn().getEpochSecond())
					//.organizationId(organization.getId())
					//.organizationName(organization.getName())
					.timeZone(user.getTimeZone())
					//.street(organization.getAddress().getStreet())
					//.city(organization.getAddress().getCity())
					//.postalCode(organization.getAddress().getPostalCode())
					//.countryCode(organization.getAddress().getCountryCode())
					//.stateCode(organization.getAddress().getStateCode())
					.build())
					.build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}
}