package com.rigel.app.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rigel.app.exception.BadGatewayRequest;
import com.rigel.app.model.dto.GenerateBillRequest;
import com.rigel.app.service.IPrintService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/print/")
public class PrintController {

	@Autowired
	private IPrintService printService;

	@PostMapping("bill")
	public ResponseEntity<Map<String, Object>> save(@RequestBody(required = true) @Valid GenerateBillRequest billRequest,
			BindingResult result, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		if (billRequest == null) {
			throw new BadGatewayRequest("Invalid Request");
		} else if (result.hasFieldErrors()) {
			throw new BadGatewayRequest(result.getFieldError().getDefaultMessage());
		} else {
			printService.billPrint(billRequest.getInvoiceNumber(), billRequest.getOwnerId(), billRequest.getUsername());
			response.put("data", data);
			response.put("status", "CREATED");
			response.put("code", "201");
			response.put("message", "Your bill has been created successfully.");
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		}
	}
}

