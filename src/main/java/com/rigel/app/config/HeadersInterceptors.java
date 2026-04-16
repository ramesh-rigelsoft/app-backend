package com.rigel.app.config;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import com.rigel.app.model.LoginDetails;
import com.rigel.app.security.JwtConfig;
import com.rigel.app.security.JwtTokenUtil;
import com.rigel.app.util.AppUtill;

@Component
public class HeadersInterceptors implements HandlerInterceptor {
//	
//	@Autowired
//	CryptoAES128 cryptoAES128;
//	
//	@Autowired
//	Environment environment;

//	@Autowired
//	com.app.todoapp.service.IUserLogOutIn userLogOutIn;

//	@Autowired
//	private JwtConfig jwtConfig;

//	@Autowired
//	private JwtTokenUtil JwtTokenUtil;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		

//		try {
//		try {
//			if (org.springframework.util.ClassUtils.isPresent("org.junit.jupiter.api.Test",
//					getClass().getClassLoader())) {
//				// JUnit के दौरान ही execute होगा
//				response.addHeader("User-Agent", "JUnit-Test-Client/1.0");
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}

//			String header = request.getHeader("Authorization");
//			String token = header.substring(7);
//			LoginDetails loginDetails=userLogOutIn.fetchLogInUser(Integer.parseInt(JwtTokenUtil.getIdFromToken(token)));
//			int c=loginDetails.getApiCount();
//			if (c<5) {
//				c++;
//				loginDetails.setLastApiHitAt(new Timestamp(new Date().getTime()));  
//				loginDetails.setApiCount(c);
//			}
//			userLogOutIn.updateCount(loginDetails);
//			System.out.println(c);
//			} catch (Exception e) {
//	//			e.printStackTrace();
//				// TODO: handle exception
//			}
////		userLogOutIn.loginUser(Integer.parseInt(JwtTokenUtil.getIdFromToken(token)), JwtTokenUtil.getEmailFromToken(token)).getId());
//		
//		
////       	System.out.println(request.getRemoteAddr());
//       
		return true;
//		}
	}

}
