package com.rigel.app.dao;

import java.util.List;

import com.rigel.app.model.dto.BinRequestCriteria;

public interface IBinDao {
	
	public int deletItems(String itemCode, int ownerId,int type);

	public int restoreItems(String itemCode, int ownerId,int type);
		
	public Object fetchDeletedItems(String itemCode, int ownerId,int type);
	
	public <T> List<T> binItemsList(BinRequestCriteria criteria);

}
