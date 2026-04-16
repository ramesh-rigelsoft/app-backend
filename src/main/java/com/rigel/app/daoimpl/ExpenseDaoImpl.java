package com.rigel.app.daoimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.rigel.app.dao.IExpenseDao;
import com.rigel.app.model.Expense;
import com.rigel.app.model.dto.ExpenseCreteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import java.time.LocalDate;


@Repository
@Transactional
public class ExpenseDaoImpl implements IExpenseDao {
	
	@Autowired
	EntityManager entityManager;

	@Override
	public Expense saveExpense(Expense expense) {
		return entityManager.merge(expense);
	}

	@Override
	public Expense updateExpense(Expense expense) {
		return entityManager.merge(expense);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Expense> searchExpense(ExpenseCreteria creteria) {

	    StringBuilder jpql = new StringBuilder("FROM Expense e WHERE ");

	    if (creteria.getUserId() != 0) {
	        jpql.append(" e.ownerId = :ownerId");
	    }

	    if (creteria.getScope() != null && !creteria.getScope().isEmpty()) {
	        jpql.append(" AND e.scope = :scope");
	    }

	    if (creteria.getType() != null && !creteria.getType().isEmpty()) {
	        jpql.append(" AND e.type = :type");
	    }

	    int year = (creteria.getYear() != 0) ? creteria.getYear() : LocalDate.now().getYear();
	    jpql.append(" AND YEAR(e.date) = :year");

	    if (creteria.getMonth() != 0) {
	        jpql.append(" AND MONTH(e.date) = :month");
	    }

	    Query query = entityManager.createQuery(jpql.toString(), Expense.class);

	    if (creteria.getUserId() != 0) {
	        query.setParameter("ownerId", creteria.getUserId());
	    }

	    if (creteria.getScope() != null && !creteria.getScope().isEmpty()) {
	        query.setParameter("scope", creteria.getScope());
	    }

	    if (creteria.getType() != null && !creteria.getType().isEmpty()) {
	        query.setParameter("type", creteria.getType());
	    }

	    query.setParameter("year", year);

	    if (creteria.getMonth() != 0) {
	        query.setParameter("month", creteria.getMonth());
	    }

	    return query.getResultList();
	}





}
