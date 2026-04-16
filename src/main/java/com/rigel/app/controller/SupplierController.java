package com.rigel.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rigel.app.exception.BadGatewayRequest;
import com.rigel.app.model.Expense;
import com.rigel.app.model.Supplier;
import com.rigel.app.model.dto.ExpenseCreteria;
import com.rigel.app.model.dto.ExpenseDTO;
import com.rigel.app.model.dto.SupplierCreteria;
import com.rigel.app.model.dto.SupplierDTO;
import com.rigel.app.service.IExpenseService;
import com.rigel.app.service.ISupplierService;
import com.rigel.app.util.RAUtility;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/supplier/")
public class SupplierController {

	@Autowired
	ISupplierService supplierService;

	@Autowired
	ObjectMapper objMapper;

	@PostMapping("save")
	public ResponseEntity<Map<String, Object>> save(@RequestBody(required = true) @Valid SupplierDTO dtoRequest,
			BindingResult result, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		if (dtoRequest == null) {
			throw new BadGatewayRequest("Invalid Request");
		} else if (result.hasFieldErrors()) {
			throw new BadGatewayRequest(result.getFieldError().getDefaultMessage());
		} else {
			List<Supplier> suppliers=supplierService.searchSupplier(SupplierCreteria.builder().gst(dtoRequest.getGstNumber()).pan(dtoRequest.getPanNumber()).build());
			if(dtoRequest.getId()==null&&suppliers.size()>0) {
				throw new BadGatewayRequest("This Supplier Already Existing with Us.");
			}else {
				Supplier supplierRes = supplierService.saveSupplier(dtoRequest);
				data.put("supplier", supplierRes);
				response.put("data", data);
				response.put("status", "CREATED");
				response.put("code", "201");
				response.put("message", "Your records has been created successfully.");
				return new ResponseEntity<>(response, HttpStatus.CREATED);
			}
		}
	}
	
	@PostMapping("search")
	public ResponseEntity<Map<String, Object>> search(@RequestBody(required = true) SupplierCreteria creteria) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();
		List<Supplier> supplierList = supplierService.searchSupplier(creteria);
		data.put("supplier", supplierList);
		response.put("data", data);
		response.put("status", "OK");
		response.put("code", "200");
		response.put("message", "Your records has been fetch successfully.");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
