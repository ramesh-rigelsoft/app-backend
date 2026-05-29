package com.rigel.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorInvoiceDTO {

    // =========================================================
    // Invoice Number
    // =========================================================

    private String invoiceNumber;

    // =========================================================
    // Invoice Wise Item Count
    // =========================================================

    @Builder.Default
    private long itemCount = 0;

    // =========================================================
    // Invoice Wise Total Amount
    // =========================================================

    @Builder.Default
    private double amount = 0;
}