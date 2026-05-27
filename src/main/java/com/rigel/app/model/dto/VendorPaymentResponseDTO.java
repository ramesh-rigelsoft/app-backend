package com.rigel.app.model.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VendorPaymentResponseDTO {

    private String id;

    private String vendorName;

    private String gstNumber;

    // ✅ ONLY RESPONSE FIELD
    private List<InvoicePaymentDTO> invoices = new ArrayList<>();
}