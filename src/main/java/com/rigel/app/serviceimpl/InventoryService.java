package com.rigel.app.serviceimpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rigel.app.dao.IInventoryDao;
import com.rigel.app.model.Inventory;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.service.IInventoryService;
import com.rigel.app.util.AppUtill;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

@Lazy 
@Service
@Transactional
public class InventoryService implements IInventoryService {

    @Autowired
    private IInventoryDao iInventoryDao;

    @Override
    public Inventory saveInventory(Inventory inventory, String existingItemCode, int ownerId, EntityManager entityManager) {
        try {
            // --- 1) Update by existingItemCode if provided ---
            if (existingItemCode != null && !existingItemCode.trim().isEmpty()) {
                TypedQuery<Inventory> queryByItemCode = entityManager.createQuery(
                        "SELECT i FROM Inventory i WHERE i.ownerId = :ownerId AND i.itemCode = :itemCode", Inventory.class)
                        .setParameter("ownerId", ownerId)
                        .setParameter("itemCode", existingItemCode)
                        .setMaxResults(1);

                List<Inventory> results = queryByItemCode.getResultList();
                if (!results.isEmpty()) {
                    Inventory existing = results.get(0);
                    existing.setQuantity(existing.getQuantity() + inventory.getQuantity());
                    return entityManager.merge(existing);
                } else {
                    inventory.setOwnerId(ownerId);
                    entityManager.persist(inventory);
                    return inventory;
                }
            }

            // --- 2) Dynamic null-safe matching on all fields ---
            StringBuilder jpql = new StringBuilder("SELECT i FROM Inventory i WHERE i.ownerId = :ownerId");
            Map<String, Object> params = new HashMap<>();
            params.put("ownerId", ownerId);

            // Optional fields
            if (inventory.getCategory() != null) {
                jpql.append(" AND i.category = :category");
                params.put("category", inventory.getCategory());
            }
            if (inventory.getCategoryType() != null) {
                jpql.append(" AND i.categoryType = :categoryType");
                params.put("categoryType", inventory.getCategoryType());
            }
            if (inventory.getMeasureType() != null) {
                jpql.append(" AND i.measureType = :measureType");
                params.put("measureType", inventory.getMeasureType());
            }
            if (inventory.getBrand() != null) {
                jpql.append(" AND i.brand = :brand");
                params.put("brand", AppUtill.replaceAllSpace(inventory.getBrand()));
            }
            if (inventory.getModelName() != null) {
                jpql.append(" AND i.modelName = :modelName");
                params.put("modelName", AppUtill.replaceAllSpace(inventory.getModelName()));
            }
            if (inventory.getInitialPrice() != null) {
                jpql.append(" AND i.initialPrice = :initialPrice");
                params.put("initialPrice", inventory.getInitialPrice());
            }
            if (inventory.getSellingPrice() != null) {
                jpql.append(" AND i.sellingPrice = :sellingPrice");
                params.put("sellingPrice", inventory.getSellingPrice());
            }
            if (inventory.getItemCondition() != null) {
                jpql.append(" AND i.itemCondition = :itemCondition");
                params.put("itemCondition", inventory.getItemCondition());
            }
            // Null-safe fields
            jpql.append(" AND ((i.ram = :ram) OR (i.ram IS NULL AND :ram IS NULL))");
            params.put("ram", inventory.getRam());

            jpql.append(" AND ((i.ramUnit = :ramUnit) OR (i.ramUnit IS NULL AND :ramUnit IS NULL))");
            params.put("ramUnit", inventory.getRamUnit());

            jpql.append(" AND ((i.storage = :storage) OR (i.storage IS NULL AND :storage IS NULL))");
            params.put("storage", inventory.getStorage());

            jpql.append(" AND ((i.storageUnit = :storageUnit) OR (i.storageUnit IS NULL AND :storageUnit IS NULL))");
            params.put("storageUnit", inventory.getStorageUnit());

            jpql.append(" AND ((i.storageType = :storageType) OR (i.storageType IS NULL AND :storageType IS NULL))");
            params.put("storageType", inventory.getStorageType());

            jpql.append(" AND ((i.description = :description) OR (i.description IS NULL AND :description IS NULL))");
            params.put("description", AppUtill.replaceAllSpace(inventory.getDescription()));

            jpql.append(" AND ((i.itemColor = :itemColor) OR (i.itemColor IS NULL AND :itemColor IS NULL))");
            params.put("itemColor", AppUtill.replaceAllSpace(inventory.getItemColor()));

            jpql.append(" AND ((i.processor = :processor) OR (i.processor IS NULL AND :processor IS NULL))");
            params.put("processor", inventory.getProcessor());

            jpql.append(" AND ((i.operatingSystem = :operatingSystem) OR (i.operatingSystem IS NULL AND :operatingSystem IS NULL))");
            params.put("operatingSystem", AppUtill.replaceAllSpace(inventory.getOperatingSystem()));

            jpql.append(" AND ((i.screenSize = :screenSize) OR (i.screenSize IS NULL AND :screenSize IS NULL))");
            params.put("screenSize", AppUtill.replaceAllSpace(inventory.getScreenSize()));

            jpql.append(" AND ((i.itemGen = :itemGen) OR (i.itemGen IS NULL AND :itemGen IS NULL))");
            params.put("itemGen", AppUtill.replaceAllSpace(inventory.getItemGen()));

            TypedQuery<Inventory> query = entityManager.createQuery(jpql.toString(), Inventory.class);
            params.forEach(query::setParameter);
            query.setMaxResults(1);

            List<Inventory> results = query.getResultList();
            if (!results.isEmpty()) {
                Inventory existing = results.get(0);
                existing.setQuantity(existing.getQuantity() + inventory.getQuantity());
                return entityManager.merge(existing);
            } else {
                inventory.setOwnerId(ownerId);
                entityManager.persist(inventory);
                return inventory;
            }

        } catch (Exception e) {
            throw new RuntimeException("Error saving inventory: " + e.getMessage(), e);
        }
    }

