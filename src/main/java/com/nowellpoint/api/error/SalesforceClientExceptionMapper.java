package com.nowellpoint.api.error;

import com.nowellpoint.client.sforce.SalesforceClientException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SalesforceClientExceptionMapper implements ExceptionMapper<SalesforceClientException> {

	@Override
	public Response toResponse(SalesforceClientException exception) {
		return Response.status(exception.getStatusCode())
				.entity(exception.getErrors())
				.build();
	}
}