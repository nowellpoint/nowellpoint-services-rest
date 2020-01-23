package com.nowellpoint.services.rest.error;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.nowellpoint.services.rest.model.Error;
import com.nowellpoint.services.rest.model.IdentityProviderException;

@Provider
public class IdentityProviderExceptionMapper implements ExceptionMapper<IdentityProviderException> {

	@Override
	public Response toResponse(IdentityProviderException exception) {
		return Response.status(Response.Status.BAD_REQUEST)
				.entity(Error.builder()
						.code(exception.getCode())
						.detail(exception.getDetail())
						.status(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()))
						.source(exception.getSource())
						.title(exception.getTitle())
						.build())
				.build();
	}
}