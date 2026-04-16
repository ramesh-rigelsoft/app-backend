package com.rigel.app.dao;

import java.util.List;

import com.rigel.app.model.Supplier;
import com.rigel.app.model.dto.SupplierCreteria;
import com.rigel.app.model.dto.SupplierDTO;

public interface ISupplierDao {
	
    public Supplier saveSupplier(SupplierDTO dto); 
	
	public Supplier updateSupplier(SupplierDTO expense);
	
	public List<Supplier> searchSupplier(SupplierCreteria creteria);
	
}
