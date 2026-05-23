package com.rigel.app.daoimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.rigel.app.dao.IGarbageDao;
import com.rigel.app.model.GarbageItemsInfo;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class GarbageDaoImpl implements IGarbageDao {
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public GarbageItemsInfo saveGarbage(GarbageItemsInfo garbage) {
		return entityManager.merge(garbage);
	}

	@Override
	public GarbageItemsInfo updateGarbage(GarbageItemsInfo garbage) {
		return entityManager.merge(garbage);
	}

	@Override
	public GarbageItemsInfo findGarbageByItemCode(String itemCode) {

	    String jpql =
	        "SELECT g FROM GarbageItemsInfo g WHERE g.itemCode = :itemCode";

	    try {
	        return entityManager.createQuery(jpql, GarbageItemsInfo.class)
	                .setParameter("itemCode", itemCode)
	                .getSingleResult();

	    } catch (Exception e) {
	        return null;
	    }
	}

}
