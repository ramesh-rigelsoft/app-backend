package com.rigel.app.service;

import java.util.List;

import com.rigel.app.model.Items;
import com.rigel.app.model.dto.SearchCriteria;

public interface IItemsService {

	public Items saveItems(Items items,boolean isUpdate);

	public Items updateItems(Items items);

	public int deleteItems(Items items);

	public List<Items> searchItems(SearchCriteria criteria);

}
