package com.rigel.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rigel.app.exception.BadGatewayRequest;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.VendorPayments;
import com.rigel.app.model.dto.VendorPaymentRequest;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.service.IVendorPaymentService;

@RestController
@RequestMapping("/api/vendor/payment/")
public class VendorPaymentController {

	@Autowired
	private IVendorPaymentService vendorPaymentService;
	
	@Autowired
	private ObjectMapper mapper; 
	
	@PostMapping("save")
	public ResponseEntity<Map<String, Object>> save(@RequestBody(required = true) VendorPaymentRequest vendorRequest) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		if (vendorRequest == null) {
			throw new BadGatewayRequest("Invalid Request");
		} else {
			VendorPayments vendorPayments= mapper.convertValue(vendorRequest.getVendorRequest(), VendorPayments.class);
			vendorPayments = vendorPaymentService.saveVendor(vendorPayments);
			data.put("vendorPayment", vendorPayments);
			response.put("data", data);
			response.put("status", "CREATED");
			response.put("code", "201");
			response.put("message", "Your tranaction have been created successfully.");
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		}
	}
	
	@PostMapping("update")
	public ResponseEntity<Map<String, Object>> update(@RequestBody(required = true) VendorPaymentRequest vendorRequest) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		if (vendorRequest == null) {
			throw new BadGatewayRequest("Invalid Request");
		} else {
			VendorPayments vendorPayments= mapper.convertValue(vendorRequest.getVendorRequest(), VendorPayments.class);
			vendorPayments = vendorPaymentService.saveVendor(vendorPayments);
			data.put("vendorPayment", vendorPayments);
			response.put("data", data);
			response.put("status", "CREATED");
			response.put("code", "201");
			response.put("message", "Your records have been created successfully.");
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		}
	}
	
}
