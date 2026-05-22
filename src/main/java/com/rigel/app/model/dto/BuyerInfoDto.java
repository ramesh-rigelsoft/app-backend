package com.rigel.app.model.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

import java.util.Set;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BuyerInfoDto {

	    @JsonProperty("id")
	    private String id;

	    @JsonProperty("invoiceNumber")
	    private String invoiceNumber;
	    
		@NotNull
		private String custumberId;
	    
	    @JsonProperty("paymentModes")
	    private String paymentModes;
	    
		private String financeId;
	    private String emiTenure;          // months
	    private double paidAmount;
	    private String imeiNumber;
        // months
	    private double totalAmount;
	    private String pendingPaymentStatus;
	    private String restAmount;
	    private LocalDateTime restAmountDate;

		
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

	    // vendor details
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

	    @JsonProperty("distric")
	    private String distric;

	    @JsonProperty("companyAddress")
	    private String companyAddress;

	    // optional
	    @JsonProperty("ownerId")
	    private int ownerId;
	    
		private String noteComment;


	    // sales list
	    @JsonProperty("items")
	    private Set<SalesInfoDto> salesInfo;

}
