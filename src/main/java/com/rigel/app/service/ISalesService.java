package com.rigel.app.service;

import java.util.List;

import com.rigel.app.model.Items;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.dto.SearchCriteria;

public interface ISalesService {

    public List<SalesInfo> saveSalesInfo(List<SalesInfo> salesInfo);
	
    public SalesInfo updateSalesInfo(SalesInfo salesInfo);
	
	public int deleteItems(List<Long> salesId,int ownerId);
	
	public List<SalesInfo> searchSalesInfo(SearchCriteria criteria);
}
