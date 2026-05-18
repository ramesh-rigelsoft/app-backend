package com.rigel.app.daoimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.rigel.app.dao.ISupplierDao;
import com.rigel.app.model.Vendors;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.model.dto.SupplierCreteria;
import com.rigel.app.model.dto.VendorsDTO;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class SupplierDao implements ISupplierDao {

	@Autowired
	private EntityManager entityManager;

	@Override
	public Vendors saveSupplier(VendorsDTO dto) {
		return entityManager.merge(toSupplier(dto, ""));
	}

	@Override
	public Vendors updateSupplier(VendorsDTO dto) {
		return entityManager.merge(toSupplier(dto, "id"));
	}

	@Override
	public List<Vendors> searchSupplier(SupplierCreteria criteria) {
		StringBuilder queryBuilder = new StringBuilder("FROM Vendors s WHERE ");
		queryBuilder.append("s.ownerId = :ownerId ");
		
		if (criteria.getSupplierName() != null && !criteria.getSupplierName().isEmpty()) {
		    queryBuilder.append(" AND LOWER(s.supplierName) LIKE :supplierName ");
		}
		if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
			queryBuilder.append("AND s.status = :status ");
		}
		if (criteria.getGstNumber() != null && !criteria.getGstNumber().isEmpty()) {
			queryBuilder.append("AND s.gstNumber = :gst ");
		}
		if (criteria.getPan() != null && !criteria.getPan().isEmpty()) {
			queryBuilder.append("AND s.panNumber = :pan ");
		}

		queryBuilder.append(" ORDER BY s.createdAt DESC");

		var query = entityManager.createQuery(queryBuilder.toString(), Vendors.class);
			query.setParameter("ownerId",+ criteria.getUserId()); // partial match
			
        if (criteria.getSupplierName() != null && !criteria.getSupplierName().isEmpty()) {
        	query.setParameter("supplierName", "%" + criteria.getSupplierName().toLowerCase() + "%");
        }
		if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
			query.setParameter("status", criteria.getStatus());
		}
		if (criteria.getGstNumber() != null && !criteria.getGstNumber().isEmpty()) {
			query.setParameter("gst", criteria.getGstNumber());
		}
		if (criteria.getPan() != null && !criteria.getPan().isEmpty()) {
			query.setParameter("pan", criteria.getPan());
		}
		query.setFirstResult(criteria.getStartIndex());
		query.setMaxResults(criteria.getMaxRecords());
		return query.getResultList();
	}
	
	@Override
	public Vendors findById(String id) {
	    return entityManager.find(Vendors.class, id);
	}

	public Vendors toSupplier(VendorsDTO dto, String id) {

		if (dto == null) {
			return null;
		}

		Vendors vendor = new Vendors();

		vendor.setId(dto.getId() == null ? null : dto.getId());
		vendor.setSupplierName(dto.getSupplierName());
		vendor.setGstNumber(dto.getGstNumber());
		vendor.setPanNumber(dto.getPanNumber());
		vendor.setEmail(dto.getEmail());
		vendor.setPhone(dto.getPhone());
		vendor.setAddress(dto.getAddress());
		vendor.setDistrict(dto.getDistrict());
		vendor.setPinCode(dto.getPinCode());
		vendor.setState(dto.getState());
		vendor.setStateCode(dto.getStateCode());

		vendor.setCreatedAt(LocalDateTime.now());
		vendor.setStatus("active");
		vendor.setOwnerId(dto.getOwnerId());

		return vendor;
	}

	@Override
	public List<Vendors> searchVender(SearchCriteria criteria) {

	    String jpql =
	        "SELECT DISTINCT s FROM Vendors s " +
	        "INNER JOIN FETCH s.items " +
	        "LEFT JOIN FETCH s.vendorPayments " +
	        "WHERE s.ownerId = :ownerId ";

	    if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
	        jpql += " AND s.status = :status ";
	    }

	    jpql += " ORDER BY s.createdAt DESC ";

	    var query = entityManager.createQuery(jpql, Vendors.class);

	    query.setParameter("ownerId", criteria.getUserId());

	    if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
	        query.setParameter("status", criteria.getStatus());
	    }

	    return query.getResultList();
	}

}
