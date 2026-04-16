package com.rigel.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder.Default;

public class HttpResponse {
	
	private String body="https://www.rigelautomation.com";

	/**
	 * @return the body
	 */
	public String body() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}
	
	

}
