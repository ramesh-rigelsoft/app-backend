package com.rigel.app.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;
    private final UserDetailService userDetailService;
    private final JwtTokenUtil jwtTokenUtil;

    public JwtTokenAuthenticationFilter(
            JwtConfig jwtConfig,
            UserDetailService userDetailService,
            JwtTokenUtil jwtTokenUtil) {

        this.jwtConfig = jwtConfig;
        this.userDetailService = userDetailService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(jwtConfig.getHeader());
        
        if (header == null || !header.startsWith(jwtConfig.getPrefix())) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(jwtConfig.getPrefix().length());
        System.out.println("token---"+token);
        try {
            String username = jwtTokenUtil.getUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                JwtUser userDetails =
                        (JwtUser) userDetailService.loadUserByUsername(username);

                if (jwtTokenUtil.validateToken(token, userDetails)) {

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }
}
//package com.rigel.app.security;
//
//import java.io.IOException;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import com.rigel.app.util.TokenSecure;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//
//public class JwtTokenAuthenticationFilter extends  OncePerRequestFilter {
//    
//	private final JwtConfig jwtConfig;
//	
//	private UserDetailService userDetailService;
//	
//	private JwtTokenUtil JwtTokenUtil;
//	
////    private CryptoAES128 cryptoAES128;
//	
//	public JwtTokenAuthenticationFilter(JwtConfig jwtConfig, UserDetailService userDetailService,JwtTokenUtil JwtTokenUtil) {//,CryptoAES128 cryptoAES128) {
//		this.jwtConfig = jwtConfig;
//		this.userDetailService=userDetailService;
//		this.JwtTokenUtil=JwtTokenUtil;
//		//this.cryptoAES128=cryptoAES128;
//	}
//
//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//			throws ServletException, IOException {
//		
//		String header = request.getHeader(jwtConfig.getHeader());
//		if(header == null || !header.startsWith(jwtConfig.getPrefix())) {
//			chain.doFilter(request, response);  		// If not valid, go to the next filter.
//			return;
//		}
//				
//		String token = header.substring(7);
//		try {
//			if(token!=null) {
//				chain.doFilter(request, response);  		// If not valid, go to the next filter.
//				return;
//			}
//			
//			Claims claims = Jwts.parserBuilder()
//					.setSigningKey(jwtConfig.getSecret())
//					.build()
//					.parseClaimsJws(token)
//					.getBody();
//			String username = claims.getSubject();
//			JwtUser userDetails = (JwtUser) userDetailService.loadUserByUsername(username);
//			System.out.println("aaa----------"+userDetails);
//			if(userDetails!=null) {
//				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
//						 userDetails.getUsername(), null, userDetails.getAuthorities());
//				SecurityContextHolder.getContext().setAuthentication(auth);
//			} else {
//				SecurityContextHolder.clearContext();
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			// In case of failure. Make sure it's clear; so guarantee user won't be authenticated
//			SecurityContextHolder.clearContext();
//		}
//		// go to the next filter in the filter chain
//		chain.doFilter(request, response);
//	}
//
//}