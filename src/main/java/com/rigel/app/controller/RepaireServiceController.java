package com.rigel.app.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rigel.app.dao.ISalesDao;
import com.rigel.app.exception.BadGatewayRequest;
import com.rigel.app.model.Items;
import com.rigel.app.model.RepaireDevice;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.dto.RepaireDeviceDto;
import com.rigel.app.model.dto.RepaireRequest;
import com.rigel.app.model.dto.SalesInfoDto;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.service.IRepaireServiceService;
import com.rigel.app.service.ISalesService;
import com.rigel.app.serviceimpl.BarcodeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/repaire/")
public class RepaireServiceController {

	@Autowired
	IRepaireServiceService repaireServiceService;

	@Autowired
	BarcodeService barcodeService;

	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	ISalesService salesService;

	@PostMapping("save")
	public ResponseEntity<Map<String, Object>> save(@RequestBody(required = true) @Valid RepaireRequest repaireRequest,
			BindingResult result, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		if (repaireRequest == null) {
			throw new BadGatewayRequest("Invalid Request");
		} else if (result.hasFieldErrors()) {
			throw new BadGatewayRequest(result.getFieldError().getDefaultMessage());
		} else {
			Set<SalesInfoDto> salesItem = repaireRequest.getRepaireDeviceDto().getItems().stream().map(item -> { item.setId(null); return item; }).collect(Collectors.toSet());
			Set<SalesInfo> sales = mapper.convertValue(salesItem,new TypeReference<Set<SalesInfo>>(){});
			RepaireDevice electronicDevice = mapper.convertValue(repaireRequest.getRepaireDeviceDto(),RepaireDevice.class);
			electronicDevice.setSalesInfo(sales);
			RepaireDevice repaireDevice = repaireServiceService.saveRepair(electronicDevice);
			data.put("device", repaireDevice);
			response.put("data", data);
			response.put("status", "CREATED");
			response.put("code", "201");
			response.put("message", "Your records has been created successfully.");
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		}
	}
	
