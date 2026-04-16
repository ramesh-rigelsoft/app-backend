package com.rigel.app.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.rigel.app.dao.ISupplierDao;
import com.rigel.app.model.Supplier;
import com.rigel.app.model.dto.SupplierCreteria;
import com.rigel.app.model.dto.SupplierDTO;
import com.rigel.app.service.ISupplierService;

@Lazy 
@Service
public class SupplierService implements ISupplierService {

	@Autowired
	ISupplierDao supplierDao;
	
	@Override
	public Supplier saveSupplier(SupplierDTO dto) {
		return supplierDao.saveSupplier(dto);
	}

	@Override
	public Supplier updateSupplier(SupplierDTO expense) {
		return supplierDao.updateSupplier(expense);
	}

	@Override
	public List<Supplier> searchSupplier(SupplierCreteria creteria) {
		return supplierDao.searchSupplier(creteria);
	}

}
