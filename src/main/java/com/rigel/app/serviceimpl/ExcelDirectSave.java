package com.rigel.app.serviceimpl;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rigel.app.model.BuyerInfo;
import com.rigel.app.model.Items;
import com.rigel.app.model.LoginActivity;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.User;
import com.rigel.app.model.dto.ItemsDashboardResponse;
import com.rigel.app.model.dto.VendorInvoiceDTO;
import com.rigel.app.model.dto.VendorPerformanceDTO;
import com.rigel.app.util.Constaints;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rigel.app.builder.BuyerInfoDTO;
import com.rigel.app.builder.SalesInfoDTO;
import com.rigel.app.dao.ILoginInfoDao;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
public class ExcelDirectSave {

	@Autowired
	private ILoginInfoDao loginInfoDao;

	@Autowired
	private ObjectMapper objectMapper;
	
	public void exportItemsDashboardExcel(ItemsDashboardResponse response)
	        throws IOException {

	    SXSSFWorkbook workbook = new SXSSFWorkbook(100);
	    workbook.setCompressTempFiles(true);

	    Sheet sheet = workbook.createSheet("ITEMS_DASHBOARD");

	    sheet.createFreezePane(0, 2);

	    // =========================================================
	    // STYLES
	    // =========================================================
	    CellStyle titleStyle = createTitleStyle(workbook);
	    CellStyle headerStyle = createHeaderStyle(workbook);
	    CellStyle dataStyle = createDataStyle(workbook);

	    int rowNum = 0;

	    // =========================================================
	    // TITLE
	    // =========================================================
	    Row titleRow = sheet.createRow(rowNum++);
	    Cell titleCell = titleRow.createCell(0);
	    titleCell.setCellValue("📊 ITEMS PURCHASE DASHBOARD");
	    titleCell.setCellStyle(titleStyle);

	    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

	    rowNum++;

	    // =========================================================
	    // SUMMARY SECTION
	    // =========================================================
	    Row s1 = sheet.createRow(rowNum++);
	    s1.createCell(0).setCellValue("TOTAL PURCHASE");
	    s1.createCell(1).setCellValue(response.getTotalPurchase());

	    Row s2 = sheet.createRow(rowNum++);
	    s2.createCell(0).setCellValue("TOTAL ITEMS");
	    s2.createCell(1).setCellValue(response.getTotalItems());

	    Row s3 = sheet.createRow(rowNum++);
	    s3.createCell(0).setCellValue("TOTAL VENDORS");
	    s3.createCell(1).setCellValue(response.getTotalVendors());

	    rowNum += 2;

	    // =========================================================
	    // VENDOR PERFORMANCE SECTION
	    // =========================================================
	    Row vendorHeader = sheet.createRow(rowNum++);
	    vendorHeader.createCell(0).setCellValue("VENDOR PERFORMANCE");

	    for (VendorPerformanceDTO v : response.getVendorPerformance()) {

	        Row vr = sheet.createRow(rowNum++);
	        vr.createCell(0).setCellValue(v.getVendorName());
	        vr.createCell(1).setCellValue(v.getTotalAmount());

	        for (VendorInvoiceDTO inv : v.getInvoices()) {

	            Row ir = sheet.createRow(rowNum++);
	            ir.createCell(1).setCellValue("Invoice");
	            ir.createCell(2).setCellValue(inv.getInvoiceNumber());
	            ir.createCell(3).setCellValue(inv.getItemCount());
	            ir.createCell(4).setCellValue(inv.getAmount());
	        }

	        rowNum++;
	    }

	    rowNum += 2;

	    // =========================================================
	    // ITEM TABLE HEADER
	    // =========================================================
	    String[] headers = {
	            "Item Code",
	            "Vendor",
	            "Invoice",
	            "Category",
	            "Brand",
	            "Model",
	            "Qty",
	            "Selling Price",
	            "Created At"
	    };

	    Row headerRow = sheet.createRow(rowNum++);

	    for (int i = 0; i < headers.length; i++) {
	        Cell cell = headerRow.createCell(i);
	        cell.setCellValue(headers[i]);
	        cell.setCellStyle(headerStyle);
	        sheet.setColumnWidth(i, 5000);
	    }

	    // =========================================================
	    // STREAM ITEMS (LOW MEMORY)
	    // =========================================================
	    try (Stream<Items> stream = response.getItemStream().stream()) {

	        stream.forEach(item -> {

	            Row row = sheet.createRow(sheet.getLastRowNum() + 1);

	            int c = 0;

	            createCell(row, c++, item.getItemCode(), dataStyle);
	            createCell(row, c++, item.getVendorName(), dataStyle);
	            createCell(row, c++, item.getVendorInvoiceNumber(), dataStyle);
	            createCell(row, c++, item.getCategory(), dataStyle);
	            createCell(row, c++, item.getBrand(), dataStyle);
	            createCell(row, c++, item.getModelName(), dataStyle);
	            createCell(row, c++, item.getQuantity(), dataStyle);
	            createCell(row, c++, item.getSellingPrice(), dataStyle);
	            createCell(row, c++, item.getCreatedAt(), dataStyle);
	        });
	    }

	    // =========================================================
	    // SAVE FILE (DOWNLOADS)
	    // =========================================================
	    String home = System.getProperty("user.home");

	    Path folder = Paths.get(home, "Downloads", "RigelEMIS");

	    if (!Files.exists(folder)) {
	        Files.createDirectories(folder);
	    }

	    Path filePath = folder.resolve(
	            "ITEMS_DASHBOARD_" + System.currentTimeMillis() + ".xlsx"
	    );

	    try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
	        workbook.write(fos);
	    }

