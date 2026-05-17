package com.rigel.app.builder;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyerInfoDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("invoiceNumber")
    private String invoiceNumber;

    @JsonProperty("custumberId")
    private String custumberId;

    @JsonProperty("paymentModes")
    private String paymentModes;

    // Buyer details
    @JsonProperty("buyerName")
    private String buyerName;

    @JsonProperty("emailId")
    private String emailId;

    @JsonProperty("mobileNumber")
    private String mobileNumber;

    @JsonProperty("countryCode")
    private String countryCode;

    @JsonProperty("buyerAddress")
    private String buyerAddress;

    // Vendor details
    @JsonProperty("companyName")
    private String companyName;

    @JsonProperty("gstNumber")
    private String gstNumber;

    @JsonProperty("panNumber")
    private String panNumber;

    @JsonProperty("pinCode")
    private String pinCode;

    @JsonProperty("state")
    private String state;

    @JsonProperty("district")
    private String district;

    @JsonProperty("companyAddress")
    private String companyAddress;

    @JsonProperty("status")
    private int status;

    @JsonProperty("createdAt")
    private String createdAt;

    @JsonProperty("noteComment")
    private String noteComment;

    @JsonProperty("ownerId")
    private int ownerId;
    
	private String financeId;
    private String emiTenure;
    private double paidAmount;
    private String imeiNumber;

    // 👇 Nested Sales DTO list
    @JsonProperty("salesInfo")
    private List<SalesInfoDTO> salesInfo;
    
    public BuyerInfoDTO addSalesInfoItem(SalesInfoDTO salesItem) {

        if (this.salesInfo == null) {
            this.salesInfo = new java.util.ArrayList<>();
        }

        if (salesItem != null) {
            this.salesInfo.add(salesItem);
        }

        return this;
    }
}