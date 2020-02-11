package com.nowellpoint.services.rest.model.sforce;

import javax.json.bind.annotation.JsonbProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;

@Getter
@RegisterForReflection
public class Identity {
	@JsonbProperty(value="id") private String id;
	@JsonbProperty(value="asserted_user") private Boolean assertedUser;
	@JsonbProperty(value="user_id") private String userId;
	@JsonbProperty(value="organization_id") private String organizationId;
	@JsonbProperty(value="username") private String username;
	@JsonbProperty(value="nick_name") private String nickName;
	@JsonbProperty(value="display_name") private String displayName;
	@JsonbProperty(value="email") private String email;
	@JsonbProperty(value="first_name") private String firstName;
	@JsonbProperty(value="last_name") private String lastName;
	@JsonbProperty(value="addr_street") private String street;
	@JsonbProperty(value="addr_city") private String city;
	@JsonbProperty(value="addr_country") private String country;
	@JsonbProperty(value="addr_state") private String state;
	@JsonbProperty(value="addr_zip") private String postalCode;
	@JsonbProperty(value="mobile_phone") private String mobilePhone;
	@JsonbProperty(value="active") private Boolean active;
	@JsonbProperty(value="user_type") private String userType;
	@JsonbProperty(value="language") private String language;
	@JsonbProperty(value="locale") private String locale;
	@JsonbProperty(value="utcOffset") private String utcOffset;
	@JsonbProperty(value="urls") private Urls urls;
}