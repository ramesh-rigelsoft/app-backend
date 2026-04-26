package com.rigel.app.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder.Default;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name="BUYER_INFO")
//@ToString
public class BuyerInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7016888389920150869L;
	/// its not be Serialize

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(length = 36, updatable = false, nullable = false)
	private String id;
	
	@NotNull
	private String invoiceNumber;
	
	@NotNull
	private String custumberId;
	
	private String paymentModes;
	
//	@Pattern(regexp="^[a-zA-Z ]+$", message="Enter valid Name")
	private String buyerName;
		
//	@Email(message = "Enter valid emailId")
	private String emailId;
	
//	@Pattern(regexp="^[6-9]\\d{9}$", message="Enter valid Mobile Number")
	private String mobileNumber;
		
//	@Pattern(regexp = "^[0-9]{1,3}$",message="Enter valid contry code") 
	private String countryCode;
	
	private String buyerAddress;
	
	// vender details
	private String companyName;
	
	private String gstNumber;
	
	private String panNumber;
	
	private String pinCode;
	
	private String state;
	
	private String distric;
	
	private String companyAddress;
	
	private int status;// 1-active,2-InActive,3-delete
	
	private LocalDateTime createdAt;
	
	private String noteComment;
		
	private int ownerId;
	
	@OneToMany(mappedBy="buyerInfo", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JsonIgnore
	private Set<SalesInfo> salesInfo = new HashSet<>();
		
}
