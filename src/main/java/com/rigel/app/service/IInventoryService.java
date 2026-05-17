package com.rigel.app.service;

import java.util.List;

import com.rigel.app.model.Inventory;
import com.rigel.app.model.dto.SearchCriteria;

import jakarta.persistence.EntityManager;

public interface IInventoryService {

	public Inventory saveInventory(Inventory inventory,EntityManager entityManager,boolean isUpdate);

	public Inventory getInventryByItemCode(String itemCode,int qty,int ownerId, EntityManager entityManager);
	
	public Inventory updateInventory(Inventory inventory);

	public int deleteInventory(String itemCode,int ownerId,String entryType);

	public List<Inventory> searchInventory(SearchCriteria criteria);

}
