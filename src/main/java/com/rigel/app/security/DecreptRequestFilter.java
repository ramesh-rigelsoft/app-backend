package com.rigel.app.security;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rigel.app.model.LoginDetails;
import com.rigel.app.service.IUserLogOutIn;

public class DecreptRequestFilter extends  OncePerRequestFilter {

	@Autowired
	IUserLogOutIn userLogOutIn;
	
	@Autowired
	private JwtConfig jwtConfig;
	
	@Autowired
	private JwtTokenUtil JwtTokenUtil;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String header = request.getHeader("Authorization");

		String token = header.substring(7);
//		LoginDetails loginDetails=userLogOutIn.fetchLogInUser(Integer.parseInt(JwtTokenUtil.getIdFromToken(token)));
//		int c=loginDetails.getApiCount();
//		if (c<5) {
//			c++;
//			loginDetails.setApiCount(c);
//		}
//		userLogOutIn.updateCount(loginDetails);
//		System.out.println(c);
//				userLogOutIn.loginUser(Integer.parseInt(JwtTokenUtil.getIdFromToken(token)), JwtTokenUtil.getEmailFromToken(token)).getId());
		
//		System.out.println(jwtConfig.getHeader()+" header "+header);
//		if(header == null || !header.startsWith(jwtConfig.getPrefix())) {
//			chain.doFilter(request, response);  		// If not valid, go to the next filter.
//			return;
//		}
				
//		System.out.println("aaaaaaaaaaaa");
//		System.out.println();
//		System.out.println(request.getQueryString());
//		fetchLogInUser
//		System.out.println(request.getRemoteAddr());

//		HttpServletRequest httpReq = (HttpServletRequest) request;
//
//		byte[] body = StreamUtils.copyToByteArray(request.getInputStream());
//
//		Map<String, Object> jsonRequest = new ObjectMapper().readValue(body, Map.class);
//		jsonRequest.put("asdfga", "dfghjkl");
//		System.out.println(jsonRequest);
		
		
		filterChain.doFilter(request, response);
		// TODO Auto-generated method stub
		
	}

}
