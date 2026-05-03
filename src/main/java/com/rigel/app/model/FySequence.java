package com.rigel.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "FY_SEQUENCE")
public class FySequence {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;    
    
    @Column(name = "fy_year")
    private String fyYear;
    
    @Column(name = "fy_month")
    private int fyMonth;

    @Column(name = "last_number")
    private String lastNumber;

    @Column(name = "seqName")
    private String seqName;
    
   
    @Column(name = "userid")
    private int userId;
   
    @Column(name = "seqCode")//, unique = true)
    private String seqCode;
    
//    public FySequence() {}
//
//    public FySequence(String fyYear,int fyMonth, int lastNumber,int userId,String numberFormateName) {
//        this.fyYear = fyYear;
//        this.fyMonth = fyMonth;
//        this.lastNumber = lastNumber;
//        this.userId = userId;
//        this.numberFormateName=numberFormateName;
//    }

    // getters & setters
}
