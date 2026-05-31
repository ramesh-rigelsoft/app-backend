package com.rigel.app.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionBorrow {
	private int key;	
	private BigDecimal amount;
	private LocalDateTime paidDate;
	private String note; //200 char max
	private String transType; // EMI/Cash/Flipkart etc...
}
