package com.rigel.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rigel.app.builder.BuyerInfoDTO;
import com.rigel.app.exception.BadGatewayRequest;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.dto.BuyerInfoDto;
import com.rigel.app.model.dto.RequrnReplaceRequest;
import com.rigel.app.model.dto.SalesRequest;
import com.rigel.app.model.dto.SalesResponse;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.service.IBuyerInfoService;
import com.rigel.app.service.ISalesService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sales/")
public class SalesController {

	@Autowired
	ISalesService salesService;


	@Autowired
	IBuyerInfoService buyerService;
	
	@Autowired
	com.rigel.app.serviceimpl.BuyerCommonService buyerCommonService;
	
	@PostMapping("search")
	public ResponseEntity<Map<String, Object>> save(@RequestBody(required = true) SearchCriteria searchCriteria) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		if (searchCriteria == null) {
			throw new BadGatewayRequest("Invalid Request");
		} else {
//			List<BuyerInfoDto> salesResponse = buyerService.searchSalesInfoDto(searchCriteria);
//			List<SalesInfo> salesResponse = salesService.searchSalesInfo(searchCriteria);
//			System.out.println("salesResponse----------"+salesResponse.size());
			List<BuyerInfoDTO> salesResponse=buyerCommonService.getAllSalesRecord(searchCriteria);
			data.put("sales", salesResponse);
			response.put("data", data);
			response.put("status", "CREATED");
			response.put("code", "201");
			response.put("message", "Your records has been fetch successfully.");
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		}
	}
	
	@PostMapping("return")
	public ResponseEntity<Map<String, Object>> saveReturn(@RequestBody(required = true) @Valid  RequrnReplaceRequest rrRequest) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		if (rrRequest == null) {
			throw new BadGatewayRequest("Invalid Request");
		} else {
			salesService.returnSalesInfo(rrRequest.getReason(), rrRequest.getSalesId(), rrRequest.getUserId());
			response.put("data", data);
			response.put("status", "Success");
			response.put("code", "200");
			response.put("message", "Your records has been returned successfully.");
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		}
	}
	
	@PostMapping("replace")
	public ResponseEntity<Map<String, Object>> saveReplace(@RequestBody(required = true) @Valid  RequrnReplaceRequest rrRequest) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		if (rrRequest == null) {
			throw new BadGatewayRequest("Invalid Request");
		} else {
			salesService.replaceSalesInfo(rrRequest.getReason(), rrRequest.getSalesId(), rrRequest.getUserId());
			response.put("data", data);
			response.put("status", "Success");
			response.put("code", "200");
			response.put("message", "Your records has been replaced successfully.");
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		}
	}
	
	@PostMapping("invoice/download")
	public ResponseEntity<Map<String, Object>> downloadInvoice(@RequestBody(required = true) @Valid  RequrnReplaceRequest rrRequest) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		if (rrRequest == null) {
			throw new BadGatewayRequest("Invalid Request");
		} else {
//			BuyerInfoDTO salesResponse=buyerCommonService.getAllSalesRecord(SearchCriteria.builder().offset(0).limit(1).userId(rrRequest.getUserId()).invoiceNumber(rrRequest.getInvoiceNumber()).build()).stream().findFirst().orElse(null);
		
			response.put("data", data);
			response.put("status", "Success");
			response.put("code", "200");
			response.put("message", "Your invoice has been downloaded successfully.");
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		}
	}
}
