package com.rigel.app.model;

import java.sql.Timestamp;

public class LoginDetails {

	int id;
	int userId;
	String username;
	Timestamp loginAt;
	Timestamp lastApiHitAt;
	int apiCount;
	int inSecApiHit;
	String requestIp;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Timestamp getLoginAt() {
		return loginAt;
	}
	public void setLoginAt(Timestamp loginAt) {
		this.loginAt = loginAt;
	}
	public Timestamp getLastApiHitAt() {
		return lastApiHitAt;
	}
	public void setLastApiHitAt(Timestamp lastApiHitAt) {
		this.lastApiHitAt = lastApiHitAt;
	}
	public int getApiCount() {
		return apiCount;
	}
	public void setApiCount(int apiCount) {
		this.apiCount = apiCount;
	}
	public int getInSecApiHit() {
		return inSecApiHit;
	}
	public void setInSecApiHit(int inSecApiHit) {
		this.inSecApiHit = inSecApiHit;
	}
	public String getRequestIp() {
		return requestIp;
	}
	public void setRequestIp(String requestIp) {
		this.requestIp = requestIp;
	}

	
}
