package com.nowellpoint.services.rest.model.sforce;

import javax.json.Json;
import javax.json.JsonObject;

import com.nowellpoint.services.rest.model.sforce.annotation.Column;
import com.nowellpoint.services.rest.model.sforce.annotation.Entity;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;

@Getter
@RegisterForReflection
@Entity("Product2")
public class Product extends SObject {
	@Column(value="QuantityUnitOfMeasure") private String quantityUnitOfMeasure;
	@Column(value="ProductCode") private String productCode;
	@Column(value="Description") private String description;
	@Column(value="Family") private String family;
	
	@Override
	public JsonObject asJsonObject() {
		return Json.createObjectBuilder()
				.add("id", getId())
				.add("productCode", getProductCode())
				.add("family", getFamily())
				.add("description", getDescription())
				.add("quantityUnitOfMeasure", getQuantityUnitOfMeasure())
				.build();
	}
}