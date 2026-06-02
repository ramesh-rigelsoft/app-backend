package com.rigel.app.validate;

import org.springframework.stereotype.Service;
import com.rigel.app.exception.ValidationException;
import com.rigel.app.model.Items;

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
		
		// ❌ Empty invoice number
		if (!items.getCategory().equalsIgnoreCase("Shop Service")&&(items.getVendorInvoiceNumber() == null || items.getVendorInvoiceNumber().isBlank())) {
			throw new ValidationException("Please enter the invoice number.");
		}
	
	}

	public static String normalizeCode(String code) {
		if (code == null)
			return "";
		return code.replaceAll("\\u00A0", "").replaceAll("\\u200B", "").trim().toUpperCase();
	}
}