package com.nowellpoint.services.rest.model.sforce;

import javax.json.Json;
import javax.json.JsonObject;

import com.nowellpoint.services.rest.model.sforce.annotation.Column;
import com.nowellpoint.services.rest.model.sforce.annotation.OneToOne;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;

@Getter
@RegisterForReflection
public class TeamMember extends SObject {
	@Column(value="TeamMemberRole") private String teamMemberRole;
	@OneToOne(value="User") private User user;
	
	@Override
	public JsonObject asJsonObject() {
		return Json.createObjectBuilder()
				.add("id", getId())
				.add("teamMemberRole", getTeamMemberRole())
				.add("user", user.asJsonObject())
				.build();
	}
}