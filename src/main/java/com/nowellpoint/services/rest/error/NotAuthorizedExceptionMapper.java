package com.nowellpoint.services.rest.error;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.nowellpoint.services.rest.model.Error;

import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;

@Provider
public class NotAuthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException> {

	@Override
	public Response toResponse(NotAuthorizedException exception) {
		return Response.status(Response.Status.BAD_REQUEST)
				.entity(Error.builder()
						.code("INVALID_CREDENTIALS")
						.detail(exception.getErrorMessage())
						.status(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()))
						.source(exception.getServiceName())
						.title(exception.getErrorCode())
						.build())
				.build();
	}
}