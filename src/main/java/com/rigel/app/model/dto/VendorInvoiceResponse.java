package com.rigel.app.model.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VendorInvoiceResponse {

    private String vendorName;
    private String id;
    private String gstNumber;
    private List<InvoiceDTO> invoices;

    // getters & setters
}