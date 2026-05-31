package com.rigel.app.daoimpl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rigel.app.dao.IItemsDao;
import com.rigel.app.model.Inventory;
import com.rigel.app.model.Items;
import com.rigel.app.model.dto.ItemsDashboardResponse;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.model.dto.VendorInvoiceDTO;
import com.rigel.app.model.dto.VendorPerformanceDTO;
//import com.rigel.app.querybuilder.ItemsQueryBuilder;
import com.rigel.app.service.IInventoryService;
import com.rigel.app.serviceimpl.FyIdGeneratorService;
import com.rigel.app.util.Constaints;
import com.rigel.app.util.DateUtility;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class ItemsDaoImpl implements IItemsDao {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	IInventoryService inventoryService;

	@Autowired
	ObjectMapper mapper;

	@Override
	public Items saveItems(Items items, boolean isUpdate) {

		Inventory Inventory = mapper.convertValue(items, Inventory.class);

		if (Inventory.getCategory().equalsIgnoreCase(Constaints.SHOP_OWNER_CATEGORY)) {
			Inventory.setQuantity(100);
			items.setQuantity(100);
		}

		Inventory existingInventory = inventoryService.saveInventory(Inventory, entityManager, isUpdate);
		if (existingInventory == null) {
			return items;
		}
		items.setItemCode(existingInventory.getItemCode());
		if (!isUpdate) {
			items.setId(null);
		}

		return entityManager.merge(items);
	}

	@Override
	public Items updateItems(Items items) {
		return entityManager.merge(items);
	}
	
	

	@Override
	public int deleteItems(Items items) {
		 String hql = "UPDATE Items i SET i.status = false WHERE i.id IN :ids";
		    
		    return entityManager.createQuery(hql)
		            .setParameter("ids", items.getId())
		            .executeUpdate();
		    
//		String hql = "DELETE FROM Items i WHERE i.id IN :ids";
//		return entityManager.createQuery(hql).setParameter("ids", items.getId()).executeUpdate();
	}

//	@Override
//	public List<Items> searchItems(SearchCriteria criteria) {
//		return itemsQueryBuilder.searchItems(criteria, entityManager);
//	}

	@Override
	public List<Items> searchItems(SearchCriteria criteria) {

		StringBuilder jpql = new StringBuilder("SELECT i FROM Items i WHERE i.status=true AND ");

		Map<String, Object> params = new HashMap<>();

		// mandatory ownerId
		jpql.append(" i.ownerId = :ownerId ");
		params.put("ownerId", criteria.getUserId());

		if (criteria.getSearchKeyword() != null && !criteria.getSearchKeyword().trim().isEmpty()) {

			jpql.append("""
					    AND (
					        LOWER(i.itemCode) LIKE :search
					        OR LOWER(i.modelName) LIKE :search
					        OR LOWER(i.brand) LIKE :search
					        OR LOWER(i.categoryType) LIKE :search
					        OR LOWER(i.description) LIKE :search
					        OR LOWER(i.vendorName) LIKE :search
					        OR LOWER(i.vendorGSTNumber) LIKE :search
					        OR LOWER(i.itemCondition) LIKE :search
					    )
					""");

			params.put("search", "%" + criteria.getSearchKeyword().toLowerCase().trim() + "%");
		}

//		// optional searchKeyword (itemCode / model / brand search)
//		if (criteria.getSearchKeyword() != null && !criteria.getSearchKeyword().isEmpty()) {
//			jpql.append(" AND (i.itemCode = :search OR i.modelName LIKE :search OR i.brand LIKE :search) ");
//			params.put("search", "" + criteria.getSearchKeyword() + "");
//		}

		// optional category filter
		if (criteria.getCategory() != null && !criteria.getCategory().isEmpty()) {
			jpql.append(" AND i.category = :category ");
			params.put("category", criteria.getCategory());
		}

		if (criteria.getItemId() != null && !criteria.getItemId().isEmpty()) {
			jpql.append(" AND i.id = :itemId ");
			params.put("itemId", criteria.getItemId());
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
			jpql.append(" AND i.createdAt BETWEEN :startDate AND :endDate ");
			params.put("startDate", start);
			params.put("endDate", end);
		}
		jpql.append(" ORDER BY i.createdAt DESC");

		var query = entityManager.createQuery(jpql.toString(), Items.class);

		// set parameters safely
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}

		if (!criteria.isIsdownload()) {
			query.setFirstResult(criteria.getStartIndex());
			query.setMaxResults(criteria.getMaxRecords());
		}
		return query.getResultList();
	}
	
	@Override
	public ItemsDashboardResponse fetchItemReportData(SearchCriteria criteria) {

	    int ownerId = criteria.getUserId();

	    // =====================================================
	    // COMMON FILTER BUILDER
	    // =====================================================

	    StringBuilder filter = new StringBuilder(" WHERE i.ownerId = :ownerId ");
	    filter.append(" AND i.vendorName IS NOT NULL ");
	    filter.append(" AND TRIM(i.vendorName) <> '' ");
	    filter.append(" AND i.vendorInvoiceNumber IS NOT NULL ");
	    filter.append(" AND TRIM(i.vendorInvoiceNumber) <> '' ");

	    LocalDateTime start = null;
	    LocalDateTime end = null;
	    String search = null;

	    if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
	        start = DateUtility.parseToDateTimes(criteria.getStartDate(), false);
	        end = DateUtility.parseToDateTimes(criteria.getEndDate(), true);

	        filter.append(" AND i.createdAt BETWEEN :startDate AND :endDate ");
	    }

	    if (criteria.getSearchKeyword() != null &&
	            !criteria.getSearchKeyword().trim().isEmpty()) {

	        search = "%" + criteria.getSearchKeyword().toLowerCase().trim() + "%";

	        filter.append("""
	            AND (
	                LOWER(i.itemCode) LIKE :search
	                OR LOWER(i.modelName) LIKE :search
	                OR LOWER(i.brand) LIKE :search
	                OR LOWER(i.categoryType) LIKE :search
	                OR LOWER(i.description) LIKE :search
	                OR LOWER(i.vendorName) LIKE :search
	                OR LOWER(i.vendorGSTNumber) LIKE :search
	                OR LOWER(i.itemCondition) LIKE :search
	            )
	        """);
	    }

	    // =====================================================
	    // SUMMARY QUERY
	    // =====================================================

	    TypedQuery<Object[]> summaryQuery = entityManager.createQuery(
	            "SELECT COALESCE(SUM(i.sellingPrice),0), " +
	                    "COUNT(i.id), " +
	                    "COUNT(DISTINCT i.vendorName) " +
	                    "FROM Items i " + filter,
	            Object[].class
	    );

	    summaryQuery.setParameter("ownerId", ownerId);

	    if (start != null && end != null) {
	        summaryQuery.setParameter("startDate", start);
	        summaryQuery.setParameter("endDate", end);
	    }

	    if (search != null) {
	        summaryQuery.setParameter("search", search);
	    }

	    Object[] summary = summaryQuery.getSingleResult();

	    double totalPurchase = ((Number) summary[0]).doubleValue();
	    long totalItems = ((Number) summary[1]).longValue();
	    long totalVendors = ((Number) summary[2]).longValue();

	    // =====================================================
	    // VENDOR PERFORMANCE (FIXED - NO NULL / EMPTY)
	    // =====================================================

	    List<Object[]> rows = entityManager.createQuery("""
	        SELECT i.vendorName,
	               i.vendorInvoiceNumber,
	               COUNT(i.id),
	               COALESCE(SUM(i.sellingPrice),0)
	        FROM Items i
	        WHERE i.ownerId = :ownerId
	          AND i.vendorName IS NOT NULL
	          AND TRIM(i.vendorName) <> ''
	          AND i.vendorInvoiceNumber IS NOT NULL
	          AND TRIM(i.vendorInvoiceNumber) <> ''
	        GROUP BY i.vendorName, i.vendorInvoiceNumber
	        ORDER BY i.vendorName ASC
	    """, Object[].class)
	    .setParameter("ownerId", ownerId)
	    .getResultList();

	    Map<String, VendorPerformanceDTO> vendorMap = new LinkedHashMap<>();

	    for (Object[] r : rows) {

	        String vendor = r[0].toString().trim();
	        String invoice = r[1].toString().trim();

	        long itemCount = ((Number) r[2]).longValue();
	        double amount = ((Number) r[3]).doubleValue();

	        VendorPerformanceDTO dto = vendorMap.computeIfAbsent(
	                vendor,
	                v -> VendorPerformanceDTO.builder()
	                        .vendorName(vendor)
	                        .invoices(new ArrayList<>())
	                        .build()
	        );

	        dto.setTotalAmount(dto.getTotalAmount() + amount);

	        dto.getInvoices().add(
	                VendorInvoiceDTO.builder()
	                        .invoiceNumber(invoice)
	                        .itemCount(itemCount)
	                        .amount(amount)
	                        .build()
	        );
	    }

	    // =====================================================
	    // ITEM LIST (NO PAGINATION)
	    // =====================================================

	    TypedQuery<Items> itemQuery = entityManager.createQuery(
	            "SELECT i FROM Items i " + filter + " ORDER BY i.createdAt DESC",
	            Items.class
	    );

	    itemQuery.setParameter("ownerId", ownerId);

	    if (start != null && end != null) {
	        itemQuery.setParameter("startDate", start);
	        itemQuery.setParameter("endDate", end);
	    }

	    if (search != null) {
	        itemQuery.setParameter("search", search);
	    }

	    List<Items> items = itemQuery.getResultList();

	    // =====================================================
	    // FINAL RESPONSE
	    // =====================================================

	    return ItemsDashboardResponse.builder()
	            .totalPurchase(totalPurchase)
	            .totalItems(totalItems)
	            .totalVendors(totalVendors)
	            .vendorPerformance(new ArrayList<>(vendorMap.values()))
	            .itemStream(items)
	            .build();
	}
}