    @Override
    public Inventory updateInventory(Inventory inventory) {
        return iInventoryDao.updateInventory(inventory);
    }

    @Override
    public int deleteInventory(String itemCode, int ownerId) {
        return iInventoryDao.deleteInventory(itemCode, ownerId);
    }

    @Override
    public List<Inventory> searchInventory(SearchCriteria criteria) {
        return iInventoryDao.searchInventory(criteria);
    }

    @Override
    public Inventory getInventryByItemCode(String itemCode, int qty, int ownerId, EntityManager entityManager) {
        try {
            TypedQuery<Inventory> query = entityManager.createQuery(
                    "SELECT i FROM Inventory i WHERE i.ownerId = :ownerId AND i.itemCode = :itemCode", Inventory.class)
                    .setParameter("ownerId", ownerId)
                    .setParameter("itemCode", itemCode)
                    .setMaxResults(1);

            List<Inventory> results = query.getResultList();
            if (results.isEmpty()) {
                throw new RuntimeException("Inventory not found for itemCode: " + itemCode);
            }

            Inventory inventory = results.get(0);
            inventory.setQuantity(inventory.getQuantity() - qty);
            return entityManager.merge(inventory);

        } catch (Exception e) {
            throw new RuntimeException("Error fetching inventory for itemCode: " + itemCode, e);
        }
    }
}

