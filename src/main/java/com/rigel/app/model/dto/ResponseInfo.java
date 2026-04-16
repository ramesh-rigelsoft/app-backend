package com.rigel.app.model.dto;

import com.rigel.app.model.User;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponseInfo {
	
	private String token;
	private String refreshToken;
	private String deviceId;
	
}
