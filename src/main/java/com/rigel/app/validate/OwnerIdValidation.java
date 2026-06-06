package com.rigel.app.validate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rigel.app.exception.ValidationException;
import com.rigel.app.model.Inventory;
import com.rigel.app.model.Items;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.dto.SalesInfoDto;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.service.IInventoryService;
import com.rigel.app.util.Constaints;

@Service
public class OwnerIdValidation {

	public void validate(int ownerId) {

		// ❌ Empty owner Id
		if (ownerId < 1) {
			throw new ValidationException("Session Expired, Please Login agin then try....");
		}
	
	}
	
	public void orderedCheckStockValidation(Inventory inventory , SalesInfo salesInfo) {
	    if (inventory.getQuantity()<salesInfo.getQuantity()) {
	        throw new ValidationException("This item is Out of Stock.");
	    }
	}
	
	public void orderedDateValidation(SalesInfo salesInfo, int warrantyPeriodInMonth) {
		LocalDateTime orderDate=salesInfo.getBuyerInfo().getCreatedAt();
		
	    LocalDateTime warrantyEndDate = orderDate.plusMonths(warrantyPeriodInMonth);
	    LocalDateTime currentDateTime = LocalDateTime.now();

	    if (currentDateTime.isAfter(warrantyEndDate)) {
	        throw new ValidationException("This item is out of warranty period");
	    }
	}
	
	public void orderReturnValidation(SalesInfo salesInfo, int returnPeriodInDays) {
		System.out.println("salesInfo.isReturnStatus()------------"+salesInfo.isReturnStatus());
		LocalDateTime orderDate=salesInfo.getBuyerInfo().getCreatedAt();
		if (returnPeriodInDays <= 0) {
	        throw new ValidationException("You can't Replace this Item.");
	    }

	    LocalDateTime returnEndDate = orderDate.plusDays(returnPeriodInDays);
	    LocalDateTime currentDateTime = LocalDateTime.now();

	    if (currentDateTime.isAfter(returnEndDate)) {
	        throw new ValidationException("Return period has expired for this item");
	    }
	    if(salesInfo.isReturnStatus()) {
	        throw new ValidationException("This Item already returned");
	    }
	   
	}
}