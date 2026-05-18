package com.rigel.app.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rigel.app.model.Items;
import com.rigel.app.model.VendorPayments;
import com.rigel.app.model.Vendors;
import com.rigel.app.model.dto.InvoiceDTO;
import com.rigel.app.model.dto.TransactionDTO;
import com.rigel.app.model.dto.VendorInvoiceResponse;

import jakarta.servlet.http.HttpServletRequest;

public class AppUtill {
	
	
	
	 private static final String[] IP_HEADERS = {
		        "X-Forwarded-For",
		        "Proxy-Client-IP",
		        "WL-Proxy-Client-IP",
		        "HTTP_X_FORWARDED_FOR",
		        "HTTP_X_FORWARDED",
		        "HTTP_X_CLUSTER_CLIENT_IP",
		        "HTTP_CLIENT_IP",
		        "HTTP_FORWARDED_FOR",
		        "HTTP_FORWARDED",
		        "HTTP_VIA",
		        "REMOTE_ADDR"

		        // you can add more matching headers here ...
		    };

	
	public static String getRequestIP(HttpServletRequest request) {
        for (String header: IP_HEADERS)  {
            String value = request.getHeader(header);
            if (value == null || value.isEmpty()) {
                continue;
            }
            String[] parts = value.split("\\s*,\\s*");
            return parts[0];
        }
        return request.getRemoteAddr();
    }
	
	public static String[] getAllRole() {
       String[] roles= {"user","userss"};      
       return roles;
    }
	
	public static String[] getUrlRole() {
	       String[] roles= {"/api/user/**","/api/todoTask/**"};
	        return roles;
	}
	
	public static String replaceAllSpace(String str) {
		if(str==null||str.isBlank())
		return null;
		str.replaceAll("\\s+", " ").strip();   
       return str;
    }
	
	
	public static List<VendorInvoiceResponse> mapToInvoiceResponse(List<Vendors> supplierList) {

	    List<VendorInvoiceResponse> responseList = new ArrayList<>();

	    for (Vendors vendor : supplierList) {

	        Map<String, InvoiceDTO> invoiceMap = new HashMap<>();

	        // 1. ITEMS → TOTAL
	        for (Items item : vendor.getItems()) {

	            String invoiceNo = item.getVendorInvoiceNumber();

	            double itemTotal = item.getQuantity() * item.getSellingPrice();

	            InvoiceDTO invoice = invoiceMap.getOrDefault(invoiceNo, new InvoiceDTO());

	            invoice.setInvoiceNumber(invoiceNo);
	            invoice.setTotalAmount(
	                invoice.getTotalAmount() + itemTotal
	            );

	            invoiceMap.put(invoiceNo, invoice);
	        }

	        // 2. PAYMENTS → PAID + TRANSACTIONS
	        for (VendorPayments pay : vendor.getVendorPayments()) {

	            String invoiceNo = pay.getVendorInvoiceNumber();

	            InvoiceDTO invoice = invoiceMap.get(invoiceNo);

	            if (invoice != null) {

	                invoice.setPaidAmount(
	                    invoice.getPaidAmount() + pay.getPaidAmount()
	                );

	                if (invoice.getTransactions() == null) {
	                    invoice.setTransactions(new ArrayList<>());
	                }

	                TransactionDTO tx = new TransactionDTO();
	                tx.setDate(pay.getCreatedAt());
	                tx.setAmount(pay.getPaidAmount());
	                tx.setStatus("PAID");

	                invoice.getTransactions().add(tx);
	            }
	        }

	        // 3. PENDING CALCULATION
	        for (InvoiceDTO inv : invoiceMap.values()) {
	            inv.setPendingAmount(
	                inv.getTotalAmount() - inv.getPaidAmount()
	            );
	        }

	        // 4. FINAL RESPONSE
	        VendorInvoiceResponse res = new VendorInvoiceResponse();
	        res.setVendorName(vendor.getSupplierName());
	        res.setId(vendor.getId());
	        res.setGstNumber(vendor.getGstNumber());
	        res.setInvoices(new ArrayList<>(invoiceMap.values()));

	        responseList.add(res);
	    }

	    return responseList;
	}
	
	
}
