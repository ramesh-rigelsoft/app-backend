package com.rigel.app.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


import com.rigel.app.service.ISalesService;
import com.rigel.app.util.SalesSlipPDF;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	
	@Autowired
	private ObjectMapper mapper;
	
	public void download(String invoiceNumber,int ownerId) {
		List<BuyerInfoDTO> buyerInfo=buyerInfoRepository.getBuyerSearch(SearchCriteria.builder().invoiceNumber(invoiceNumber).userId(ownerId).startIndex(0).maxRecords(10).build());

		String userObj = userDao.findLoginActivityByOwnerId(ownerId).getUserObject();
		User user=null;
		try {
			user = mapper.readValue(userObj, User.class);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//		SalesSlipPDF.createSlip(invoiceNumber, user, null, null);
	}
}