	    workbook.dispose();
	    workbook.close();

	    System.out.println("Excel Created: " + filePath);
	}
	
	private void createCell(Row row, int col, Object value, CellStyle style) {

	    Cell cell = row.createCell(col);

	    if (value == null) {
	        cell.setCellValue("");
	    } else if (value instanceof Number) {
	        cell.setCellValue(((Number) value).doubleValue());
	    } else {
	        cell.setCellValue(value.toString());
	    }

	    cell.setCellStyle(style);
	}
	
	private CellStyle createTitleStyle(Workbook wb) {

	    Font font = wb.createFont();
	    font.setBold(true);
	    font.setFontHeightInPoints((short) 16);

	    CellStyle style = wb.createCellStyle();
	    style.setFont(font);
	    style.setAlignment(HorizontalAlignment.CENTER);

	    return style;
	}

	private CellStyle createHeaderStyle(Workbook wb) {

	    Font font = wb.createFont();
	    font.setBold(true);
	    font.setColor(IndexedColors.WHITE.getIndex());

	    CellStyle style = wb.createCellStyle();
	    style.setFont(font);
	    style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
	    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

	    return style;
	}

	private CellStyle createDataStyle(Workbook wb) {

	    CellStyle style = wb.createCellStyle();
	    style.setBorderTop(BorderStyle.THIN);
	    style.setBorderBottom(BorderStyle.THIN);
	    style.setBorderLeft(BorderStyle.THIN);
	    style.setBorderRight(BorderStyle.THIN);

	    return style;
	}
	
