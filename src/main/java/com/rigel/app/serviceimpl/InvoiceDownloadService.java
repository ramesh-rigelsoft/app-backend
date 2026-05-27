package com.rigel.app.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


import com.rigel.app.service.ISalesService;
import com.rigel.app.util.SalesSlipPDF;
import com.rigel.app.builder.BuyerInfoDTO;
import com.rigel.app.builder.BuyerInfoRepository;
import com.rigel.app.dao.IUserDao;
import com.rigel.app.model.User;
import com.rigel.app.model.dto.SearchCriteria;

@Lazy 
@Service
public class InvoiceDownloadService  {
	
	@Autowired
	ISalesService salesService;

	@Autowired
	IUserDao userDao;
	
	@Autowired
	private BuyerInfoRepository buyerInfoRepository;
	
	public void download(String invoiceNumber,int ownerId) {
		List<BuyerInfoDTO> buyerInfo=buyerInfoRepository.getBuyerSearch(SearchCriteria.builder().invoiceNumber(invoiceNumber).userId(ownerId).startIndex(0).maxRecords(10).build());
		User user=userDao.findUserById(ownerId);
//		SalesSlipPDF.createSlip(invoiceNumber, user, null, null);
	}
}