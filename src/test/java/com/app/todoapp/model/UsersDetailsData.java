package com.app.todoapp.model;

public class UsersDetailsData {
	
	private String token; 
	private String email_id; 
	private String mobile_no;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getEmail_id() {
		return email_id;
	}
	public void setEmail_id(String email_id) {
		this.email_id = email_id;
	}
	public String getMobile_no() {
		return mobile_no;
	}
	public void setMobile_no(String mobile_no) {
		this.mobile_no = mobile_no;
	}
	@Override
	public String toString() {
		return "UsersDetailsData [token=" + token + ", email_id=" + email_id + ", mobile_no=" + mobile_no + "]";
	} 
	
	
	
}
