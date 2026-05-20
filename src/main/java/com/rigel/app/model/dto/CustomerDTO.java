package com.rigel.app.model.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CustomerDTO {

	private String invoiceNumber;
	private String customerName;
	private String mobileNo;
	private String customerId;

	private double totalAmount;
	private double paidAmount;
	private double pendingAmount;


    private String restAmount;
    private String restAmountDate;
	private String paymentStatus;
}