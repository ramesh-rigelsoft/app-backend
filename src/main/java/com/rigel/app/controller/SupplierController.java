package com.rigel.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.rigel.app.model.Vendors;
import com.rigel.app.model.dto.ExpenseCreteria;
import com.rigel.app.model.dto.ExpenseDTO;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.model.dto.SupplierCreteria;
import com.rigel.app.model.dto.VendorInvoiceResponse;
import com.rigel.app.model.dto.VendorPaymentResponseDTO;
import com.rigel.app.model.dto.VendorsDTO;
import com.rigel.app.service.IExpenseService;
import com.rigel.app.service.ISupplierService;
import com.rigel.app.serviceimpl.GSTNumberService;
import com.rigel.app.util.AppUtill;
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

	@Autowired
	GSTNumberService gstNumberService;

	@PostMapping("save")
	public ResponseEntity<Map<String, Object>> save(@RequestBody(required = true) @Valid VendorsDTO dtoRequest,
			BindingResult result, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		if (dtoRequest == null) {
			throw new BadGatewayRequest("Invalid Request");
		} else if (result.hasFieldErrors()) {
			throw new BadGatewayRequest(result.getFieldError().getDefaultMessage());
		} else {
			SupplierCreteria creteria = SupplierCreteria.builder().userId(dtoRequest.getOwnerId()).maxRecords(100)
					.gstNumber(dtoRequest.getGstNumber()).build();

			System.out.println("creteria---" + creteria.toString());
			List<Vendors> suppliers = supplierService.searchSupplier(creteria);
			System.out.println("supplier-------" + suppliers.size());
			System.out.println("dtoRequest=-----" + dtoRequest.toString());
			if (dtoRequest.getId() == null && suppliers.size() > 0) {
				throw new BadGatewayRequest("This vendor already existing with Us.");
			} else {
				Vendors supplierRes = supplierService.saveSupplier(dtoRequest);
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
		List<Vendors> supplierList = supplierService.searchSupplier(creteria);
		data.put("supplier", supplierList);
		response.put("data", data);
		response.put("status", "OK");
		response.put("code", "200");
		response.put("message", "Your records has been fetch successfully.");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("searchGst")
	public ResponseEntity<Map<String, Object>> searchGst(@RequestBody SupplierCreteria creteria) {

		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		gstNumberService.openGstBrowser(creteria.getGstNumber(), creteria.getUserId());

		response.put("data", data);
		response.put("status", "OK");
		response.put("code", "200");
		response.put("message", "Fetched successfully");

		return ResponseEntity.ok(response);
	}
	
	@PostMapping("vendors")
	public ResponseEntity<Map<String, Object>> getVendersLatest(@RequestBody(required = true) SearchCriteria creteria) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();
		List<VendorPaymentResponseDTO> supplierList = supplierService.searchVenderPayment(creteria);
//		List<VendorPaymentResponseDTO> arrayList=supplierList.getContent().stream().toList();
		data.put("vendors", supplierList);
		response.put("data", data);
		response.put("status", "OK");
		response.put("code", "200");
		response.put("message", "Your records has been fetch successfully.");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("vendorss")
	public ResponseEntity<Map<String, Object>> getVenders(@RequestBody(required = true) SearchCriteria creteria) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();
		List<Vendors> supplierList = supplierService.searchVender(creteria);
		List<VendorInvoiceResponse>  list=AppUtill.mapToInvoiceResponse(supplierList);
		data.put("vendors", list);
		response.put("data", data);
		response.put("status", "OK");
		response.put("code", "200");
		response.put("message", "Your records has been fetch successfully.");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}