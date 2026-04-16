package com.rigel.app.daoimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.rigel.app.dao.ISupplierDao;
import com.rigel.app.model.Supplier;
import com.rigel.app.model.dto.SupplierCreteria;
import com.rigel.app.model.dto.SupplierDTO;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class SupplierDao implements ISupplierDao {

	@Autowired
	private EntityManager entityManager;

	@Override
	public Supplier saveSupplier(SupplierDTO dto) {
		return entityManager.merge(toSupplier(dto, ""));
	}

	@Override
	public Supplier updateSupplier(SupplierDTO dto) {
		return entityManager.merge(toSupplier(dto, "id"));
	}

	@Override
	public List<Supplier> searchSupplier(SupplierCreteria criteria) {
		StringBuilder queryBuilder = new StringBuilder("FROM Supplier s WHERE ");
		queryBuilder.append("s.ownerId = :ownerId ");
		
		if (criteria.getSupplierName() != null && !criteria.getSupplierName().isEmpty()) {
			queryBuilder.append("AND s.supplierName LIKE :supplierName ");
		}
		if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
			queryBuilder.append("AND s.status = :status ");
		}
		if (criteria.getGst() != null && !criteria.getGst().isEmpty()) {
			queryBuilder.append("AND s.gstNumber = :gst ");
		}
		if (criteria.getPan() != null && !criteria.getPan().isEmpty()) {
			queryBuilder.append("AND s.panNumber = :pan ");
		}

		queryBuilder.append(" ORDER BY s.createdAt DESC");

		var query = entityManager.createQuery(queryBuilder.toString(), Supplier.class);
			query.setParameter("ownerId",+ criteria.getOwnerId()); // partial match
        if (criteria.getSupplierName() != null && !criteria.getSupplierName().isEmpty()) {
			query.setParameter("supplierName", "%" + criteria.getSupplierName() + "%"); // partial match
		}
		if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
			query.setParameter("status", criteria.getStatus());
		}
		if (criteria.getGst() != null && !criteria.getGst().isEmpty()) {
			query.setParameter("gst", criteria.getGst());
		}
		if (criteria.getPan() != null && !criteria.getPan().isEmpty()) {
			query.setParameter("pan", criteria.getPan());
		}

		return query.getResultList();
	}

	public Supplier toSupplier(SupplierDTO dto, String id) {

		if (dto == null) {
			return null;
		}

		return Supplier.builder()
				.id(dto.getId() == null ? null : dto.getId())
				.supplierName(dto.getSupplierName())
				.gstNumber(dto.getGstNumber())
				.panNumber(dto.getPanNumber())
				.email(dto.getEmail())
				.phone(dto.getPhone())
				.address(dto.getAddress())
				.district(dto.getDistrict())
				.pinCode(dto.getPinCode())
				.createdAt(LocalDateTime.now())
				.status("active")
				.ownerId(dto.getOwnerId())
				.build();
	}
}
