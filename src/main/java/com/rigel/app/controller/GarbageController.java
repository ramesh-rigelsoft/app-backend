package com.rigel.app.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.rigel.app.model.GarbageItemsInfo;
import com.rigel.app.model.Vendors;
import com.rigel.app.model.dto.ExpenseCreteria;
import com.rigel.app.model.dto.ExpenseDTO;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.model.dto.SupplierCreteria;
import com.rigel.app.model.dto.VendorInvoiceResponse;
import com.rigel.app.model.dto.VendorsDTO;
import com.rigel.app.service.IExpenseService;
import com.rigel.app.service.IGarbageService;
import com.rigel.app.service.ISupplierService;
import com.rigel.app.serviceimpl.GSTNumberService;
import com.rigel.app.util.AppUtill;
import com.rigel.app.util.Constaints;
import com.rigel.app.util.RAUtility;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/garbage/")
public class GarbageController {

	@Autowired
	ISupplierService supplierService;

	@Autowired
	ObjectMapper objMapper;

	@Autowired
	IGarbageService garbageService;

	
	@PostMapping("list")
	public ResponseEntity<Map<String, Object>> getVenders(@RequestBody(required = true) SearchCriteria creteria) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();
		List<Vendors> supplierList = supplierService.searchVender(creteria);
		data.put("vendors", supplierList);
		response.put("data", data);
		response.put("status", "OK");
		response.put("code", "200");
		response.put("message", "Your records has been fetch successfully.");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("update")
	public ResponseEntity<Map<String, Object>> updateStatus(@RequestBody SearchCriteria criteria) {

	    Map<String, Object> response = new HashMap<>();

	    if (criteria == null || criteria.getItemCodes() == null || criteria.getItemCodes().isEmpty()) {
	        response.put("status", "FAILED");
	        response.put("code", "400");
	        response.put("message", "itemCodes list is required");
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }

	    Set<String> itemCodes = criteria.getItemCodes();
	    List<GarbageItemsInfo> items = new ArrayList<>();
	    itemCodes.forEach(code -> {
	        GarbageItemsInfo item = garbageService.findGarbageByItemCode(code);
	        if (item != null) {
	            item.setGarbageStatus(Constaints.GARBAGE_RETURN);
	            item.setUpdatedAt(LocalDateTime.now());
	            items.add(item);
	            garbageService.saveGarbage(item);
	        }
	    });
	    
	    response.put("status", "OK");
	    response.put("code", "200");
	    response.put("updatedCount", items.size());
	    response.put("message", "Your records have been updated successfully.");
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}
}