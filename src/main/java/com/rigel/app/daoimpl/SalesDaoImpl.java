package com.rigel.app.daoimpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.rigel.app.dao.IItemsDao;
import com.rigel.app.dao.ISalesDao;
import com.rigel.app.model.Inventory;
import com.rigel.app.model.Items;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.querybuilder.SalesQueryBuilder;
import com.rigel.app.service.IInventoryService;
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

	@Autowired
	SalesQueryBuilder salesQueryBuilder;

	@Override
	public List<SalesInfo> saveSalesInfo(List<SalesInfo> salesInfoList) {
		List<SalesInfo> savedSales = new ArrayList<>();
		int batchSize = 10;

		for (int i = 0; i < salesInfoList.size(); i++) {
			SalesInfo saved = entityManager.merge(salesInfoList.get(i));
			savedSales.add(saved);

			if (i % batchSize == 0) {
				entityManager.flush(); // DB sync
				entityManager.clear(); // memory free
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

	@Override
	public int deleteItems(List<Long> salesId, int ownerId) {
		int count = entityManager.createQuery("DELETE FROM SalesInfo s WHERE s.id IN :ids AND s.ownerId = :ownerId")
				.setParameter("ids", salesId).setParameter("ownerId", ownerId).executeUpdate();

		return count;
	}

	@Override
	public List<SalesInfo> searchSalesInfo(SearchCriteria criteria) {

		StringBuilder jpql = new StringBuilder("SELECT i FROM SalesInfo i INNER JOIN FETCH i.buyerInfo WHERE 1=1 ");

		Map<String, Object> params = new HashMap<>();
		jpql.append(" AND i.ownerId = :ownerId ");
		params.put("ownerId", criteria.getUserId());
		
		if (criteria.getInvoiceNumber() != null && !criteria.getInvoiceNumber().isEmpty()) {
		    jpql.append(" AND i.buyerInfo.invoiceNumber = :invoiceNumber ");
		    params.put("invoiceNumber", criteria.getInvoiceNumber());
		}

		// optional searchKeyword (itemCode / model / brand search)
		if (criteria.getSearchKeyword() != null && !criteria.getSearchKeyword().isEmpty()) {
			jpql.append(" AND (i.itemCode = :search OR i.modelName LIKE :search OR i.brand LIKE :search) ");
			params.put("search", "%" + criteria.getSearchKeyword() + "%");
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

			LocalDateTime start = DateUtility.parseToDateTime(criteria.getStartDate(), false);
			LocalDateTime end = DateUtility.parseToDateTime(criteria.getEndDate(), true);
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
		    query.setFirstResult(criteria.getStartIndex());
			query.setMaxResults(criteria.getMaxRecords());
		}
		return query.getResultList();
	}

}