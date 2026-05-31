package com.rigel.app.model.dto;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class InvoicePaymentDTO {

    private String invoiceNumber;

    private BigDecimal totalAmount;

    private BigDecimal paidAmount;

    private BigDecimal pendingAmount;

    private List<TransactionDTO> transactions =
            new ArrayList<>();

  }