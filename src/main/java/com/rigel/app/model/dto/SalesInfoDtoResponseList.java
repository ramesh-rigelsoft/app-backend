package com.rigel.app.model.dto;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SalesInfoDtoResponseList {

    // ================= BUYER INFO (ONLY USEFUL) =================
    private String invoiceNumber;
    private String custumberId;
    private String buyerName;
    private String mobileNumber;
    private String emailId;

    private String companyName;
    private String gstNumber;
    private String state;

    private String paymentModes;
    private double paidAmount;
    private String pendingAmount;
    

    // ================= SALES INFO (ONLY REPORT RELEVANT) =================
    private String itemCode;
    private String brand;
    private String modelName;
    private String categoryType;

    private Integer quantity;

    private Double initialPrice;
    private Double sellingPrice;
    private Double soldPrice;

    private String vendorName;
    private String vendorGSTNumber;

    private String serialNumber;

    private int warrantyInMonth;

    private LocalDateTime createdAt;
}
