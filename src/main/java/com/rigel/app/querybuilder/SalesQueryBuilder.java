package com.rigel.app.querybuilder;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rigel.app.model.Inventory;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.dto.SearchCriteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class SalesQueryBuilder {

	public List<SalesInfo> searchSales(SearchCriteria filterRequest, EntityManager entityManager) {

	    StringBuilder jpql = new StringBuilder("SELECT i FROM BuyerInfo b INNER JOIN SalesInfo i ON b.id=i.buyerInfo.id WHERE 1=1 ");

	    if (filterRequest.getCategory() != null) {
	        jpql.append(" AND i.category = :category ");
	    }

	    if (filterRequest.getCategoryType() != null) {
	        jpql.append(" AND i.categoryType = :categoryType ");
	    }

	    if (filterRequest.getBrand() != null) {
	        jpql.append(" AND i.brand = :brand ");
	    }

	    if (filterRequest.getProcessor() != null) {
	        jpql.append(" AND i.processor = :processor ");
	    }

	    if (filterRequest.getItemCondition() != null) {
	        jpql.append(" AND i.itemCondition = :itemCondition ");
	    }

	    if (filterRequest.getModelName() != null && !filterRequest.getModelName().isEmpty()) {
	        jpql.append(" AND LOWER(i.modelName) LIKE :modelName ");
	    }

	    if (filterRequest.getRam() != null) {
	        jpql.append(" AND i.ram = :ram ");
	    }

	    if (filterRequest.getStorage() != null) {
	        jpql.append(" AND i.storage = :storage ");
	    }

	    if (filterRequest.getStorageType() != null) {
	        jpql.append(" AND i.storageType = :storageType ");
	    }

	    if (filterRequest.getOperatingSystem() != null) {
	        jpql.append(" AND i.operatingSystem = :operatingSystem ");
	    }

	    if (filterRequest.getScreenSize() != null) {
	        jpql.append(" AND i.screenSize = :screenSize ");
	    }

	    if (filterRequest.getItemGen() != null) {
	        jpql.append(" AND i.itemGen = :itemGen ");
	    }

	    if (filterRequest.getDescription() != null && !filterRequest.getDescription().isEmpty()) {
	        jpql.append(" AND LOWER(i.description) LIKE :description ");
	    }

	    jpql.append(" ORDER BY i.createdAt DESC ");

	    TypedQuery<SalesInfo> query = entityManager.createQuery(jpql.toString(), SalesInfo.class);

	    if (filterRequest.getCategory() != null) {
	        query.setParameter("category", filterRequest.getCategory());
	    }

	    if (filterRequest.getCategoryType() != null) {
	        query.setParameter("categoryType", filterRequest.getCategoryType());
	    }

	    if (filterRequest.getBrand() != null) {
	        query.setParameter("brand", filterRequest.getBrand());
	    }

	    if (filterRequest.getProcessor() != null) {
	        query.setParameter("processor", filterRequest.getProcessor());
	    }

	    if (filterRequest.getItemCondition() != null) {
	        query.setParameter("itemCondition", filterRequest.getItemCondition());
	    }

	    if (filterRequest.getModelName() != null && !filterRequest.getModelName().isEmpty()) {
	        query.setParameter("modelName", "%" + filterRequest.getModelName().toLowerCase() + "%");
	    }

	    if (filterRequest.getRam() != null) {
	        query.setParameter("ram", filterRequest.getRam());
	    }

	    if (filterRequest.getStorage() != null) {
	        query.setParameter("storage", filterRequest.getStorage());
	    }

	    if (filterRequest.getStorageType() != null) {
	        query.setParameter("storageType", filterRequest.getStorageType());
	    }

	    if (filterRequest.getOperatingSystem() != null) {
	        query.setParameter("operatingSystem", filterRequest.getOperatingSystem());
	    }

	    if (filterRequest.getScreenSize() != null) {
	        query.setParameter("screenSize", filterRequest.getScreenSize());
	    }

	    if (filterRequest.getItemGen() != null) {
	        query.setParameter("itemGen", filterRequest.getItemGen());
	    }

	    if (filterRequest.getDescription() != null && !filterRequest.getDescription().isEmpty()) {
	        query.setParameter("description", "%" + filterRequest.getDescription().toLowerCase() + "%");
	    }

	    return query.getResultList();
	}

}
