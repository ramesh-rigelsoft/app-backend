package com.rigel.app.serviceimpl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
	public Inventory saveInventory(Inventory inventory, EntityManager em) {

		try {
			String fingerPrint = generateFingerprint(inventory);
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

			List<Inventory> results = query.getResultList();

			if (!results.isEmpty()) {
				Inventory existing = results.get(0);
				if(!inventory.getCategory().equalsIgnoreCase(Constaints.SHOP_OWNER_CATEGORY)) {
				   existing.setQuantity(existing.getQuantity() + inventory.getQuantity());
				   return em.merge(existing);
				}else {
				   return null;
				}
			}

			String itemCode = fyIdGeneratorService.generateFyId(inventory.getOwnerId(), "ITM", "");
			inventory.setItemCode(itemCode);
			inventory.setFingerPrint(fingerPrint);
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
		append(sb, inv.getGstRate());
		append(sb, inv.getDescription());

		return sha256(sb.toString());
	}

	private static void append(StringBuilder sb, Object value) {
		if (sb.length() > 0) {
			sb.append("|");
		}
		// IMPORTANT: whitespace preserve ho raha hai
		sb.append(value == null ? "NULL" : value.toString().strip());
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
