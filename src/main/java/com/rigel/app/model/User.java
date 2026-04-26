package com.rigel.app.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name="USERS")
public class User implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7016888389920150869L;
	/// its not be Serialize

	public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Pattern(
		    regexp = "^[A-Za-z]+(\\s[A-Za-z]+){0,3}$",
		    message = "Name must contain maximum 4 words (letters only)"
		)private String name;
	
	@Email
	@NotBlank(message = "Email is mandatory")
	private String email_id;
	
	@Pattern(regexp = "^[0-9]{10,10}$",message="Enter valid mobile number") 
	private String mobile_no;
	
	
	@Pattern(regexp = "^[0-9]{1,3}$",message="Enter valid contry code") 
	private String country_code;
	
	@NotBlank(message = "Password is mandatory")
	private String password;
	
	private int status;// 1-active,2-InActive,3-delete
	private Timestamp created_at;
//	private List<String> role;// 1- admin,2-user
	private String role;// 1- admin,2-user
	
	private String gender;
	private Date lastPasswordResetDate;
	
    // ================= COMPANY INFO =================
	private String shopType;
    
    private String companyName;
    private String companyLogo;

    private String gstNumber;
    private String panNumber;
    private String cinNumber;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String pincode;

    private String website;
    private String companyType; // Pvt Ltd, LLP, Proprietor, etc.
    private Integer companyEmployeeCount;
    // ================= RELATION =================
    
    private String logo;
    
    private String softwareKey;
    private String macAddress;
    
	public static PasswordEncoder getPasswordEncoder() {
		return PASSWORD_ENCODER;
	}
	
}

