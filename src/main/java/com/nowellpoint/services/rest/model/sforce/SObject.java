package com.nowellpoint.services.rest.model.sforce;

import java.time.LocalDateTime;

import com.nowellpoint.services.rest.model.sforce.annotation.Column;
import com.nowellpoint.services.rest.model.sforce.annotation.Id;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;

@Getter
@RegisterForReflection
public class SObject {
	@Id private String id;
	@Column(value="CreatedDate") private LocalDateTime createdDate;
	@Column(value="LastModifiedDate") private LocalDateTime lastModifiedDate;
	private Attributes attributes;
}