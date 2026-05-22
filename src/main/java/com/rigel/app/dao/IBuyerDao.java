package com.rigel.app.dao;

import java.time.LocalDateTime;
import java.util.List;

import com.rigel.app.model.BuyerInfo;
import com.rigel.app.model.dto.BuyerInfoDto;
import com.rigel.app.model.dto.SearchCriteria;

public interface IBuyerDao {

    public BuyerInfo saveBuyerInfo(BuyerInfo buyerInfo);
	
    public BuyerInfo updateBuyerInfo(BuyerInfo buyerInfo);
	
	public int deleteBuyerInfo(List<Long> buyerId);
	
	
	
	public List<BuyerInfo> searchBuyerInfo(SearchCriteria criteria);
	public List<BuyerInfoDto> searchSalesInfoDto(SearchCriteria criteria);
	public int updateRestAmountAndDate(String id, String restAmount, LocalDateTime restAmountDate);

}
