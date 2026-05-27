package com.rigel.app.serviceimpl;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rigel.app.dao.IRepaireServiceDao;
import com.rigel.app.dao.ISalesDao;
import com.rigel.app.model.*;
import com.rigel.app.model.dto.ExpenseCreteria;
import com.rigel.app.model.dto.RepaireDeviceDto;
import com.rigel.app.model.dto.SalesInfoDto;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.service.IRepaireServiceService;
import com.rigel.app.validate.ItemsUpdateValidation;

@Service
public class RepaireServiceServiceImpl implements IRepaireServiceService {
	
	@Autowired
	IRepaireServiceDao repaireServiceDao;
	
	@Autowired
	InvoiceGeneratorService invoiceGenerateService;
	
	@Autowired
	ISalesDao salesDao;
	
	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	com.rigel.app.validate.SalesInfoValidator salesInfoValidator;
	
	@Autowired
	ItemsUpdateValidation itemsUpdateValidation;

	@Override
	public RepaireDevice saveRepair(RepaireDevice repairDevice) {
		 // NULL CHECK
	    if (repairDevice == null) {
	        throw new RuntimeException("Repair Device is required");
	    }

	    List<SalesInfo> salesList = new ArrayList<>(repairDevice.getSalesInfo());
	    if (salesList == null || salesList.isEmpty()) {
	        throw new RuntimeException("Sales Info is required");
	    }
	    salesInfoValidator.validateSalesInfo(salesList, repairDevice.getOwnerId());
	    itemsUpdateValidation.repaireItemValidation(salesList);
	    
		String invoiceNumber=invoiceGenerateService.generateInvoiceNumber(repairDevice.getOwnerId(), "INV","");
		String custumberId=invoiceGenerateService.generateCustId(repairDevice.getOwnerId(), "CUST_ID","");
		repairDevice.setInvoiceNumber(invoiceNumber);
		repairDevice.setCustumberId(custumberId);
		repairDevice.setCreatedAt(LocalDateTime.now());
		repairDevice.setStatus(true);
		repairDevice.setUpdatedAt(LocalDateTime.now());	
		
		return repaireServiceDao.saveRepair(repairDevice);
	}

	@Override
	public RepaireDevice updateRepaire(RepaireDevice repaireDevice) {
		 List<SalesInfo> existingSales = salesDao.fetchSalesByRepaireDevice(repaireDevice.getId(),repaireDevice.getOwnerId());
				
		// Assume repaireDevice.getSalesInfo() returns a Set<SalesInfo>
		Set<SalesInfo> salesInfoSet = repaireDevice.getSalesInfo();

		// Convert Set to List
		List<SalesInfo> salesInfoList = new ArrayList<>(salesInfoSet);
		itemsUpdateValidation.repaireDeleteItems(existingSales);
		
		
		salesDao.deleteById(repaireDevice.getId(), repaireDevice.getOwnerId());
		
		salesInfoValidator.validateSalesInfo(salesInfoList, repaireDevice.getOwnerId());
		
		itemsUpdateValidation.repaireItemValidation(salesInfoList);
		
		return repaireServiceDao.saveRepair(repaireDevice);
	}

	@Override
	public RepaireDevice updateStatus(RepaireDevice repaireDevice) {
	    return repaireServiceDao.updateRepaire(repaireDevice);
	}

	@Override
	public List<RepaireDevice> searchRepair(SearchCriteria expaDevice) {
		return repaireServiceDao.searchRepair(expaDevice);
	}
	


}
