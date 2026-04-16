package com.rigel.app.model.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SupplierDTO {

    private String id;

    @NotBlank(message = "Supplier name is required")
    private String supplierName;

    private String gstNumber;

    private String panNumber;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String phone;
    
    private String district;
    
    private String pinCode;
    
    private String status;

    private String address;
    
    private int ownerId;
    
}
