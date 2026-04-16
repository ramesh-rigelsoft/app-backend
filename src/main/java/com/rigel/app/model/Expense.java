package com.rigel.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.Builder.Default;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "EXPENSE")
public class Expense implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    @NotBlank(message = "Type is required")
    private String type;   // Travel, Food, Salary etc.

    @NotBlank(message = "Scope is required")
    private String scope;  // Personal / Business

    @Column(length = 500)
    private String description;

    @NotNull(message = "Amount is required")
    private Double amount;

    private String proof;  // file path / image URL

    @NotNull(message = "Date is required")
    private LocalDateTime date;

    private int ownerId; // reference to the user/owner
    private LocalDateTime createdAt;

}
