package com.rigel.app.model.dto;
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

    private Double totalAmount = 0.0;

    private Double paidAmount = 0.0;

    private Double pendingAmount = 0.0;

    private List<TransactionDTO> transactions =
            new ArrayList<>();

  }