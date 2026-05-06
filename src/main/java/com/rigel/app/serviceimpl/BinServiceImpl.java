package com.rigel.app.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rigel.app.dao.IBinDao;
import com.rigel.app.model.dto.BinRequestCriteria;
import com.rigel.app.service.IBinService;

@Service
public class BinServiceImpl implements IBinService {

	@Autowired
	private IBinDao binDao;

	@Override
	public int deletItems(String itemCode, int ownerId,int type) {
		return binDao.deletItems(itemCode, ownerId,type);
	}

	@Override
	public int restoreItems(String itemCode, int ownerId,int type) {
		return binDao.restoreItems(itemCode, ownerId,type);
	}

	@Override
	public Object fetchDeletedItems(String itemCode, int ownerId,int type) {
		return binDao.fetchDeletedItems(itemCode, ownerId,type);
	}

	@Override
	public <T> List<T> binItemsList(BinRequestCriteria criteria) {
		return binDao.binItemsList(criteria);
	}

}
