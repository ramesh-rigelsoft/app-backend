package com.rigel.app.querybuilder;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rigel.app.model.Items;
import com.rigel.app.model.dto.SearchCriteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class ItemsQueryBuilder {


	public List<Items> searchItems(SearchCriteria criteria, EntityManager entityManager) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Items> cq = cb.createQuery(Items.class);
		Root<Items> root = cq.from(Items.class);

		List<Predicate> predicates = new ArrayList<>();

		// String filters
		addInt(cb, root, predicates, "ownerId", criteria.getUserId());
		
		addString(cb, root, predicates, "itemCode", criteria.getItemCode());
		addString(cb, root, predicates, "category", criteria.getCategory());
		addString(cb, root, predicates, "categoryType", criteria.getCategoryType());
		addString(cb, root, predicates, "itemType", criteria.getItemType());
		addString(cb, root, predicates, "measureType", criteria.getMeasureType());
		addString(cb, root, predicates, "brand", criteria.getBrand());
		addString(cb, root, predicates, "ramUnit", criteria.getRamUnit());
		addString(cb, root, predicates, "storageUnit", criteria.getStorageUnit());

		// LIKE modelName
		if (isValid(criteria.getModelName())) {
			predicates
					.add(cb.like(cb.lower(root.get("modelName")), "%" + criteria.getModelName().toLowerCase() + "%"));
		}

		// Numeric safely parsed
		addParsedInt(cb, root, predicates, "ram", criteria.getRam());
		addParsedInt(cb, root, predicates, "storage", criteria.getStorage());
		addParsedInt(cb, root, predicates, "quantity", criteria.getQuantity());

		// Initial price >=
		if (criteria.getInitialPrice() != null) {
			predicates.add(cb.greaterThanOrEqualTo(root.get("initialPrice"), criteria.getInitialPrice()));
		}

		// Selling price <=
		if (criteria.getSellingPrice() != null) {
			predicates.add(cb.lessThanOrEqualTo(root.get("sellingPrice"), criteria.getSellingPrice()));
		}

		cq.where(predicates.toArray(new Predicate[0]));

		// Order
		cq.orderBy("DESC".equalsIgnoreCase(criteria.getOrder()) ? cb.desc(root.get("id")) : cb.asc(root.get("id")));

		// Query + pagination
		TypedQuery<Items> query = entityManager.createQuery(cq);
		query.setFirstResult(Math.max(criteria.getStartIndex(), 0));
		query.setMaxResults(criteria.getMaxRecords() <= 0 ? 50 : criteria.getMaxRecords());
		query.getResultList().stream().forEach(a -> {
			System.out.println("-------------ssssssssssss-----------" + a);
		});

		return query.getResultList();
	}

	private boolean isValid(String value) {
		return value != null && !value.trim().isEmpty();
	}

	private void addString(CriteriaBuilder cb, Root<?> root, List<Predicate> predicates, String field, String value) {
		if (isValid(value)) {
			predicates.add(cb.equal(root.get(field), value));
		}
	}

	private void addParsedInt(CriteriaBuilder cb, Root<?> root, List<Predicate> predicates, String field,
			String value) {
		if (isValid(value)) {
			try {
				Integer number = Integer.parseInt(value.trim());
				predicates.add(cb.equal(root.get(field), number));
			} catch (NumberFormatException ignored) {
				// Ignore bad numeric input
			}
		}
	}
	private void addInt(CriteriaBuilder cb, Root<?> root, List<Predicate> predicates, String field,
			int value) {
			try {
				predicates.add(cb.equal(root.get(field), value));
			} catch (NumberFormatException ignored) {
				// Ignore bad numeric input
			}		
	}
}
