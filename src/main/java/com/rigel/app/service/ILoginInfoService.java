package com.rigel.app.service;

import java.util.List;

import com.rigel.app.model.LoginActivity;
import com.rigel.app.model.dto.SearchCriteria;

public interface ILoginInfoService {
	
	public List<LoginActivity> saveLoginActivity(List<LoginActivity> loginActivity);

    public LoginActivity updateLoginActivity(LoginActivity loginActivity);
	
	public List<LoginActivity> searchSalesInfo(SearchCriteria criteria);
	
	public LoginActivity findLoginActivityByUsername(String username);

}
