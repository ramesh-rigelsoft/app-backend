package com.rigel.app.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rigel.app.model.BuyerInfo;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.User;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.service.ILoginInfoService;
import com.rigel.app.service.IPrintService;
import com.rigel.app.service.ISalesService;
import com.rigel.app.util.SalesSlipPDF;

@Lazy 
@Service
public class PrintService implements IPrintService {
    
	@Autowired
	private ISalesService salesService;
	
	@Autowired
	private ILoginInfoService loginInfoService;
	
	@Autowired
	private ObjectMapper mapper;

	@Override
	public boolean billPrint(String invoiceNumber,int ownerId,String username) {
		SearchCriteria searchCriteria = SearchCriteria.builder().startIndex(0).maxRecords(100).isdownload(false).invoiceNumber(invoiceNumber).userId(ownerId).build();
		System.out.println("searchCriteria---"+searchCriteria.toString());
		List<SalesInfo> item=salesService.searchSalesInfo(searchCriteria);
		System.out.println("item-------------"+item.toString());
		BuyerInfo buyer=item.get(0).getBuyerInfo();
		String userObject=loginInfoService.findLoginActivityByUsername(username).getUserObject();
		System.out.println("userObject-------------"+userObject);
		
		try {
			User user = mapper.readValue(userObject, User.class);
			
			SalesSlipPDF.createSlip("bill_"+buyer.getInvoiceNumber(),user,buyer,item);
			return true;
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean barCodePrint(String invoiceNumber) {
		// TODO Auto-generated method stub
		return false;
	}

}
