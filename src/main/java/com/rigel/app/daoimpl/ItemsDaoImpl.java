package com.rigel.app.daoimpl;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rigel.app.dao.IItemsDao;
import com.rigel.app.model.Inventory;
import com.rigel.app.model.Items;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.querybuilder.ItemsQueryBuilder;
import com.rigel.app.service.IInventoryService;
import com.rigel.app.serviceimpl.FyIdGeneratorService;
import com.rigel.app.util.DateUtility;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class ItemsDaoImpl implements IItemsDao {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private ItemsQueryBuilder itemsQueryBuilder;

	@Autowired
	FyIdGeneratorService fyIdGeneratorService;

	@Autowired
	IInventoryService inventoryService;

	@Autowired
	ObjectMapper mapper;

	@Override
	public Items saveItems(Items items) {

		String existingItemCode = items.getItemCode();
		if (items.getItemCode() == null) {
			items.setItemCode(fyIdGeneratorService.generateFyId(items.getOwnerId(), "ITM"));
		}
		System.out.println("item--" + items);
		Inventory Inventory = mapper.convertValue(items, Inventory.class);
		System.out.println("Inventory--" + Inventory);

		if (Inventory.getCategory().equals("Repair Installation")) {
			Inventory.setQuantity(50);
		}

		Inventory existingInventory = inventoryService.saveInventory(Inventory, existingItemCode, items.getOwnerId(),
				entityManager);
		if (existingInventory != null) {
			items.setItemCode(existingInventory.getItemCode());
		}
		if (Inventory.getCategory().equals("Repair Installation")) {
			items.setQuantity(50);
		}

		return entityManager.merge(items);
	}

	@Override
	public Items updateItems(Items items) {
		return entityManager.merge(items);
	}

	@Override
	public int deleteItems(List<Long> itemsId, int ownerId) {
		String hql = "DELETE FROM Items i WHERE i.id IN :ids";
		return entityManager.createQuery(hql).setParameter("ids", itemsId).executeUpdate();
	}

//	@Override
//	public List<Items> searchItems(SearchCriteria criteria) {
//		return itemsQueryBuilder.searchItems(criteria, entityManager);
//	}

	@Override
	public List<Items> searchItems(SearchCriteria criteria) {

		StringBuilder jpql = new StringBuilder("SELECT i FROM Items i WHERE 1=1 ");

		Map<String, Object> params = new HashMap<>();

		// mandatory ownerId
		jpql.append(" AND i.ownerId = :ownerId ");
		params.put("ownerId", criteria.getUserId());

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

		var query = entityManager.createQuery(jpql.toString(), Items.class);

		// set parameters safely
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}

		if (!criteria.isIsdownload()) {
			// pagination safe handling
			int startIndex = Math.max(criteria.getStartIndex() - 1, 0);
			query.setFirstResult(startIndex);
			query.setMaxResults(criteria.getMaxRecords());
		}
		return query.getResultList();
	}
}
