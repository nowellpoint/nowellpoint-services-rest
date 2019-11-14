package com.nowellpoint.services.rest;
//package com.nowellpoint.api;
//
//import java.io.IOException;
//
//import javax.inject.Inject;
//import javax.json.Json;
//import javax.json.JsonObject;
//import javax.servlet.http.HttpServletRequest;
//import javax.ws.rs.container.ContainerRequestContext;
//import javax.ws.rs.container.ContainerRequestFilter;
//import javax.ws.rs.container.ContainerResponseContext;
//import javax.ws.rs.container.ContainerResponseFilter;
//import javax.ws.rs.container.ResourceInfo;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.ext.Provider;
//
//import org.jboss.logging.Logger;
//
//@Provider
//public class SecurityContextFilter implements ContainerRequestFilter, ContainerResponseFilter {
//	
//	@Inject
//	Logger logger;
//	
//	@Context
//	private ResourceInfo resourceInfo;
//	
//	@Context
//	private HttpServletRequest httpRequest;
//
//	@Override
//	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
//		final String subject = "testuser"; //UserContext.getSecurityContext() != null ? UserContext.getSecurityContext().getUserPrincipal().getName() : null;
//		final String path = httpRequest.getPathInfo().concat(httpRequest.getQueryString() != null ? "?".concat(httpRequest.getQueryString()) : "");
//		final Integer statusCode = responseContext.getStatus();
//		final String statusInfo = responseContext.getStatusInfo().toString();
//		final String requestMethod = requestContext.getMethod();
//		
//		JsonObject json = Json.createObjectBuilder()
//				.add("hostname", httpRequest.getLocalAddr())
//				.add("subject", subject)
//				.add("date", System.currentTimeMillis())
//				.add("method", requestMethod)
//				.add("path", path)
//				.add("statusCode", statusCode)
//				.add("statusInfo", statusInfo)
//				.build();
//		
//		logger.info(json.toString());
//	}
//
//	@Override
//	public void filter(ContainerRequestContext requestContext) throws IOException {
//		
//	} 
//}