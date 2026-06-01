package com.rigel.app.model.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorPerformanceDTO {

    // =========================================================
    // Vendor Name
    // =========================================================

    private String vendorName;

    // =========================================================
    // Total Vendor Purchase Amount
    // =========================================================

    private BigDecimal totalAmount;

    // =========================================================
    // Total Item Count
    // =========================================================

    private long totalItems;

    // =========================================================
    // Invoice Wise Data
    // =========================================================

    @Builder.Default
    private List<VendorInvoiceDTO> invoices =
            new ArrayList<>();
}