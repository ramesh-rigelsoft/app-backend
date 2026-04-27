package com.rigel.app.security;

import java.io.Serializable;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Key key;
    private final long expiration;

    public JwtTokenUtil(
            @Value("${security.jwt.secret}") String secretHex,
            @Value("${security.jwt.expiration}") long expirationMs) {

        // validate hex
        if (secretHex == null || !secretHex.matches("(?i)^[0-9a-f]+$") || secretHex.length() % 2 != 0) {
            throw new IllegalArgumentException("Invalid JWT secret (must be hex)");
        }

        byte[] keyBytes = hexToBytes(secretHex);

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 bytes");
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expiration = expirationMs;

        // safe fingerprint log (not secret)
        String fingerprint = Base64.getEncoder()
                .encodeToString(Arrays.copyOfRange(keyBytes, 0, 8));
        System.out.println("[JWT] Key fingerprint: " + fingerprint);
    }

    // ================= TOKEN GENERATION =================

    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ================= CLAIM EXTRACTION =================

    public String getUsername(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    public <T> T getClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(parseToken(token));
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ================= VALIDATION =================

    public boolean isTokenExpired(String token) {
        try {
        	Date date=getExpiration(token);
        	System.out.println("toke expre date---"+date);
            return date.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = getUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ================= UTIL =================

    private byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] =
                    (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                            + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}

//package com.rigel.app.security;
//
//import java.io.Serializable;
//import java.nio.charset.StandardCharsets;
//import java.security.Key;
//import java.security.KeyPair;
//import java.security.PrivateKey;
//import java.security.PublicKey;
//import java.util.Arrays;
//import java.util.Base64;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Function;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.servlet.http.HttpServletRequest;
//
//import io.jsonwebtoken.Claims;
//import java.time.Clock;//io.jsonwebtoken.Clock;
//
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.SignatureException;
//import io.jsonwebtoken.security.Keys;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import com.rigel.app.util.TokenSecure;
//
//@Component
//public class JwtTokenUtil implements Serializable {
//
//	static final String CLAIM_KEY_USERNAME = "sub";
//	static final String CLAIM_KEY_CREATED = "iat";
//
//	static final String OS_INFO = "clientOsInfo";
//	static final String BROWSER_INFO = "clientBrowserInfo";
//
//	private static final long serialVersionUID = -3301605591108950415L;
//
//	private Clock clock = Clock.systemUTC();// DefaultClock.INSTANCE;
//
//	private final Key key;
//	private final long expiration;
//
//	public JwtTokenUtil(@Value("${security.jwt.secret}") String secretHex,
//			@Value("${security.jwt.expiration}") long expirationSeconds) {
//
//		// 1) Normalize & validate hex secret
//		if (secretHex == null) {
//			throw new IllegalStateException("security.jwt.secret is null");
//		}
//		secretHex = secretHex.trim();
//		if ((secretHex.length() % 2) != 0) {
//			throw new IllegalArgumentException("security.jwt.secret must have even hex length");
//		}
//		if (!secretHex.matches("(?i)^[0-9a-f]+$")) {
//			throw new IllegalArgumentException("security.jwt.secret must be hex (0-9 a-f)");
//		}
//		byte[] keyBytes = hexStringToByteArray(secretHex);
//		if (keyBytes.length < 32) { // 256-bit minimum for HS256
//			throw new IllegalArgumentException("security.jwt.secret must be at least 64 hex chars (32 bytes)");
//		}
//
//		this.key = Keys.hmacShaKeyFor(keyBytes);
//		this.expiration = expirationSeconds;
//
//		// 2) Log a stable fingerprint (compare across all instances)
//		String fingerprint = Base64.getEncoder().encodeToString(Arrays.copyOfRange(key.getEncoded(), 0, 8));
//		System.out.println("[JWT] Key fingerprint (first 8 bytes, Base64): " + fingerprint);
//		System.out.println("p12 file loaded : "+secretHex);
//	}
//
//	public Key getKey() {
//		return key;
//	}
//
//	public long getExpiration() {
//		return expiration;
//	}
//
//
//	public String getUsernameFromToken(String token) {
//		return getClaimFromToken(token, Claims::getSubject);
//	}
//
//	public String getEmailFromToken(String token) {
//		token = token.replaceAll("\\s+", "");
//		return getClaimFromToken(token, Claims::getSubject);
//	}
//
//	public String getIdFromToken(String token) {
//		token = token.replaceAll("\\s+", "");
//		return getClaimFromToken(token, Claims::getId);
//	}
//
//
//	public String getUserIdFromToken(String token) {
//		return getClaimFromToken(token, Claims::getId);
//	}
//
//	public Date getIssuedAtDateFromToken(String token) {
//		return getClaimFromToken(token, Claims::getIssuedAt);
//	}
//
//	public Date getExpirationDateFromToken(String token) {
//		return getClaimFromToken(token, Claims::getExpiration);
//	}
//	public Claims getProExpirationDateFromToken(String token) {
//	    try {
//	        return Jwts.parserBuilder()
//	                .setSigningKey(key)
//	                .build()
//	                .parseClaimsJws(token)
//	                .getBody();
//
//	    } catch (ExpiredJwtException ex) {
//	        System.out.println("Token expired");
//	        return null;
//	    }
//	}
//
//	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
//		final Claims claims = getAllClaimsFromToken(token);
//		return claimsResolver.apply(claims);
//	}
//
//	private Claims getAllClaimsFromToken(String token) {
//		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
//	}
//
//	public Boolean isTokenExpired(String token) {
//		try {
//			Claims clams = getProExpirationDateFromToken(token);
//			final Date expiration;
//			if(clams==null) {
//				return true;
//			}else {
//				expiration=clams.getExpiration();
//			}
//			System.out.println("expiration----"+expiration);
//			return expiration.before(Date.from(clock.instant()));
//		} catch (Exception e) {
//			return true;
//		}
//	}
//
//	private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
//		return false;
//		// System.out.println(">>>>>>>>>>>>>>>>>>"+created.toLocaleString()+"
//		// "+lastPasswordReset);
//		// return (lastPasswordReset != null && created.before(lastPasswordReset));
//	}
//
//	private Boolean ignoreTokenExpiration(String token) {
//		return false;
//	}
//
//	public String generateToken(JwtUser userDetails, HttpServletRequest request) {
//		Map<String, Object> claims = new HashMap<>();
//		return doGenerateToken(claims, userDetails);
//	}
//
//	public String pbGenerateToken(UserDetails userDetails, int experyDate) {
//		Map<String, Object> claims = new HashMap<>();
//		return pbDoGenerateToken(claims, userDetails.getUsername(), experyDate);
//	}
//
//	private String doGenerateToken(Map<String, Object> claims, JwtUser userDetails) {
//		final Date createdDate = Date.from(clock.instant());
//
////		 return Jwts.builder()
////	                .setSubject(username)
////	                .setIssuedAt(new Date(System.currentTimeMillis()))
////	                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
////	                .signWith(key, SignatureAlgorithm.HS256)
////	                .compact();
////	    }
//
//		final Date expirationDate = calculateExpirationDate(createdDate);
//		return Jwts.builder().setClaims(claims).setId(String.valueOf(userDetails.getId()))
//				.setSubject(userDetails.getUsername()).setIssuedAt(createdDate).setExpiration(expirationDate)
//				.signWith(key, SignatureAlgorithm.HS256).compact();
//	}
//
//	private String pbDoGenerateToken(Map<String, Object> claims, String subject, int experyDate) {
//		final Date createdDate = Date.from(clock.instant());
//		final Date expirationDate = calculateExpirationDates(createdDate, experyDate);
//		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(createdDate)
//				.setExpiration(expirationDate).signWith(key, SignatureAlgorithm.HS256).compact();
//	}
//
//	public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
//		final Date created = getIssuedAtDateFromToken(token);
//		return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset)
//				&& (!isTokenExpired(token) || ignoreTokenExpiration(token));
//	}
//
//	public String refreshToken(String token) {
//		final Date createdDate = Date.from(clock.instant());
//		final Date expirationDate = calculateExpirationDate(createdDate);
//
//		final Claims claims = getAllClaimsFromToken(token);
//		claims.setIssuedAt(createdDate);
//		claims.setExpiration(expirationDate);
//
//		return Jwts.builder().setClaims(claims).signWith(key, SignatureAlgorithm.HS256).compact();
//	}
//
//	public Boolean validateToken(String token, UserDetails userDetails) {
//		try {
//			final String username = getUsernameFromToken(token);
//			final Date created = getClaim(token, Claims::getIssuedAt);
//			return username.equals(userDetails.getUsername()) && !isTokenExpired(token)
//					&& !isCreatedBeforeLastPasswordReset(created, null);
//		} catch (ExpiredJwtException e) {
//			return false;
//		} catch (SignatureException e) {
//			return false;
//		} catch (JwtException | IllegalArgumentException e) {
//			return false;
//		}
//
////		JwtUser user = (JwtUser) userDetails;
////		final String username = getUsernameFromToken(token);
////		final Date created = getIssuedAtDateFromToken(token);
////		// final Date expiration = getExpirationDateFromToken(token);
////		return (username.equals(user.getUsername()) && !isTokenExpired(token)
////				&& !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate()));
//	}
//
//	private <T> T getClaim(String token, java.util.function.Function<Claims, T> resolver) {
//		return resolver.apply(parseClaims(token));
//	}
//
//	private Claims parseClaims(String token) {
//		token = token == null ? "" : token.replaceAll("\\s+", ""); // strip spaces only
//		// Debug: check header alg without verifying signature
//		// printHeader(token);
//		return Jwts.parserBuilder().setSigningKey(key) // exact same Key instance
//				.build().parseClaimsJws(token).getBody();
//	}
//
//	public int InValidOrValidateToken(String token, UserDetails userDetails) {
//		JwtUser user = (JwtUser) userDetails;
//		try {
//			final String username = getUsernameFromToken(token);
//			final Date created = getIssuedAtDateFromToken(token);
//			// final Date expiration = getExpirationDateFromToken(token);
//			return (username.equals(user.getUsername()) && !isTokenExpired(token)
//					&& !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate())) ? (1) : (0);
//		} catch (ExpiredJwtException e) {
//			return 2;
//
//		} catch (SignatureException e) {
//			e.printStackTrace();
//			return 0;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return -1;
//		}
//	}
//
//	private Date calculateExpirationDate(Date createdDate) {
//		return new Date(createdDate.getTime() + expiration);
//	}
//
//	private Date calculateExpirationDates(Date createdDate, int expiration) {
//		return new Date(createdDate.getTime() + expiration);
//	}
//
//	// utility
//	public static byte[] hexStringToByteArray(String s) {
//		int len = s.length();
//		byte[] data = new byte[len / 2];
//		for (int i = 0; i < len; i += 2) {
//			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
//		}
//		return data;
//	}
//
//}
