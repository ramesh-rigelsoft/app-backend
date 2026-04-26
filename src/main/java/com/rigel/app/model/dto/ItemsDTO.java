package com.rigel.app.model.dto;


import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemsDTO {

    private Long id;

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

    private Integer quantity;
    private Double initialPrice;
    private Double sellingPrice;

//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private MultipartFile image;

    private Integer status;

    private Long createdAt;

    private int ownerId;
    
	private String gstRate;
	    
	private String description;
}
