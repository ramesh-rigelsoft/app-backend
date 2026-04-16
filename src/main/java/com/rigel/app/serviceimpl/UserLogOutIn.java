package com.rigel.app.serviceimpl;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.rigel.app.model.LoginDetails;
import com.rigel.app.service.IUserLogOutIn;

@Lazy 
@Service
@CacheConfig(cacheNames = "logInOut", keyGenerator = "TransferKeyGenerator")
public class UserLogOutIn implements IUserLogOutIn {
	
	        @Cacheable /// store cache
		    public LoginDetails loginUser(LoginDetails login) {
	    	   
		       return login;
		    }

		     @CacheEvict /// clear cache
		     public LoginDetails logOutUser(int id,String username) {
	        	   LoginDetails login=new LoginDetails();
		    	   login.setUsername(username);
		    	   login.setId(id);
			       return login;
	  		 }
	           
	         @Cacheable /// store cache
	         public LoginDetails fetchLogInUser(int id) {
	        	   LoginDetails login=new LoginDetails();
		    	   login.setId(id);
			       return login;
	  		}
	         
	         @CachePut /// update cache
	         public LoginDetails apiRate(LoginDetails login) {
	        	   login.setLastApiHitAt(new Timestamp(new Date().getTime()));
		    	   login.setInSecApiHit(1);
		    	   login.setApiCount(1);
			       return login;
	  		}

			@Override
			public LoginDetails updateCount(LoginDetails loginDetails) {
				return loginDetails;
			}
}