	@PostMapping("search")
	public ResponseEntity<Map<String, Object>> search(@RequestBody(required = true) @Valid SearchCriteria criteria,
			BindingResult result, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		if (criteria == null) {
			throw new BadGatewayRequest("Invalid Request");
		} else if (result.hasFieldErrors()) {
			throw new BadGatewayRequest(result.getFieldError().getDefaultMessage());
		} else {
			List<RepaireDevice> repaireDeviceList = repaireServiceService.searchRepair(criteria);
			data.put("devices", repaireDeviceList);
			response.put("data", data);
			response.put("status", "Success");
			response.put("code", "200");
			response.put("message", "Your records has been fetch successfully.");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
	
	@PostMapping("update")
	public ResponseEntity<Map<String, Object>> update(@RequestBody(required = true) @Valid RepaireRequest repaireRequest,
			BindingResult result, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		if (repaireRequest == null) {
			throw new BadGatewayRequest("Invalid Request");
		} else if (result.hasFieldErrors()) {
			throw new BadGatewayRequest(result.getFieldError().getDefaultMessage());
		} else {
			SearchCriteria criteria=SearchCriteria.builder().id(repaireRequest.getRepaireDeviceDto().getId()).userId(repaireRequest.getRepaireDeviceDto().getOwnerId()).startIndex(0).maxRecords(10).build();
			RepaireDevice repaireDevice = repaireServiceService.searchRepair(criteria).stream().findFirst().orElse(null);
//			System.out.println(repaireDevice+", repaireRequest.isStatusUpdate()----"+criteria.toString());
			if(repaireRequest.isStatusUpdate()) {
				RepaireDeviceDto repaireDeviceDto=repaireRequest.getRepaireDeviceDto();
				if(repaireDeviceDto.getDeviceStatus().equalsIgnoreCase("DELIVERED")) {
				   double rest=repaireDevice.getTotalAmount()-repaireDevice.getAdvanceAmount();
				   repaireDevice.setDeliveredDate(LocalDateTime.now());
				   repaireDevice.setRestAmount(String.valueOf(rest));
				   double pendingAmount=0.0;
				   repaireDevice.setPendingAmount(pendingAmount);
				}
				
				repaireDevice.setRestAmount(repaireDeviceDto.getRestAmount());
				repaireDevice.setUpdatedAt(LocalDateTime.now());
				repaireDevice.setDeviceStatus(repaireDeviceDto.getDeviceStatus());
    			repaireDevice=repaireServiceService.updateStatus(repaireDevice);
			}else {
				RepaireDeviceDto repaireDeviceDto=repaireRequest.getRepaireDeviceDto();
				Set<SalesInfoDto> salesItem = repaireRequest.getRepaireDeviceDto().getItems().stream().map(item -> { item.setId(null); return item; }).collect(Collectors.toSet());
				Set<SalesInfo> sales = mapper.convertValue(salesItem,new TypeReference<Set<SalesInfo>>(){});
				repaireDevice.setCustomerName(repaireDeviceDto.getCustomerName());
				repaireDevice.setMobileNumber(repaireDeviceDto.getMobileNumber());
				repaireDevice.setDeviceModelName(repaireDeviceDto.getDeviceModelName());
				repaireDevice.setCategory(repaireDeviceDto.getCategory());
				repaireDevice.setCategoryType(repaireDeviceDto.getCategoryType());
				repaireDevice.setDeliveryDate(repaireDeviceDto.getDeliveryDate());
				repaireDevice.setSerialNumber(repaireDeviceDto.getSerialNumber());
				repaireDevice.setDeviceStatus(repaireDeviceDto.getDeviceStatus());
				repaireDevice.setDefectDescription(repaireDeviceDto.getDefectDescription());
				repaireDevice.setDeviceLockType(repaireDeviceDto.getDeviceLockType());
				repaireDevice.setDevicePassword(repaireDeviceDto.getDevicePassword());
				repaireDevice.setTotalAmount(repaireDeviceDto.getTotalAmount());
				repaireDevice.setAdvanceAmount(repaireDeviceDto.getAdvanceAmount());
				repaireDevice.setPendingAmount(repaireDeviceDto.getPendingAmount());
				repaireDevice.setUpdatedAt(LocalDateTime.now());
				repaireDevice.setSalesInfo(sales);
				repaireDevice = repaireServiceService.updateRepaire(repaireDevice);
			}
			data.put("devices", repaireDevice);
			response.put("data", data);
			response.put("status", "Success");
			response.put("code", "200");
			response.put("message", "Your records has been updated successfully.");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
	
	@PostMapping("delete")
	public ResponseEntity<Map<String, Object>> delete(
	        @RequestBody @Valid List<SalesInfoDto> salesInfoDto,
	        BindingResult result,
	        HttpServletRequest request) {

	    Map<String, Object> response = new HashMap<>();
	    Map<String, Object> data = new HashMap<>();

	    if (salesInfoDto == null || salesInfoDto.isEmpty()) {
	        throw new BadGatewayRequest("Invalid Request");
	    } else if (result.hasFieldErrors()) {
	        throw new BadGatewayRequest(result.getFieldError().getDefaultMessage());
	    } else {
	        // Convert List<SalesInfoDto> → List<SalesInfo>
	        List<SalesInfo> sales = mapper.convertValue(
	                salesInfoDto,
	                new com.fasterxml.jackson.core.type.TypeReference<List<SalesInfo>>() {}
	        );

	        // Call service method
	        int deletedCount = salesService.deleteById(sales,
	                sales.get(0).getOwnerId()
	        );

	        data.put("deletedCount", deletedCount);
	        response.put("data", data);
	        response.put("status", "Success");
	        response.put("code", "200");
	        response.put("message", "Your records have been deleted successfully.");

	        return new ResponseEntity<>(response, HttpStatus.OK);
	    }
	}
}