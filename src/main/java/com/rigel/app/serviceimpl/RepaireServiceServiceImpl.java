package com.rigel.app.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;
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

	@Override
	public RepaireDevice saveRepair(RepaireDevice expaDevice) {
		String invoiceNumber=invoiceGenerateService.generateInvoiceNumber(expaDevice.getOwnerId(), "INV","");
		String custumberId=invoiceGenerateService.generateCustId(expaDevice.getOwnerId(), "CUST_ID","");
	    expaDevice.setInvoiceNumber(invoiceNumber);
		expaDevice.setCustumberId(custumberId);
		expaDevice.setCreatedAt(LocalDateTime.now());
		expaDevice.setStatus(true);
		expaDevice.setUpdatedAt(LocalDateTime.now());	
		return repaireServiceDao.saveRepair(expaDevice);
	}

	@Override
	public RepaireDevice updateRepaire(RepaireDevice repaireDevice) {

	    salesDao.deleteById(repaireDevice.getId(), repaireDevice.getOwnerId());
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
