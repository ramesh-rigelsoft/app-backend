package com.rigel.app.service;

import java.util.List;
import java.util.Map;

import com.rigel.app.model.LoginActivity;
import com.rigel.app.model.Mail;
import com.rigel.app.model.User;

public interface IUserService {
	
	public User saveUser(User user);
	
	public User findUserById(int id);
	
	public User findUserByEmailId(String email);
	
	public Map<String,Object> sendEmailToAll(Mail emailDetails);
	
	public LoginActivity findLoginActivity(String username);

	
}
