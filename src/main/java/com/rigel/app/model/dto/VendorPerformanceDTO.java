package com.rigel.app.model.dto;

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

    @Builder.Default
    private double totalAmount = 0;

    // =========================================================
    // Total Item Count
    // =========================================================

    @Builder.Default
    private long totalItems = 0;

    // =========================================================
    // Invoice Wise Data
    // =========================================================

    @Builder.Default
    private List<VendorInvoiceDTO> invoices =
            new ArrayList<>();
}