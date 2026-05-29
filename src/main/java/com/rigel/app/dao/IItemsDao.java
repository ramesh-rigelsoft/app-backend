package com.rigel.app.dao;

import java.util.List;

import com.rigel.app.model.Items;
import com.rigel.app.model.dto.ItemsDashboardResponse;
import com.rigel.app.model.dto.SearchCriteria;

public interface IItemsDao {

    public Items saveItems(Items items,boolean isUpdate);
	
    public Items updateItems(Items items);
	
	public int deleteItems(Items items);
	
	public List<Items> searchItems(SearchCriteria criteria);
	
	public ItemsDashboardResponse fetchItemReportData(SearchCriteria criteria);

}
