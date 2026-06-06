package com.rigel.app.controller;

import java.time.Duration;
import java.time.LocalDateTime;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rigel.app.exception.BadGatewayRequest;
import com.rigel.app.model.Notification;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.service.INotificationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notification/")
public class NotificationController {

	@Autowired
	private INotificationService notificationService;
	
	@PostMapping("search")
	public ResponseEntity<Map<String, Object>> search(@RequestBody(required = true) @Valid SearchCriteria serCriteria,
			BindingResult result, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		if (serCriteria == null) {
			throw new BadGatewayRequest("Invalid Request");
		} else if (result.hasFieldErrors()) {
			throw new BadGatewayRequest(result.getFieldError().getDefaultMessage());
		} else {
			List<Notification> notificationResponse = notificationService.findNotification(serCriteria)
			    .stream()
			    .map(n -> {

			        LocalDateTime createdAt = !n.isSeenStatus()?n.getCreatedAt():n.getSeenAt();
			        LocalDateTime now = LocalDateTime.now();

			        Duration duration = Duration.between(createdAt, now);

			        long seconds = duration.getSeconds();
			        long minutes = duration.toMinutes();
			        long hours = duration.toHours();
			        long days = duration.toDays();

			        String timeAgo;

			        if (seconds < 60) {
			            timeAgo = seconds + " sec ago";
			        } else if (minutes < 60) {
			            timeAgo = minutes + " min ago";
			        } else if (hours < 24) {
			            timeAgo = hours + " hr ago";
			        } else {
			            timeAgo = days + " days ago";
			        }

			        n.setTime(timeAgo);

			        return n;
			    })
			    .toList();
			data.put("notificationList", notificationResponse);
			response.put("data", data);
			response.put("status", "SUCCESS");
			response.put("code", "200");
			response.put("message", "Your records has been fetch successfully.");
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		}
	}
	
	@PostMapping("update")
	public ResponseEntity<Map<String, Object>> seenUpdate(@RequestBody(required = true) @Valid SearchCriteria serCriteria,
			BindingResult result, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		if (serCriteria == null) {
			throw new BadGatewayRequest("Invalid Request");
		} else if (result.hasFieldErrors()) {
			throw new BadGatewayRequest(result.getFieldError().getDefaultMessage());
		} else {
			int notificationCount = notificationService.notificationUpdate(serCriteria);
			data.put("notificationCount", notificationCount);
			response.put("data", data);
			response.put("status", "SUCCESS");
			response.put("code", "200");
			response.put("message", "Your records has been fetch successfully.");
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		}
	}
	
	@PostMapping("count")
	public ResponseEntity<Map<String, Object>> count(@RequestBody(required = true) @Valid SearchCriteria serCriteria,
			BindingResult result, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		if (serCriteria == null) {
			throw new BadGatewayRequest("Invalid Request");
		} else if (result.hasFieldErrors()) {
			throw new BadGatewayRequest(result.getFieldError().getDefaultMessage());
		} else {
			int notificationCount = notificationService.notificationCount(serCriteria);
			int unSeenNotificationCount = notificationService.unSeenNotificationCount(serCriteria);
			data.put("notificationCount", notificationCount);
			data.put("unSeenNotificationCount", unSeenNotificationCount);
			response.put("data", data);
			response.put("status", "SUCCESS");
			response.put("code", "200");
			response.put("message", "Your records has been fetch successfully.");
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		}
	}
}
