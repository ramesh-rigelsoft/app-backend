package com.rigel.app.builder;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.util.DateUtility;


@Component
public class SalesQueryBuilder {

    public String getBuyerSalesSearch(SearchCriteria criteria, List<Object> preparedStmtList) {

        List<Object> params = new ArrayList<>();

        StringBuilder base = new StringBuilder("""
            SELECT bi.ID
            FROM BUYER_INFO bi
            INNER JOIN SALES_INFO si ON si.BUYERINFO = bi.ID
        """);

        // =========================
        // OWNER FILTER (MANDATORY)
        // =========================
        addClause(base, params);
        base.append(" si.OWNER_ID = ? ");
        params.add(criteria.getUserId());

        // =========================
        // INVOICE NUMBER (BUYER TABLE CHECK REMOVED)
        // =========================
        if (!ObjectUtils.isEmpty(criteria.getInvoiceNumber())) {
            addClause(base, params);
            base.append(" bi.INVOICENUMBER = ? ");
            params.add(criteria.getInvoiceNumber().trim());
        }

        // =========================
        // SEARCH KEYWORD
        // =========================
        if (!ObjectUtils.isEmpty(criteria.getSearchKeyword())) {

            addClause(base, params);

            base.append("""
                (
                    LOWER(bi.INVOICENUMBER) LIKE ?
                    OR LOWER(bi.BUYERNAME) LIKE ?
                    OR LOWER(bi.EMAILID) LIKE ?
                    OR LOWER(bi.MOBILENUMBER) LIKE ?
                    
                    OR LOWER(bi.companyName) LIKE ?
                    OR LOWER(bi.gstNumber) LIKE ?
                    OR LOWER(bi.panNumber) LIKE ?
                   
                    OR LOWER(si.BRAND) LIKE ?
                    OR LOWER(si.MODEL_NAME) LIKE ?
                    OR LOWER(si.ITEM_CODE) LIKE ?
                    OR LOWER(si.CATEGORY_TYPE) LIKE ?
                )
            """);

            String search = "%" + criteria.getSearchKeyword().toLowerCase().trim() + "%";

            for (int i = 0; i < 11; i++) {
                params.add(search);
            }
        }

        // =========================
        // CATEGORY FILTER
        // =========================
        if (!ObjectUtils.isEmpty(criteria.getCategory())) {
            addClause(base, params);
            base.append(" si.CATEGORY = ? ");
            params.add(criteria.getCategory());
        }

        // =========================
        // BRAND FILTER
        // =========================
        if (!ObjectUtils.isEmpty(criteria.getBrand())) {
            addClause(base, params);
            base.append(" si.BRAND = ? ");
            params.add(criteria.getBrand());
        }

        // =========================
        // DATE FILTER
        // =========================
        if (!ObjectUtils.isEmpty(criteria.getStartDate())
                && !ObjectUtils.isEmpty(criteria.getEndDate())) {

            addClause(base, params);

            base.append(" si.CREATED_AT BETWEEN ? AND ? ");

            LocalDateTime start = DateUtility.parseToDateTimes(criteria.getStartDate(), false);
            LocalDateTime end = DateUtility.parseToDateTimes(criteria.getEndDate(), true);

            params.add(start);
            params.add(end);
        }

        // =========================
        // GROUP + PAGINATION
        // =========================
        base.append("""
            GROUP BY bi.ID
            ORDER BY MAX(si.CREATED_AT) DESC
            LIMIT ? OFFSET ?
        """);

        long limit = criteria.getLimit() != 0 ? criteria.getLimit() : 50;
        long offset = criteria.getOffset() != 0 ? criteria.getOffset() : 0;

        params.add(limit);
        params.add(offset);

        // =========================
        // FINAL QUERY
        // =========================
        String finalQuery = """
            WITH buyer_page AS (
        """ + base + """
            )
            SELECT 

		    bi.ID AS buyer_id,
		    bi.BUYERADDRESS AS buyer_address,
		    bi.BUYERNAME AS buyer_name,
		    bi.COMPANYADDRESS AS company_address,
		    bi.COMPANYNAME AS company_name,
		    bi.COUNTRYCODE AS country_code,
		    bi.CREATEDAT AS created_at,
		    bi.CUSTUMBERID AS customer_id,
		    bi.DISTRIC AS district,
		    bi.EMAILID AS email_id,
		    bi.GSTNUMBER AS gst_number,
		    bi.INVOICENUMBER AS invoice_number,
		    bi.MOBILENUMBER AS mobile_number,
		    bi.NOTECOMMENT AS note_comment,
		    bi.OWNERID AS owner_id,
		    bi.PANNUMBER AS pan_number,
		    bi.PAYMENTMODES AS payment_modes,
		    bi.PINCODE AS pin_code,
		    bi.STATE AS state,
		    bi.STATUS AS buyer_status,
		    bi.FINANCEID AS finance_id,
		    bi.IMEINUMBER AS imei_number,
		    bi.EMITENURE AS emi_tenure,
		
		    si.ID AS sales_id,
		    si.ADDITIONAL_DETAILS AS additional_details,
		    si.BRAND AS brand,
		    si.CATEGORY AS category,
		    si.CATEGORY_TYPE AS category_type,
		    si.CREATED_AT AS sales_created_at,
		    si.DESCRIPTION AS description,
		    si.GST_RATE AS gst_rate,
		    si.IMAGE AS image,
		    si.INITIAL_PRICE AS initial_price,
		    si.ITEM_CODE AS item_code,
		    si.ITEM_COLOR AS item_color,
		    si.ITEM_CONDITION AS item_condition,
		    si.ITEM_GEN AS item_gen,
		    si.ITEM_SOURCE AS item_source,
		    si.MEASURE_TYPE AS measure_type,
		    si.MODEL_NAME AS model_name,
		    si.OPERATING_SYSTEM AS operating_system,
		    si.OWNER_ID AS sales_owner_id,
		    si.PROCESSOR AS processor,
		    si.QUANTITY AS quantity,
		    si.RAM AS ram,
		    si.RAM_UNIT AS ram_unit,
		    si.SCREEN_SIZE AS screen_size,
		    si.SELLING_PRICE AS selling_price,
		    si.SERIAL_NUMBER AS serial_number,
		    si.SOLD_PRICE AS sold_price,
		    si.STATUS AS sales_status,
		    si.STORAGE AS storage,
		    si.STORAGE_TYPE AS storage_type,
		    si.STORAGE_UNIT AS storage_unit,
		    si.VENDORGSTNUMBER AS vendor_gst_number,
		    si.VENDORNAME AS vendor_name,
		    si.DISCOUNT_PERCENTAGE AS discount_percentage,
		    si.DISCOUNT_TYPE AS discount_type,
		    si.DISCOUNT AS discount,
		    si.VENDOR_GST_NUMBER AS vendor_gst_number_alt,
		    si.VENDOR_NAME AS vendor_name_alt
           
            FROM BUYER_INFO bi
            INNER JOIN SALES_INFO si ON si.BUYERINFO = bi.ID INNER JOIN buyer_page bp ON bp.ID = bi.ID
            ORDER BY si.BUYERINFO, si.CREATED_AT DESC
        """;

        preparedStmtList.addAll(params);
        return finalQuery;
    }

