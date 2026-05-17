package com.rigel.app.model.dto;

import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RepaireDeviceDto {

	private String id;
	
	// CUSTOMER DETAILS
	private String customerName;
	private String InvoiceNumber;
	private String custumberId;
	private String mobileNumber;
	private String address;
	// DEVICE DETAILS
	private String category;

	private String categoryType;

	private String categoryTypeMode;

	private String deviceModelName;

	private String serialNumber;

	private String defectDescription;

	// LOCK DETAILS
	private String deviceLockType;

	private String devicePassword;
	// STATUS
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

	// sales list
	@JsonProperty("items")
	private Set<SalesInfoDto> items;
}
