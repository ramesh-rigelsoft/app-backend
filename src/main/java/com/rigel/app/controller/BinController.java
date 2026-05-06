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

import com.rigel.app.exception.BadGatewayRequest;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.dto.BinRequestCriteria;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.service.IBinService;

@RestController
@RequestMapping("/api/bin/")
public class BinController {

	@Autowired
	private IBinService binService;
	
	@PostMapping("traced")
	public ResponseEntity<Map<String, Object>> deleteItem(@RequestBody(required = true) BinRequestCriteria criteria) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		if (criteria == null) {
			throw new BadGatewayRequest("Invalid Request");
		} else {
			int status = binService.deletItems(criteria.getItemCode(),criteria.getUserId(),criteria.getType());
			data.put("resStatus", status);
			response.put("data", data);
			response.put("status", "CREATED");
			response.put("code", "201");
			response.put("message", "Your records have been traced successfully.");
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		}
	}
	
	@PostMapping("delete")
	public ResponseEntity<Map<String, Object>> deleteItemPermanent(@RequestBody(required = true) BinRequestCriteria criteria) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		if (criteria == null) {
			throw new BadGatewayRequest("Invalid Request");
		} else {
			int status = binService.deletItems(criteria.getItemCode(),criteria.getUserId(),criteria.getType());
			data.put("resStatus", status);
			response.put("data", data);
			response.put("status", "CREATED");
			response.put("code", "201");
			response.put("message", "Your records have been traced successfully.");
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		}
	}
	
	@PostMapping("restored")
	public ResponseEntity<Map<String, Object>> restored(@RequestBody(required = true) BinRequestCriteria criteria) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		if (criteria == null) {
			throw new BadGatewayRequest("Invalid Request");
		} else {
			int status = binService.restoreItems(criteria.getItemCode(),criteria.getUserId(),criteria.getType());
			data.put("resStatus", status);
			response.put("data", data);
			response.put("status", "CREATED");
			response.put("code", "201");
			response.put("message", "Your records has been restored successfully.");
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		}
	}
	
	@PostMapping("list")
	public ResponseEntity<Map<String, Object>> binList(@RequestBody(required = true) BinRequestCriteria criteria) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		if (criteria == null) {
			throw new BadGatewayRequest("Invalid Request");
		} else {
			Object status = binService.binItemsList(criteria);
			data.put("resStatus", status);
			response.put("data", data);
			response.put("status", "CREATED");
			response.put("code", "201");
			response.put("message", "Your records has been restored successfully.");
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		}
	}
}
