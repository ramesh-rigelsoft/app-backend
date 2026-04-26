package com.rigel.app.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder.Default;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name="SALES_INFO")
//@ToString(exclude = "buyerInfo")
public class SalesInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7016888389920150869L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(length = 36, updatable = false, nullable = false)
	private String id;
	
	@Column(name = "item_code")
	private String itemCode;

	@Column(name = "category")
	private String category;

	@Column(name = "category_type")
	private String categoryType;

	@Column(name = "measure_type")
	private String measureType;

	@Column(name = "brand")
	private String brand;

	@Column(name = "model_name")
	private String modelName;

	@Column(name = "item_condition")
	private String itemCondition;

	@Column(name = "item_source")
	private String itemSource;

	@Column(name = "ram")
	private String ram;

	@Column(name = "ram_unit")
	private String ramUnit;

	@Column(name = "storage")
	private String storage;

	@Column(name = "storage_type")
	private String storageType;

	@Column(name = "storage_unit")
	private String storageUnit;

	@Column(name = "quantity")
	private Integer quantity;

	@Column(name = "initial_price")
	private Double initialPrice;

	@Column(name = "selling_price")
	private Double sellingPrice;

	@Column(name = "sold_price")
	private Double soldPrice;

	@Column(name = "description", length = 1000)
	private String description;

	@Column(name = "item_color")
	private String itemColor;

	@Column(name = "image")
	private String image;

	@Column(name = "processor")
	private String processor;

	@Column(name = "operating_system")
	private String operatingSystem;

	@Column(name = "screen_size")
	private String screenSize;

	@Column(name = "item_gen")
	private String itemGen;

	@Column(name = "status")
	private int status;
	
	@Column(name = "gst_rate")
	private String gstRate;
	
	@Column(name = "serial_number")
	private String serialNumber;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "owner_id")
	private int ownerId;
	
	@ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	@JoinColumn(name="buyerInfo")
	private BuyerInfo buyerInfo;
		
}
