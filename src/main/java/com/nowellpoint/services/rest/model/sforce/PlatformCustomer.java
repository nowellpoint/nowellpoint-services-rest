package com.nowellpoint.services.rest.model.sforce;

import javax.json.JsonObject;

import com.nowellpoint.services.rest.model.sforce.annotation.Column;
import com.nowellpoint.services.rest.model.sforce.annotation.Entity;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;

@Getter
@RegisterForReflection
@Entity("PlatformCustomer__c")
public class PlatformCustomer extends SObject {
	@Column(value="Name") private String name;
	@Column(value="CustomerId__c") private String customerId;
	
	@Override
	public JsonObject asJsonObject() {
		// TODO Auto-generated method stub
		return null;
	}
}