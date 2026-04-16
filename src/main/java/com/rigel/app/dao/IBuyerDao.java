package com.rigel.app.dao;

import java.util.List;

import com.rigel.app.model.BuyerInfo;
import com.rigel.app.model.dto.SearchCriteria;

public interface IBuyerDao {

    public BuyerInfo saveBuyerInfo(BuyerInfo buyerInfo);
	
    public BuyerInfo updateBuyerInfo(BuyerInfo buyerInfo);
	
	public int deleteBuyerInfo(List<Long> buyerId);
	
	
	
	public List<BuyerInfo> searchBuyerInfo(SearchCriteria criteria);

}
