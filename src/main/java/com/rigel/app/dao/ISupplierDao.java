package com.rigel.app.dao;

import java.util.List;

import org.springframework.data.domain.Page;

import com.rigel.app.model.Items;
import com.rigel.app.model.Vendors;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.model.dto.SupplierCreteria;
import com.rigel.app.model.dto.VendorPaymentResponseDTO;
import com.rigel.app.model.dto.VendorsDTO;

public interface ISupplierDao {
	
    public Vendors saveSupplier(VendorsDTO dto); 
	
	public Vendors updateSupplier(VendorsDTO expense);
	
	public List<Vendors> searchSupplier(SupplierCreteria creteria);
	
	public Vendors findById(String id); 
	
	public List<Vendors> searchVender(SearchCriteria criteria);
	
	public List<VendorPaymentResponseDTO> searchVenderPayment(SearchCriteria criteria);
	
}
