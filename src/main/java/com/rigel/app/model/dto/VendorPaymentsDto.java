package com.rigel.app.model.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder.Default;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;


@Setter
@Getter
@Entity
@Table(name="VENDOR_PAYMENT")
public class VendorPaymentsDto implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8660549112314714532L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(length = 36, updatable = false, nullable = false)
	private String id;
	
	@NotNull
	private String vendorId;
	private String gstNumber;
	private String vendorInvoiceNumber;
	private double paidAmount;
	private LocalDateTime createdAt;
	private LocalDateTime updateAt;
	private int ownerId;
	
//	@OneToMany(mappedBy="buyerInfo", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
//	@JsonIgnore
//	private Set<SalesInfo> salesInfo = new HashSet<>();
		
}
