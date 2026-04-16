package com.rigel.app.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rigel.app.exception.BadGatewayRequest;
import com.rigel.app.model.Expense;
import com.rigel.app.model.dto.ExpenseCreteria;
import com.rigel.app.model.dto.ExpenseDTO;
import com.rigel.app.model.dto.ExpenseRequest;
import com.rigel.app.service.IExpenseService;
import com.rigel.app.util.RAUtility;
import com.rigel.app.util.UploadFileUtlity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/expense/")
public class ExpenseController {

	@Autowired
	IExpenseService expenseService;
	

	@Autowired
	ObjectMapper objMapper;

	@PostMapping(value = "save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Map<String, Object>> save(@ModelAttribute @Valid ExpenseDTO expenseDto,
			BindingResult result, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		if (expenseDto == null) {
			throw new BadGatewayRequest("Invalid Request");
		} else if (result.hasFieldErrors()) {
			throw new BadGatewayRequest(result.getFieldError().getDefaultMessage());
		} else {
			Expense expenseResponse = expenseService.saveExpense(expenseDto);
			data.put("expense", expenseResponse);
			response.put("data", data);
			response.put("status", "CREATED");
			response.put("code", "201");
			response.put("message", "Your records has been created successfully.");
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		}
	}
	
	@PostMapping("search")
	public ResponseEntity<Map<String, Object>> search(@RequestBody(required = true) @Valid ExpenseCreteria creteria,
			BindingResult result, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		if (creteria == null) {
			throw new BadGatewayRequest("Invalid Request");
		} else if (result.hasFieldErrors()) {
			throw new BadGatewayRequest(result.getFieldError().getDefaultMessage());
		} else {
			List<Expense> expenseResponse = expenseService.searchExpense(creteria);
			data.put("expenses", expenseResponse);
			response.put("data", data);
			response.put("status", "OK");
			response.put("code", "200");
			response.put("message", "Your records has been fetch successfully.");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
}
