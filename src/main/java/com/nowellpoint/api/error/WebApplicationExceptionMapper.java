package com.nowellpoint.api.error;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.mongodb.MongoWriteException;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

	@Override
	public Response toResponse(WebApplicationException exception) {
		
		if (exception.getClass().isAssignableFrom( MongoWriteException.class ) ) {
			System.out.println(exception.getMessage());
		}
		
		return Response.status(exception.getResponse().getStatus())
				.entity(exception.getMessage())
				.build();
	}
}