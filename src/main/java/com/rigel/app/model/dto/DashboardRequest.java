package com.rigel.app.model.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardRequest {

	private int userId; 
	private String cycle;
	
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String searchKeyWords;
	
}
