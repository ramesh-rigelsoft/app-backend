package com.rigel.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "FY_SEQUENCE")
public class FySequence {

    @Id
    @Column(name = "fy_year")
    private String fyYear;
    
    @Column(name = "fy_month")
    private int fyMonth;

    @Column(name = "last_number")
    private int lastNumber;

    @Column(name = "userid")
    private int userId;

    @Column(name = "numberFormateName")
    private String numberFormateName;
    
    public FySequence() {}

    public FySequence(String fyYear,int fyMonth, int lastNumber,int userId,String numberFormateName) {
        this.fyYear = fyYear;
        this.fyMonth = fyMonth;
        this.lastNumber = lastNumber;
        this.userId = userId;
        this.numberFormateName=numberFormateName;
    }

    // getters & setters
}
