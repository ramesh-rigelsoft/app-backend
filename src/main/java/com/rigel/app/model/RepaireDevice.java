package com.rigel.app.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Entity
@Table(name = "REPAIRE_DEVICE")
@ToString
public class RepaireDevice implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7016888389920150869L;
	/// its not be Serialize

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(length = 36, updatable = false, nullable = false)
	private String id;

	// CUSTOMER DETAILS
	private String customerName;
	private String custumberId;
	private String mobileNumber;

	private String invoiceNumber;
	private String address;
	private String category;

	private String categoryType;

	private String categoryTypeMode;

	private String deviceModelName;

	private String serialNumber;

	@Column(length = 500)
	private String defectDescription;
	
	@Column(name = "lvstatus")
	private boolean lvstatus;


	// LOCK DETAILS
	private String deviceLockType;

	private String devicePassword;
	private String deviceStatus;
	private boolean status;
	
	// PAYMENT
	private double advanceAmount;
	private double totalAmount;
	private double pendingAmount;

	private String restAmount;
	
	private LocalDateTime deliveredDate;
	
	private LocalDateTime deliveryDate;
	private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
	
    private int ownerId;

	@OneToMany(mappedBy = "repaireDevice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonManagedReference
	private Set<SalesInfo> salesInfo = new HashSet<>();

}