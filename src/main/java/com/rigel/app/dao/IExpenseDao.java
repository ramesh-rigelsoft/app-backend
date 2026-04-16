package com.rigel.app.dao;

import java.util.List;

import com.rigel.app.model.Expense;
import com.rigel.app.model.dto.ExpenseCreteria;

public interface IExpenseDao {

	public Expense saveExpense(Expense expense);

	public Expense updateExpense(Expense expense);

	public List<Expense> searchExpense(ExpenseCreteria creteria);

}
