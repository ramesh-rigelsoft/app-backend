package com.rigel.app.daoimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.rigel.app.dao.IRepaireServiceDao;
import com.rigel.app.model.RepaireDevice;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.dto.SearchCriteria;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class RepaireServiceDaoImpl implements IRepaireServiceDao {
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public RepaireDevice saveRepair(RepaireDevice repairDevice) {

	    // NULL CHECK
	    if (repairDevice == null) {
	        throw new RuntimeException("Repair Device is required");
	    }

	    // GET SALES INFO
	    Set<SalesInfo> salesSet = repairDevice.getSalesInfo();

	    if (salesSet == null || salesSet.isEmpty()) {
	        throw new RuntimeException("Sales Info is required");
	    }

	    // SAVE / UPDATE REPAIR DEVICE
	    RepaireDevice savedRepair =
	            entityManager.merge(repairDevice);

	    // SET REFERENCE
	    salesSet.forEach(
	            sales -> sales.setRepaireDevice(savedRepair)
	    );

	    // CONVERT SET TO LIST
	    List<SalesInfo> salesList = new ArrayList<>(salesSet);

	    int batchSize = 10;

	    // SAVE SALES INFO
	    for (int i = 0; i < salesList.size(); i++) {

	        entityManager.merge(salesList.get(i));

	        // BATCH FLUSH
	        if (i > 0 && i % batchSize == 0) {

	            entityManager.flush();
//	            entityManager.clear();
	        }
	    }

	    return savedRepair;
	}

	@Override
	public RepaireDevice updateRepaire(RepaireDevice expaDevice) {

	    RepaireDevice updatedDevice = entityManager.merge(expaDevice);

	    entityManager.flush();
//	    entityManager.clear();

	    return updatedDevice;
	}

	@Override
	public List<RepaireDevice> searchRepair(SearchCriteria search) {

		StringBuilder jpql = new StringBuilder(
			    "SELECT DISTINCT r " +
			    "FROM RepaireDevice r " +
			    "INNER JOIN FETCH r.salesInfo s " +
			    "WHERE s.status = true " +
			    "AND r.ownerId = :ownerId"
			);
		// 🔥 KEYWORD SEARCH
	    if (search.getSearchKeyword() != null &&
	        !search.getSearchKeyword().trim().isEmpty()) {

	        jpql.append(
	            " AND (" +
	            "LOWER(r.customerName) LIKE LOWER(:keyword) " +
	            "OR LOWER(r.mobileNumber) LIKE LOWER(:keyword) " +
	            "OR LOWER(r.deviceModelName) LIKE LOWER(:keyword) " +
	            "OR LOWER(r.serialNumber) LIKE LOWER(:keyword) " +
	            "OR LOWER(r.custumberId) LIKE LOWER(:keyword) " +
	            ")"
	        );
	    }


	    if (search.getStatus() != null && !search.getStatus().isEmpty()) {
	        jpql.append(" AND r.deviceStatus = :status");
	    }
	    
	    if (search.getId() != null && !search.getId().isEmpty()) {
	        jpql.append(" AND r.id = :id");
	    }

	    var query = entityManager.createQuery(
	        jpql.toString(),
	        RepaireDevice.class
	    );

	    // SET PARAMETERS
	    query.setParameter("ownerId", search.getUserId());
	   
	    if (search.getSearchKeyword() != null &&
	            !search.getSearchKeyword().trim().isEmpty()) {

	            query.setParameter(
	                "keyword",
	                "%" + search.getSearchKeyword().trim() + "%"
	            );
	        }

	    if (search.getStatus() != null && !search.getStatus().isEmpty()) {
	        query.setParameter("status", search.getStatus());
	    }
	    
	    if (search.getId() != null && !search.getId().isEmpty()) {
	        query.setParameter("id", search.getId());
	    }
	    
	    query.setFirstResult(search.getStartIndex());
	    query.setMaxResults(search.getMaxRecords());

	    return query.getResultList();
	}

}
