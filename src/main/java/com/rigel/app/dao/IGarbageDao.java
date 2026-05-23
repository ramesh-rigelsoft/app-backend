package com.rigel.app.dao;

import java.util.List;

import com.rigel.app.model.Expense;
import com.rigel.app.model.GarbageItemsInfo;
import com.rigel.app.model.dto.ExpenseCreteria;

public interface IGarbageDao {

	public GarbageItemsInfo saveGarbage(GarbageItemsInfo garbage);
	public GarbageItemsInfo updateGarbage(GarbageItemsInfo garbage);
	public GarbageItemsInfo findGarbageByItemCode(String itemCode);
}
