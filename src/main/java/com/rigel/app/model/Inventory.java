package com.rigel.app.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@Entity
@Table(name = "INVENTORY")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Inventory implements Serializable {

    private static final long serialVersionUID = -7016888389920150869L;

    @Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(length = 36, updatable = false, nullable = false)
	private String id;

    @Column(unique = true, nullable = false)
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
    private Double initialPrice;
    private Double sellingPrice;
    private String itemColor;
    private String processor;
    private String operatingSystem;
    private String screenSize;
    private String itemGen;
    private String gstRate;
    private int ownerId;
    private String description;
    
    @Column( name = "finger_print",unique = true,nullable = false,insertable = true,updatable = false)
    private String fingerPrint;

    private Integer quantity;
    private String image;
    @Column(name = "created_at")
	private LocalDateTime createdAt;

}
