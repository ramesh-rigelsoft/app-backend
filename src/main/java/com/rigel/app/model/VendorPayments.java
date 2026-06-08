package com.rigel.app.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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


@Getter
@Setter
@Entity
@Table(name = "VENDORS_PAYMENTS")
@ToString
public class VendorPayments{

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(length = 36, updatable = false, nullable = false)
	private String id;

	@Transient
	private String vendorId;
	
	private String gstNumber;
	private String vendorInvoiceNumber;
	private BigDecimal paidAmount;
	private LocalDateTime createdAt;
	private LocalDateTime updateAt;
	private int ownerId;
	private String officeBranch;
	
	private String paymentModes;
	private String comments;
	
	@ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	@JoinColumn(name="vendors")
	@JsonBackReference
	private Vendors vendors;
    
		
}
