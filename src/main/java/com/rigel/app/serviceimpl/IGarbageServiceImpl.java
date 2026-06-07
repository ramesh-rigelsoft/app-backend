package com.rigel.app.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rigel.app.dao.IGarbageDao;
import com.rigel.app.exception.ValidationException;
import com.rigel.app.model.GarbageItemsInfo;
import com.rigel.app.service.IGarbageService;

@Service
public class IGarbageServiceImpl implements IGarbageService {
	
	@Autowired
	private IGarbageDao garbageDao;

	@Override
	public GarbageItemsInfo saveGarbage(GarbageItemsInfo garbage) {
		if (garbage.getOwnerId() < 1) {
			throw new ValidationException("Session Expired, Please Login again then try....");
		}
		return garbageDao.saveGarbage(garbage);
	}

	@Override
	public GarbageItemsInfo updateGarbage(GarbageItemsInfo garbage) {
		if (garbage.getOwnerId() < 1) {
			throw new ValidationException("Session Expired, Please Login again then try....");
		}
		return garbageDao.updateGarbage(garbage);
	}

	@Override
	public GarbageItemsInfo findGarbageByItemCode(String itemCode) {
		return garbageDao.findGarbageByItemCode(itemCode);
	}

	@Override
	public GarbageItemsInfo findGarbageById(String id) {
		return garbageDao.findGarbageById(id);
	}
}
