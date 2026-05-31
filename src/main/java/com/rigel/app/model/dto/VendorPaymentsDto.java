package com.rigel.app.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.rigel.app.model.Vendors;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class VendorPaymentsDto{

	private String id;
	private String vendorId;
	private String gstNumber;
	private String vendorInvoiceNumber;
	private BigDecimal paidAmount;
	private LocalDateTime createdAt;
	private LocalDateTime updateAt;
	
	private String paymentModes;
	private String comments;
	private int ownerId;
	
	private Vendors vendors;
		
}
