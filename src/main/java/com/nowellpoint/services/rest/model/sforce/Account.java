package com.nowellpoint.services.rest.model.sforce;

import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.stream.JsonCollectors;

import com.nowellpoint.services.rest.model.sforce.annotation.Column;
import com.nowellpoint.services.rest.model.sforce.annotation.OneToMany;
import com.nowellpoint.services.rest.model.sforce.annotation.Entity;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;

@Getter
@Entity("Account")
@RegisterForReflection
public class Account extends SObject {
	@Column(value="Name") private String name;
	@Column(value="BillingAddress") private Address billingAddress;
	@Column(value="ShippingAddress") private Address shippingAddress;
	@OneToMany(value="AccountTeamMembers") private List<TeamMember> accountTeamMembers;
	@OneToMany(value="SBQQ__Subscriptions__r") private List<Subscription> subscriptions;
	
	@Override
	public JsonObject asJsonObject() {
		return Json.createObjectBuilder()
				.add("id", getId())
				.add("name", getName())
				.add("billingAddress", addBillingAddress())
				.add("shippingAddress", addShippingAddress())
				.addAll(addAccountTeamMembers())
				.add("subscriptions", addSubscriptions())
				.build();
	}
	
	private JsonValue addBillingAddress() {
		return billingAddress == null ? JsonValue.NULL : Json.createObjectBuilder()
				.add("street", billingAddress.getStreet())
				.add("city", billingAddress.getCity())
				.add("state", billingAddress.getState())
				.add("stateCode", billingAddress.getStateCode())
				.add("postalCode", billingAddress.getPostalCode())
				.add("country", billingAddress.getCountry())
				.add("countryCode", billingAddress.getCountryCode())
				.build();
	}
	
	private JsonValue addShippingAddress() {
		return shippingAddress == null ? JsonValue.NULL : Json.createObjectBuilder()
				.add("street", shippingAddress.getStreet())
				.add("city", shippingAddress.getCity())
				.add("state", shippingAddress.getState())
				.add("stateCode", shippingAddress.getStateCode())
				.add("postalCode", shippingAddress.getPostalCode())
				.add("country", shippingAddress.getCountry())
				.add("countryCode", shippingAddress.getCountryCode())
				.build();
	}
	
	private JsonObjectBuilder addAccountTeamMembers() {
		var teamMembers = Json.createObjectBuilder()
				.add("customerSuccessManager", JsonValue.NULL)
				.add("accountExecutive", JsonValue.NULL);
		
		getAccountTeamMembers().stream()
				.forEach(member -> {
					if ("Customer Success Manager (CSM)".equals(member.getTeamMemberRole())) {
						teamMembers.add("customerSuccessManager", member.asJsonObject());
					} else if ("Account Executive (AE)".equals(member.getTeamMemberRole())) {
						teamMembers.add("accountExecutive", member.asJsonObject());
					}
				});
		
		return teamMembers;
	}
	
	private JsonValue addSubscriptions() {
		return subscriptions == null ? JsonValue.NULL : subscriptions.stream()
				.map(Subscription::asJsonObject)
				.collect(JsonCollectors.toJsonArray());
	}
}