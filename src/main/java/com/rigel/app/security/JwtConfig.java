package com.rigel.app.security;

import java.security.Key;
import java.util.Arrays;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.security.Keys;

@Component
public class JwtConfig {
	
    @Value("${security.jwt.uri:/users/**}")
    private String Uri;

    @Value("${security.jwt.header:Authorization}")
    private String header;

    @Value("${security.jwt.prefix:Bearer }")
    private String prefix;

//    @Value("${security.jwt.expiration}")
//    private int expiration;
//
//    @Value("${security.jwt.secret}")
//    private String secret;
    
	private final Key key;
    private final long expiration;

    public JwtConfig(
            @Value("${security.jwt.secret}") String secretHex,
            @Value("${security.jwt.expiration}") long expirationSeconds) {

        // 1) Normalize & validate hex secret
        if (secretHex == null) {
            throw new IllegalStateException("security.jwt.secret is null");
        }
        secretHex = secretHex.trim();
        if ((secretHex.length() % 2) != 0) {
            throw new IllegalArgumentException("security.jwt.secret must have even hex length");
        }
        if (!secretHex.matches("(?i)^[0-9a-f]+$")) {
            throw new IllegalArgumentException("security.jwt.secret must be hex (0-9 a-f)");
        }
        byte[] keyBytes = JwtTokenUtil.hexStringToByteArray(secretHex);
        if (keyBytes.length < 32) { // 256-bit minimum for HS256
            throw new IllegalArgumentException("security.jwt.secret must be at least 64 hex chars (32 bytes)");
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expiration = expirationSeconds;

        // 2) Log a stable fingerprint (compare across all instances)
        String fingerprint = Base64.getEncoder().encodeToString(Arrays.copyOfRange(key.getEncoded(), 0, 8));
        System.out.println("[JWT] Key fingerprint (first 8 bytes, Base64): " + fingerprint);
    }

    
    public Key getKey() {
        return key;
    }

    
    public String getUri() {
		return Uri;
	}

	public void setUri(String uri) {
		Uri = uri;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

//	public int getExpiration() {
//		return expiration;
//	}
//
//	public void setExpiration(int expiration) {
//		this.expiration = expiration;
//	}
//
//	public String getSecret() {
//		return secret;
//	}
//
//	public void setSecret(String secret) {
//		this.secret = secret;
//	}
}
