package com.rigel.app.service;

import java.util.List;

import com.rigel.app.model.VendorPayments;
import com.rigel.app.model.dto.VendorPaymentRequest;

public interface IVendorPaymentService {

	public VendorPayments saveVendor(VendorPayments vendorPayments);

	public VendorPayments update(VendorPayments vendorPayments);

}
