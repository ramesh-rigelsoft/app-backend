package com.rigel.app.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import jakarta.persistence.*;

import jakarta.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "EMPLOYEE")
@Getter
@Setter
@ToString
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    // ================= PRIMARY KEY =================

	@Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    // ================= BASIC INFO =================

    private String name;
    private String email;
    private String mobileNo;
    private String gender;
    private long dateOfBirth;

    // ================= EMPLOYMENT INFO =================

    @Column(unique = true)
    private String employeeCode;

    private String designation;
    private String department;

    private long joiningDate;
    private long resignationDate;

    private String employmentType; // Full-Time, Contract
    private String workMode;       // WFO, WFH, Hybrid

    private Double salary;
    private int status; // 1-active, 2-inactive, 3-resigned

    private long createdAt;

    // ================= DOCUMENT INFO =================

    // Aadhaar
    @Pattern(
        regexp = "^[2-9]{1}[0-9]{11}$",
        message = "Enter valid Aadhaar number"
    )
    private String aadharCardNumber;

    @Column(name = "aadhar_doc")
    private String aadharDocumentPath; // PDF / image path or URL

    // PAN
    @Pattern(
        regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}",
        message = "Enter valid PAN number"
    )
    private String panCardNumber;

    @Column(name = "pan_doc")
    private String panCardDocumentPath;

    // Resignation
    @Column(name = "resignation_letter")
    private String resignationLetterPath;

    private String resignationReason;

 
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
