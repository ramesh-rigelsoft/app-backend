package com.rigel.app.daoimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.rigel.app.dao.IInventoryDao;
import com.rigel.app.model.Inventory;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.querybuilder.InventoryQueryBuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class InventoryDaoImpl implements IInventoryDao {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private InventoryQueryBuilder inventoryQueryBuilder;

//	@Override
//	public Inventory saveInventory(Inventory inventory) {
//		String jpql = "SELECT i FROM Inventory i " + "WHERE i.category = :category "
//				+ "AND i.categoryType = :categoryType " + "AND i.measureType = :measureType " + "AND i.brand = :brand "
//				+ "AND i.modelName = :modelName " + "AND i.ram = :ram " + "AND i.ramUnit = :ramUnit "
//				+ "AND i.storage = :storage " + "AND i.storageUnit = :storageUnit "
//				+ "AND i.description = :description " + "AND i.itemcolor = :itemcolor";
//
//		TypedQuery<Inventory> query = entityManager.createQuery(jpql, Inventory.class)
//				.setParameter("category", inventory.getCategory())
//				.setParameter("categoryType", inventory.getCategoryType())
//				.setParameter("measureType", inventory.getMeasureType()).setParameter("brand", inventory.getBrand())
//				.setParameter("modelName", inventory.getModelName()).setParameter("ram", inventory.getRam())
//				.setParameter("ramUnit", inventory.getRamUnit()).setParameter("storage", inventory.getStorage())
//				.setParameter("storageUnit", inventory.getStorageUnit())
//				.setParameter("description", inventory.getDescription())
//				.setParameter("itemcolor", inventory.getItemcolor());
//
//		List<Inventory> results = query.getResultList();
//
//		if (!results.isEmpty()) {
//			// Already exists → increment quantity
//			Inventory existing = results.get(0);
//			existing.setQuantity(existing.getQuantity() + inventory.getQuantity());
//			existing.setInitialPrice(inventory.getInitialPrice());
//			existing.setSellingPrice(inventory.getSellingPrice());
//			// New item → insert
//			entityManager.merge(existing);
//		} else {
//			entityManager.persist(inventory);
//		}
//
//		return null;
//	}

	@Override
	public Inventory updateInventory(Inventory inventory) {
		return entityManager.merge(inventory);
	}

	@Override
	public int deleteInventory(String itemCode,int ownerId) {
	    String hql = "DELETE FROM Inventory i WHERE i.itemCode = :itemCode AND ownerId = :ownerId ";
	    return entityManager.createQuery(hql)
	            .setParameter("itemCode", itemCode)
	            .setParameter("ownerId", ownerId)
		        .executeUpdate();
	}
	
	@Override
	public Inventory findInventoryByCode(String itemCode,int ownerId) {
	    String hql = "SELECT i FROM Inventory i WHERE i.itemCode = :itemCode AND ownerId = :ownerId ";

	    return entityManager
	            .createQuery(hql, Inventory.class)
	            .setParameter("itemCode", itemCode)
	            .setParameter("ownerId", ownerId)
		        .getSingleResult();
	}

	
	@Override
	public int updateInventory(String itemCode, int quantity,int ownerId) {
	    String hql = "update Inventory SET quantity = :quantity WHERE ownerId = :ownerId AND itemCode = :itemCode";
      try {
	    return entityManager.createQuery(hql)
	            .setParameter("quantity", quantity)
	            .setParameter("ownerId", ownerId)
	            .setParameter("itemCode", itemCode)
	            .executeUpdate();
      }catch(Exception e){
    	 e.printStackTrace();
	    return 0;
      }
    }

	
	@Override
	public List<Inventory> searchInventory(SearchCriteria criteria) {

	    StringBuilder jpql = new StringBuilder("SELECT i FROM Inventory i WHERE ");

	    if (criteria.getUserId() != 0) {
	        jpql.append("i.ownerId = :ownerId ");
	    }

	    if (criteria.getCategory() != null) {
	        jpql.append(" AND i.category = :category ");
	    }
	    
	    if (criteria.getCategory() != null) {
	        jpql.append(" AND i.category = :category ");
	    }

	    if (criteria.getCategoryType() != null) {
	        jpql.append(" AND i.categoryType = :categoryType ");
	    }

	    if (criteria.getBrand() != null) {
	        jpql.append(" AND i.brand = :brand ");
	    }

	    if (criteria.getProcessor() != null) {
	        jpql.append(" AND i.processor = :processor ");
	    }

	    if (criteria.getItemCondition() != null) {
	        jpql.append(" AND i.itemCondition = :itemCondition ");
	    }

	    if (criteria.getModelName() != null && !criteria.getModelName().isEmpty()) {
	        jpql.append(" AND LOWER(i.modelName) LIKE :modelName ");
	    }

	    if (criteria.getRam() != null) {
	        jpql.append(" AND i.ram = :ram ");
	    }

	    if (criteria.getStorage() != null) {
	        jpql.append(" AND i.storage = :storage ");
	    }

	    if (criteria.getStorageType() != null) {
	        jpql.append(" AND i.storageType = :storageType ");
	    }

	    if (criteria.getOperatingSystem() != null) {
	        jpql.append(" AND i.operatingSystem = :operatingSystem ");
	    }

	    if (criteria.getScreenSize() != null) {
	        jpql.append(" AND i.screenSize = :screenSize ");
	    }

	    if (criteria.getItemGen() != null) {
	        jpql.append(" AND i.itemGen = :itemGen ");
	    }

	    if (criteria.getDescription() != null && !criteria.getDescription().isEmpty()) {
	        jpql.append(" AND LOWER(i.description) LIKE :description ");
	    }

//	    jpql.append(" ORDER BY i.createdAt DESC ");

	    TypedQuery<Inventory> query = entityManager.createQuery(jpql.toString(), Inventory.class);
	   
	    if (criteria.getUserId() != 0) {
	    	query.setParameter("ownerId",criteria.getUserId());
	    }
	    
	    if (criteria.getCategory() != null) {
	        query.setParameter("category", criteria.getCategory());
	    }

	    if (criteria.getCategoryType() != null) {
	        query.setParameter("categoryType", criteria.getCategoryType());
	    }

	    if (criteria.getBrand() != null) {
	        query.setParameter("brand", criteria.getBrand());
	    }

	    if (criteria.getProcessor() != null) {
	        query.setParameter("processor", criteria.getProcessor());
	    }

	    if (criteria.getItemCondition() != null) {
	        query.setParameter("itemCondition", criteria.getItemCondition());
	    }

	    if (criteria.getModelName() != null && !criteria.getModelName().isEmpty()) {
	        query.setParameter("modelName", "%" + criteria.getModelName().toLowerCase() + "%");
	    }

	    if (criteria.getRam() != null) {
	        query.setParameter("ram", criteria.getRam());
	    }

	    if (criteria.getStorage() != null) {
	        query.setParameter("storage", criteria.getStorage());
	    }

	    if (criteria.getStorageType() != null) {
	        query.setParameter("storageType", criteria.getStorageType());
	    }

	    if (criteria.getOperatingSystem() != null) {
	        query.setParameter("operatingSystem", criteria.getOperatingSystem());
	    }

	    if (criteria.getScreenSize() != null) {
	        query.setParameter("screenSize", criteria.getScreenSize());
	    }

	    if (criteria.getItemGen() != null) {
	        query.setParameter("itemGen", criteria.getItemGen());
	    }

	    if (criteria.getDescription() != null && !criteria.getDescription().isEmpty()) {
	        query.setParameter("description", "%" + criteria.getDescription().toLowerCase() + "%");
	    }
	  System.out.println("start-"+criteria.getStartIndex());
	  System.out.println("max-"+criteria.getMaxRecords());
	    query.setFirstResult(criteria.getStartIndex());
	    query.setMaxResults(criteria.getMaxRecords());	    
	    return query.getResultList();
	}

}
