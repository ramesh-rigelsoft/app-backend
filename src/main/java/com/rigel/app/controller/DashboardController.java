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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rigel.app.exception.BadGatewayRequest;
import com.rigel.app.model.Items;
import com.rigel.app.model.dto.DashboardRequest;
import com.rigel.app.model.dto.ExpenseDTO;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.service.IDashboardService;
import com.rigel.app.service.IItemsService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/dashboard/")
public class DashboardController {

	@Autowired
	IDashboardService dashboardService;

	@PostMapping("view")
	public ResponseEntity<Map<String, Object>> save(
	        @RequestBody(required = true) @Valid DashboardRequest dashboardRequest,
	        BindingResult result,
	        HttpServletRequest request) {

	    Map<String, Object> response = new HashMap<>();

	    // 🔴 Validation check
	    if (result.hasErrors()) {

	        Map<String, String> errors = new HashMap<>();
	        result.getFieldErrors().forEach(error ->
	                errors.put(error.getField(), error.getDefaultMessage())
	        );

	        response.put("status", "ERROR");
	        response.put("code", "400");
	        response.put("message", "Validation failed");
	        response.put("errors", errors);

	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }

	    // ✅ If valid request
	    Map<String, Object> data = new HashMap<>();
	    Map<String, Object> dashboard = dashboardService.viewDashboard(dashboardRequest);

	    data.put("dashboard", dashboard);
	    response.put("data", data);
	    response.put("status", "OK");
	    response.put("code", "200");
	    response.put("message", "Your records have been fetched successfully.");

	    return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
