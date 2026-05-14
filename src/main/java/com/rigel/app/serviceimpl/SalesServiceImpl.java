package com.rigel.app.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.rigel.app.dao.IInventoryDao;
import com.rigel.app.dao.IItemsDao;
import com.rigel.app.dao.ISalesDao;
import com.rigel.app.model.Inventory;
import com.rigel.app.model.Items;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.service.IItemsService;
import com.rigel.app.service.ISalesService;
import com.rigel.app.util.ExcelDirectSave;

@Lazy 
@Service
public class SalesServiceImpl implements ISalesService {

	@Autowired
	ISalesDao salesDao;
	
	@Autowired
	IInventoryDao inventoryDao;
	
	@Override
	public List<SalesInfo> saveSalesInfo(List<SalesInfo> salesInfo) {
		return salesDao.saveSalesInfo(salesInfo);
	}

	@Override
	public SalesInfo updateSalesInfo(SalesInfo salesInfo) {
		return salesDao.updateSalesInfo(salesInfo);
	}
	
	@Override
	public SalesInfo returnSalesInfo(String returnReason,String salesId,int ownerId) {
		SalesInfo salesInfo=salesDao.findById(salesId);
		salesInfo.setReturnStatus(true);
		salesInfo.setReturnReason(returnReason);
		
		Inventory inventory = inventoryDao.findInventoryByCode(salesInfo.getItemCode(), ownerId);
		inventory.setQuantity(inventory.getQuantity()+1);
		inventoryDao.updateInventory(inventory);
		
		return salesDao.updateSalesInfo(salesInfo);
	}
	
	
	@Override
	public List<SalesInfo> searchSalesInfo(SearchCriteria criteria) {
		List<SalesInfo> sales=salesDao.searchSalesInfo(criteria);
		if(criteria.isIsdownload()&&sales.size()>0){
			ExcelDirectSave.exportSalesToExcel(sales);
		}
		return sales;
	}

	
}
