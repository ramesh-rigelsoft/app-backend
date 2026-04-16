package com.rigel.app.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.rigel.app.util.TokenSecure;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtTokenAuthenticationFilter extends  OncePerRequestFilter {
    
	private final JwtConfig jwtConfig;
	
	private UserDetailService userDetailService;
	
	private JwtTokenUtil JwtTokenUtil;
	
//    private CryptoAES128 cryptoAES128;
	
	public JwtTokenAuthenticationFilter(JwtConfig jwtConfig, UserDetailService userDetailService,JwtTokenUtil JwtTokenUtil) {//,CryptoAES128 cryptoAES128) {
		this.jwtConfig = jwtConfig;
		this.userDetailService=userDetailService;
		this.JwtTokenUtil=JwtTokenUtil;
		//this.cryptoAES128=cryptoAES128;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		
		String header = request.getHeader(jwtConfig.getHeader());
//		System.out.println(jwtConfig.getHeader()+" header "+header);
		if(header == null || !header.startsWith(jwtConfig.getPrefix())) {
			chain.doFilter(request, response);  		// If not valid, go to the next filter.
			return;
		}
				
		String token = header.substring(7);
//		System.out.println("Token 1 "+token);
//		token= cryptoAES128.decrypt(token);
//		response.setHeader("Authorization", "Bearer "+token);
//		System.out.println("Token 2 "+token);
		try {
//			if(JwtTokenUtil.isTokenExpired(token)){
//				token=JwtTokenUtil.refreshToken(token);
//			}
			boolean osInfo=!TokenSecure.getOSInfo().equals(JwtTokenUtil.getOSInfoFromToken(token,1));
			boolean browserInfo=!TokenSecure.getBrowserInfo(request).equals(JwtTokenUtil.getOSInfoFromToken(token, 2));
         	if(osInfo&&browserInfo) {
				chain.doFilter(request, response);  		// If not valid, go to the next filter.
				return;
			}
			
			Claims claims = Jwts.parserBuilder()
					.setSigningKey(jwtConfig.getKey())
					.build()
					.parseClaimsJws(token)
					.getBody();
			String username = claims.getSubject();
			JwtUser userDetails = (JwtUser) userDetailService.loadUserByUsername(username);
			
			if(userDetails!=null) {
//				System.out.println("Username  "+userDetails.getUsername());
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
						 userDetails.getUsername(), null, userDetails.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(auth);
			} else {
				SecurityContextHolder.clearContext();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			// In case of failure. Make sure it's clear; so guarantee user won't be authenticated
			SecurityContextHolder.clearContext();
		}
//		System.out.println("Username111  ");
		// go to the next filter in the filter chain
		chain.doFilter(request, response);
	}

}