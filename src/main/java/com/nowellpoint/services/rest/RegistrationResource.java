package com.nowellpoint.services.rest;

import java.net.URI;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.nowellpoint.api.service.RegistrationService;
import com.nowellpoint.services.rest.model.Registration;
import com.nowellpoint.services.rest.model.RegistrationRequest;
import com.nowellpoint.services.rest.model.ResponseEntity;
import com.nowellpoint.services.rest.model.ServiceException;

@Path("/registrations")
@RequestScoped
public class RegistrationResource {
	
	@Inject
	Validator validator;
	
	@Inject
	RegistrationService registrationService;
	
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response register(RegistrationRequest registrationRequest) {
		Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(registrationRequest);
    	if (violations.isEmpty()) {
    		Registration registration = null;
        	try {
        		registration = registrationService.register(registrationRequest);
        	} catch (ServiceException e) {
        		throw new WebApplicationException(e.getMessage(), Status.FORBIDDEN);
        	}
        	
        	return Response.created(URI.create(registration.getUser().getHref()))
        			.entity(ResponseEntity.of(registration))
        			.build();
    	} else {
    		return Response.status(Status.BAD_REQUEST)
            		.entity(ResponseEntity.of(violations))
            		.build();
    	}
	}
}