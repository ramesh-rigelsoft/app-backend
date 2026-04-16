package com.rigel.app.dao;

import java.util.List;

import com.rigel.app.model.Inventory;
import com.rigel.app.model.Items;
import com.rigel.app.model.dto.SearchCriteria;

public interface IInventoryDao {
	
//    public Inventory saveInventory(Inventory inventory);
	
    public Inventory updateInventory(Inventory inventory);
	
	public int deleteInventory(String itemCode,int ownerId);
	
	public int updateInventory(String itemCode,int qty,int ownerId);
	
	public Inventory findInventoryByCode(String itemCode,int ownerId);
	
	public List<Inventory> searchInventory(SearchCriteria criteria);

}
