package com.rigel.app.model.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InvoiceDTO {

    private String invoiceNumber;
    private double totalAmount;
    private double paidAmount;
    private double pendingAmount;
    private List<TransactionDTO> transactions;

    // getters & setters
}
