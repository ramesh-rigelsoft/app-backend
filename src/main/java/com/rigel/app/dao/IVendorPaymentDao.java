package com.rigel.app.dao;

import java.util.List;

import com.rigel.app.model.VendorPayments;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.model.dto.VendorPaymentRequest;

public interface IVendorPaymentDao {
	
	public VendorPayments saveVendor(VendorPayments vendorPayments);
	public VendorPayments update(VendorPayments vendorPayments);
	public List<Object> vendorList(SearchCriteria criteria);
	
}
