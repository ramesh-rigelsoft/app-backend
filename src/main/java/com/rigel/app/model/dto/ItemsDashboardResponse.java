package com.rigel.app.model.dto;

import java.util.List;
import java.util.stream.Stream;

import com.rigel.app.model.Items;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemsDashboardResponse {

    private double totalPurchase;

    private long totalItems;

    private long totalVendors;

    private List<VendorPerformanceDTO>
            vendorPerformance;

    private List<Items> itemStream;
}