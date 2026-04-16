package com.rigel.app.model.dto;

import java.net.http.HttpResponse.ResponseInfo;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class SalesResponse {

	@JsonProperty("responseInfo")
	private ResponseInfo responseInfo;

	@JsonProperty("buyers")
	private List<BuyerInfoDto> buyerInfoDto;

}
