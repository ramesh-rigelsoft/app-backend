package com.rigel.app.model.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ItemSalesCompare {

    private String name;     // Item name
    private int qty;         // Total quantity

    // Current Sales
    private int day;         // Current daily sold quantity
    private int month;       // Current monthly sold quantity
    private int year;        // Current yearly sold quantity

    // Previous Sales
    private int previousDay;     // Previous day sold quantity
    private int previousMonth;   // Previous month sold quantity
    private int previousYear;    // Previous year sold quantity

//    private double selling;  // Total selling amount
    private LocalDate date;     // Reference date
    private String cycle;
    
    @Override
    public String toString() {
        return "ItemSalesCompare{" +
                "name='" + name + '\'' +
                ", qty=" + qty +
                ", day=" + day +
                ", month=" + month +
                ", year=" + year +
                ", previousDay=" + previousDay +
                ", previousMonth=" + previousMonth +
                ", previousYear=" + previousYear +
                ", cycle=" + cycle +
                ", date='" + date + '\'' +
                '}';
    }
}
