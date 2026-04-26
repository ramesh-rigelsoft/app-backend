package com.rigel.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rigel.app.exception.BadGatewayRequest;
import com.rigel.app.model.Items;
import com.rigel.app.model.dto.ItemsDTO;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.service.IItemsService;
import com.rigel.app.serviceimpl.BarcodeService;
import com.rigel.app.util.UploadFileUtlity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/items/")
public class ItemsController {

	@Autowired
	IItemsService itemsService;
	
	@Autowired
	BarcodeService barcodeService;
	
	@Autowired
	ObjectMapper mapper;


	@PostMapping(value = "save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Map<String, Object>> signup(@ModelAttribute @Valid ItemsDTO itemDto, BindingResult result,
				HttpServletRequest request) {
		
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();
		if (itemDto == null) {
			throw new BadGatewayRequest("Invalid Request");
		} else if (result.hasFieldErrors()) {
			throw new BadGatewayRequest(result.getFieldError().getDefaultMessage());
		} else {
			System.out.println("ssss-"+itemDto);
			String image=itemDto.getImage()==null?null:UploadFileUtlity.uploadImageNfiles(itemDto.getImage(),"product",null);			
			Items items=mapper.convertValue(itemDto, Items.class);
			items.setImage(image);
			Items itemsDetails = itemsService.saveItems(items);
			barcodeService.barcodeGenerate(itemsDetails.getItemCode(), itemsDetails.getQuantity());
			data.put("items", itemsDetails);
			response.put("data", data);
			response.put("status", "CREATED");
			response.put("code", "201");
			response.put("message", "Your records has been created successfully.");
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		}
	}
	
	
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
			List<Items> itemsDetails = itemsService.searchItems(criteria);
			data.put("items", itemsDetails);
			response.put("data", data);
			response.put("status", "OK");
			response.put("code", "200");
			response.put("message", "Your records has been fetch successfully.");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
}
