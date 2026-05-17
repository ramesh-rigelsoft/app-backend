package com.rigel.app.dao;

import java.util.List;

import com.rigel.app.model.Expense;
import com.rigel.app.model.RepaireDevice;
import com.rigel.app.model.dto.ExpenseCreteria;
import com.rigel.app.model.dto.SearchCriteria;

public interface IRepaireServiceDao {

	public RepaireDevice saveRepair(RepaireDevice expaDevice);

	public RepaireDevice updateRepaire(RepaireDevice expaDevice);

	public List<RepaireDevice> searchRepair(SearchCriteria expaDevice);

}
