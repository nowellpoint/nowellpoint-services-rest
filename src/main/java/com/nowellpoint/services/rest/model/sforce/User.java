package com.nowellpoint.services.rest.model.sforce;

import javax.json.Json;
import javax.json.JsonObject;

import com.nowellpoint.services.rest.model.sforce.annotation.Column;
import com.nowellpoint.services.rest.model.sforce.annotation.Entity;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;

@Getter
@RegisterForReflection
@Entity("User")
public class User extends SObject {
	@Column(value="Name") private String name;
	@Column(value="Phone") private String phone;
	@Column(value="Email") private String email;
	@Column(value="FirstName") private String firstName;
	@Column(value="LastName") private String lastName;
	@Column(value="MediumPhotoUrl") private String mediumPhotoUrl;
	
	@Override
	public JsonObject asJsonObject() {
		return Json.createObjectBuilder()
				.add("id", getId())
				.add("email", getOrDefault(email))
				.add("firstName", getOrDefault(firstName))
				.add("lastName", getOrDefault(lastName))
				.add("name", getOrDefault(name))
				.add("phone", getOrDefault(phone))
				.build();
	}
}