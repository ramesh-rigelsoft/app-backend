package com.rigel.app.serviceimpl;

import java.time.LocalDateTime;
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
		salesInfo.setReturnStatus(true);
		salesInfo.setReturnReason(returnReason);
		
		LocalDateTime orderedDate=salesInfo.getBuyerInfo().getCreatedAt();
		ownerIdValidation.orderReturnValidation(orderedDate, 15);
		
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
		salesInfo.setReplaceCount(salesInfo.getReplaceCount()+1);
		salesInfo.setReplaceStatus(true);
		salesInfo.setReturnReason(returnReason);
		LocalDateTime orderedDate=salesInfo.getBuyerInfo().getCreatedAt();
		ownerIdValidation.orderedDateValidation(orderedDate, salesInfo.getWarrantyInMonth());
		Inventory inventory = inventoryDao.findInventoryByCode(salesInfo.getItemCode(), ownerId);
		ownerIdValidation.orderedCheckStockValidation(inventory, salesInfo);
		inventory.setQuantity(inventory.getQuantity()-salesInfo.getQuantity());
		inventoryDao.updateInventory(inventory);
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
		return salesDao.deleteBySalesId(ids.get(0), ownerId);
	}

	
}
