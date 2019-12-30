package com.nowellpoint.services.rest.model.sforce;

import com.nowellpoint.services.rest.model.sforce.annotation.Entity;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;

@Getter
@RegisterForReflection
@Entity("Product2")
public class Product {
	private String id;
	private String productLine;
	private String productToken;
	private String productCode;
	private String description;
}