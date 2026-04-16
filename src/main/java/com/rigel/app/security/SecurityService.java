package com.rigel.app.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

	public String username() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		if(name == "anonymousUser") {
			return null;
		}
		System.out.println(name);
		return name;
	}

}