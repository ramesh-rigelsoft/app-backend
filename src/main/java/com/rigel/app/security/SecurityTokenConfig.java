package com.rigel.app.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityTokenConfig {

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailService userDetailService;

    @Value("${jwt.route.authentication.path}")
    private String authenticationPath;

    private static final String[] AUTH_WHITELIST = {
            "/authenticate",
            "/actuator/**",
            "/v2/api-docs/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/webjars/**",
            "/api/todoTask/test/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(
                    (req, res, e) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                )
            )

            // JWT filter
            .addFilterBefore(
                new JwtTokenAuthenticationFilter(jwtConfig, userDetailService, jwtTokenUtil),
                UsernamePasswordAuthenticationFilter.class
            )

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(AUTH_WHITELIST).permitAll()

                .requestMatchers(HttpMethod.POST,
                        "/api/user/signup/**",
                        "/api/user/login/**",
                        "/api/user/view/**",
                        "/api/user/**"
                ).permitAll()

                .requestMatchers(HttpMethod.GET,
                        "/api/user/view/**"
                ).permitAll()

                .requestMatchers(
                        "/",
                        "/index.html",
                        "/favicon.ico",
                        "/static/**",
                        "/images/**",
                        "/js/**",
                        "/css/**"
                ).permitAll()

                .anyRequest().authenticated()
            )

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .httpBasic(Customizer.withDefaults())

            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )

            .cors(Customizer.withDefaults());

        return http.build();
    }

    // ✅ FIXED CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:8091"));
        config.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization","Content-Type","Cache-Control","filter"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
//package com.rigel.app.security;
//
//import java.util.Arrays;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import com.rigel.app.util.AppUtill;
//
//import org.springframework.http.HttpMethod;
//
////import com.app.todoapp.util.CryptoAES128;
//import jakarta.servlet.http.HttpServletResponse;
//
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true) // Boot 3.x
//public class SecurityTokenConfig {
//
//	@Autowired
//	private JwtConfig jwtConfig;
//
////	@Autowired
////	private CryptoAES128 cryptoAES128;
//
//	@Autowired
//	private JwtTokenUtil jwtTokenUtil;
//
//	@Value("${security.jwt.header}")
//	private String tokenHeader;
//
//	@Value("${jwt.route.authentication.path}")
//	private String authenticationPath;
//
//	@Autowired
//	private UserDetailService userDetailService;
//    
//	private static final String[] AUTH_WHITELIST = {
//		    "/authenticate",
//		    "/actuator/**",
//		    "/v2/api-docs",
//		    "/api/todoTask/test/**",
//		    "/api-docs/**",
//		    "/api/actuator/**",
//		    "/swagger-ui/**",
//		    "/swagger-resources/**",
//		    "/swagger-ui.html",
//		    "/v3/api-docs/**",
//		    "/webjars/**"
//		};
//
//	@Bean
//	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//		http
//				// disable csrf for APIs
//				.csrf(csrf -> csrf.disable())
//
//				// handle unauthorized
//				.exceptionHandling(ex -> ex
//						.authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
//
//				// add JWT filter
//				.addFilterAfter(
//						new JwtTokenAuthenticationFilter(jwtConfig, userDetailService, jwtTokenUtil),//, cryptoAES128),
//						UsernamePasswordAuthenticationFilter.class)
//
//				// authorize requests
//				.authorizeHttpRequests(auth -> auth.requestMatchers(AUTH_WHITELIST).permitAll()
//
//						
////						.requestMatchers(HttpMethod.GET, "/**").permitAll()
////						.requestMatchers(HttpMethod.POST, "/api/**").permitAll()
//						
//						.requestMatchers(HttpMethod.POST, "/api/user/signup/**", "/api/user/login/**").permitAll()
////						.requestMatchers(AppUtill.getUrlRole()).hasAnyAuthority(AppUtill.getAllRole()).anyRequest()
//						.requestMatchers(
//					            "/",
//					            "/index.html",
//					            "/static/**",
//					            "/favicon.ico"
//					        ).permitAll()
//					        .anyRequest() //.authenticated()
//						.authenticated())
//
//				// use stateless sessions
//				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//
//				// basic auth enabled (optional if JWT only)
//				.httpBasic(Customizer.withDefaults());
//
//		// security headers
//		http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()).cacheControl(Customizer.withDefaults()));
//
//		http.cors(Customizer.withDefaults());
//
//		return http.build();
//	}
//	
//	 @Bean
//	    public WebSecurityCustomizer webSecurityCustomizer(){
//	    	return (web) -> web.ignoring()
//	            .requestMatchers(
//	                HttpMethod.POST,
//	                authenticationPath)
//	            .and()
//	            .ignoring()
//	            .requestMatchers(
//	                HttpMethod.GET,"/","/*.html",
//	                "/favicon.ico",
//	                "/**/*.html",
//	                "/swagger-ui.html#/",
//	                "/v2/api-docs/",
//	                "/**/*.jsp",
//	                "/**/*.css",
//	                "/**/*.js",
//	                "/**/*.png",
//	                "/**/*.jpg",
//	                "/**/*.jpeg",
//	                "/**/*.JPG",
//	                "/**/*.gif",
//	                "/**/*.map",
//	                "/fonts/**",
//	                "/js/**",
//	                "css/**",
//	                "/images/**",
//	                "/frontend/app/static/**"
//	            ).and()
//	            .ignoring()
//	            .requestMatchers("/h2-console/**");
//	    }
//	 
//	 @Bean
//	    public CorsConfigurationSource corsConfigurationSource() {
//	        final CorsConfiguration configuration = new CorsConfiguration();
//	        configuration.setAllowCredentials(true);
////	        configuration.setAllowedOrigins(Arrays.asList("*"));
//	        configuration.addAllowedOrigin("http://localhost:8091");
//	        configuration.setAllowedMethods(Arrays.asList("HEAD",
//	                "GET", "POST", "PUT", "DELETE", "PATCH"));
////	        configuration.addAllowedHeader("*");
//	        configuration.setAllowedHeaders(Arrays.asList("Authorization","filter", "Cache-Control", "Content-Type"));
//	        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//	        source.registerCorsConfiguration("/**", configuration);
//	        return source;
//	    }
//	 
//	 
//}
