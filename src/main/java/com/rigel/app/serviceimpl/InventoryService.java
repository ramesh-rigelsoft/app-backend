package com.rigel.app.serviceimpl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import com.rigel.app.util.Constaints;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Lazy
@Service
@Transactional
public class InventoryService implements IInventoryService {

	@Autowired
	private IInventoryDao iInventoryDao;

	@Autowired
	FyIdGeneratorService fyIdGeneratorService;

	@Override
	public Inventory saveInventory(Inventory inventory, EntityManager em, boolean isUpdate) {

		try {
			String fingerPrint = generateFingerprint(inventory);
			System.out.println("fingerPrint:::::::::::"+fingerPrint);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Inventory> cq = cb.createQuery(Inventory.class);
			Root<Inventory> i = cq.from(Inventory.class);

			List<Predicate> predicates = new ArrayList<>();

			// ownerId (mandatory)
			predicates.add(cb.equal(i.get("ownerId"), inventory.getOwnerId()));

			// --- ALL 23 FIELDS NULL SAFE ---
			addIfNotNull(cb, predicates, i, "fingerPrint", fingerPrint);

			cq.where(cb.and(predicates.toArray(new Predicate[0])));

			TypedQuery<Inventory> query = em.createQuery(cq);
			query.setMaxResults(1);

			
			if(isUpdate) {
				String itemCode = inventory.getItemCode();	
			Inventory exinventory = iInventoryDao.findInventoryByCode(itemCode, inventory.getOwnerId());
			String fingerPrintUpdate = generateFingerprint(exinventory);
			System.out.println("fingerPrint:::::::::::"+fingerPrint);
			
			if(exinventory!=null) {
					exinventory.setStatus(true);
					exinventory.setQuantity(exinventory.getQuantity()+inventory.getQuantity());
					exinventory.setCategory(inventory.getCategory());
					exinventory.setCategoryType(inventory.getCategoryType());
					exinventory.setMeasureType(inventory.getMeasureType());
					exinventory.setBrand(inventory.getBrand());
					exinventory.setModelName(inventory.getModelName());
					exinventory.setItemCondition(inventory.getItemCondition());
					exinventory.setItemSource(inventory.getItemSource());
					exinventory.setVendorGSTNumber(inventory.getVendorGSTNumber());
					exinventory.setVendorName(inventory.getVendorName());
					exinventory.setRam(inventory.getRam());
					exinventory.setRamUnit(inventory.getRamUnit());
					exinventory.setStorage(inventory.getStorage());
					exinventory.setStorageType(inventory.getStorageType());
					exinventory.setStorageUnit(inventory.getStorageUnit());
					exinventory.setInitialPrice(inventory.getInitialPrice());
					exinventory.setSellingPrice(inventory.getSellingPrice());
					exinventory.setItemColor(inventory.getItemColor());
					exinventory.setProcessor(inventory.getProcessor());
					exinventory.setOperatingSystem(inventory.getOperatingSystem());
					exinventory.setScreenSize(inventory.getScreenSize());
					exinventory.setItemGen(inventory.getItemGen());
					exinventory.setGstRate(inventory.getGstRate());
					exinventory.setSerialNumber(inventory.getSerialNumber());
					exinventory.setDescription(inventory.getDescription());
					exinventory.setImage(inventory.getImage());
					exinventory.setUpdatedAt(LocalDateTime.now());
					exinventory.setAdditionalDetails(inventory.getAdditionalDetails());
					exinventory.setFingerPrint(fingerPrintUpdate);
					return iInventoryDao.updateInventory(exinventory);
				}	
			}
			
			List<Inventory> results = query.getResultList();
			if (!results.isEmpty()) {
				if(!inventory.getCategory().equalsIgnoreCase(Constaints.SHOP_OWNER_CATEGORY)) {
				   Inventory existing = results.get(0);
				   existing.setQuantity(existing.getQuantity() + inventory.getQuantity());
				   existing.setUpdatedAt(LocalDateTime.now());
				   return em.merge(existing);
				}else {
				   return null;
				}
			}
			
			String itemCode = fyIdGeneratorService.generateItemCode(inventory.getOwnerId(), "ITM");
			inventory.setItemCode(itemCode);
			inventory.setFingerPrint(fingerPrint);
			inventory.setCreatedAt(LocalDateTime.now());
			em.persist(inventory);
			return inventory;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error saving inventory", e);
		}
	}

	private void addIfNotNull(CriteriaBuilder cb, List<Predicate> predicates, Root<Inventory> root, String field,
			Object value) {
		if (value != null) {
			predicates.add(cb.equal(root.get(field), value));
		}

//		if (value != null) {
//		    predicates.add(cb.equal(root.get(field), value));
//		} else {
//		    predicates.add(cb.isNull(root.get(field)));
//		}
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
			TypedQuery<Inventory> query = entityManager
					.createQuery("SELECT i FROM Inventory i WHERE i.ownerId = :ownerId AND i.itemCode = :itemCode",
							Inventory.class)
					.setParameter("ownerId", ownerId).setParameter("itemCode", itemCode).setMaxResults(1);

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

	public static String generateFingerprint(Inventory inv) {
		StringBuilder sb = new StringBuilder();
		append(sb, inv.getOwnerId());
		append(sb, inv.getCategory());
		append(sb, inv.getCategoryType());
		append(sb, inv.getMeasureType());
		append(sb, inv.getBrand());
		append(sb, inv.getModelName());
		append(sb, inv.getItemCondition());
		append(sb, inv.getRam());
		append(sb, inv.getRamUnit());
		append(sb, inv.getStorage());
		append(sb, inv.getStorageType());
		append(sb, inv.getStorageUnit());
		append(sb, inv.getInitialPrice());
		append(sb, inv.getSellingPrice());
		append(sb, inv.getItemColor());
		append(sb, inv.getProcessor());
		append(sb, inv.getOperatingSystem());
		append(sb, inv.getScreenSize());
		append(sb, inv.getItemGen());
		append(sb, inv.getDescription());
		return sha256(sb.toString());
	}

	private static void append(StringBuilder sb, Object value) {
		if (sb.length() > 0) {
			sb.append("|");
		}
		sb.append(value == null ? "NULL" : value.toString().replaceAll("\\s+", " ").toLowerCase().strip());
	}

	private static String sha256(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

			StringBuilder hex = new StringBuilder();
			for (byte b : hash) {
				String h = Integer.toHexString(0xff & b);
				if (h.length() == 1)
					hex.append('0');
				hex.append(h);
			}
			return hex.toString();

		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Error generating fingerprint", e);
		}
	}
}
