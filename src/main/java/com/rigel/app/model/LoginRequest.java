package com.rigel.app.model;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder.Default;

@Setter
@Getter
public class LoginRequest {
	private String username;
	private String password;

	@Override
	public String toString() {
	    return "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";
	}
}
