package com.rigel.app.serviceimpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.rigel.app.dao.IExpenseDao;
import com.rigel.app.model.Expense;
import com.rigel.app.model.dto.ExpenseCreteria;
import com.rigel.app.model.dto.ExpenseDTO;
import com.rigel.app.service.IExpenseService;
import com.rigel.app.util.RAUtility;
import com.rigel.app.util.UploadFileUtlity;


@Lazy 
@Service
public class ExpenseService implements IExpenseService{
	
	@Autowired
	private IExpenseDao expenseDao;

	@Override
	public Expense saveExpense(ExpenseDTO dto) {
		return expenseDao.saveExpense(convertToEntity(dto));
	}

	@Override
	public Expense updateExpense(Expense expense) {
		return expenseDao.updateExpense(expense);
	}

	@Override
	public List<Expense> searchExpense(ExpenseCreteria creteria) {
		return expenseDao.searchExpense(creteria);
	}
	
	public Expense convertToEntity(ExpenseDTO dto) {
		String filName=UploadFileUtlity.uploadImageNfiles(dto.getProof(),"expense",null);
		
	    return Expense.builder()
	            // Skip ID → DB will generate
	            .type(dto.getType())
	            .scope(dto.getScope())
	            .description(dto.getDescription())
	            .amount(dto.getAmount())
	            .proof(filName)
	            .date(RAUtility.epochToLocalDateTime(Long.valueOf(dto.getDate())))
	            .ownerId(dto.getOwnerId())
	            .createdAt(LocalDateTime.now())
//	            .status("active");
	            // Skip createdAt → default set in entity
	            .build();
	}


}
