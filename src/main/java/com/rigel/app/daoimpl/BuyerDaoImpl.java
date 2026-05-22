package com.rigel.app.daoimpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rigel.app.dao.IBuyerDao;
import com.rigel.app.dao.IItemsDao;
import com.rigel.app.model.BuyerInfo;
import com.rigel.app.model.Items;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.dto.BuyerInfoDto;
import com.rigel.app.model.dto.SalesInfoDto;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.util.DateUtility;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class BuyerDaoImpl implements IBuyerDao {

	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	ObjectMapper mapper;

	@Override
	public BuyerInfo saveBuyerInfo(BuyerInfo buyerInfo) {
		try {
			return entityManager.merge(buyerInfo);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public BuyerInfo updateBuyerInfo(BuyerInfo buyerInfo) {
		try {
			return entityManager.merge(buyerInfo);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public int updateRestAmountAndDate(String id, String restAmount, LocalDateTime restAmountDate) {
        String jpql = "UPDATE BuyerInfo b SET b.pendingPaymentStatus='Paid' b.restAmount = :restAmount, b.restAmountDate = :restAmountDate WHERE b.id = :id";
        return entityManager.createQuery(jpql)
                .setParameter("restAmount", restAmount)
                .setParameter("restAmountDate", restAmountDate)
                .setParameter("id", id)
                .executeUpdate();
	}

	@Override
	public int deleteBuyerInfo(List<Long> buyerId) {
		int query = entityManager.createQuery("delete from buyer_info where id in(" + buyerId + ")").executeUpdate();
		return query;
	}

	@Override
	public List<BuyerInfo> searchBuyerInfo(SearchCriteria criteria) {

		StringBuilder jpql = new StringBuilder("FROM BuyerInfo bi JOIN SalesInfo i ON bi.id = i.buyerInfo.id WHERE i.status=true AND ");
		Map<String, Object> params = new HashMap<>();
		jpql.append(" i.ownerId = :ownerId ");
		params.put("ownerId", criteria.getUserId());
		if(criteria.getInvoiceNumber()!= null && !criteria.getInvoiceNumber().strip().isEmpty()) {
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
		    jpql.append(" AND i.createdAt BETWEEN :startDate AND :endDate ");
			params.put("startDate", start);
			params.put("endDate", end);
		}

		jpql.append(" ORDER BY i.createdAt DESC");

		var query = entityManager.createQuery(jpql.toString(), BuyerInfo.class);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		
		if (!criteria.isIsdownload()) {
			query.setFirstResult(criteria.getStartIndex());
			query.setMaxResults(criteria.getMaxRecords());
		}
		return query.getResultList();
	}
	
	@Override
	public List<BuyerInfoDto> searchSalesInfoDto(SearchCriteria criteria) {

	    Map<String, Object> params = new HashMap<>();

	    /*
	     * =========================================================
	     * STEP 1 : BUYER IDS (NO ORDER BY ISSUE + NO DUPLICATION)
	     * =========================================================
	     */

	    StringBuilder buyerJpql = new StringBuilder("""
	        SELECT bi.id
	        FROM BuyerInfo bi
	        WHERE EXISTS (
	            SELECT 1
	            FROM SalesInfo i
	            WHERE i.buyerInfo.id = bi.id
	            AND i.status = true
	            AND i.ownerId = :ownerId
	        )
	    """);

	    params.put("ownerId", criteria.getUserId());

	    // invoice filter
	    if (criteria.getInvoiceNumber() != null &&
	        !criteria.getInvoiceNumber().strip().isEmpty()) {

	        buyerJpql.append(" AND bi.invoiceNumber = :invoiceNumber ");
	        params.put("invoiceNumber", criteria.getInvoiceNumber().strip());
	    }

	    // search filter
	    if (criteria.getSearchKeyword() != null &&
	        !criteria.getSearchKeyword().strip().isEmpty()) {

	        buyerJpql.append("""
	            AND (
	                LOWER(bi.invoiceNumber) LIKE :search
	                OR LOWER(bi.custumberId) LIKE :search
	                OR LOWER(bi.buyerName) LIKE :search
	                OR LOWER(bi.emailId) LIKE :search
	                OR LOWER(bi.mobileNumber) LIKE :search
	                OR LOWER(bi.countryCode) LIKE :search
	                OR LOWER(bi.buyerAddress) LIKE :search
	            )
	        """);

	        params.put("search",
	                "%" + criteria.getSearchKeyword().toLowerCase().strip() + "%");
	    }

	    /*
	     * ⚠️ IMPORTANT: ORDER BY REMOVE KAR DO (H2 ISSUE FIX)
	     */
	    TypedQuery<String> buyerQuery =
	            entityManager.createQuery(buyerJpql.toString(), String.class);

	    params.forEach(buyerQuery::setParameter);

	    if (!criteria.isIsdownload()) {
	        buyerQuery.setFirstResult(criteria.getStartIndex());
	        buyerQuery.setMaxResults(criteria.getMaxRecords());
	    }

	    List<String> buyerIds = buyerQuery.getResultList();

	    if (buyerIds.isEmpty()) {
	        return Collections.emptyList();
	    }

	    /*
	     * =========================================================
	     * STEP 2 : FETCH SALES (ONE BUYER = MULTIPLE SALES OK)
	     * =========================================================
	     */

	    String salesJpql = """
	        SELECT i
	        FROM SalesInfo i
	        JOIN FETCH i.buyerInfo bi
	        WHERE bi.id IN :buyerIds
	        AND i.status = true
	        ORDER BY bi.id, i.createdAt DESC
	    """;

	    List<SalesInfo> salesInfos =
	            entityManager.createQuery(salesJpql, SalesInfo.class)
	                    .setParameter("buyerIds", buyerIds)
	                    .getResultList();

	    /*
	     * =========================================================
	     * STEP 3 : GROUPING FIX (IMPORTANT)
	     * =========================================================
	     */

	    Map<String, BuyerInfoDto> dtoMap = new LinkedHashMap<>();

	    for (SalesInfo sales : salesInfos) {

	        BuyerInfo buyer = sales.getBuyerInfo();
	        String buyerId = buyer.getId();

	        BuyerInfoDto dto = dtoMap.computeIfAbsent(buyerId, id -> {

	            BuyerInfoDto d = new BuyerInfoDto();

	            d.setId(id);
	            d.setBuyerName(buyer.getBuyerName());
	            d.setInvoiceNumber(buyer.getInvoiceNumber());
	            d.setCustumberId(buyer.getCustumberId());
	            d.setEmailId(buyer.getEmailId());
	            d.setMobileNumber(buyer.getMobileNumber());
	            d.setCountryCode(buyer.getCountryCode());
	            d.setBuyerAddress(buyer.getBuyerAddress());

	            // ✅ MUST BE LIST (NOT SET)
	            d.setSalesInfo(new HashSet<>());

	            return d;
	        });

	        SalesInfoDto salesDto = mapper.convertValue(sales, SalesInfoDto.class);
	        dto.getSalesInfo().add(salesDto);
	    }

	    return new ArrayList<>(dtoMap.values());
	}
}