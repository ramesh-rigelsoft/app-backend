package com.rigel.app.model.dto;

import java.util.List;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequrnReplaceRequest {
	
	private String reason;
	private String salesId;
	private String invoiceNumber;
	private int userId;
}