//	public void exportItemsToExcel(List<Items> items) {
//		LoginActivity loginActivity = loginInfoDao.findLoginActivityByuserId(items.get(0).getOwnerId());
//		User user = null;
//		try {
//			user = objectMapper.readValue(loginActivity.getUserObject(), User.class);
//		} catch (JsonMappingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		try (Workbook workbook = new XSSFWorkbook()) {
//
//			Sheet sheet = workbook.createSheet("Items");
//
//			// =========================================================
//			// STYLES
//			// =========================================================
//
//			// Company Title Style
//			Font companyFont = workbook.createFont();
//			companyFont.setBold(true);
//			companyFont.setFontHeightInPoints((short) 18);
//			companyFont.setColor(IndexedColors.DARK_BLUE.getIndex());
//
//			CellStyle companyStyle = workbook.createCellStyle();
//			companyStyle.setFont(companyFont);
//			companyStyle.setAlignment(HorizontalAlignment.CENTER);
//
//			// Report Title Style
//			Font titleFont = workbook.createFont();
//			titleFont.setBold(true);
//			titleFont.setFontHeightInPoints((short) 13);
//
//			CellStyle titleStyle = workbook.createCellStyle();
//			titleStyle.setFont(titleFont);
//			titleStyle.setAlignment(HorizontalAlignment.CENTER);
//
//			// Header Style
//			Font headerFont = workbook.createFont();
//			headerFont.setBold(true);
//			headerFont.setColor(IndexedColors.WHITE.getIndex());
//
//			CellStyle headerStyle = workbook.createCellStyle();
//			headerStyle.setFont(headerFont);
//			headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
//			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//			headerStyle.setAlignment(HorizontalAlignment.CENTER);
//
//			headerStyle.setBorderTop(BorderStyle.THIN);
//			headerStyle.setBorderBottom(BorderStyle.THIN);
//			headerStyle.setBorderLeft(BorderStyle.THIN);
//			headerStyle.setBorderRight(BorderStyle.THIN);
//
//			// Normal Cell Style
//			CellStyle dataStyle = workbook.createCellStyle();
//			dataStyle.setBorderTop(BorderStyle.THIN);
//			dataStyle.setBorderBottom(BorderStyle.THIN);
//			dataStyle.setBorderLeft(BorderStyle.THIN);
//			dataStyle.setBorderRight(BorderStyle.THIN);
//
//			// Money Style
//			CellStyle moneyStyle = workbook.createCellStyle();
//			DataFormat format = workbook.createDataFormat();
//			moneyStyle.setDataFormat(format.getFormat("₹ #,##,##0.00"));
//
//			moneyStyle.setBorderTop(BorderStyle.THIN);
//			moneyStyle.setBorderBottom(BorderStyle.THIN);
//			moneyStyle.setBorderLeft(BorderStyle.THIN);
//			moneyStyle.setBorderRight(BorderStyle.THIN);
//
//			// Date Style
//			CellStyle dateCellStyle = workbook.createCellStyle();
//			CreationHelper createHelper = workbook.getCreationHelper();
//
//			dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy HH:mm"));
//
//			// =========================================================
//			// CALCULATE TOTAL SALES
//			// =========================================================
//
//			double totalSales = items.stream().mapToDouble(i -> i.getInitialPrice() != null ? i.getSellingPrice() : 0)
//					.sum();
//
//			// =========================================================
//			// TOP PROFESSIONAL HEADER
//			// =========================================================
//
//			// Company Name
//			Row companyRow = sheet.createRow(0);
//			companyRow.setHeightInPoints(28);
//
//			Cell companyCell = companyRow.createCell(0);
//			companyCell.setCellValue(user.getCompanyName().toUpperCase());
//			companyCell.setCellStyle(companyStyle);
//
//			// Merge company row
//			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 23));
//
//			// Report Title
//			Row titleRow = sheet.createRow(1);
//
//			Cell titleCell = titleRow.createCell(0);
//			titleCell.setCellValue("Items Purchase Report");
//			titleCell.setCellStyle(titleStyle);
//
//			sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 23));
//
//			// Report Info Row
//			Row infoRow = sheet.createRow(2);
//
//			infoRow.createCell(0).setCellValue("Generated On: " + java.time.LocalDateTime.now());
//
//			infoRow.createCell(10).setCellValue("Total Purchase Value:");
//
//			Cell totalCell = infoRow.createCell(12);
//			totalCell.setCellValue(totalSales);
//			totalCell.setCellStyle(moneyStyle);
//
//			// Empty Row
//			sheet.createRow(3);
//
//			// =========================================================
//			// TABLE HEADER
//			// =========================================================
//
//			// 📌 Columns
//			String[] headers = { "Item Code", "Vendor Name", "GSTIN Number", "Category", "Category Type",
//					"Measure Type", "Brand", "Model", "Condition", "Source", "RAM", "Storage", "Storage Type",
//					"Quantity", "Initial Price", "Selling Price", "Description", "Color", "Processor", "OS",
//					"Screen Size", "Generation", "Serial No", "Created At" };
//			Row headerRow = sheet.createRow(4);
//
//			for (int i = 0; i < headers.length; i++) {
//
//				Cell cell = headerRow.createCell(i);
//
//				cell.setCellValue(headers[i]);
//				cell.setCellStyle(headerStyle);
//			}
//
//			// 🔹 Data Rows
//			int rowIndex = 5;
//
//			for (Items item : items) {
//
//				Row row = sheet.createRow(rowIndex++);
//
//				int col = 0;
//				row.createCell(col++).setCellValue(nvl(item.getItemCode()));
//
//				row.createCell(col++).setCellValue(nvl(item.getVendorName()));
//				row.createCell(col++).setCellValue(nvl(item.getVendorGSTNumber()));
//
//				row.createCell(col++).setCellValue(nvl(item.getCategory()));
//				row.createCell(col++).setCellValue(nvl(item.getCategoryType()));
//				row.createCell(col++).setCellValue(nvl(item.getMeasureType()));
//
//				row.createCell(col++).setCellValue(nvl(item.getBrand()));
//				row.createCell(col++).setCellValue(nvl(item.getModelName()));
//				row.createCell(col++).setCellValue(nvl(item.getItemCondition()));
//				row.createCell(col++).setCellValue(nvl(item.getItemSource()));
//
//				row.createCell(col++)
//						.setCellValue(nvl(item.getRam() != "null" && item.getRam() != null
//								? (item.getRam() + "" + item.getRamUnit())
//								: "-"));
//				row.createCell(col++)
//						.setCellValue(nvl(item.getStorage() != "null" && item.getStorage() != null
//								? (item.getStorage() + "" + item.getStorageUnit())
//								: "-"));
//				row.createCell(col++).setCellValue(nvl(item.getStorageType()));
//
//				row.createCell(col++).setCellValue(item.getQuantity() != null ? item.getQuantity() : 0);
//
//				// Initial Price
//				Cell initPriceCell = row.createCell(col++);
//				if (item.getInitialPrice() != null) {
//					initPriceCell.setCellValue(item.getInitialPrice());
//					initPriceCell.setCellStyle(moneyStyle);
//				} else {
//					initPriceCell.setCellValue(0);
//				}
//
//				// Selling Price
//				Cell sellPriceCell = row.createCell(col++);
//				if (item.getSellingPrice() != null) {
//					sellPriceCell.setCellValue(item.getSellingPrice());
//					sellPriceCell.setCellStyle(moneyStyle);
//				} else {
//					sellPriceCell.setCellValue(0);
//				}
//
////	            row.createCell(col++).setCellValue(item.getInitialPrice() != null ? item.getInitialPrice() : 0);
////	            row.createCell(col++).setCellValue(item.getSellingPrice() != null ? item.getSellingPrice() : 0);
//
//				row.createCell(col++).setCellValue(nvl(item.getDescription()));
//				row.createCell(col++).setCellValue(nvl(item.getItemColor()));
//
//				row.createCell(col++).setCellValue(nvl(item.getProcessor()));
//				row.createCell(col++).setCellValue(nvl(item.getOperatingSystem()));
//				row.createCell(col++).setCellValue(nvl(item.getScreenSize()));
//
//				row.createCell(col++).setCellValue(nvl(item.getItemGen()));
//				row.createCell(col++).setCellValue(nvl(item.getSerialNumber()));
//
//				Cell dateCell = row.createCell(col++);
//
//				if (item.getCreatedAt() != null) {
//					dateCell.setCellValue(java.sql.Timestamp.valueOf(item.getCreatedAt()));
//					dateCell.setCellStyle(dateCellStyle);
//				} else {
//					dateCell.setCellValue("");
//				}
//
//			}
//
//			// 📏 Auto-size columns
//			for (int i = 0; i < headers.length; i++) {
//				sheet.autoSizeColumn(i);
//			}
//
//			// =========================================================
//			// SAVE FILE
//			// =========================================================
//
//			String userHome = System.getProperty("user.home");
//
//			Path folder = Paths.get(userHome, "Downloads", Constaints.DOWNLOAD_FOLDER_NAME);
//
//			if (!Files.exists(folder)) {
//				Files.createDirectories(folder);
//			}
//
//			String fileName = "items_report_" + System.currentTimeMillis() + ".xlsx";
//
//			Path filePath = folder.resolve(fileName);
//
//			try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
//
//				workbook.write(fos);
//			}
//
//			System.out.println("Excel file created: " + filePath);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public void exportBuyerSalesExcel(List<BuyerInfoDTO> buyers) throws IOException {

	    SXSSFWorkbook workbook = new SXSSFWorkbook(200);
	    workbook.setCompressTempFiles(true);

	    Sheet sheet = workbook.createSheet("BUYER_SALES_REPORT");

	    // =========================================================
	    // STYLES
	    // =========================================================

	    CellStyle headerStyle = workbook.createCellStyle();

	    Font headerFont = workbook.createFont();
	    headerFont.setBold(true);
	    headerFont.setColor(IndexedColors.WHITE.getIndex());

	    headerStyle.setFont(headerFont);
	    headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
	    headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

	    headerStyle.setBorderBottom(BorderStyle.THIN);
	    headerStyle.setBorderTop(BorderStyle.THIN);
	    headerStyle.setBorderLeft(BorderStyle.THIN);
	    headerStyle.setBorderRight(BorderStyle.THIN);

	    headerStyle.setAlignment(HorizontalAlignment.CENTER);

	    CellStyle dataStyle = workbook.createCellStyle();

	    dataStyle.setBorderBottom(BorderStyle.THIN);
	    dataStyle.setBorderTop(BorderStyle.THIN);
	    dataStyle.setBorderLeft(BorderStyle.THIN);
	    dataStyle.setBorderRight(BorderStyle.THIN);

	    // =========================================================
	    // SUMMARY STYLES
	    // =========================================================

	    CellStyle summaryLabelStyle = workbook.createCellStyle();

	    Font summaryFont = workbook.createFont();

	    summaryFont.setBold(true);
	    summaryFont.setColor(IndexedColors.WHITE.getIndex());

	    summaryLabelStyle.setFont(summaryFont);

	    summaryLabelStyle.setFillForegroundColor(
	            IndexedColors.DARK_BLUE.getIndex());

	    summaryLabelStyle.setFillPattern(
	            FillPatternType.SOLID_FOREGROUND);

	    summaryLabelStyle.setBorderBottom(BorderStyle.THIN);
	    summaryLabelStyle.setBorderTop(BorderStyle.THIN);
	    summaryLabelStyle.setBorderLeft(BorderStyle.THIN);
	    summaryLabelStyle.setBorderRight(BorderStyle.THIN);

	    CellStyle summaryValueStyle = workbook.createCellStyle();

	    Font valueFont = workbook.createFont();

	    valueFont.setBold(true);
	    valueFont.setFontHeightInPoints((short) 12);

	    summaryValueStyle.setFont(valueFont);

	    summaryValueStyle.setBorderBottom(BorderStyle.THIN);
	    summaryValueStyle.setBorderTop(BorderStyle.THIN);
	    summaryValueStyle.setBorderLeft(BorderStyle.THIN);
	    summaryValueStyle.setBorderRight(BorderStyle.THIN);

	    // =========================================================
	    // TOTAL SUMMARY
	    // =========================================================

	    int totalSoldItemCount = 0;

	    BigDecimal totalValue = BigDecimal.ZERO;
	    BigDecimal totalSellingPrice = BigDecimal.ZERO;
	    BigDecimal totalSoldPrice = BigDecimal.ZERO;
	    BigDecimal totalPaidAmount = BigDecimal.ZERO;
	    BigDecimal totalPendingAmount = BigDecimal.ZERO;

	    for (BuyerInfoDTO buyer : buyers) {

	        BigDecimal restAmount = BigDecimal.ZERO;

	        if (buyer.getRestAmount() != null && !buyer.getRestAmount().isBlank()) {
	            restAmount = new BigDecimal(buyer.getRestAmount());
	        }

	        totalPaidAmount = totalPaidAmount
	                .add(BigDecimal.valueOf(buyer.getPaidAmount()))
	                .add(restAmount);

	        List<SalesInfoDTO> salesList = buyer.getSalesInfo();

	        if (salesList == null) {
	            continue;
	        }

	        for (SalesInfoDTO sales : salesList) {

	            totalSoldItemCount += sales.getQuantity();

	            totalValue = totalValue.add(
	                    BigDecimal.valueOf(sales.getInitialPrice())
	            );

	            totalSellingPrice = totalSellingPrice.add(
	                    BigDecimal.valueOf(sales.getSellingPrice())
	            );

	            totalSoldPrice = totalSoldPrice.add(
	                    BigDecimal.valueOf(sales.getSoldPrice())
	            );
	        }
	    }

	    totalPendingAmount = totalSoldPrice.subtract(totalPaidAmount);

	    // =========================================================
	    // REPORT TITLE
	    // =========================================================

	    Row titleRow = sheet.createRow(0);

	    Cell titleCell = titleRow.createCell(0);

	    titleCell.setCellValue("BUYER SALES REPORT");

	    titleCell.setCellStyle(summaryLabelStyle);

	    sheet.addMergedRegion(
	            new CellRangeAddress(0, 0, 0, 4));

	    // =========================================================
	    // SUMMARY BOX
	    // =========================================================

	    String[][] summaryData = {
	    		{"Sold Count",
                    " " + totalSoldItemCount},

	            {"Total Value",
	                    "₹ " + totalValue},

	            {"Total Selling Value",
	                    "₹ " + totalSellingPrice},

	            {"Total Sold Value",
	                    "₹ " + totalSoldPrice},

	            {"Total Paid Amount",
	                    "₹ " + totalPaidAmount},

	            {"Pending Amount",
	                    "₹ " + totalPendingAmount}
	                    
	    };

	    int summaryStartRow = 2;

	    for (int i = 0; i < summaryData.length; i++) {

	        Row row = sheet.createRow(summaryStartRow + i);

	        Cell labelCell = row.createCell(0);

	        labelCell.setCellValue(summaryData[i][0]);

	        labelCell.setCellStyle(summaryLabelStyle);

	        Cell valueCell = row.createCell(1);

	        valueCell.setCellValue(summaryData[i][1]);

	        valueCell.setCellStyle(summaryValueStyle);
	    }

	    sheet.setColumnWidth(0, 7000);
	    sheet.setColumnWidth(1, 5000);

	    // =========================================================
	    // DYNAMIC COLUMNS
	    // =========================================================

	    LinkedHashMap<String, Function<RowData, Object>> columns =
	            new LinkedHashMap<>();

	    // ================= BUYER =================

	    addColumnIfDataExists(columns, buyers, "Invoice Number",
	            r -> r.buyer != null ? r.buyer.getInvoiceNumber() : null);

	    addColumnIfDataExists(columns, buyers, "Customer Id",
	            r -> r.buyer != null ? r.buyer.getCustumberId() : null);

	    addColumnIfDataExists(columns, buyers, "Buyer Name",
	            r -> r.buyer != null ? r.buyer.getBuyerName() : null);

	    addColumnIfDataExists(columns, buyers, "Mobile Number",
	            r -> r.buyer != null ? r.buyer.getMobileNumber() : null);

	    addColumnIfDataExists(columns, buyers, "Email",
	            r -> r.buyer != null ? r.buyer.getEmailId() : null);

	    addColumnIfDataExists(columns, buyers, "Company Name",
	            r -> r.buyer != null ? r.buyer.getCompanyName() : null);

	    addColumnIfDataExists(columns, buyers, "GST Number",
	            r -> r.buyer != null ? r.buyer.getGstNumber() : null);

	    addColumnIfDataExists(columns, buyers, "PAN Number",
	            r -> r.buyer != null ? r.buyer.getPanNumber() : null);

	    addColumnIfDataExists(columns, buyers, "State",
	            r -> r.buyer != null ? r.buyer.getState() : null);

	    addColumnIfDataExists(columns, buyers, "District",
	            r -> r.buyer != null ? r.buyer.getDistrict() : null);

	    addColumnIfDataExists(columns, buyers, "Payment Modes",
	            r -> r.buyer != null ? r.buyer.getPaymentModes() : null);	         
	    
	    addColumnIfDataExists(columns, buyers, "Paid Amount",
	            r -> r.buyer != null
	                    ? r.buyer.getPaidAmount() +
	                      ((r.buyer.getRestAmount() != null &&
	                        !r.buyer.getRestAmount().isBlank())
	                              ? Double.parseDouble(r.buyer.getRestAmount())
	                              : 0.0)
	                    : null);
	    addColumnIfDataExists(columns, buyers, "Pending Amount",
	            r -> {
	                if (r.buyer == null) return null;

	                double soldTotal = (r.buyer.getSalesInfo() == null)
	                        ? 0.0
	                        : r.buyer.getSalesInfo().stream()
	                            .mapToDouble(SalesInfoDTO::getSoldPrice)
	                            .sum();

	                double paid = r.buyer.getPaidAmount();
	                double rest = (r.buyer.getRestAmount() != null && !r.buyer.getRestAmount().isBlank())
	                        ? Double.parseDouble(r.buyer.getRestAmount())
	                        : 0.0;

	                return soldTotal - (paid + rest);
	            });
	    
	    addColumnIfDataExists(columns, buyers, "Finance Id",
	            r -> r.buyer != null ? r.buyer.getFinanceId() : null);

	    addColumnIfDataExists(columns, buyers, "IMEI Number",
	            r -> r.buyer != null ? r.buyer.getImeiNumber() : null);

	    // ================= SALES =================

	    addColumnIfDataExists(columns, buyers, "Item Code",
	            r -> r.sales != null ? r.sales.getItemCode() : null);

	    addColumnIfDataExists(columns, buyers, "Category",
	            r -> r.sales != null ? r.sales.getCategory() : null);

	    addColumnIfDataExists(columns, buyers, "Brand",
	            r -> r.sales != null ? r.sales.getBrand() : null);

	    addColumnIfDataExists(columns, buyers, "Model Name",
	            r -> r.sales != null ? r.sales.getModelName() : null);

	    addColumnIfDataExists(columns, buyers, "Quantity",
	            r -> r.sales != null ? r.sales.getQuantity() : null);

	    addColumnIfDataExists(columns, buyers, "Selling Price",
	            r -> r.sales != null ? r.sales.getSellingPrice() : null);

	    addColumnIfDataExists(columns, buyers, "Sold Price",
	            r -> r.sales != null ? r.sales.getSoldPrice() : null);

	    addColumnIfDataExists(columns, buyers, "Serial Number",
	            r -> r.sales != null ? r.sales.getSerialNumber() : null);

	    addColumnIfDataExists(columns, buyers, "Warranty",
	            r -> r.sales != null ? r.sales.getWarrantyInMonth() : null);

	    // ================= SUPPLIER =================

	    addColumnIfDataExists(columns, buyers, "Supplier Name",
	            r -> r.sales != null ? r.sales.getVendorName() : null);

	    addColumnIfDataExists(columns, buyers, "Supplier GST Number",
	            r -> r.sales != null ? r.sales.getVendorGstNumber() : null);

	    // =========================================================
	    // HEADER
	    // =========================================================

	    int tableStartRow = 10;

	    Row headerRow = sheet.createRow(tableStartRow);

	    int headerCol = 0;

	    for (String header : columns.keySet()) {

	        Cell cell = headerRow.createCell(headerCol);

	        cell.setCellValue(header);

	        cell.setCellStyle(headerStyle);

	        sheet.setColumnWidth(headerCol, 6000);

	        headerCol++;
	    }

	    // =========================================================
	    // DATA
	    // =========================================================

	    int rowIdx = tableStartRow + 1;

	    for (BuyerInfoDTO buyer : buyers) {

	        List<SalesInfoDTO> salesList = buyer.getSalesInfo();

	        if (salesList == null || salesList.isEmpty()) {

	            Row row = sheet.createRow(rowIdx++);

	            int col = 0;

	            RowData rowData = new RowData(buyer, null);

	            for (Function<RowData, Object> extractor
	                    : columns.values()) {

	                Object value = extractor.apply(rowData);

	                Cell cell = row.createCell(col++);

	                setCellValue(cell, value);

	                cell.setCellStyle(dataStyle);
	            }

	            continue;
	        }

	        for (SalesInfoDTO sales : salesList) {

	            Row row = sheet.createRow(rowIdx++);

	            int col = 0;

	            RowData rowData = new RowData(buyer, sales);

	            for (Function<RowData, Object> extractor
	                    : columns.values()) {

	                Object value = extractor.apply(rowData);

	                Cell cell = row.createCell(col++);

	                setCellValue(cell, value);

	                cell.setCellStyle(dataStyle);
	            }
	        }
	    }

	    // =========================================================
	    // FREEZE HEADER
	    // =========================================================

	    sheet.createFreezePane(0, tableStartRow + 1);

	    // =========================================================
	    // SAVE FILE
	    // =========================================================

	    String userHome = System.getProperty("user.home");

	    Path folder = Paths.get(
	            userHome,
	            "Downloads",
	            Constaints.DOWNLOAD_FOLDER_NAME
	    );

	    if (!Files.exists(folder)) {
	        Files.createDirectories(folder);
	    }

	    String fileName =
	            "sales_report_" + System.currentTimeMillis() + ".xlsx";

	    Path filePath = folder.resolve(fileName);

	    try (FileOutputStream fos =
	                 new FileOutputStream(filePath.toFile())) {

	        workbook.write(fos);

	        fos.flush();

	    } finally {

	        workbook.close();

	        workbook.dispose();
	    }
	}

