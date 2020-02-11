package com.nowellpoint.services.rest.model;

import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@RegisterForReflection
public class UserRequest {
	@Email 
	private String email;
	
	private String firstName;
	
	@NotEmpty 
	private String lastName;
	
	@NotEmpty 
	private String phone;
	
	@NotEmpty(message="A valid time zone must be provided. Valid time zones at this time are: America/New_York")
	private @Builder.Default String timeZone = TimeZone.getDefault().getID();
	
	@NotEmpty
	private @Builder.Default String locale = Locale.getDefault().toString();
	
	private static final char[] chars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'q', 'w', 'e', 'r', 't', 'z', 'u', 'i', 'o', 'p', 'a', 's',
	        'd', 'f', 'g', 'h', 'j', 'k', 'l', 'y', 'x', 'c', 'v', 'b', 'n', 'm', 'Q', 'W', 'E', 'R', 'T', 'Z', 'U', 'I', 'O', 'P', 'A',
	        'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'Y', 'X', 'C', 'V', 'B', 'N', 'M', '<', '=', '>', '?', '@' };
	
	
	public String getPassword() {
		return generateTemporaryPassword(12);
	}
	
	private String generateTemporaryPassword(int length) {
		StringBuilder stringBuilder = new StringBuilder();
	    for (int i = 0; i < length; i++) {
	        stringBuilder.append(chars[new Random().nextInt(chars.length)]);
	    }
	    return stringBuilder.toString();
	}
}