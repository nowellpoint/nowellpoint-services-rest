package com.nowellpoint.services.rest.error;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.nowellpoint.services.rest.model.Error;
import com.nowellpoint.services.rest.model.sforce.SalesforceServiceException;

@Provider
public class SalesforceServiceExceptionMapper implements ExceptionMapper<SalesforceServiceException> {

	@Override
	public Response toResponse(SalesforceServiceException exception) {
		return Response.status(Response.Status.BAD_REQUEST)
				.entity(Error.builder()
						.code(exception.getErrorCode())
						.detail(exception.getMessage())
						.status(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()))
						.source(exception.getErrorDescription())
						.title(exception.getError())
						.build())
				.build();
	}
}