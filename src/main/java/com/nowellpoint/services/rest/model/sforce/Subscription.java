package com.nowellpoint.services.rest.model.sforce;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;

@Getter
@RegisterForReflection
public class Subscription {
	private String id;
	private String quantityUnitOfMeasure;
	private Integer quantity;
	private Long startDate;
	private Long endDate;
	private String status;
	private Product product;
}