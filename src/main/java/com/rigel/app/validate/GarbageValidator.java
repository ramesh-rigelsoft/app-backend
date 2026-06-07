package com.rigel.app.validate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rigel.app.exception.ValidationException;
import com.rigel.app.model.Inventory;
import com.rigel.app.model.Items;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.serviceimpl.InventoryService;
import com.rigel.app.util.Constaints;

@Service
public class GarbageValidator {
	
	@Autowired
	InventoryService inventoryService;

	public void validate(SearchCriteria criteria,Inventory inventory) {

		if (criteria == null||inventory==null) {
			throw new ValidationException("Invalid Request");
		}
		
		if (inventory.getCategory().equalsIgnoreCase(Constaints.SHOP_OWNER_CATEGORY)) {
			throw new ValidationException("Invalid Request");
		}

		// ❌ Empty owner Id
		if (criteria.getUserId() < 1) {
			throw new ValidationException("Session Expired, Please Login agin then try....");
		}
		if(criteria.getQuantity()<=inventory.getQuantity()){
			if(criteria.getQuantity()==inventory.getQuantity()) {
				inventory.setStatus(false);
				inventory.setQuantity(0);
			    inventoryService.updateInventory(inventory);   
		    }else {
		    	inventory.setStatus(true);
				inventory.setQuantity(inventory.getQuantity()-criteria.getQuantity());
			    inventoryService.updateInventory(inventory);   
		    }
		}else {
			throw new ValidationException("Entered quantity is more than available stock");
		}
		
		
	}

	
}