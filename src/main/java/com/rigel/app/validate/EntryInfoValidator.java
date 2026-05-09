package com.rigel.app.validate;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rigel.app.exception.ValidationException;
import com.rigel.app.model.Inventory;
import com.rigel.app.model.Items;
import com.rigel.app.model.dto.SalesInfoDto;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.service.IInventoryService;
import com.rigel.app.util.Constaints;

@Service
public class EntryInfoValidator {

	public void validate(Items items) {

		if (items == null) {
			throw new ValidationException("Item info cannot be empty");
		}

		// ❌ Empty owner Id
		if (items.getOwnerId() == 0) {
			throw new ValidationException("Session Expired, Please Login agin then try....");
		}
	
	}

	public static String normalizeCode(String code) {
		if (code == null)
			return "";
		return code.replaceAll("\\u00A0", "").replaceAll("\\u200B", "").trim().toUpperCase();
	}
}