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

        // OWNER FILTER (MANDATORY)
        addClause(base, params);
        base.append(" si.OWNER_ID = ? ");
        params.add(criteria.getUserId());

        // INVOICE NUMBER
        if (!ObjectUtils.isEmpty(criteria.getInvoiceNumber())) {
            addClause(base, params);
            base.append(" bi.INVOICENUMBER = ? ");
            params.add(criteria.getInvoiceNumber().trim());
        }

        // SEARCH KEYWORD
        if (!ObjectUtils.isEmpty(criteria.getSearchKeyword())) {
            addClause(base, params);
            base.append("""
                (
                    LOWER(bi.INVOICENUMBER) LIKE ?
                    OR LOWER(bi.BUYERNAME) LIKE ?
                    OR LOWER(bi.EMAILID) LIKE ?
                    OR LOWER(bi.MOBILENUMBER) LIKE ?
                    OR LOWER(bi.COMPANYNAME) LIKE ?
                    OR LOWER(bi.GSTNUMBER) LIKE ?
                    OR LOWER(bi.PANNUMBER) LIKE ?
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

        // CATEGORY FILTER
        if (!ObjectUtils.isEmpty(criteria.getCategory())) {
            addClause(base, params);
            base.append(" si.CATEGORY = ? ");
            params.add(criteria.getCategory());
        }

        // BRAND FILTER
        if (!ObjectUtils.isEmpty(criteria.getBrand())) {
            addClause(base, params);
            base.append(" si.BRAND = ? ");
            params.add(criteria.getBrand());
        }

        // DATE FILTER
        if (!ObjectUtils.isEmpty(criteria.getStartDate()) && !ObjectUtils.isEmpty(criteria.getEndDate())) {
            addClause(base, params);
            base.append(" si.CREATED_AT BETWEEN ? AND ? ");
            LocalDateTime start = DateUtility.parseToDateTimes(criteria.getStartDate(), false);
            LocalDateTime end = DateUtility.parseToDateTimes(criteria.getEndDate(), true);
            params.add(start);
            params.add(end);
        }

        // GROUP + PAGINATION
        base.append("""
            GROUP BY bi.ID
            ORDER BY MAX(si.CREATED_AT) DESC
            LIMIT ? OFFSET ?
        """);

        long limit = criteria.getLimit() != 0 ? criteria.getLimit() : 50;
        long offset = criteria.getOffset() != 0 ? criteria.getOffset() : 0;
        params.add(limit);
        params.add(offset);

        // FINAL QUERY
        String finalQuery = """
            WITH buyer_page AS (
        """ + base + """
            )
            SELECT 
                bi.ID AS buyer_id,
				bi.invoiceNumber AS invoice_number,
				bi.custumberId AS customer_id,
				bi.paymentModes AS payment_modes,
				bi.financeId AS finance_id,
				bi.emiTenure AS emi_tenure,
				bi.paidAmount AS paid_amount,
				bi.imeiNumber AS imei_number,
				bi.buyerName AS buyer_name,
				bi.emailId AS email_id,
				bi.mobileNumber AS mobile_number,
				bi.countryCode AS country_code,
				bi.buyerAddress AS buyer_address,
				bi.companyName AS company_name,
				bi.gstNumber AS gst_number,
				bi.panNumber AS pan_number,
				bi.pinCode AS pin_code,
				bi.state AS state,
				bi.distric AS district,
				bi.companyAddress AS company_address,
				bi.status AS buyer_status,
				bi.createdAt AS created_at,
				bi.noteComment AS note_comment,
				bi.ownerId AS owner_id,


                si.ID AS sales_id,
				si.item_code AS item_code,
				si.category AS category,
				si.category_type AS category_type,
				si.measure_type AS measure_type,
				si.brand AS brand,
				si.model_name AS model_name,
				si.item_condition AS item_condition,
				si.item_source AS item_source,
				si.ram AS ram,
				si.ram_unit AS ram_unit,
				si.storage AS storage,
				si.storage_type AS storage_type,
				si.storage_unit AS storage_unit,
				si.quantity AS quantity,
				si.initial_price AS initial_price,
				si.selling_price AS selling_price,
				si.sold_price AS sold_price,
				si.description AS description,
				si.item_color AS item_color,
				si.image AS image,
				si.processor AS processor,
				si.operating_system AS operating_system,
				si.screen_size AS screen_size,
				si.item_gen AS item_gen,
				si.status AS sales_status,
				si.gst_rate AS gst_rate,
				si.discount_type AS discount_type,
				si.created_at AS sales_created_at,
				si.owner_id AS sales_owner_id,
				si.vendor_name AS vendor_name,
				si.vendor_gst_number AS vendor_gst_number,
				si.return_status AS return_status,
				si.replace_status AS replace_status,
				si.replace_count AS replace_count,
				si.warranty_in_month AS warranty_in_month,
				si.return_reason AS return_reason,
				si.entry_type AS entry_type,
				si.serial_number_type AS serial_number_type,
				si.serial_number AS serial_number,
				si.additional_details AS additional_details

            FROM BUYER_INFO bi
            INNER JOIN SALES_INFO si ON si.BUYERINFO = bi.ID
            INNER JOIN buyer_page bp ON bp.ID = bi.ID
            ORDER BY bi.ID, si.CREATED_AT DESC
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
