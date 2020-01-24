package com.nowellpoint.services.rest.model.sforce;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;

import com.nowellpoint.services.rest.model.sforce.annotation.Entity;
import com.nowellpoint.services.rest.model.sforce.annotation.OneToOne;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;

@Getter
@Entity("SBQQ__QuoteLine__c")
@RegisterForReflection
public class QuoteLine extends SObject {
	@OneToOne(value="SBQQ__UpgradedSubscription__r") private Subscription upgradedSubscription;
	@OneToOne(value="SBQQ__RenewedSubscription__r") private Subscription renewedSubscription;
	
	@Override
	public JsonObject asJsonObject() {
		return Json.createObjectBuilder()
				.add("id", getId())
				.add("createdDate", addCreatedDate())
				.add("lastModifiedDate", addLastModifiedDate())
				//.add("upgradedSubscription", getUpgradedSubscription() == null ? JsonValue.NULL : getUpgradedSubscription().asJsonObject())
				.add("renewedSubscription", addRenewedSubscription())
				.build();
	}
	
	private JsonValue addRenewedSubscription() {
		return renewedSubscription == null ? JsonValue.NULL : renewedSubscription.asJsonObject();
	}
}