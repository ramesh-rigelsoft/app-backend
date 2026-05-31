package com.rigel.app.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransactionDTO {

    private LocalDateTime date;
    private BigDecimal amount;
    private String status;
    private String comments;
    private String paymentModes;

    // getters & setters
}