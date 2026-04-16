package com.rigel.app.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.rigel.app.model.BuyerInfo;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.User;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.service.IPrintService;
import com.rigel.app.service.ISalesService;
import com.rigel.app.util.SalesSlipPDF;

@Lazy 
@Service
public class PrintService implements IPrintService {
    
	@Autowired
	ISalesService salesService;

	@Override
	public boolean billPrint(String invoiceNumber) {
		List<SalesInfo> item=salesService.searchSalesInfo(new SearchCriteria());
		BuyerInfo buyer=item.get(0).getBuyerInfo();
		buyer.setInvoiceNumber("ITM225100003");
		SalesSlipPDF.createSlip("bill_"+buyer.getInvoiceNumber(),new User(),buyer,item);
		return false;
	}

	@Override
	public boolean barCodePrint(String invoiceNumber) {
		// TODO Auto-generated method stub
		return false;
	}

}
