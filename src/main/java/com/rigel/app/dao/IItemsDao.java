package com.rigel.app.dao;

import java.util.List;

import com.rigel.app.model.Items;
import com.rigel.app.model.dto.SearchCriteria;

public interface IItemsDao {

    public Items saveItems(Items items);
	
    public Items updateItems(Items items);
	
	public int deleteItems(List<Long> itemsId,int ownerId);
	
	public List<Items> searchItems(SearchCriteria criteria);

}
