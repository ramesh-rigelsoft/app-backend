package com.rigel.app.daoimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.rigel.app.dao.IRepaireServiceDao;
import com.rigel.app.model.RepaireDevice;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.dto.SearchCriteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class RepaireServiceDaoImpl implements IRepaireServiceDao {
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public RepaireDevice saveRepair(RepaireDevice repairDevice) {

	    if (repairDevice == null) {
	        throw new RuntimeException("Repair Device is required");
	    }

	    Set<SalesInfo> salesSet = repairDevice.getSalesInfo();

	    if (salesSet == null || salesSet.isEmpty()) {
	        throw new RuntimeException("Sales Info is required");
	    }

	    // SET BIDIRECTIONAL MAPPING
	    salesSet.forEach(sales -> {
	    	sales.setRepaireDevice(repairDevice);
	    });

	    // SAVE ONLY PARENT
	    RepaireDevice savedRepair =entityManager.merge(repairDevice);

	    entityManager.flush();

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

	    StringBuilder jpql = new StringBuilder("""
	        SELECT DISTINCT r.id
	        FROM RepaireDevice r
	        JOIN r.salesInfo s
	        WHERE s.status = true
	        AND r.ownerId = :ownerId
	    """);

	    // SEARCH KEYWORD
	    if (search.getSearchKeyword() != null &&
	        !search.getSearchKeyword().trim().isEmpty()) {

	        jpql.append("""
	            AND (
	                LOWER(r.customerName) LIKE LOWER(:keyword)
	                OR LOWER(r.mobileNumber) LIKE LOWER(:keyword)
	                OR LOWER(r.deviceModelName) LIKE LOWER(:keyword)
	                OR LOWER(r.serialNumber) LIKE LOWER(:keyword)
	                OR LOWER(r.custumberId) LIKE LOWER(:keyword)
	            )
	        """);
	    }

	    // STATUS
	    if (search.getStatus() != null &&
	        !search.getStatus().isBlank()) {

	        jpql.append(" AND r.deviceStatus = :status");
	    }

	    // ID
	    if (search.getId() != null &&
	        !search.getId().isBlank()) {

	        jpql.append(" AND r.id = :id");
	    }

	    jpql.append(" ORDER BY r.id DESC");

	    TypedQuery<String> idQuery =
	        entityManager.createQuery(jpql.toString(), String.class);

	    idQuery.setParameter("ownerId", search.getUserId());

	    if (search.getSearchKeyword() != null &&
	        !search.getSearchKeyword().trim().isEmpty()) {

	        idQuery.setParameter(
	            "keyword",
	            "%" + search.getSearchKeyword().trim() + "%"
	        );
	    }

	    if (search.getStatus() != null &&
	        !search.getStatus().isBlank()) {

	        idQuery.setParameter("status", search.getStatus());
	    }

	    if (search.getId() != null &&
	        !search.getId().isBlank()) {

	        idQuery.setParameter("id", search.getId());
	    }

	    // PAGINATION
	    idQuery.setFirstResult(search.getStartIndex());
	    idQuery.setMaxResults(search.getMaxRecords());

	    List<String> ids = idQuery.getResultList();

	    if (ids.isEmpty()) {
	        return Collections.emptyList();
	    }

	    // FETCH ACTUAL DATA
	    return entityManager.createQuery("""
	        SELECT DISTINCT r
	        FROM RepaireDevice r
	        LEFT JOIN FETCH r.salesInfo s
	        WHERE r.id IN :ids
	        ORDER BY r.createdAt DESC
	    """, RepaireDevice.class)
	    .setParameter("ids", ids)
	    .getResultList();
	}
}
