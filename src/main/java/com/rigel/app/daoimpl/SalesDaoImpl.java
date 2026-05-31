package com.rigel.app.daoimpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.rigel.app.dao.IItemsDao;
import com.rigel.app.dao.ISalesDao;
import com.rigel.app.model.BuyerInfo;
import com.rigel.app.model.Inventory;
import com.rigel.app.model.Items;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.dto.ReportSummaryDTO;
import com.rigel.app.model.dto.SalesInfoDto;
import com.rigel.app.model.dto.SalesInfoDtoResponseList;
import com.rigel.app.model.dto.SearchCriteria;
//import com.rigel.app.querybuilder.SalesQueryBuilder;
import com.rigel.app.service.IInventoryService;
import com.rigel.app.util.Constaints;
import com.rigel.app.util.DateUtility;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class SalesDaoImpl implements ISalesDao {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	IInventoryService iInventoryService;

	@Override
	public List<SalesInfo> saveSalesInfo(List<SalesInfo> salesInfoList) {

		if (salesInfoList == null || salesInfoList.isEmpty()) {
			return Collections.emptyList();
		}

		BuyerInfo buyerInfo = salesInfoList.get(0).getBuyerInfo();

		if (buyerInfo == null) {
			throw new RuntimeException("BuyerInfo is required");
		}

		// SAVE / UPDATE BUYER
		BigDecimal total = salesInfoList.stream()
		        .map(s -> BigDecimal.valueOf(s.getSoldPrice()))
		        .reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal paidAmount = buyerInfo.getPaidAmount();

		if (paidAmount.compareTo(total) != 0) {
		    buyerInfo.setPendingPaymentStatus(Constaints.PENDING_PAYMENT_STATUS);
		} else {
		    buyerInfo.setPendingPaymentStatus(Constaints.CLEARED_PAYMENT_STATUS);
		}
		
		BuyerInfo savedBuyer = entityManager.merge(buyerInfo);

		// SET SAME BUYER INTO ALL SALES
		salesInfoList.forEach(s -> s.setBuyerInfo(savedBuyer));

		List<SalesInfo> savedSales = new ArrayList<>();

		int batchSize = 10;

		for (int i = 0; i < salesInfoList.size(); i++) {

			SalesInfo saved = entityManager.merge(salesInfoList.get(i));

			savedSales.add(saved);

			if (i > 0 && i % batchSize == 0) {

				entityManager.flush();
				entityManager.clear();
			}
		}

		return savedSales;
	}

	@Override
	public SalesInfo updateSalesInfo(SalesInfo salesInfo) {
		try {
			return entityManager.merge(salesInfo);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public SalesInfo findById(String id, int ownerId) {

		String jpql = """
				    SELECT s
				    FROM SalesInfo s
				    JOIN FETCH s.buyerInfo b
				    WHERE s.id = :id
				    AND s.ownerId = :ownerId
				    AND s.status = true
				""";

		try {
			return entityManager.createQuery(jpql, SalesInfo.class).setParameter("id", id)
					.setParameter("ownerId", ownerId).getSingleResult();

		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<SalesInfo> searchSalesInfo(SearchCriteria criteria) {

		StringBuilder jpql = new StringBuilder(
				"SELECT i FROM SalesInfo i JOIN BuyerInfo bi ON bi.id = i.buyerInfo.id WHERE i.status=true AND ");
		Map<String, Object> params = new HashMap<>();
		jpql.append(" i.ownerId = :ownerId ");
		params.put("ownerId", criteria.getUserId());
		if (criteria.getInvoiceNumber() != null && !criteria.getInvoiceNumber().strip().isEmpty()) {
			jpql.append(" AND i.buyerInfo.invoiceNumber = :invoiceNumber");
			params.put("invoiceNumber", criteria.getInvoiceNumber().strip());

		}

		if (criteria.getSearchKeyword() != null && !criteria.getSearchKeyword().strip().isEmpty()) {
			jpql.append("""
					    AND (
					        LOWER(i.buyerInfo.invoiceNumber) LIKE :search
					        OR LOWER(i.buyerInfo.custumberId) LIKE :search
					        OR LOWER(i.buyerInfo.buyerName) LIKE :search
					        OR LOWER(i.buyerInfo.emailId) LIKE :search
					        OR LOWER(i.buyerInfo.mobileNumber) LIKE :search
					        OR LOWER(i.buyerInfo.countryCode) LIKE :search
					        OR LOWER(i.buyerInfo.buyerAddress) LIKE :search
					        OR LOWER(i.itemCode) LIKE :search
					        OR LOWER(i.modelName) LIKE :search
					        OR LOWER(i.brand) LIKE :search
					        OR LOWER(i.categoryType) LIKE :search
					        OR LOWER(i.description) LIKE :search
					     OR LOWER(i.vendorName) LIKE :search
					     OR LOWER(i.vendorGSTNumber) LIKE :search
					     OR LOWER(i.itemCondition) LIKE :search
					    )
					""");

			params.put("search", "%" + criteria.getSearchKeyword().toLowerCase().strip() + "%");
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

			LocalDateTime start = DateUtility.parseToDateTimes(criteria.getStartDate(), false);
			LocalDateTime end = DateUtility.parseToDateTimes(criteria.getEndDate(), true);
			System.out.println("start----" + start);
			System.out.println("end----" + end);
			jpql.append(" AND i.createdAt BETWEEN :startDate AND :endDate ");
			params.put("startDate", start);
			params.put("endDate", end);
		}

		jpql.append(" ORDER BY i.createdAt DESC");

		var query = entityManager.createQuery(jpql.toString(), SalesInfo.class);

		// set parameters dynamically
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		if (!criteria.isIsdownload()) {
			System.out.println("criteria.getStartIndex()-----------" + criteria.getStartIndex());
			query.setFirstResult(criteria.getStartIndex());
			query.setMaxResults(criteria.getMaxRecords());
		}
		return query.getResultList();
	}

	@Override
	public boolean deleteById(String deviceId, int ownerId) {

		String jpql = """
				UPDATE SalesInfo s
				SET s.status = false
				WHERE s.repaireDevice.id = :id
				AND s.ownerId = :ownerId
				""";

		int updatedCount = entityManager.createQuery(jpql).setParameter("id", deviceId).setParameter("ownerId", ownerId)
				.executeUpdate();

		return updatedCount > 0;
	}

	@Override
	public int deleteBySalesId(String ids, int ownerId) {

		String jpql = """
				UPDATE SalesInfo s
				SET s.status = false
				WHERE s.id = :id
				AND s.ownerId = :ownerId
				""";

		int updatedCount = entityManager.createQuery(jpql).setParameter("id", ids).setParameter("ownerId", ownerId)
				.executeUpdate();

		return updatedCount;
	}

	@Override
	public int permantalyDeleteBySalesId(String id, int ownerId) {

		String jpql = """
				DELETE FROM SalesInfo s
				WHERE s.id = :id
				AND s.ownerId = :ownerId
				""";

		int deletedCount = entityManager.createQuery(jpql).setParameter("id", id).setParameter("ownerId", ownerId)
				.executeUpdate();

		return deletedCount;
	}

	@Override
	public List<SalesInfo> fetchSalesByRepaireDevice(String deviceId, int ownerId) {

		if (deviceId == null || deviceId.isBlank()) {
			return Collections.emptyList();
		}

		String jpql = """
				SELECT s
				FROM SalesInfo s
				WHERE s.repaireDevice.id = :deviceId
				AND s.ownerId = :ownerId
				AND s.status = true
				""";

		return entityManager.createQuery(jpql, SalesInfo.class).setParameter("deviceId", deviceId)
				.setParameter("ownerId", ownerId).getResultList();
	}

	@Override
	public ReportSummaryDTO getReportSummary(SearchCriteria criteria) {

		Map<String, Object> params = new HashMap<>();

		// =========================================================
		// COMMON WHERE CLAUSE
		// =========================================================
		StringBuilder whereClause = new StringBuilder("""
				    WHERE s.status = true
				    AND s.returnStatus = false
				""");

		// =========================================================
		// OWNER FILTER
		// =========================================================
		whereClause.append(" AND s.ownerId = :ownerId ");
		params.put("ownerId", criteria.getUserId());

		// =========================================================
		// SEARCH FILTER
		// =========================================================
		if (criteria.getSearchKeyword() != null && !criteria.getSearchKeyword().strip().isEmpty()) {

			whereClause.append("""
					    AND (
					        LOWER(b.invoiceNumber) LIKE :search
					        OR LOWER(b.custumberId) LIKE :search
					        OR LOWER(b.buyerName) LIKE :search
					        OR LOWER(b.emailId) LIKE :search
					        OR LOWER(b.mobileNumber) LIKE :search
					        OR LOWER(b.countryCode) LIKE :search
					        OR LOWER(b.buyerAddress) LIKE :search

					        OR LOWER(s.itemCode) LIKE :search
					        OR LOWER(s.modelName) LIKE :search
					        OR LOWER(s.brand) LIKE :search
					        OR LOWER(s.categoryType) LIKE :search
					        OR LOWER(s.description) LIKE :search
					        OR LOWER(s.vendorName) LIKE :search
					        OR LOWER(s.vendorGSTNumber) LIKE :search
					        OR LOWER(s.itemCondition) LIKE :search
					    )
					""");

			params.put("search", "%" + criteria.getSearchKeyword().toLowerCase().strip() + "%");
		}

		// =========================================================
		// DATE FILTER
		// =========================================================
		if (criteria.getStartDate() != null && criteria.getEndDate() != null) {

			LocalDateTime start = DateUtility.parseToDateTimes(criteria.getStartDate(), false);

			LocalDateTime end = DateUtility.parseToDateTimes(criteria.getEndDate(), true);

			whereClause.append(" AND s.createdAt BETWEEN :startDate AND :endDate ");

			params.put("startDate", start);
			params.put("endDate", end);
		}

		// =========================================================
		// SALES SUMMARY QUERY
		// =========================================================
		Query summaryQuery = entityManager.createNativeQuery("""
			    SELECT
			        s.total_quantity,
			        s.total_initial_price,
			        s.total_selling_price,
			        s.total_sold_price,
			        b.total_paid_amount,
			        b.total_borrow_amount
			    FROM
			    (
			        SELECT
			            COALESCE(SUM(quantity),0) AS total_quantity,
			            COALESCE(SUM(initial_price),0) AS total_initial_price,
			            COALESCE(SUM(selling_price),0) AS total_selling_price,
			            COALESCE(SUM(sold_price),0) AS total_sold_price
			        FROM sales_info
			        WHERE status = true
			          AND return_status = false
			          AND owner_id = :ownerId
			    ) s
			    CROSS JOIN
			    (
			        SELECT
			            COALESCE(SUM(PAIDAMOUNT),0) AS total_paid_amount,
			            COALESCE(SUM(borrowAmount),0) AS total_borrow_amount
			        FROM buyer_info
			        WHERE id IN (
			            SELECT DISTINCT buyerInfo
			            FROM sales_info
			            WHERE status = true
			              AND return_status = false
			              AND owner_id = :ownerId
			        )
			    ) b
			""");

			summaryQuery.setParameter("ownerId", criteria.getUserId());

			Object[] result = (Object[]) summaryQuery.getSingleResult();

			Long totalSoldCount =
			        ((Number) result[0]).longValue();

			BigDecimal totalInitialPrice =
			        BigDecimal.valueOf(((Number) result[1]).doubleValue());

			BigDecimal totalSellingPrice =
			        BigDecimal.valueOf(((Number) result[2]).doubleValue());

			BigDecimal totalSoldPrice =
			        BigDecimal.valueOf(((Number) result[3]).doubleValue());

			BigDecimal totalPaidAmount =
			        BigDecimal.valueOf(((Number) result[4]).doubleValue());

			BigDecimal pending = BigDecimal.valueOf(((Number) result[5]).doubleValue());

		// =========================================================
		// FINAL TOTAL PAID AMOUNT
		// totalPaidAmount = paid + rest
		// =========================================================
			
		// =========================================================
		// SALES LIST QUERY
		// =========================================================
		TypedQuery<SalesInfo> listQuery = entityManager.createQuery("""
				    SELECT s
				    FROM SalesInfo s
				    INNER JOIN FETCH s.buyerInfo b
				""" + whereClause + """
				    ORDER BY s.createdAt DESC
				""", SalesInfo.class);

		params.forEach(listQuery::setParameter);

		// =========================================================
		// SAFETY LIMIT
		// =========================================================
		listQuery.setMaxResults(5000);

		List<SalesInfo> salesEntities = listQuery.getResultList();

		// =========================================================
		// DTO MAPPING
		// =========================================================

		List<SalesInfoDtoResponseList> salesList = salesEntities.stream().map((SalesInfo s) -> {

			BuyerInfo b = s.getBuyerInfo();
			BigDecimal paidAmount = b != null ? b.getPaidAmount() : BigDecimal.ZERO;
			BigDecimal borrowAmount = b != null ? b.getBorrowAmount() : BigDecimal.ZERO;
			BigDecimal totalAmount = b != null ? b.getTotalAmount() : BigDecimal.ZERO;

//			double borrowAmount = 0.0;
//			borrowAmount = b.getBorrowAmount();

			// =========================
			// totalPaidAmount = paid + rest
			// =========================
//			double totalPaidAmountRow = paidAmount;

			double soldPrice = s.getSoldPrice() != null ? s.getSoldPrice() : 0.0;

			// =========================
			// pending = sold - totalPaidAmount
			// =========================

//			double pendingAmount = borrowAmount;//soldPrice - totalPaidAmountRow;

			return SalesInfoDtoResponseList.builder()

					// ================= BUYER =================
					.invoiceNumber(b != null ? b.getInvoiceNumber() : null)

					.custumberId(b != null ? b.getCustumberId() : null)

					.buyerName(b != null ? b.getBuyerName() : null)

					.mobileNumber(b != null ? b.getMobileNumber() : null)

					.emailId(b != null ? b.getEmailId() : null)

					.companyName(b != null ? b.getCompanyName() : null)

					.gstNumber(b != null ? b.getGstNumber() : null)

					.state(b != null ? b.getState() : null)

					.paymentModes(b != null ? b.getPaymentModes() : null)

					// ================= PAYMENT =================
					
					.paidAmount(paidAmount)
                    .totalAmount(totalAmount)
					.borrowAmount(borrowAmount)
					
					// ================= SALES =================
					
					.itemCode(s.getItemCode())
                    
					.categoryType(s.getCategoryType())

					.brand(s.getBrand())

					.modelName(s.getModelName())

					.quantity(s.getQuantity())

					.initialPrice(s.getInitialPrice())

					.sellingPrice(s.getSellingPrice())

					.soldPrice(soldPrice)

					.vendorName(s.getVendorName())

					.vendorGSTNumber(s.getVendorGSTNumber())

					.serialNumber(s.getSerialNumber())

					.warrantyInMonth(s.getWarrantyInMonth())

					.createdAt(s.getCreatedAt())

					.build();

		}).collect(Collectors.toList());

		// =========================================================
		// FINAL RESPONSE
		// =========================================================
		return new ReportSummaryDTO(totalSoldCount, totalInitialPrice, totalSellingPrice, totalSoldPrice,
				totalPaidAmount, pending, salesList);
	}

}