package com.rigel.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GenerateBillRequest {
	
	private int type;
	private int ownerId;
	private String username;
	private String invoiceNumber;
	
		
}
