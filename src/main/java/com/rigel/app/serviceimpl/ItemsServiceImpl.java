
package com.rigel.app.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.rigel.app.dao.IItemsDao;
import com.rigel.app.model.Inventory;
import com.rigel.app.model.Items;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.service.IInventoryService;
import com.rigel.app.service.IItemsService;
import com.rigel.app.util.AppUtill;
import com.rigel.app.util.ExcelDirectSave;
import com.rigel.app.validate.*;

@Lazy
@Service
//@CacheConfig(cacheNames = "userCache", keyGenerator = "TransferKeyGenerator")
public class ItemsServiceImpl implements IItemsService {

	@Autowired
	IItemsDao itemsDao;

	@Autowired
	EntryInfoValidator entryInfoValidator;

	@Autowired
	ItemsUpdateValidation itemsUpdateValidation;

//	@Autowired
//	IInventoryService inventoryDao;

	@Override
	public Items saveItems(Items items, boolean isUpdate) {

		String desc = AppUtill.replaceAllSpace(items.getDescription());
		String brand = AppUtill.replaceAllSpace(items.getBrand());
		String modelName = AppUtill.replaceAllSpace(items.getModelName());
		String itemColor = AppUtill.replaceAllSpace(items.getItemColor());
		String itemGen = AppUtill.replaceAllSpace(items.getItemGen());
		String screenSize = AppUtill.replaceAllSpace(items.getScreenSize());
		String operatingSystem = AppUtill.replaceAllSpace(items.getOperatingSystem());
		String gstRate = AppUtill.replaceAllSpace(items.getGstRate());

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
		items.setStatus(true);
		entryInfoValidator.validate(items);
		if (isUpdate) {
			System.out.println("itemId--"+items.getId());
			System.out.println("itemCode--"+items.getItemCode());
			items.setUpdatedAt(LocalDateTime.now());
			Items existingItem = itemsDao.searchItems(SearchCriteria.builder().itemId(items.getId()).isdownload(true).userId(items.getOwnerId()).build()).stream().findFirst().orElse(null);
			itemsUpdateValidation.isValidForEditItems(items,existingItem);
		}
		return itemsDao.saveItems(items, isUpdate);
	}

	@Override
	public Items updateItems(Items items) {
		return itemsDao.updateItems(items);
	}

	@Override
	public int deleteItems(Items items) {

		Items existingItem = itemsDao.searchItems(SearchCriteria.builder().isdownload(true).itemId(items.getId()).userId(items.getOwnerId()).build()).stream().findFirst().orElse(null);
		
		itemsUpdateValidation.isValidForEditItems(items,existingItem);
		
		return itemsDao.deleteItems(items);
	}

	@Override
	public List<Items> searchItems(SearchCriteria criteria) {
		List<Items> items = itemsDao.searchItems(criteria);
		if (criteria.isIsdownload() && items.size() > 0) {
			ExcelDirectSave.exportItemsToExcel(items);
		}
		return items;
	}

}
