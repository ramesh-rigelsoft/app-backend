package com.rigel.app.model.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransactionDTO {

    private LocalDateTime date;
    private double amount;
    private String status;

    // getters & setters
}