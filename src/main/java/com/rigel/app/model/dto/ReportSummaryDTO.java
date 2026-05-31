package com.rigel.app.model.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReportSummaryDTO {

    private Long totalSoldCount;
    private BigDecimal totalInitialPrice;
    private BigDecimal totalSellingPrice;
    private BigDecimal totalSoldPrice;
    private BigDecimal totalPaidAmount;
    private BigDecimal pendingAmount;
    
    List<SalesInfoDtoResponseList> salesList;
}