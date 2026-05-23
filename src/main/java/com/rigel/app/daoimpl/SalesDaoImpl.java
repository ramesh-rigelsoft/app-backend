package com.rigel.app.daoimpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.rigel.app.dao.IItemsDao;
import com.rigel.app.dao.ISalesDao;
import com.rigel.app.model.BuyerInfo;
import com.rigel.app.model.Inventory;
import com.rigel.app.model.Items;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.dto.SearchCriteria;
//import com.rigel.app.querybuilder.SalesQueryBuilder;
import com.rigel.app.service.IInventoryService;
import com.rigel.app.util.Constaints;
import com.rigel.app.util.DateUtility;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class SalesDaoImpl implements ISalesDao {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	IInventoryService iInventoryService;


	@Override
	public List<SalesInfo> saveSalesInfo(List<SalesInfo> salesInfoList) {
		
		if (salesInfoList == null || salesInfoList.isEmpty()) {
	        return Collections.emptyList();
	    }

	    BuyerInfo buyerInfo = salesInfoList.get(0).getBuyerInfo();

	    if (buyerInfo == null) {
	        throw new RuntimeException("BuyerInfo is required");
	    }

	    // SAVE / UPDATE BUYER
	    double total = salesInfoList.stream()
	            .mapToDouble(s -> s.getSoldPrice())
	            .sum();
	    if(buyerInfo.getPaidAmount()!=total) {
	       buyerInfo.setPendingPaymentStatus(Constaints.PENDING_PAYMENT_STATUS);
	    }else {
	    	buyerInfo.setPendingPaymentStatus(Constaints.CLEARED_PAYMENT_STATUS);
	    }
	    BuyerInfo savedBuyer = entityManager.merge(buyerInfo);

	    // SET SAME BUYER INTO ALL SALES
	    salesInfoList.forEach(s -> s.setBuyerInfo(savedBuyer));

	    List<SalesInfo> savedSales = new ArrayList<>();

	    int batchSize = 10;

	    for (int i = 0; i < salesInfoList.size(); i++) {

	        SalesInfo saved = entityManager.merge(salesInfoList.get(i));

	        savedSales.add(saved);

	        if (i > 0 && i % batchSize == 0) {

	            entityManager.flush();
	            entityManager.clear();
	        }
	    }

	    return savedSales;
	}
	
	@Override
	public SalesInfo updateSalesInfo(SalesInfo salesInfo) {
		try {
			return entityManager.merge(salesInfo);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public SalesInfo findById(String id, int ownerId) {

	    String jpql = """
	        SELECT s
	        FROM SalesInfo s
	        JOIN FETCH s.buyerInfo b
	        WHERE s.id = :id
	        AND s.ownerId = :ownerId
	        AND s.status = true
	    """;

	    try {
	        return entityManager.createQuery(jpql, SalesInfo.class)
	                .setParameter("id", id)
	                .setParameter("ownerId", ownerId)
	                .getSingleResult();

	    } catch (Exception e) {
	        return null;
	    }
	}
	
	
	@Override
	public List<SalesInfo> searchSalesInfo(SearchCriteria criteria) {

		StringBuilder jpql = new StringBuilder("SELECT i FROM SalesInfo i JOIN BuyerInfo bi ON bi.id = i.buyerInfo.id WHERE i.status=true AND ");
		Map<String, Object> params = new HashMap<>();
		jpql.append(" i.ownerId = :ownerId ");
		params.put("ownerId", criteria.getUserId());
		if(criteria.getInvoiceNumber()!= null && !criteria.getInvoiceNumber().strip().isEmpty()) {
			 jpql.append(" AND i.buyerInfo.invoiceNumber = :invoiceNumber");
	         params.put("invoiceNumber", criteria.getInvoiceNumber().strip());
			
		}
		
		if (criteria.getSearchKeyword() != null && !criteria.getSearchKeyword().strip().isEmpty()) {
		    jpql.append("""
		        AND (
		            LOWER(i.buyerInfo.invoiceNumber) LIKE :search
		            OR LOWER(i.buyerInfo.custumberId) LIKE :search
		            OR LOWER(i.buyerInfo.buyerName) LIKE :search
		            OR LOWER(i.buyerInfo.emailId) LIKE :search
		            OR LOWER(i.buyerInfo.mobileNumber) LIKE :search
		            OR LOWER(i.buyerInfo.countryCode) LIKE :search
		            OR LOWER(i.buyerInfo.buyerAddress) LIKE :search
		            OR LOWER(i.itemCode) LIKE :search
		            OR LOWER(i.modelName) LIKE :search
		            OR LOWER(i.brand) LIKE :search
		            OR LOWER(i.categoryType) LIKE :search
		            OR LOWER(i.description) LIKE :search
			        OR LOWER(i.vendorName) LIKE :search
			        OR LOWER(i.vendorGSTNumber) LIKE :search
			        OR LOWER(i.itemCondition) LIKE :search		            
		        )
		    """);

		    params.put("search", "%" + criteria.getSearchKeyword().toLowerCase().strip() + "%");
		}
		// optional category filter
		if (criteria.getCategory() != null && !criteria.getCategory().isEmpty()) {
			jpql.append(" AND i.category = :category ");
			params.put("category", criteria.getCategory());
		}

		// optional categoryType filter
		if (criteria.getCategoryType() != null && !criteria.getCategoryType().isEmpty()) {
			jpql.append(" AND i.categoryType = :categoryType ");
			params.put("categoryType", criteria.getCategoryType());
		}

		// optional brand filter
		if (criteria.getBrand() != null && !criteria.getBrand().isEmpty()) {
			jpql.append(" AND i.brand = :brand ");
			params.put("brand", criteria.getBrand());
		}

		if (criteria.getStartDate() != null && criteria.getEndDate() != null) {

			LocalDateTime start = DateUtility.parseToDateTimes(criteria.getStartDate(), false);
			LocalDateTime end = DateUtility.parseToDateTimes(criteria.getEndDate(), true);
			System.out.println("start----"+start);
			System.out.println("end----"+end);
		    jpql.append(" AND i.createdAt BETWEEN :startDate AND :endDate ");
			params.put("startDate", start);
			params.put("endDate", end);
		}

		jpql.append(" ORDER BY i.createdAt DESC");

		var query = entityManager.createQuery(jpql.toString(), SalesInfo.class);

		// set parameters dynamically
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		if (!criteria.isIsdownload()) {
			System.out.println("criteria.getStartIndex()-----------"+criteria.getStartIndex());
		    query.setFirstResult(criteria.getStartIndex());
			query.setMaxResults(criteria.getMaxRecords());
		}
		return query.getResultList();
	}
	
	@Override
	public boolean deleteById(String deviceId, int ownerId) {

	    String jpql = """
	        UPDATE SalesInfo s
	        SET s.status = false
	        WHERE s.repaireDevice.id = :id
	        AND s.ownerId = :ownerId
	        """;

	    int updatedCount = entityManager.createQuery(jpql)
	            .setParameter("id", deviceId)
	            .setParameter("ownerId", ownerId)
	            .executeUpdate();

	    return updatedCount > 0;
	}

	@Override
	public int deleteBySalesId(String ids, int ownerId) {

	    String jpql = """
	        UPDATE SalesInfo s
	        SET s.status = false
	        WHERE s.id = :id
	        AND s.ownerId = :ownerId
	        """;

	    int updatedCount = entityManager.createQuery(jpql)
	            .setParameter("id", ids)
	            .setParameter("ownerId", ownerId)
	            .executeUpdate();

	    return updatedCount;
	}
	
	@Override
	public List<SalesInfo> fetchSalesByRepaireDevice(String deviceId, int ownerId) {

	    if (deviceId == null || deviceId.isBlank()) {
	        return Collections.emptyList();
	    }

	    String jpql = """
	        SELECT s
	        FROM SalesInfo s
	        WHERE s.repaireDevice.id = :deviceId
	        AND s.ownerId = :ownerId
	        AND s.status = true
	        """;

	    return entityManager.createQuery(jpql, SalesInfo.class)
	            .setParameter("deviceId", deviceId)
	            .setParameter("ownerId", ownerId)
	            .getResultList();
	}
}