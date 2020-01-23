package com.nowellpoint.services.rest.error;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.nowellpoint.services.rest.model.Error;

@Provider
public class UnauthorizedExceptionMapper implements ExceptionMapper<org.jose4j.jwt.consumer.InvalidJwtException> {

	@Override
	public Response toResponse(org.jose4j.jwt.consumer.InvalidJwtException exception) {
		return Response.status(Response.Status.UNAUTHORIZED)
				.entity(Error.builder()
						.code("UNAUTHORIZED")
						.detail(exception.getErrorDetails().get(0).getErrorMessage())
						.status(String.valueOf(Response.Status.UNAUTHORIZED))
						.source(exception.getJwtContext().getJwt())
						.title(exception.getMessage())
						.build())
				.build();
	}
}