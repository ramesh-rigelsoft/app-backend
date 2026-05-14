package com.rigel.app.builder;

import java.time.LocalDateTime;

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
public class SalesInfoDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("itemCode")
    private String itemCode;

    @JsonProperty("category")
    private String category;

    @JsonProperty("categoryType")
    private String categoryType;

    @JsonProperty("measureType")
    private String measureType;

    @JsonProperty("brand")
    private String brand;

    @JsonProperty("modelName")
    private String modelName;

    @JsonProperty("itemCondition")
    private String itemCondition;

    @JsonProperty("itemSource")
    private String itemSource;

    @JsonProperty("ram")
    private String ram;

    @JsonProperty("storage")
    private String storage;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("initialPrice")
    private Double initialPrice;

    @JsonProperty("sellingPrice")
    private Double sellingPrice;

    @JsonProperty("soldPrice")
    private Double soldPrice;

    @JsonProperty("description")
    private String description;

    @JsonProperty("itemColor")
    private String itemColor;

    @JsonProperty("image")
    private String image;

    @JsonProperty("processor")
    private String processor;

    @JsonProperty("operatingSystem")
    private String operatingSystem;

    @JsonProperty("screenSize")
    private String screenSize;

    @JsonProperty("status")
    private boolean status;

    @JsonProperty("gstRate")
    private String gstRate;

    @JsonProperty("discountType")
    private String discountType;

    @JsonProperty("serialNumber")
    private String serialNumber;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("ownerId")
    private int ownerId;

    @JsonProperty("vendorName")
    private String vendorName;

    @JsonProperty("vendorGstNumber")
    private String vendorGstNumber;

    @JsonProperty("additionalDetails")
    private String additionalDetails;
}