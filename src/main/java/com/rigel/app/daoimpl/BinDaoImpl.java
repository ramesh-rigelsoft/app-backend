package com.rigel.app.daoimpl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.rigel.app.dao.IBinDao;
import com.rigel.app.model.dto.BinRequestCriteria;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class BinDaoImpl implements IBinDao {

	@Autowired
	private EntityManager entityManager;

	@Override
	public int deletItems(String itemCode, int ownerId, int type) {
		String hql = null;
		if (type == 1) {
			hql = "update Items SET status = false WHERE ownerId = :ownerId AND itemCode = :itemCode";
		} else if (type == 2) {
			hql = "update SalesInfo SET status = false WHERE ownerId = :ownerId AND itemCode = :itemCode";
		} else if (type == 3) {
			hql = "update Inventory SET status = false WHERE ownerId = :ownerId AND itemCode = :itemCode";
		}

		try {
			return entityManager.createQuery(hql).setParameter("ownerId", ownerId).setParameter("itemCode", itemCode)
					.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public int restoreItems(String itemCode, int ownerId, int type) {
		String hql = null;
		if (type == 1) {
			hql = "update Items SET status = true WHERE ownerId = :ownerId AND itemCode = :itemCode";
		} else if (type == 2) {
			hql = "update SalesInfo SET status = false WHERE ownerId = :ownerId AND itemCode = :itemCode";
		} else if (type == 3) {
			hql = "update Inventory SET status = false WHERE ownerId = :ownerId AND itemCode = :itemCode";
		}
		try {
			return entityManager.createQuery(hql).setParameter("ownerId", ownerId).setParameter("itemCode", itemCode)
					.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public Object fetchDeletedItems(String itemCode, int ownerId, int type) {

	    String hql;

	    if (type == 1) {
	        hql = "FROM Items WHERE ownerId = :ownerId AND itemCode = :itemCode AND status = false";
	    } else if (type == 2) {
	        hql = "FROM SalesInfo WHERE ownerId = :ownerId AND itemCode = :itemCode AND status = false";
	    } else if (type == 3) {
	        hql = "FROM Inventory WHERE ownerId = :ownerId AND itemCode = :itemCode AND status = false";
	    } else {
	        throw new IllegalArgumentException("Invalid type: " + type);
	    }

	    try {
	        return entityManager.createQuery(hql)
	                .setParameter("ownerId", ownerId)
	                .setParameter("itemCode", itemCode)
	                .getSingleResult();   // ✅ safer than getSingleResult()
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Collections.emptyList();
	    }
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> binItemsList(BinRequestCriteria criteria) {

	    int ownerId = criteria.getUserId();
	    int type = criteria.getType();
	    int startIndex = criteria.getStartIndex();
	    int maxRecord = criteria.getMaxRecords();

	    String hql;

	    switch (type) {
	        case 1:
	            hql = "FROM Items WHERE ownerId = :ownerId AND status = false";
	            break;
	        case 2:
	            hql = "FROM SalesInfo WHERE ownerId = :ownerId AND status = false";
	            break;
	        case 3:
	            hql = "FROM Inventory WHERE ownerId = :ownerId AND status = false";
	            break;
	        default:
	            throw new IllegalArgumentException("Invalid type: " + type);
	    }

	    try {
	        return entityManager.createQuery(hql)
	                .setParameter("ownerId", ownerId)
	                .setFirstResult(startIndex)
	                .setMaxResults(maxRecord)
	                .getResultList();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Collections.emptyList();
	    }
	}

}
