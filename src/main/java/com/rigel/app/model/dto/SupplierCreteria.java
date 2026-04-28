package com.rigel.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierCreteria {
	
	private int startIndex;
    private int maxRecords;    
	private String status;
	private String supplierName;
	private String gstNumber;
	private String pan;
	private int userId;

}
