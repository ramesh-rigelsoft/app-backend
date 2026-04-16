package com.rigel.app.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.rigel.app.dao.IItemsDao;
import com.rigel.app.dao.ISalesDao;
import com.rigel.app.model.Items;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.service.IItemsService;
import com.rigel.app.service.ISalesService;
import com.rigel.app.util.ExcelDirectSave;

@Lazy 
@Service
@CacheConfig(cacheNames = "userCache", keyGenerator = "TransferKeyGenerator")
public class SalesServiceImpl implements ISalesService {

	@Autowired
	ISalesDao salesDao;

	@Override
	public List<SalesInfo> saveSalesInfo(List<SalesInfo> salesInfo) {
		return salesDao.saveSalesInfo(salesInfo);
	}

	@Override
	public SalesInfo updateSalesInfo(SalesInfo salesInfo) {
		return salesDao.updateSalesInfo(salesInfo);
	}

	@Override
	public int deleteItems(List<Long> salesId,int ownerId) {
		return salesDao.deleteItems(salesId,ownerId);
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
