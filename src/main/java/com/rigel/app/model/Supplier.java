package com.rigel.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.Builder.Default;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "SUPPLIER")
public class Supplier implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    @NotBlank(message = "Supplier name is required")
    @Column(nullable = false)
    private String supplierName;

    @NotBlank(message = "GST name is required")
    @Column(nullable = false, unique = true)
    private String gstNumber;

//    @Column(length = 10)
    private String panNumber;
    
//    @Column(length = 6)
    private String pinCode;

    @Email(message = "Invalid email format")
//    @Column(length = 100)
    private String email;

//    @Pattern(regexp = "^[0-9]{12}$", message = "Phone must be 10 digits")
//    @Column(length = 10)
    private String phone;
    
    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address can't exceed 500 characters")
    @Column(length = 500, nullable = false)
    private String address;
 
    private String status;
    
    private String district;
    
    private int ownerId; // optional if multi-user system

    private LocalDateTime createdAt;
    }

