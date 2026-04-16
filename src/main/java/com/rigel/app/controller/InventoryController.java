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

import com.rigel.app.exception.BadGatewayRequest;
import com.rigel.app.model.Inventory;
import com.rigel.app.model.Items;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.service.IInventoryService;
import com.rigel.app.service.IItemsService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inventory/")
public class InventoryController {

	@Autowired
	IInventoryService inventoryService;

		
	
	@PostMapping("search")
	public ResponseEntity<Map<String, Object>> search(@RequestBody(required = true) SearchCriteria criteria,
			BindingResult result, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		if (criteria == null) {
			throw new BadGatewayRequest("Invalid Request");
		} else if (result.hasFieldErrors()) {
			throw new BadGatewayRequest(result.getFieldError().getDefaultMessage());
		} else {
			List<Inventory> itemsDetails = inventoryService.searchInventory(criteria);
			data.put("items", itemsDetails);
			response.put("data", data);
			response.put("status", "OK");
			response.put("code", "200");
			response.put("message", "Your records has been fetch successfully.");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
}
