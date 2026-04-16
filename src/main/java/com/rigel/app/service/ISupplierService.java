package com.rigel.app.service;

import java.util.List;

import com.rigel.app.model.Expense;
import com.rigel.app.model.Supplier;
import com.rigel.app.model.dto.ExpenseCreteria;
import com.rigel.app.model.dto.ExpenseDTO;
import com.rigel.app.model.dto.SupplierCreteria;
import com.rigel.app.model.dto.SupplierDTO;

public interface ISupplierService {
	
    public Supplier saveSupplier(SupplierDTO dto); 
	
	public Supplier updateSupplier(SupplierDTO dto);
	
	public List<Supplier> searchSupplier(SupplierCreteria creteria);

}
