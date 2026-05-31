package com.rigel.app.model.dto;

import java.math.BigDecimal;
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

	private BigDecimal totalAmount;
	private BigDecimal paidAmount;
	private BigDecimal borrowAmount;
//    private double paidBorrowAmount;

    private String lastTranactionDate;
	private String paymentStatus;
	private String paymentModes;
	private String createdAt;
	private List<TransactionBorrow> transactionBorrow;
}