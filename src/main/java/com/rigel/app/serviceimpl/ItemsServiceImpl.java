
package com.rigel.app.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.rigel.app.dao.IItemsDao;
import com.rigel.app.model.Items;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.service.IItemsService;
import com.rigel.app.util.AppUtill;
import com.rigel.app.util.ExcelDirectSave;

@Lazy 
@Service
//@CacheConfig(cacheNames = "userCache", keyGenerator = "TransferKeyGenerator")
public class ItemsServiceImpl implements IItemsService {

	@Autowired
	IItemsDao itemsDao;
	
	@Override
	public Items saveItems(Items items) {
		String desc=AppUtill.replaceAllSpace(items.getDescription());
		String brand=AppUtill.replaceAllSpace(items.getBrand());
		String modelName=AppUtill.replaceAllSpace(items.getModelName());
		String itemColor=AppUtill.replaceAllSpace(items.getItemColor());
		String itemGen=AppUtill.replaceAllSpace(items.getItemGen());
		String screenSize=AppUtill.replaceAllSpace(items.getScreenSize());
		String operatingSystem=AppUtill.replaceAllSpace(items.getOperatingSystem());
		String gstRate=AppUtill.replaceAllSpace(items.getGstRate());
		System.out.println("gst--"+gstRate);
		System.out.println("desc--"+desc);
		
		items.setCreatedAt(LocalDateTime.now());
		items.setDescription(desc);
		items.setBrand(brand);
		items.setModelName(modelName);
		items.setItemColor(itemColor);
		items.setItemGen(itemGen);
		items.setScreenSize(screenSize);
		items.setOperatingSystem(operatingSystem);
		items.setCreatedAt(LocalDateTime.now());
		items.setGstRate(gstRate);
		return itemsDao.saveItems(items);
	}

	@Override
	public Items updateItems(Items items) {
		return itemsDao.updateItems(items);
	}

	@Override
	public int deleteItems(List<Long> itemsId,int ownerId) {
		return itemsDao.deleteItems(itemsId,ownerId);
	}

	@Override
	public List<Items> searchItems(SearchCriteria criteria) {
		List<Items> items=itemsDao.searchItems(criteria);
		if(criteria.isIsdownload()&&items.size()>0){
			ExcelDirectSave.exportItemsToExcel(items);
		}
		return items;
	}
	
}
