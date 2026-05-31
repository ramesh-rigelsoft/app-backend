package com.rigel.app.daoimpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import com.rigel.app.dao.ISupplierDao;
import com.rigel.app.model.Items;
import com.rigel.app.model.VendorPayments;
import com.rigel.app.model.Vendors;
import com.rigel.app.model.dto.InvoicePaymentDTO;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.model.dto.SupplierCreteria;
import com.rigel.app.model.dto.TransactionDTO;
import com.rigel.app.model.dto.VendorPaymentResponseDTO;
import com.rigel.app.model.dto.VendorsDTO;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
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

		if (criteria.getCompanyName() != null && !criteria.getCompanyName().isEmpty()) {
			queryBuilder.append(" AND LOWER(s.companyName) LIKE :companyName ");
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
		query.setParameter("ownerId", +criteria.getUserId()); // partial match

		if (criteria.getCompanyName() != null && !criteria.getCompanyName().isEmpty()) {
			query.setParameter("companyName", "%" + criteria.getCompanyName().toLowerCase() + "%");
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
		vendor.setCompanyName(dto.getCompanyName());
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

		String jpql = "SELECT DISTINCT s FROM Vendors s " + "INNER JOIN FETCH s.items i "
				+ "LEFT JOIN FETCH s.vendorPayments " + "WHERE s.ownerId = :ownerId ";

		if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
			jpql += " AND s.status = :status ";
		}
		System.out.println("search---------" + criteria.getSearchKeyword());

		// 🔍 Invoice number search (ITEM level)
		if (criteria.getSearchKeyword() != null && !criteria.getSearchKeyword().isEmpty()) {
			jpql += " AND LOWER(i.vendorInvoiceNumber) LIKE LOWER(:searchKeywords)"
					+ "OR LOWER(s.companyName) LIKE LOWER(:searchKeywords)";
		}

		jpql += " ORDER BY s.createdAt DESC ";

		var query = entityManager.createQuery(jpql, Vendors.class);

		query.setParameter("ownerId", criteria.getUserId());

		if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
			query.setParameter("status", criteria.getStatus());
		}

		if (criteria.getSearchKeyword() != null && !criteria.getSearchKeyword().isEmpty()) {
			query.setParameter("searchKeywords", "%" + criteria.getSearchKeyword().toLowerCase() + "%");
		}

		query.setFirstResult(criteria.getStartIndex());
		query.setMaxResults(criteria.getMaxRecords());
		return query.getResultList();
	}

	@Override
	public List<VendorPaymentResponseDTO> searchVenderPayment(SearchCriteria criteria) {

	    int page = criteria.getStartIndex();
	    int size = criteria.getMaxRecords();

	    // =====================================================
	    // STEP 1: PAGINATED INVOICE LIST
	    // =====================================================

	    StringBuilder invoiceJpql = new StringBuilder(
	        "SELECT i.vendorInvoiceNumber " +
	        "FROM Items i " +
	        "JOIN i.vendors v " +
	        "WHERE v.ownerId = :ownerId " +
	        "AND i.vendorInvoiceNumber IS NOT NULL " +
	        "AND TRIM(i.vendorInvoiceNumber) <> '' "
	    );

	    if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
	        invoiceJpql.append(" AND v.status = :status ");
	    }

	    if (criteria.getSearchKeyword() != null && !criteria.getSearchKeyword().isEmpty()) {

	        invoiceJpql.append(
	            " AND (LOWER(i.vendorInvoiceNumber) LIKE :searchKeywords " +
	            "OR LOWER(v.companyName) LIKE :searchKeywords) "
	        );
	    }

	    invoiceJpql.append(" GROUP BY i.vendorInvoiceNumber ");
	    invoiceJpql.append(" ORDER BY MAX(i.createdAt) DESC ");

	    TypedQuery<String> invoiceQuery =
	        entityManager.createQuery(invoiceJpql.toString(), String.class);

	    invoiceQuery.setParameter("ownerId", criteria.getUserId());

	    if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
	        invoiceQuery.setParameter("status", criteria.getStatus());
	    }

	    if (criteria.getSearchKeyword() != null && !criteria.getSearchKeyword().isEmpty()) {

	        invoiceQuery.setParameter(
	            "searchKeywords",
	            "%" + criteria.getSearchKeyword().toLowerCase() + "%"
	        );
	    }

	    // ✅ PAGINATION
	    invoiceQuery.setFirstResult(page);
	    invoiceQuery.setMaxResults(size);

	    List<String> invoiceNumbers = invoiceQuery.getResultList();

	    if (invoiceNumbers.isEmpty()) {
	        return new ArrayList<>();
	    }

	    // =====================================================
	    // STEP 2: TOTAL AMOUNT
	    // total = SUM(quantity * initialPrice)
	    // =====================================================

	    List<Object[]> totalResults = entityManager.createQuery(
	        "SELECT i.vendorInvoiceNumber, " +
	        "SUM(COALESCE(i.quantity,0) * COALESCE(i.initialPrice,0)) " +
	        "FROM Items i " +
	        "WHERE i.vendorInvoiceNumber IN :invoiceNumbers " +
	        "GROUP BY i.vendorInvoiceNumber",
	        Object[].class
	    )
	    .setParameter("invoiceNumbers", invoiceNumbers)
	    .getResultList();

	    Map<String, BigDecimal> totalMap = new HashMap<>();

	    for (Object[] row : totalResults) {

	        String invoiceNumber = (String) row[0];

	        BigDecimal totalAmount =
	        	    row[1] != null
	        	        ? new BigDecimal(((Number) row[1]).toString())
	        	        : BigDecimal.ZERO;

	        totalMap.put(invoiceNumber, totalAmount);
	    }

	    // =====================================================
	    // STEP 3: PAID AMOUNT
	    // paid = SUM(paidAmount)
	    // =====================================================

	    List<Object[]> paidResults = entityManager.createQuery(
	        "SELECT vp.vendorInvoiceNumber, " +
	        "SUM(COALESCE(vp.paidAmount,0)) " +
	        "FROM VendorPayments vp " +
	        "WHERE vp.vendorInvoiceNumber IN :invoiceNumbers " +
	        "GROUP BY vp.vendorInvoiceNumber",
	        Object[].class
	    )
	    .setParameter("invoiceNumbers", invoiceNumbers)
	    .getResultList();

	    Map<String, BigDecimal> paidMap = new HashMap<>();

	    for (Object[] row : paidResults) {

	        String invoiceNumber = (String) row[0];

	        BigDecimal paidAmount =
	        	    row[1] != null
	        	        ? new BigDecimal(((Number) row[1]).toString())
	        	        : BigDecimal.ZERO;
	        paidMap.put(invoiceNumber, paidAmount);
	    }

	    // =====================================================
	    // STEP 4: TRANSACTION HISTORY
	    // =====================================================

	    List<VendorPayments> paymentTransactions = entityManager.createQuery(
	        "SELECT vp " +
	        "FROM VendorPayments vp " +
	        "WHERE vp.vendorInvoiceNumber IN :invoiceNumbers " +
	        "ORDER BY vp.createdAt DESC",
	        VendorPayments.class
	    )
	    .setParameter("invoiceNumbers", invoiceNumbers)
	    .getResultList();

	    Map<String, List<TransactionDTO>> transactionMap = new HashMap<>();

	    for (VendorPayments payment : paymentTransactions) {

	        String invoiceNumber = payment.getVendorInvoiceNumber();

	        TransactionDTO tx = new TransactionDTO();

	        tx.setDate(payment.getCreatedAt());

	        tx.setAmount(
	        	    payment.getPaidAmount() != null
	        	        ? payment.getPaidAmount()
	        	        : BigDecimal.ZERO
	        	);

	        tx.setStatus("paid");
	        tx.setComments(payment.getComments());
	        tx.setPaymentModes(payment.getPaymentModes());

	        transactionMap
	            .computeIfAbsent(invoiceNumber, k -> new ArrayList<>())
	            .add(tx);
	    }

	    // =====================================================
	    // STEP 5: FETCH VENDOR INFO
	    // =====================================================

	    List<Object[]> vendorResults = entityManager.createQuery(
	        "SELECT DISTINCT v.id, v.companyName, v.gstNumber, i.vendorInvoiceNumber " +
	        "FROM Items i " +
	        "JOIN i.vendors v " +
	        "WHERE i.vendorInvoiceNumber IN :invoiceNumbers",
	        Object[].class
	    )
	    .setParameter("invoiceNumbers", invoiceNumbers)
	    .getResultList();

	    // =====================================================
	    // STEP 6: BUILD RESPONSE
	    // =====================================================

	    Map<String, VendorPaymentResponseDTO> vendorMap = new LinkedHashMap<>();

	    for (Object[] row : vendorResults) {

	        String vendorId = (String) row[0];
	        String vendorName = (String) row[1];
	        String gstNumber = (String) row[2];
	        String invoiceNumber = (String) row[3];

	        if (invoiceNumber == null || invoiceNumber.trim().isEmpty()) {
	            continue;
	        }

	        VendorPaymentResponseDTO vendorDTO =
	            vendorMap.computeIfAbsent(vendorId, k -> {

	                VendorPaymentResponseDTO dto =
	                    new VendorPaymentResponseDTO();

	                dto.setId(vendorId);
	                dto.setVendorName(vendorName);
	                dto.setGstNumber(gstNumber);
	                dto.setInvoices(new ArrayList<>());

	                return dto;
	            });

	        // ✅ avoid duplicate invoice in same vendor
	        boolean invoiceExists =
	            vendorDTO.getInvoices()
	                .stream()
	                .anyMatch(inv ->
	                    invoiceNumber.equals(inv.getInvoiceNumber())
	                );

	        if (invoiceExists) {
	            continue;
	        }

	        BigDecimal totalAmount =
	            totalMap.getOrDefault(invoiceNumber, BigDecimal.ZERO);

	        BigDecimal paidAmount =
	            paidMap.getOrDefault(invoiceNumber, BigDecimal.ZERO);

	        BigDecimal pendingAmount = totalAmount.subtract(paidAmount);

	        if (pendingAmount.compareTo(BigDecimal.ZERO) < 0) {
	            pendingAmount = BigDecimal.ZERO;
	        }

	        InvoicePaymentDTO invoiceDTO =
	            new InvoicePaymentDTO();

	        invoiceDTO.setInvoiceNumber(invoiceNumber);

	        invoiceDTO.setTotalAmount(totalAmount);

	        invoiceDTO.setPaidAmount(paidAmount);

	        invoiceDTO.setPendingAmount(pendingAmount);

	        // ✅ TRANSACTION HISTORY
	        invoiceDTO.setTransactions(
	            transactionMap.getOrDefault(
	                invoiceNumber,
	                new ArrayList<>()
	            )
	        );

	        vendorDTO.getInvoices().add(invoiceDTO);
	    }

	    // =====================================================
	    // FINAL RESPONSE
	    // =====================================================

	    return new ArrayList<>(vendorMap.values());
	}
	
}