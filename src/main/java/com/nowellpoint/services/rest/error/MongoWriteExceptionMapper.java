package com.nowellpoint.services.rest.error;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;

@Provider
public class MongoWriteExceptionMapper implements ExceptionMapper<MongoWriteException> {

	@Override
	public Response toResponse(MongoWriteException exception) {
		
		if (exception.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
			return Response.status(Status.CONFLICT)
					.entity(exception.getError().getMessage())
					.build();
        }
		
		return Response.status(Status.BAD_REQUEST)
				.entity(exception.getMessage())
				.build();
	}
}