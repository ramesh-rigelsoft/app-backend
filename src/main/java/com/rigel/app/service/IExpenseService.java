package com.rigel.app.service;

import java.util.List;

import com.rigel.app.model.Expense;
import com.rigel.app.model.dto.ExpenseCreteria;
import com.rigel.app.model.dto.ExpenseDTO;

public interface IExpenseService {
	
	public Expense saveExpense(ExpenseDTO dto); 
	
	public Expense updateExpense(Expense expense);
	
	public List<Expense> searchExpense(ExpenseCreteria creteria);

}
