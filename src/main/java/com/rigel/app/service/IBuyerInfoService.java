package com.rigel.app.service;

import java.util.List;

import com.rigel.app.model.BuyerInfo;
import com.rigel.app.model.dto.BuyerInfoDto;
import com.rigel.app.model.dto.SalesRequest;
import com.rigel.app.model.dto.SalesResponse;
import com.rigel.app.model.dto.SearchCriteria;

public interface IBuyerInfoService {

	public SalesResponse saveBuyerInfo(SalesRequest salesRequest);

	public int updateBuyerInfo(SalesRequest salesRequest);

	public SalesResponse searchBuyerInfo(SearchCriteria criteria);

}
