package com.rigel.app.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rigel.app.builder.BuyerInfoDTO;
import com.rigel.app.builder.BuyerInfoRepository;
import com.rigel.app.model.dto.SearchCriteria;

@Service
public class BuyerCommonService {

	@Autowired
	private BuyerInfoRepository buyerInfoRepository;
	
	public List<BuyerInfoDTO> getAllSalesRecord(SearchCriteria searchCriteria){
		return buyerInfoRepository.getBuyerSearch(searchCriteria);
	}
}
