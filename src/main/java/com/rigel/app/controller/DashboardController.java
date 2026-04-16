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
	public ResponseEntity<Map<String, Object>> save(@RequestBody(required = true) @Valid DashboardRequest dashboardRequest,
			BindingResult result, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();
		Map<String, Object> dashboard = dashboardService.viewDashboard(dashboardRequest.getCycle().replace('"', ' ').replace('"', ' ').trim(),dashboardRequest.getUserId());
			data.put("dashboard", dashboard);
			response.put("data", data);
			response.put("status", "OK");
			response.put("code", "200");
			response.put("message", "Your records has been fech successfully.");
			return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