//package com.app.todoapp.serviceimpl;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.app.todoapp.dao.IInventoryDao;
//import com.app.todoapp.model.Inventory;
//import com.app.todoapp.model.dto.SearchCriteria;
//import com.app.todoapp.service.IInventoryService;
//import com.app.todoapp.util.AppUtill;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.TypedQuery;
//
//@Service
//@Transactional
//public class InventoryService implements IInventoryService {
//
//    @Autowired
//    IInventoryDao iInventoryDao;
//
//    @Override
//    public Inventory saveInventory(Inventory inventory, String existingItemCode,int ownerId, EntityManager entityManager) {
//System.out.println("existingcode---"+existingItemCode);
//        try {
//            // 1) Update by existingItemCode if provided
//            if (existingItemCode != null && !existingItemCode.trim().isEmpty()) {
//                TypedQuery<Inventory> queryByItemcode = entityManager.createQuery(
//                        "SELECT i FROM Inventory i WHERE i.ownerId=:ownerId AND i.itemCode = :itemcode", Inventory.class)
//                		 .setParameter("ownerId", ownerId)
//                         .setParameter("itemcode", existingItemCode)
//                        .setMaxResults(1); // fetch only first match
//
//                List<Inventory> results = queryByItemcode.getResultList();
//
//                if (!results.isEmpty()) {
//                    Inventory existing = results.get(0);
//                    existing.setQuantity(existing.getQuantity() + inventory.getQuantity());
//                    return entityManager.merge(existing); // managed entity auto-updates
//                } else {
//                    entityManager.persist(inventory);
//                    entityManager.flush();
//                    entityManager.clear(); // free memory
//                    return inventory;
//                }
//            }
//
//            // 2) Null-safe matching on fields
//            String jpql = "SELECT i FROM Inventory i " +
//                    "WHERE i.ownerId = :ownerId " +
//                    "AND i.category = :category " +
//                    "AND i.categoryType = :categoryType " +
//                    "AND i.measureType = :measureType " +
//                    "AND i.brand = :brand " +
//                    "AND i.modelName = :modelName " +
//                    "AND i.initialPrice = :initialPrice " +
//                    "AND i.sellingPrice = :sellingPrice " +
//                    "AND i.itemCondition = :itemCondition " +
//                    "AND ((i.ram = :ram) OR (i.ram IS NULL AND :ram IS NULL)) " +
//                    "AND ((i.ramUnit = :ramUnit) OR (i.ramUnit IS NULL AND :ramUnit IS NULL)) " +
//                    "AND ((i.storage = :storage) OR (i.storage IS NULL AND :storage IS NULL)) " +
//                    "AND ((i.storageUnit = :storageUnit) OR (i.storageUnit IS NULL AND :storageUnit IS NULL)) " +
//                    "AND ((i.storageType = :storageType) OR (i.storageType IS NULL AND :storageType IS NULL)) " +
//                    "AND ((i.description = :description) OR (i.description IS NULL AND :description IS NULL)) " +
//                    "AND ((i.itemColor = :itemColor) OR (i.itemColor IS NULL AND :itemColor IS NULL)) " +
//                    "AND ((i.processor = :processor) OR (i.processor IS NULL AND :processor IS NULL)) " +
//                    "AND ((i.operatingSystem = :operatingSystem) OR (i.operatingSystem IS NULL AND :operatingSystem IS NULL)) " +
//                    "AND ((i.screenSize = :screenSize) OR (i.screenSize IS NULL AND :screenSize IS NULL)) " +
//                    "AND ((i.itemGen = :itemGen) OR (i.itemGen IS NULL AND :itemGen IS NULL))";
//
//            TypedQuery<Inventory> query = entityManager.createQuery(jpql, Inventory.class)
//            		.setParameter("ownerId", ownerId)
//                    .setParameter("category", inventory.getCategory())
//                    .setParameter("categoryType", inventory.getCategoryType())
//                    .setParameter("measureType", inventory.getMeasureType())
//                    .setParameter("brand",AppUtill.replaceAllSpace(inventory.getBrand()))
//                    .setParameter("modelName", AppUtill.replaceAllSpace(inventory.getModelName()))
//                    .setParameter("initialPrice", inventory.getInitialPrice())
//                    .setParameter("sellingPrice", inventory.getSellingPrice())
//                    .setParameter("itemCondition", inventory.getItemCondition())
//                    .setParameter("ram", inventory.getRam())
//                    .setParameter("ramUnit", inventory.getRamUnit())
//                    .setParameter("storage", inventory.getStorage())
//                    .setParameter("storageUnit", inventory.getStorageUnit())
//                    .setParameter("storageType", inventory.getStorageType())
//                    .setParameter("description", AppUtill.replaceAllSpace(inventory.getDescription()))
//                    .setParameter("itemColor", AppUtill.replaceAllSpace(inventory.getItemColor()))
//                    .setParameter("processor", inventory.getProcessor())
//                    .setParameter("operatingSystem", AppUtill.replaceAllSpace(inventory.getOperatingSystem()))
//                    .setParameter("screenSize", AppUtill.replaceAllSpace(inventory.getScreenSize()))
//                    .setParameter("itemGen", AppUtill.replaceAllSpace(inventory.getItemGen()))
//                    .setMaxResults(1); // fetch only one match
//
//            List<Inventory> results = query.getResultList();
//            if (!results.isEmpty()) {
//                Inventory existing = results.get(0);
//                existing.setQuantity(existing.getQuantity() + inventory.getQuantity());
//                return entityManager.merge(existing);
//            } else {
//                entityManager.persist(inventory);
//                entityManager.flush();
//                entityManager.clear(); // free memory
//                return inventory;
//            }
//
//        } catch (Exception e) {
//            // log and throw
//            throw new RuntimeException("Error saving inventory: " + e.getMessage(), e);
//        }
//    }
//
//    @Override
//    public Inventory updateInventory(Inventory inventory) {
//        return iInventoryDao.updateInventory(inventory);
//    }
//
//    @Override
//    public int deleteInventory(String itemCode,int ownerId) {
//        return iInventoryDao.deleteInventory(itemCode,ownerId);
//    }
//
//    @Override
//    public List<Inventory> searchInventory(SearchCriteria criteria) {
//        return iInventoryDao.searchInventory(criteria);
//    }
//
//    @Override
//    public Inventory getInventryByItemCode(String itemCode, int qty,int ownerId, EntityManager entityManager) {
//        try {
//            TypedQuery<Inventory> query = entityManager.createQuery(
//                    "SELECT i FROM Inventory i WHERE i.ownerId = :ownerId AND i.itemCode = :itemcode", Inventory.class)
//            	    .setParameter("ownerId", ownerId)
//                    .setParameter("itemcode", itemCode)
//                    .setMaxResults(1); // fetch only one
//            Inventory result = query.getResultList().get(0);
//            result.setQuantity(result.getQuantity() - qty);
//            return entityManager.merge(result);
//        } catch (Exception e) {
//            throw new RuntimeException("Inventory not found for itemCode: " + itemCode, e);
//        }
//    }
//}
