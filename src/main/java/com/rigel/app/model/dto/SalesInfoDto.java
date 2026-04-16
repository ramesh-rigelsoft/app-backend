package com.rigel.app.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SalesInfoDto {

    @JsonProperty("id")
    private String id;


    private String itemCode;
    
    private String category;

    private String categoryType;

    private String measureType;

    private String brand;

    private String modelName;

    private String itemCondition;

    private String itemSource;

    private String ram;

    private String ramUnit;

    private String storage;

    private String storageType;

    private String storageUnit;

    private Integer quantity;

    private Double initialPrice;

    private Double sellingPrice;

    private String description;

    private String itemColor;

    private String image;

    private String processor;

    private String operatingSystem;

    private String screenSize;

    private String itemGen;
    
	private String gstRate;
    
    private int ownerId;

}
