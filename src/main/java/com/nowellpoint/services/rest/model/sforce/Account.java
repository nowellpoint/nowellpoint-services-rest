package com.nowellpoint.services.rest.model.sforce;

import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

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
	
	@Override
	public JsonObject asJsonObject() {
		return Json.createObjectBuilder()
				.add("id", getId())
				.add("name", getName())
				.add("billingAddress", getBillingAddress() == null ? JsonValue.NULL : Json.createObjectBuilder()
						.add("street", getBillingAddress().getStreet())
						.add("city", getBillingAddress().getCity())
						.add("state", getBillingAddress().getState())
						.add("stateCode", getBillingAddress().getStateCode())
						.add("postalCode", getBillingAddress().getPostalCode())
						.add("country", getBillingAddress().getCountry())
						.add("countryCode", getBillingAddress().getCountryCode())
						.build())
				.add("shippingAddress", getShippingAddress() == null ? JsonValue.NULL : Json.createObjectBuilder()
						.add("street", getShippingAddress().getStreet())
						.add("city", getShippingAddress().getCity())
						.add("state", getShippingAddress().getState())
						.add("stateCode", getShippingAddress().getStateCode())
						.add("postalCode", getShippingAddress().getPostalCode())
						.add("country", getShippingAddress().getCountry())
						.add("countryCode", getShippingAddress().getCountryCode())
						.build())
				.addAll(addAccountTeamMembers())
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
}