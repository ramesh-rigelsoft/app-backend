package com.rigel.app.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.rigel.app.dao.IUserDao;
import com.rigel.app.model.User;


@Component
@Service
public class UserDetailService implements UserDetailsService {

	@Autowired
	IUserDao userDao;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		System.out.println("User name >>> "+username);
		User user = userDao.findUserByEmailId(username);
//		System.out.println("User name >>> "+user.getId());
        if (user!= null) {
        	return new JwtUser(user.getId(), user.getEmail_id(),
        			user.getPassword(), mapToGrantedAuthorities(user.getRole()),
        			user.getStatus(), user.getLastPasswordResetDate());
          }else {
        	throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));	
        }
	}
	
	 private static Set<GrantedAuthority> mapToGrantedAuthorities(String role) {
    	Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
	    grantedAuthorities.add(new SimpleGrantedAuthority(role));
//	    System.out.println(grantedAuthorities.contains(new SimpleGrantedAuthority("user")));
    	return grantedAuthorities;
    }

}
