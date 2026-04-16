package com.rigel.app.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="LOGIN_ACTIVITY")
@Entity
public class LoginActivity implements Serializable {

	private static final long serialVersionUID = -7016888344920150869L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
 
    private int userId;
    private String emailId;
    private String mobileNumber;
    private LocalDateTime loginAt;
    private String requestIp;
    private String macAddress;
   
//    @Column(name = "token", columnDefinition = "text")
    @Column(columnDefinition = "CLOB")
    private String token;  // JSON string में store करें
    
    private String secret;

//    @Column(name = "user_object", columnDefinition = "text")
    @Column(columnDefinition = "CLOB")
    private String userObject;
}