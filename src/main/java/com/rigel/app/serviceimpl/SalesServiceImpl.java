package com.rigel.app.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rigel.app.dao.IGarbageDao;
import com.rigel.app.dao.IInventoryDao;
import com.rigel.app.dao.IItemsDao;
import com.rigel.app.dao.ISalesDao;
import com.rigel.app.dao.ISupplierDao;
import com.rigel.app.model.GarbageItemsInfo;
import com.rigel.app.model.Inventory;
import com.rigel.app.model.Items;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.Vendors;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.service.IItemsService;
import com.rigel.app.service.ISalesService;
import com.rigel.app.util.Constaints;
import com.rigel.app.validate.ItemsUpdateValidation;
import com.rigel.app.validate.OwnerIdValidation;

@Lazy 
@Service
public class SalesServiceImpl implements ISalesService {

	@Autowired
	ISalesDao salesDao;
	
	@Autowired
	IInventoryDao inventoryDao;
	
	@Autowired
	OwnerIdValidation ownerIdValidation;

	@Autowired
	private ExcelDirectSave directSave;
	
	@Autowired
	private ItemsUpdateValidation itemsUpdateValidation;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private IGarbageDao garbageDao;
	
	@Autowired
	private ISupplierDao supplierDao;
	
	
	@Override
	public List<SalesInfo> saveSalesInfo(List<SalesInfo> salesInfo) {
		salesInfo.stream().forEach(s->{
			ownerIdValidation.validate(s.getOwnerId());	
		});
		return salesDao.saveSalesInfo(salesInfo);
	}

	@Override
	public SalesInfo updateSalesInfo(SalesInfo salesInfo) {
		ownerIdValidation.validate(salesInfo.getOwnerId());
		return salesDao.updateSalesInfo(salesInfo);
	}
	
	@Override
	public SalesInfo returnSalesInfo(String returnReason,String salesId,int ownerId) {
		ownerIdValidation.validate(ownerId);
		SalesInfo salesInfo=salesDao.findById(salesId,ownerId);
		if(salesInfo==null)return null;
		ownerIdValidation.orderReturnValidation(salesInfo, 15);
		salesInfo.setReturnStatus(true);
		salesInfo.setReturnReason(returnReason);
		
		Inventory inventory = inventoryDao.findInventoryByCode(salesInfo.getItemCode(), ownerId);
		inventory.setQuantity(inventory.getQuantity()+1);
		inventoryDao.updateInventory(inventory);
		return salesDao.updateSalesInfo(salesInfo);
	}
	
	@Override
	public SalesInfo replaceSalesInfo(String returnReason,String salesId,int ownerId) {
		ownerIdValidation.validate(ownerId);
		SalesInfo salesInfo=salesDao.findById(salesId,ownerId);
		if(salesInfo==null)return null;
		ownerIdValidation.orderedDateValidation(salesInfo, salesInfo.getWarrantyInMonth());
		
		salesInfo.setReplaceCount(salesInfo.getReplaceCount()+1);
		salesInfo.setReplaceStatus(true);
		salesInfo.setReturnReason(returnReason);
		
		Inventory inventory = inventoryDao.findInventoryByCode(salesInfo.getItemCode(), ownerId);
		ownerIdValidation.orderedCheckStockValidation(inventory, salesInfo);
		inventory.setQuantity(inventory.getQuantity()-salesInfo.getQuantity());
		inventoryDao.updateInventory(inventory);
		
		GarbageItemsInfo garbage = objectMapper.convertValue(salesInfo, GarbageItemsInfo.class);
		GarbageItemsInfo garbage2 = garbageDao.findGarbageByItemCode(garbage.getItemCode());

		if (garbage2 == null) {
			Vendors vendor = supplierDao.findById(garbage.getItemSource());
		    garbage.setVendors(vendor);
		    garbage.setId(null);
		    garbage.setGarbageStatus(Constaints.GARBAGE_COLLECTED);
		    if (garbage.getQuantity() == null) {
		        garbage.setQuantity(1);
		    }
		    garbageDao.saveGarbage(garbage);
		} else {
		    int qty = garbage2.getQuantity() == null ? 0 : garbage2.getQuantity();
		    garbage2.setQuantity(qty + 1);
		    garbage2.setGarbageStatus(Constaints.GARBAGE_COLLECTED);
		    garbageDao.saveGarbage(garbage2);
		}
		return salesDao.updateSalesInfo(salesInfo);
	}
	
	@Override
	public List<SalesInfo> searchSalesInfo(SearchCriteria criteria) {
		List<SalesInfo> sales=salesDao.searchSalesInfo(criteria);
		if(criteria.isIsdownload()&&sales.size()>0){
			directSave.exportSalesToExcel(sales);
		}
		return sales;
	}

	@Override
	public int deleteById(List<SalesInfo> salesinfo,int ownerId) {
		List<String> ids=salesinfo.stream().map(s->s.getId()).toList();
		itemsUpdateValidation.repaireDeleteItems(salesinfo);
		return salesDao.permantalyDeleteBySalesId(ids.get(0), ownerId);
	}

	
}
