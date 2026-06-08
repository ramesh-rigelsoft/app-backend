package com.rigel.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.Builder.Default;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Getter
@Setter
@Entity
@Table(name = "VENDORS")
public class Vendors implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(length = 36, updatable = false, nullable = false)
	private String id;

	@NotBlank(message = "company name is required")
	@Column(nullable = false)
	private String companyName;

	@NotBlank(message = "GST Number is required")
	@Column(nullable = false, unique = true)
	private String gstNumber;

//    @Column(length = 10)
	private String panNumber;

//    @Column(length = 6)
	private String pinCode;

//	@Email(message = "Invalid email format")
//    @Column(length = 100)
	private String email;

//    @Pattern(regexp = "^[0-9]{12}$", message = "Phone must be 10 digits")
//    @Column(length = 10)
	private String phone;

	@NotBlank(message = "Address is required")
	@Size(max = 500, message = "Address can't exceed 500 characters")
	@Column(length = 500, nullable = false)
	private String address;

	private String status;

	private String district;
	
	@NotBlank(message = "State name is required")
	private String state;
	
	@NotBlank(message = "State code is required")
	private String stateCode;

	private int ownerId; // optional if multi-user system

	private LocalDateTime createdAt;

	private String officeBranch;

	private String additionalDetails;
	
	@OneToMany(mappedBy="vendors", cascade=CascadeType.PERSIST, fetch=FetchType.LAZY)
//	@JsonManagedReference
	@JsonIgnore
	private Set<Items> items = new HashSet<>();
	
	@OneToMany(mappedBy="vendors", cascade=CascadeType.PERSIST, fetch=FetchType.LAZY)
	@JsonManagedReference
	private Set<VendorPayments> vendorPayments = new HashSet<>();
	
	@OneToMany(mappedBy="vendors", cascade=CascadeType.PERSIST, fetch=FetchType.LAZY)
	@JsonManagedReference
	private Set<GarbageItemsInfo> garbageItemInfo = new HashSet<>();

}
