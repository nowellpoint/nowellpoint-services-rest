package com.nowellpoint.services.rest.model.sforce;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.slf4j.LoggerFactory;

public class DynamicInvocationHandler implements InvocationHandler {
	
	private static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(DynamicInvocationHandler.class);
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		LOGGER.info("Invoked method: {}", method.getName()); 
		return null;
    }
}