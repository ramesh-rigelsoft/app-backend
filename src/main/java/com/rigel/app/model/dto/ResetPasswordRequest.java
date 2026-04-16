package com.rigel.app.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Builder
@ToString
@Setter
@Getter
public class ResetPasswordRequest {

	private String otp;
	
	@NotNull
	private String mobile_no;
	
	private String password;

}
