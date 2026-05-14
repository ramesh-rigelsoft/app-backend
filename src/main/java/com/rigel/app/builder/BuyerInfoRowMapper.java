package com.rigel.app.builder;

import com.rigel.app.builder.*;


import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;



@Component
public class BuyerInfoRowMapper implements ResultSetExtractor<List<BuyerInfoDTO>> {

    @Override
    public List<BuyerInfoDTO> extractData(ResultSet rs) throws SQLException {

        Map<String, BuyerInfoDTO> map = new LinkedHashMap<>();

        while (rs.next()) {

            String buyerId = rs.getString("buyer_id");
            if (buyerId == null) continue;

            BuyerInfoDTO buyer = map.get(buyerId);

            if (buyer == null) {

                buyer = BuyerInfoDTO.builder()
                        .id(buyerId)
                        .invoiceNumber(rs.getString("invoice_number"))
                        .custumberId(rs.getString("customer_id"))
                        .paymentModes(rs.getString("payment_modes"))
                        .buyerName(rs.getString("buyer_name"))
                        .emailId(rs.getString("email_id"))
                        .mobileNumber(rs.getString("mobile_number"))
                        .countryCode(rs.getString("country_code"))
                        .buyerAddress(rs.getString("buyer_address"))
                        .companyName(rs.getString("company_name"))
                        .gstNumber(rs.getString("gst_number"))
                        .panNumber(rs.getString("pan_number"))
                        .pinCode(rs.getString("pin_code"))
                        .state(rs.getString("state"))
                        .district(rs.getString("district"))
                        .companyAddress(rs.getString("company_address"))
                        .status(rs.getInt("buyer_status"))
                        .createdAt(rs.getTimestamp("created_at") != null
                                ? rs.getTimestamp("created_at").toLocalDateTime()
                                : null)
                        .noteComment(rs.getString("note_comment"))
                        .ownerId(rs.getInt("owner_id"))
//                        .financeId(rs.getString("finance_id"))
//                        .imeiNumber(rs.getString("imei_number"))
//                        .emiTenure(rs.getString("emi_tenure"))
                        .build();

                map.put(buyerId, buyer);
            }

            // =========================
            // SALES CHILD
            // =========================
            String salesId = rs.getString("sales_id");

            if (salesId != null) {

                SalesInfoDTO sales = SalesInfoDTO.builder()
                        .id(salesId)
                        .itemCode(rs.getString("item_code"))
                        .category(rs.getString("category"))
                        .categoryType(rs.getString("category_type"))
                        .measureType(rs.getString("measure_type"))
                        .brand(rs.getString("brand"))
                        .modelName(rs.getString("model_name"))
                        .itemCondition(rs.getString("item_condition"))
                        .itemSource(rs.getString("item_source"))
                        .ram(rs.getString("ram"))
//                        .ramUnit(rs.getString("ram_unit"))
                        .storage(rs.getString("storage"))
//                        .storageType(rs.getString("storage_type"))
//                        .storageUnit(rs.getString("storage_unit"))
                        .quantity(rs.getInt("quantity"))
                        .initialPrice(rs.getDouble("initial_price"))
                        .sellingPrice(rs.getDouble("selling_price"))
                        .soldPrice(rs.getDouble("sold_price"))
                        .description(rs.getString("description"))
                        .itemColor(rs.getString("item_color"))
                        .image(rs.getString("image"))
                        .processor(rs.getString("processor"))
                        .operatingSystem(rs.getString("operating_system"))
                        .screenSize(rs.getString("screen_size"))
//                        .status(rs.getInt("sales_status"))
                        .gstRate(rs.getString("gst_rate"))
                        .serialNumber(rs.getString("serial_number"))
                        .createdAt(rs.getTimestamp("sales_created_at") != null
                                ? rs.getTimestamp("sales_created_at").toLocalDateTime()
                                : null)
                        .ownerId(rs.getInt("sales_owner_id"))
                        .vendorName(rs.getString("vendor_name"))
                        .vendorGstNumber(rs.getString("vendor_gst_number"))
                        .additionalDetails(rs.getString("additional_details"))
//                        .discount(rs.getDouble("discount"))
                        .discountType(rs.getString("discount_type"))
//                        .discountPercentage(rs.getDouble("discount_percentage"))
                        .build();

                buyer.addSalesInfoItem(sales);
            }
        }

        return new ArrayList<>(map.values());
    }
}
//
//@Component
//public class BuyerInfoRowMapper implements ResultSetExtractor<List<BuyerInfoDTO>> {
//
//    @Override
//    public List<BuyerInfoDTO> extractData(ResultSet rs) throws SQLException, DataAccessException {
//
//        Map<String, BuyerInfoDTO> buyerMap = new LinkedHashMap<>();
//
//        while (rs.next()) {
//
//            String buyerId = rs.getString("buyer_id");
//
//            BuyerInfoDTO buyer = buyerMap.get(buyerId);
//
//            if (buyer == null) {
//
//                buyer = BuyerInfoDTO.builder()
//                        .id(buyerId)
//                        .invoiceNumber(rs.getString("invoice_number"))
//                        .custumberId(rs.getString("customer_id"))
//                        .paymentModes(rs.getString("payment_modes"))
//                        .buyerName(rs.getString("buyer_name"))
//                        .emailId(rs.getString("email_id"))
//                        .mobileNumber(rs.getString("mobile_number"))
//                        .countryCode(rs.getString("country_code"))
//                        .buyerAddress(rs.getString("buyer_address"))
//                        .companyName(rs.getString("company_name"))
//                        .gstNumber(rs.getString("gst_number"))
//                        .panNumber(rs.getString("pan_number"))
//                        .pinCode(rs.getString("pin_code"))
//                        .state(rs.getString("state"))
//                        .district(rs.getString("district"))
//                        .companyAddress(rs.getString("company_address"))
//                        .status(rs.getInt("status"))
//                        .createdAt(rs.getTimestamp("created_at") != null
//                                ? rs.getTimestamp("created_at").toLocalDateTime()
//                                : null)
//                        .noteComment(rs.getString("note_comment"))
//                        .ownerId(rs.getInt("owner_id"))
//                        .build();
//
//                buyerMap.put(buyerId, buyer);
//            }
//
//            // ===============================
//            // SALES INFO (child table mapping)
//            // ===============================
//            String salesId = rs.getString("sales_id");
//
//            if (salesId != null) {
//
//                SalesInfoDTO sales = SalesInfoDTO.builder()
//                        .id(salesId)
//                        .itemCode(rs.getString("item_code"))
//                        .category(rs.getString("category"))
//                        .categoryType(rs.getString("category_type"))
//                        .measureType(rs.getString("measure_type"))
//                        .brand(rs.getString("brand"))
//                        .modelName(rs.getString("model_name"))
//                        .itemCondition(rs.getString("item_condition"))
//                        .itemSource(rs.getString("item_source"))
//                        .ram(rs.getString("ram"))
//                        .storage(rs.getString("storage"))
//                        .quantity(rs.getInt("quantity"))
//                        .initialPrice(rs.getDouble("initial_price"))
//                        .sellingPrice(rs.getDouble("selling_price"))
//                        .soldPrice(rs.getDouble("sold_price"))
//                        .description(rs.getString("description"))
//                        .itemColor(rs.getString("item_color"))
//                        .image(rs.getString("image"))
//                        .processor(rs.getString("processor"))
//                        .operatingSystem(rs.getString("operating_system"))
//                        .screenSize(rs.getString("screen_size"))
//                        .status(rs.getBoolean("status"))
//                        .gstRate(rs.getString("gst_rate"))
//                        .discountType(rs.getString("discount_type"))
//                        .serialNumber(rs.getString("serial_number"))
//                        .createdAt(rs.getTimestamp("sales_created_at") != null
//                                ? rs.getTimestamp("sales_created_at").toLocalDateTime()
//                                : null)
//                        .ownerId(rs.getInt("sales_owner_id"))
//                        .vendorName(rs.getString("vendor_name"))
//                        .vendorGstNumber(rs.getString("vendor_gst_number"))
//                        .additionalDetails(rs.getString("additional_details"))
//                        .build();
//
//                buyer.addSalesInfoItem(sales);
//            }
//        }
//
//        return new ArrayList<>(buyerMap.values());
//    }
//}