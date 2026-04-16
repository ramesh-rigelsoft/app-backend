package com.rigel.app.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "INVOICE")
public class Invoice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

    @Column(name = "inv_date")
    private LocalDate invDate;
    
    @Column(name = "formate")
    private String formate;
    
    @Column(name = "formate_name")
    private String formateName;
    
    @Column(name = "last_number")
    private long lastNumber;

    @Column(name = "fy")
    private String fy;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LocalDate getInvDate() {
		return invDate;
	}

	public void setInvDate(LocalDate invDate) {
		this.invDate = invDate;
	}

	public String getFormate() {
		return formate;
	}

	public void setFormate(String formate) {
		this.formate = formate;
	}

	public String getFormateName() {
		return formateName;
	}

	public void setFormateName(String formateName) {
		this.formateName = formateName;
	}

	public long getLastNumber() {
		return lastNumber;
	}

	public void setLastNumber(long lastNumber) {
		this.lastNumber = lastNumber;
	}

	public String getFy() {
		return fy;
	}

	public void setFy(String fy) {
		this.fy = fy;
	}  
    
}
