package com.rigel.app.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Builder
@ToString
@Setter
@Getter
public class SalesRequest {

	private int userId;
	
	@JsonProperty("sales")
	private BuyerInfoDto buyerInfoDto;
		
}