// =========================================================
// HELPER CLASS
// =========================================================

private static class RowData {

    private final BuyerInfoDTO buyer;
    private final SalesInfoDTO sales;

    public RowData(BuyerInfoDTO buyer, SalesInfoDTO sales) {
        this.buyer = buyer;
        this.sales = sales;
    }
}

// =========================================================
// CHECK COLUMN HAS DATA
// =========================================================

private void addColumnIfDataExists(
        LinkedHashMap<String, Function<RowData, Object>> columns,
        List<BuyerInfoDTO> buyers,
        String header,
        Function<RowData, Object> extractor) {

    boolean hasData = buyers.stream().anyMatch(buyer -> {

        List<SalesInfoDTO> salesList = buyer.getSalesInfo();

        if (salesList == null || salesList.isEmpty()) {

            Object value = extractor.apply(new RowData(buyer, null));

            return value != null &&
                    !String.valueOf(value).trim().isEmpty();
        }

        return salesList.stream().anyMatch(sales -> {

            Object value =
                    extractor.apply(new RowData(buyer, sales));

            return value != null &&
                    !String.valueOf(value).trim().isEmpty();
        });
    });

    if (hasData) {
        columns.put(header, extractor);
    }
}

// =========================================================
// SAFE CELL VALUE
// =========================================================

private void setCellValue(Cell cell, Object value) {

    if (value == null) {
        cell.setCellValue("");
        return;
    }

    if (value instanceof Number) {
        cell.setCellValue(((Number) value).doubleValue());
    } else if (value instanceof Boolean) {
        cell.setCellValue((Boolean) value);
    } else {
        cell.setCellValue(String.valueOf(value));
    }
}

	private static String nvl(String val) {
		return (val != null && !val.trim().isEmpty()) ? val : "-";
	}

}