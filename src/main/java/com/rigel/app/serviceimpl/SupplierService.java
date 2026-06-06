package com.rigel.app.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.rigel.app.dao.ISupplierDao;
import com.rigel.app.exception.ValidationException;
import com.rigel.app.model.Items;
import com.rigel.app.model.Vendors;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.model.dto.SupplierCreteria;
import com.rigel.app.model.dto.VendorPaymentResponseDTO;
import com.rigel.app.model.dto.VendorsDTO;
import com.rigel.app.service.ISupplierService;

@Lazy 
@Service
public class SupplierService implements ISupplierService {

	@Autowired
	ISupplierDao supplierDao;
	
	@Override
	public Vendors saveSupplier(VendorsDTO dto) {
		if (dto.getOwnerId() < 1) {
			throw new ValidationException("Session Expired, Please Login again then try....");
		}
		return supplierDao.saveSupplier(dto);
	}

	@Override
	public Vendors updateSupplier(VendorsDTO expense) {
		if (expense.getOwnerId() < 1) {
			throw new ValidationException("Session Expired, Please Login again then try....");
		}
		return supplierDao.updateSupplier(expense);
	}

	@Override
	public List<Vendors> searchSupplier(SupplierCreteria creteria) {
		return supplierDao.searchSupplier(creteria);
	}

	@Override
	public List<Vendors> searchVender(SearchCriteria criteria) {
		return supplierDao.searchVender(criteria);
	}

	@Override
	public List<VendorPaymentResponseDTO> searchVenderPayment(SearchCriteria criteria){
		return supplierDao.searchVenderPayment(criteria);
	}

}
