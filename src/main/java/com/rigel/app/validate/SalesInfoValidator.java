package com.rigel.app.validate;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rigel.app.exception.ValidationException;
import com.rigel.app.model.Inventory;
import com.rigel.app.model.dto.SalesInfoDto;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.service.IInventoryService;
import com.rigel.app.util.Constaints;

@Service
public class SalesInfoValidator {

	@Autowired
	private IInventoryService inventoryService;

	public void validate(List<SalesInfoDto> salesList, int ownerId) {

		if (salesList == null || salesList.isEmpty()) {
			throw new ValidationException("Sales list cannot be empty");
		}

		// 🔹 Collect unique itemCodes from request
		Set<String> requestCodes = salesList.stream()
				.map(salesInfo -> salesInfo.getItemCode() == null ? "" : salesInfo.getItemCode().trim())
				.filter(code -> !code.isEmpty()).collect(Collectors.toSet());

		SearchCriteria criteria = SearchCriteria.builder().userId(ownerId).startIndex(0).maxRecords(10)
				.itemCodes(requestCodes).build();
		Map<String, Inventory> inventoryMap = inventoryService.searchInventory(criteria).stream()
				.collect(Collectors.toMap(inv -> inv.getItemCode().trim(), inv -> inv));

		// 🔹 Check duplicates inside request
		Set<String> seenCodes = new HashSet<>();

		for (int i = 0; i < salesList.size(); i++) {

			SalesInfoDto salesInfo = salesList.get(i);
			String code = salesInfo.getItemCode() == null ? "" : salesInfo.getItemCode().trim();

			// ❌ Empty itemCode
			if (code.isEmpty()) {
				throw new ValidationException("Item code is required at row " + (i + 1));
			}

			// ❌ Duplicate in request
			code = normalizeCode(code).strip().toUpperCase();
//            System.out.println(seenCodes+"--------------"+code);
			if (!seenCodes.add(code)) {
				throw new ValidationException("Duplicate itemCode at row " + (i + 1) + ": " + code);
			}

			Inventory inventory = inventoryMap.get(code);

			// ❌ Not found in DB
			if (inventory == null) {
				throw new ValidationException("Invalid itemCode at row " + (i + 1) + ": " + code);
			}

			// ❌ Quantity check
			if (salesInfo.getQuantity() == null || salesInfo.getQuantity() <= 0) {
				throw new ValidationException("Quantity must be greater than 0 at row " + (i + 1));
			}

			// ❌ Quantity exceeds inventory
			if (salesInfo.getQuantity() > inventory.getQuantity()) {
				throw new ValidationException("Quantity exceeds available stock at row " + (i + 1));
			}
			if (!salesInfo.getCategory().equalsIgnoreCase(Constaints.SHOP_OWNER_CATEGORY)) {
				// ❌ Selling price < purchase price
				if (salesInfo.getSoldPrice() == null || salesInfo.getSoldPrice() < inventory.getInitialPrice()) {
					throw new ValidationException("Selling price must be grater then purchase price at row " + (i + 1));
				}
			}
		}
	}

	public static String normalizeCode(String code) {
		if (code == null)
			return "";
		return code.replaceAll("\\u00A0", "").replaceAll("\\u200B", "").trim().toUpperCase();
	}
}