package com.rigel.app.util;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

public class AppUtill {
	
	
	
	 private static final String[] IP_HEADERS = {
		        "X-Forwarded-For",
		        "Proxy-Client-IP",
		        "WL-Proxy-Client-IP",
		        "HTTP_X_FORWARDED_FOR",
		        "HTTP_X_FORWARDED",
		        "HTTP_X_CLUSTER_CLIENT_IP",
		        "HTTP_CLIENT_IP",
		        "HTTP_FORWARDED_FOR",
		        "HTTP_FORWARDED",
		        "HTTP_VIA",
		        "REMOTE_ADDR"

		        // you can add more matching headers here ...
		    };

	
	public static String getRequestIP(HttpServletRequest request) {
        for (String header: IP_HEADERS)  {
            String value = request.getHeader(header);
            if (value == null || value.isEmpty()) {
                continue;
            }
            String[] parts = value.split("\\s*,\\s*");
            return parts[0];
        }
        return request.getRemoteAddr();
    }
	
	public static String[] getAllRole() {
       String[] roles= {"user","userss"};      
       return roles;
    }
	
	public static String[] getUrlRole() {
	       String[] roles= {"/api/user/**","/api/todoTask/**"};
	        return roles;
	}
	
	public static String replaceAllSpace(String str) {
		if(str==null||str.isBlank())
		return null;
		str.replaceAll("\\s+", " ").strip();   
       return str;
    }
	
	
}
