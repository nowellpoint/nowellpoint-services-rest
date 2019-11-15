package com.nowellpoint.services.rest;

import java.io.IOException;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import io.vertx.core.http.HttpServerRequest;

@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Inject
	Logger logger;
    
    @Context
    HttpServerRequest request;

	@Context
    UriInfo uriInfo;
    
    @Context 
    SecurityContext securityContext;
    
    long startTime;

    @Override
    public void filter(ContainerRequestContext requestContext) {
    	startTime = System.currentTimeMillis();
    }

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
		final String subject = securityContext.getUserPrincipal() == null ? "anonymous" : securityContext.getUserPrincipal().getName();
		final String method = requestContext.getMethod();
	    final String path = uriInfo.getPath();
	    final String address = request.remoteAddress().toString();
	    final Integer statusCode = responseContext.getStatus();
	    final String statusInfo = responseContext.getStatusInfo().getReasonPhrase();
	    final Long executionTime = System.currentTimeMillis() - startTime;
	    		
		JsonObject json = Json.createObjectBuilder()
				.add("address", address)
				.add("subject", subject)
				.add("date", System.currentTimeMillis())
				.add("executionTime", executionTime)
				.add("method", method)
				.add("path", path)
				.add("statusCode", statusCode)
				.add("statusInfo", statusInfo)
				.build();
		
		logger.info(json.toString());
	}
}