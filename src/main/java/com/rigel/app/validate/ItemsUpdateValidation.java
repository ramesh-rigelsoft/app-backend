package com.rigel.app.validate;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rigel.app.dao.IInventoryDao;
import com.rigel.app.exception.ValidationException;
import com.rigel.app.model.Inventory;
import com.rigel.app.model.Items;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.dto.SalesInfoDto;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.service.IInventoryService;
import com.rigel.app.service.IItemsService;

@Service
public class ItemsUpdateValidation {

	@Autowired
	private IInventoryDao inventoryDao;

	public void isValidForEditItems(Items items, Items existingItem) {

		LocalDateTime oneHourAfterCreation = existingItem.getCreatedAt().plusMinutes(20);

//		if (LocalDateTime.now().isAfter(oneHourAfterCreation)) {
//		    throw new ValidationException("You can delete or update item only under 20 minutes of creation.");
//		}

		if (items.getId() == null || existingItem == null || items.getItemCode() == null) {
			throw new ValidationException("Item can not be update or delete at this time.");
		}
		Inventory inventory = inventoryDao.searchInventory(SearchCriteria.builder().userId(items.getOwnerId())
				.startIndex(0).maxRecords(10).itemCode(items.getItemCode()).build()).stream().findFirst().orElse(null);

		if (inventory.getQuantity() > existingItem.getQuantity()) {
			int updatedQty = inventory.getQuantity() - existingItem.getQuantity();
			inventory.setQuantity(updatedQty);
			inventoryDao.updateInventory(inventory);
		} else if (inventory.getQuantity() == existingItem.getQuantity()) {
			int updatedQty = inventory.getQuantity() - existingItem.getQuantity();
			inventory.setQuantity(updatedQty);
			inventory.setStatus(false);
			inventoryDao.updateInventory(inventory);
		} else {
			throw new ValidationException("This items has been sold so you can not be delete or update.");
		}

	}
	public void repaireUpdateItemValidation(List<SalesInfoDto> sales) {
		sales.forEach(salesInfo -> {

			if (salesInfo.getItemCode() == null||salesInfo.getOwnerId()==0) {
				throw new ValidationException("Session Expired, Please Login agin then try....");
			}
			Inventory inventory = inventoryDao
					.searchInventory(SearchCriteria.builder().userId(salesInfo.getOwnerId()).startIndex(0)
							.maxRecords(10).itemCode(salesInfo.getItemCode()).build())
					.stream().findFirst().orElse(null);
            System.out.println("update-2");
			if (inventory.getQuantity() > salesInfo.getQuantity()) {
				int updatedQty = inventory.getQuantity() - salesInfo.getQuantity();
				inventory.setQuantity(updatedQty);
				inventoryDao.updateInventory(inventory);
			} else if (inventory.getQuantity() == salesInfo.getQuantity()) {
				int updatedQty = inventory.getQuantity() - salesInfo.getQuantity();
				inventory.setQuantity(updatedQty);
				inventory.setStatus(false);
				inventoryDao.updateInventory(inventory);
			} else {
				throw new ValidationException("This items has been sold.");
			}

		});
	}

	public void repaireItemValidation(List<SalesInfo> sales) {
		sales.forEach(salesInfo -> {

			if (salesInfo.getItemCode() == null||salesInfo.getOwnerId() < 1) {
				throw new ValidationException("Session Expired, Please Login agin then try....");
			}
			Inventory inventory = inventoryDao
					.searchInventory(SearchCriteria.builder().userId(salesInfo.getOwnerId()).startIndex(0)
							.maxRecords(10).itemCode(salesInfo.getItemCode()).build())
					.stream().findFirst().orElse(null);
            System.out.println("update-2");
			if (inventory.getQuantity() > salesInfo.getQuantity()) {
				int updatedQty = inventory.getQuantity() - salesInfo.getQuantity();
				inventory.setQuantity(updatedQty);
				inventoryDao.updateInventory(inventory);
			} else if (inventory.getQuantity() == salesInfo.getQuantity()) {
				int updatedQty = inventory.getQuantity() - salesInfo.getQuantity();
				inventory.setQuantity(updatedQty);
				inventory.setStatus(false);
				inventoryDao.updateInventory(inventory);
			} else {
				throw new ValidationException("This items has been sold.");
			}

		});
	}
	public void repaireDeleteItems(List<SalesInfo> sales) {
		sales.forEach(salesInfo -> {
			
			if (salesInfo.getOwnerId() < 1) {
				throw new ValidationException("Session Expired, Please Login again then try....");
			}

			if (salesInfo.getItemCode() == null) {
				throw new ValidationException("Item can not be update or delete at this time.");
			}
			Inventory inventory = inventoryDao
					.searchInventory(SearchCriteria.builder().userId(salesInfo.getOwnerId()).startIndex(0)
							.maxRecords(10).itemCode(salesInfo.getItemCode()).build())
					.stream().findFirst().orElse(null);
			int updatedQty = inventory.getQuantity() + salesInfo.getQuantity();
			inventory.setQuantity(updatedQty);
			inventory.setStatus(true);
			System.out.println("update-1");
			inventoryDao.updateInventory(inventory);
		});
	}
}
