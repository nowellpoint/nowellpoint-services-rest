package com.nowellpoint.services.rest.error;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.nowellpoint.services.rest.model.Error;
import com.nowellpoint.services.rest.model.IdentityProviderServiceException;

@Provider
public class IdentityProviderServiceExceptionMapper implements ExceptionMapper<IdentityProviderServiceException> {

	@Override
	public Response toResponse(IdentityProviderServiceException exception) {
		return Response.status(exception.getStatusCode())
				.entity(Error.builder()
						.code(exception.getCode())
						.detail(exception.getDetail())
						.status(String.valueOf(exception.getStatusCode()))
						.source(exception.getSource())
						.title(exception.getTitle())
						.build())
				.build();
	}
}