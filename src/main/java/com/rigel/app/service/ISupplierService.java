package com.rigel.app.service;

import java.util.List;

import com.rigel.app.model.Expense;
import com.rigel.app.model.Vendors;
import com.rigel.app.model.dto.ExpenseCreteria;
import com.rigel.app.model.dto.ExpenseDTO;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.model.dto.SupplierCreteria;
import com.rigel.app.model.dto.VendorsDTO;

public interface ISupplierService {
	
    public Vendors saveSupplier(VendorsDTO dto); 
	
	public Vendors updateSupplier(VendorsDTO dto);
	
	public List<Vendors> searchSupplier(SupplierCreteria creteria);
	
	public List<Vendors> searchVender(SearchCriteria criteria);

}
