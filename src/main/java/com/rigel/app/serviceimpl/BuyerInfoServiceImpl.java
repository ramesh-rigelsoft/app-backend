package com.rigel.app.serviceimpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rigel.app.dao.IBuyerDao;
import com.rigel.app.dao.IInventoryDao;
import com.rigel.app.dao.ISalesDao;
import com.rigel.app.model.BuyerInfo;
import com.rigel.app.model.Inventory;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.dto.BuyerInfoDto;
import com.rigel.app.model.dto.RequestInfo;
import com.rigel.app.model.dto.SalesInfoDto;
import com.rigel.app.model.dto.SalesRequest;
import com.rigel.app.model.dto.SalesResponse;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.service.IBuyerInfoService;
import com.rigel.app.util.Constaints;
import com.rigel.app.validate.SalesInfoValidator;

@Lazy 
@Service
//@CacheConfig(cacheNames = "userCache", keyGenerator = "TransferKeyGenerator")
public class BuyerInfoServiceImpl implements IBuyerInfoService {
	
	@Autowired
	IBuyerDao buyerDao;
	
	@Autowired
	ISalesDao salesDao;

	@Autowired
	IInventoryDao inventoryDao;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	InvoiceGeneratorService invoiceService;
	
	@Autowired
	SalesInfoValidator salesInfoValidator;

	@Override
	public SalesResponse saveBuyerInfo(SalesRequest salesRequest) {
		
		List<SalesInfoDto> sales = objectMapper.convertValue(
		        salesRequest.getBuyerInfoDto().getSalesInfo(),
		        new TypeReference<List<SalesInfoDto>>() {}
		);

		salesInfoValidator.validate(sales,salesRequest.getUserId());
		
        BuyerInfo buyer = objectMapper.convertValue(salesRequest.getBuyerInfoDto(),BuyerInfo.class);
        String invoiceNumber=invoiceService.generateFyId(salesRequest.getUserId(), "INV","");
        String customberId=invoiceService.generateCustId(salesRequest.getUserId(), "CUST_ID","");
	    buyer.setCreatedAt(LocalDateTime.now());
	    buyer.setStatus(1);
	    buyer.setInvoiceNumber(invoiceNumber);
	    buyer.setCountryCode("91");
	    buyer.setOwnerId(salesRequest.getUserId());
	    buyer.setCustumberId(customberId);
	    
	    List<SalesInfo> salesSet = salesRequest
	            .getBuyerInfoDto().getSalesInfo()
	            .stream()
	            .map(dto -> {
	                SalesInfo sale = objectMapper.convertValue(dto, SalesInfo.class);
	                sale.setBuyerInfo(buyer);
	                sale.setCreatedAt(LocalDateTime.now());
	                sale.setStatus(1);
	                sale.setBuyerInfo(buyer);
	                sale.setOwnerId(salesRequest.getUserId());
	                System.out.println("sale.toString()-------"+sale.toString());
	                return sale;
	            })
	            .toList();
	    List<SalesInfo> list=new ArrayList<>();
	    salesSet.forEach(s->{
	    	list.add(s);
	    });
	    
	    
	    salesSet.stream().forEach(sal->{
	    	Inventory icount=inventoryDao.findInventoryByCode(sal.getItemCode(),sal.getOwnerId());
	    	if(!sal.getCategory().equalsIgnoreCase(Constaints.SHOP_OWNER_CATEGORY)) {	
		    	if(sal.getQuantity()<icount.getQuantity()) {
		    		int count=icount.getQuantity()-sal.getQuantity();
		    		inventoryDao.updateInventory(sal.getItemCode(), count,sal.getOwnerId());
		    	}else if(sal.getQuantity()==icount.getQuantity()){
		    	  	inventoryDao.deleteInventory(sal.getItemCode(),sal.getOwnerId());
		  		}
	    	}
	    });
	    List<SalesInfo> savedSales=salesDao.saveSalesInfo(list);
	    Set<SalesInfoDto> salesInfo = savedSales.stream()
	            .map(s -> objectMapper.convertValue(s, SalesInfoDto.class))
	            .collect(java.util.stream.Collectors.toSet());      
	       
	    BuyerInfoDto buyerInfoDto=salesRequest.getBuyerInfoDto();
	    buyerInfoDto.setInvoiceNumber(invoiceNumber);
	    buyerInfoDto.setSalesInfo(salesInfo);
	   return SalesResponse.builder().buyerInfoDto(Arrays.asList(buyerInfoDto)).build();
	}
	
	@Override
	public SalesResponse updateBuyerInfo(SalesRequest salesRequest) {
	
		
	    return null;
	}

//	@Override
//	public int deleteBuyerInfo(List<Long> buyerId) {
//		return buyerDao.deleteBuyerInfo(buyerId);
//	}

	@Override
	public SalesResponse searchBuyerInfo(SearchCriteria criteria) {
		return null;//buyerDao.searchBuyerInfo(criteria);
	}
	
}