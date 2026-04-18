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
public class ExpenseCreteria {
	private int startIndex;
	private int maxRecords;
	private int year;
	private int month;
	private int userId;
	private String scope;
	private String type;

}
