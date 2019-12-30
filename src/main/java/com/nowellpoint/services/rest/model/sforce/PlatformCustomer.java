package com.nowellpoint.services.rest.model.sforce;

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
}