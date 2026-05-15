package com.rigel.app.dao;

import java.util.List;

import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.dto.SearchCriteria;

public interface ISalesDao {

	public List<SalesInfo> saveSalesInfo(List<SalesInfo> salesInfo);
	
    public SalesInfo updateSalesInfo(SalesInfo salesInfo);
	
	public SalesInfo findById(String Id,int ownerId);
	
	public List<SalesInfo> searchSalesInfo(SearchCriteria criteria);
	
}
