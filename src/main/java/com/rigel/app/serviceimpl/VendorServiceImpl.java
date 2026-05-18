package com.rigel.app.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rigel.app.dao.ISupplierDao;
import com.rigel.app.dao.IVendorPaymentDao;
import com.rigel.app.model.VendorPayments;
import com.rigel.app.model.Vendors;
import com.rigel.app.model.dto.VendorPaymentRequest;
import com.rigel.app.service.IVendorPaymentService;

@Service
public class VendorServiceImpl implements IVendorPaymentService {

	@Autowired
	private IVendorPaymentDao vendorPaymentDao;
	
	@Autowired
	private ISupplierDao supplierDao;

	@Override
	public VendorPayments saveVendor(VendorPayments vendorPayments) {
		Vendors vendors=supplierDao.findById(vendorPayments.getVendorId());
		vendorPayments.setVendors(vendors);
		vendorPayments.setCreatedAt(LocalDateTime.now());
		return vendorPaymentDao.saveVendor(vendorPayments);
	}

	@Override
	public VendorPayments update(VendorPayments vendorPayments) {
		return vendorPaymentDao.update(vendorPayments);
	}


}
