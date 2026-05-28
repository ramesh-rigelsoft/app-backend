package com.rigel.app.serviceimpl;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
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
	public RepaireDevice updateRepaire(RepaireDeviceDto repaireDeviceDto) {
		 String repairId=repaireDeviceDto.getId();
		 int ownerId=repaireDeviceDto.getOwnerId();
		 List<SalesInfo> existingSales = salesDao.fetchSalesByRepaireDevice(repairId,ownerId);
				
		// Assume repaireDevice.getSalesInfo() returns a Set<SalesInfo>
		Set<SalesInfoDto> salesInfoSet = repaireDeviceDto.getItems();
		
		// Convert Set to List
		List<SalesInfoDto> salesInfoList = new ArrayList<>(salesInfoSet);
		itemsUpdateValidation.repaireDeleteItems(existingSales);
		
		salesInfoValidator.validateUpdateSalesInfo(salesInfoList, repaireDeviceDto.getOwnerId());
		
		itemsUpdateValidation.repaireUpdateItemValidation(salesInfoList);
		
		SearchCriteria criteria=SearchCriteria.builder().id(repaireDeviceDto.getId()).userId(repaireDeviceDto.getOwnerId()).startIndex(0).maxRecords(10).build();
		RepaireDevice repaireDevice=repaireServiceDao.searchRepair(criteria).get(0);
	    Set<SalesInfo> sales = mapper.convertValue(salesInfoSet,new TypeReference<Set<SalesInfo>>(){});
		repaireDevice.setCustomerName(repaireDeviceDto.getCustomerName());
		repaireDevice.setMobileNumber(repaireDeviceDto.getMobileNumber());
		repaireDevice.setDeviceModelName(repaireDeviceDto.getDeviceModelName());
		repaireDevice.setCategory(repaireDeviceDto.getCategory());
		repaireDevice.setCategoryType(repaireDeviceDto.getCategoryType());
		repaireDevice.setDeliveryDate(repaireDeviceDto.getDeliveryDate());
		repaireDevice.setSerialNumber(repaireDeviceDto.getSerialNumber());
		repaireDevice.setDeviceStatus(repaireDeviceDto.getDeviceStatus());
		repaireDevice.setDefectDescription(repaireDeviceDto.getDefectDescription());
		repaireDevice.setDeviceLockType(repaireDeviceDto.getDeviceLockType());
		repaireDevice.setDevicePassword(repaireDeviceDto.getDevicePassword());
		repaireDevice.setTotalAmount(repaireDeviceDto.getTotalAmount());
		repaireDevice.setAdvanceAmount(repaireDeviceDto.getAdvanceAmount());
		repaireDevice.setPendingAmount(repaireDeviceDto.getPendingAmount());
		repaireDevice.setUpdatedAt(LocalDateTime.now());
		repaireDevice.setSalesInfo(sales);
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
