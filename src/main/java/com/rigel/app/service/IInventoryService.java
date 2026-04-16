package com.rigel.app.service;

import java.util.List;

import com.rigel.app.model.Inventory;
import com.rigel.app.model.dto.SearchCriteria;

import jakarta.persistence.EntityManager;

public interface IInventoryService {

	public Inventory saveInventory(Inventory inventory,String itemCode,int ownerId,EntityManager entityManager);

	public Inventory getInventryByItemCode(String itemCode,int qty,int ownerId, EntityManager entityManager);
	
	public Inventory updateInventory(Inventory inventory);

	public int deleteInventory(String itemCode,int ownerId);

	public List<Inventory> searchInventory(SearchCriteria criteria);

}
