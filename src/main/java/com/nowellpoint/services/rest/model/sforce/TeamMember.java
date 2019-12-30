package com.nowellpoint.services.rest.model.sforce;

import com.nowellpoint.services.rest.model.sforce.annotation.Column;
import com.nowellpoint.services.rest.model.sforce.annotation.OneToOne;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;

@Getter
@RegisterForReflection
public class TeamMember extends SObject {
	@Column(value="TeamMemberRole") private String teamMemberRole;
	@OneToOne(value="User") private User user;
}