package com.rigel.app.dao;

import com.rigel.app.model.LoginActivity;
import com.rigel.app.model.User;

public interface IUserDao {
	
    public User saveUser(User user);
	
	public User findUserById(int id);
	
	public User findUserByEmailId(String email);
	
	public LoginActivity findLoginActivity(String username);

}
