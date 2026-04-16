package com.rigel.app.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@ToString
@Setter
@Getter
public class ExpenseRequest {
	
	private int userId;
	
	private MultipartFile file;
	
	private ExpenseDTO expenseDTO;
		
}
