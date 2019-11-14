package com.nowellpoint.services.rest;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import javax.enterprise.context.ApplicationScoped;

@Liveness
@ApplicationScoped
public class ServiceHealthCheck implements HealthCheck {

	@Override
	public HealthCheckResponse call() {
		return HealthCheckResponse.named("application-check").up()
                .withData("CPUAvailable", Runtime.getRuntime().availableProcessors())
                .withData( "MemoryFree", Runtime.getRuntime().freeMemory())
                .withData("TotalMemory", Runtime.getRuntime().totalMemory())
                .build();
	}
}