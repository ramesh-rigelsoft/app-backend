package com.rigel.app.model.dto;

import java.util.List;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {
	
	private int startIndex;
	private int maxRecords;
	private String order;
	private String itemId;
	private boolean isdownload;
	private long limit;
	private long offset;
	private String id;
	

	// items search
	private String searchKeyword;
	private String startDate;
	private String endDate;
	

	private String pendingPaymentStatus;
	private boolean vendorType;
	private String invoiceNumber;
	private String repaireDeviceId;
    private Set<String> itemCodes;
    private String itemCode;
    private String category;
	private String categoryType;
	private String itemType;
	private String measureType;
	private String brand;
	private String modelName;
	private String ram;
	private String ramUnit;
	private String storage;
	private String storageUnit;
	private String quantity;
	private String initialPrice;
	private String sellingPrice;
	private String processor;
	private String itemCondition;
	private String storageType;
	private String operatingSystem;
	private String screenSize;
	private String itemGen;
	private String description;
	private String itemQuentity;
	
	private String MobileNumber;
	private String emailId;
	private String CustomerName;
	private String deviceModelName;
	private String serialNumber;
	private String status;
		
	private int userId;
}