    private void addClause(StringBuilder query, List<Object> params) {
        if (params.isEmpty()) {
            query.append(" WHERE ");
        } else {
            query.append(" AND ");
        }
    }
}

//@Component
//public class SalesQueryBuilder {
//	
//	public String getBuyerSalesSearch(SearchCriteria criteria, List<Object> preparedStmtList) {
//
//	    StringBuilder subQuery = new StringBuilder("");
//	    List<Object> subQueryParams = new ArrayList<>();
//
//	    /*
//	     * =========================================================
//	     * BASE BUYER FILTER QUERY (PAGINATION BUYER LEVEL)
//	     * =========================================================
//	     */
//
//	    StringBuilder base = new StringBuilder("""
//	        SELECT bi.id
//	        FROM buyer_info bi
//	        INNER JOIN sales_info si ON si.buyer_id = bi.id
//	    """);
//
//	    /*
//	     * =========================================================
//	     * FILTERS
//	     * =========================================================
//	     */
//
//	    // ownerId (mandatory)
//	    addClauseIfRequired(base, subQueryParams);
//	    base.append(" si.owner_id = ? ");
//	    subQueryParams.add(criteria.getUserId());
//
//	    // invoice number
//	    if (!ObjectUtils.isEmpty(criteria.getInvoiceNumber())) {
//	        addClauseIfRequired(base, subQueryParams);
//	        base.append(" bi.invoice_number = ? ");
//	        subQueryParams.add(criteria.getInvoiceNumber().strip());
//	    }
//
//	    // search keyword
//	    if (!ObjectUtils.isEmpty(criteria.getSearchKeyword())) {
//
//	        addClauseIfRequired(base, subQueryParams);
//
//	        base.append("""
//	            (
//	                LOWER(bi.invoice_number) LIKE ?
//	                OR LOWER(bi.customer_id) LIKE ?
//	                OR LOWER(bi.buyer_name) LIKE ?
//	                OR LOWER(bi.email_id) LIKE ?
//	                OR LOWER(bi.mobile_number) LIKE ?
//	                OR LOWER(bi.address) LIKE ?
//	                OR LOWER(si.item_code) LIKE ?
//	                OR LOWER(si.brand) LIKE ?
//	                OR LOWER(si.model_name) LIKE ?
//	                OR LOWER(si.category_type) LIKE ?
//	            )
//	        """);
//
//	        String search = "%" + criteria.getSearchKeyword().toLowerCase().strip() + "%";
//
//	        // same param multiple times
//	        for (int i = 0; i < 10; i++) {
//	            subQueryParams.add(search);
//	        }
//	    }
//
//	    // category
//	    if (!ObjectUtils.isEmpty(criteria.getCategory())) {
//	        addClauseIfRequired(base, subQueryParams);
//	        base.append(" si.category = ? ");
//	        subQueryParams.add(criteria.getCategory());
//	    }
//
//	    // brand
//	    if (!ObjectUtils.isEmpty(criteria.getBrand())) {
//	        addClauseIfRequired(base, subQueryParams);
//	        base.append(" si.brand = ? ");
//	        subQueryParams.add(criteria.getBrand());
//	    }
//
//	    // date filter
//	    if (!ObjectUtils.isEmpty(criteria.getStartDate())
//	            && !ObjectUtils.isEmpty(criteria.getEndDate())) {
//
//	        addClauseIfRequired(base, subQueryParams);
//
//	        base.append(" si.created_at BETWEEN ? AND ? ");
//
//	        LocalDateTime start = DateUtility.parseToDateTimes(criteria.getStartDate(), false);
//	        LocalDateTime end = DateUtility.parseToDateTimes(criteria.getEndDate(), true);
//
//	        subQueryParams.add(start);
//	        subQueryParams.add(end);
//	    }
//
//	    /*
//	     * =========================================================
//	     * GROUP + ORDER + PAGINATION (BUYER LEVEL)
//	     * =========================================================
//	     */
//
//	    base.append("""
//	        GROUP BY bi.id
//	        ORDER BY MAX(si.created_at) DESC
//	        LIMIT ? OFFSET ?
//	    """);
//
//	    long limit = criteria.getLimit() != 0
//	            ? criteria.getLimit()
//	            : 50;
//
//	    long offset = criteria.getOffset() != 0
//	            ? criteria.getOffset()
//	            : 0;
//
//	    subQueryParams.add(limit);
//	    subQueryParams.add(offset);
//
//	    /*
//	     * =========================================================
//	     * FINAL QUERY BUILD
//	     * =========================================================
//	     */
//
//	    String finalQuery =
//	            "WITH buyer_page AS (" +
//	            base +
//	            ") " +
//	            """
//	            SELECT si.*
//	            FROM sales_info si
//	            INNER JOIN buyer_page bp ON bp.id = si.buyer_id
//	            ORDER BY si.buyer_id, si.created_at DESC
//	            """;
//
//	    preparedStmtList.addAll(subQueryParams);
//
//	    return finalQuery;
//	}
//	
//	private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList) {
//	    if (preparedStmtList.isEmpty()) {
//	        query.append("WHERE ");
//	    } else {
//	        query.append("AND ");
//	    }
//	}
//
//}
