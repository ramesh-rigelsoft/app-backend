package com.rigel.app.serviceimpl;

import org.apache.poi.xssf.streaming.SXSSFSheet;
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
import com.rigel.app.model.dto.ReportSummaryDTO;
import com.rigel.app.model.dto.SalesInfoDto;
import com.rigel.app.model.dto.SalesInfoDtoResponseList;
import com.rigel.app.model.dto.VendorInvoiceDTO;
import com.rigel.app.model.dto.VendorPerformanceDTO;
import com.rigel.app.util.Constaints;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rigel.app.builder.BuyerInfoDTO;
import com.rigel.app.builder.SalesInfoDTO;
import com.rigel.app.dao.ILoginInfoDao;
import com.rigel.app.dao.ISalesDao;

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
	private ISalesDao salesDao;

	public void exportItemsDashboardExcel(ItemsDashboardResponse response) throws IOException {

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
		String[] headers = { "Item Code", "Vendor", "Invoice", "Category", "Brand", "Model", "Qty", "Selling Price",
				"Created At" };

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

		Path filePath = folder.resolve("ITEMS_DASHBOARD_" + System.currentTimeMillis() + ".xlsx");

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

	///////////////////////////////// --------Sales Info Excel
	///////////////////////////////// Download--------//////////////////////////////////////

	public void exportReportSummaryExcel(ReportSummaryDTO summary) throws IOException {
        
	    SXSSFWorkbook workbook = new SXSSFWorkbook(100);
	    workbook.setCompressTempFiles(true);

	    Sheet sheet = workbook.createSheet("REPORT");

	    // ================= STYLES =================
	    CellStyle labelStyle = workbook.createCellStyle();
	    Font font = workbook.createFont();
	    font.setBold(true);
	    labelStyle.setFont(font);

	    CellStyle valueStyle = workbook.createCellStyle();

	    CellStyle headerStyle = workbook.createCellStyle();
	    Font hFont = workbook.createFont();
	    hFont.setBold(true);
	    headerStyle.setFont(hFont);

	    CellStyle dataStyle = workbook.createCellStyle();

	    int rowIdx = 0;

	    // ================= SUMMARY =================
	    rowIdx = writeRow(sheet, rowIdx, "Total Sold Count", summary.getTotalSoldCount(), labelStyle, valueStyle);
	    rowIdx = writeRow(sheet, rowIdx, "Total Initial Price", summary.getTotalInitialPrice(), labelStyle, valueStyle);
	    rowIdx = writeRow(sheet, rowIdx, "Total Selling Price", summary.getTotalSellingPrice(), labelStyle, valueStyle);
	    rowIdx = writeRow(sheet, rowIdx, "Total Sold Price", summary.getTotalSoldPrice(), labelStyle, valueStyle);
	    rowIdx = writeRow(sheet, rowIdx, "Total Paid Amount", summary.getTotalPaidAmount(), labelStyle, valueStyle);
	    rowIdx = writeRow(sheet, rowIdx, "Pending Amount", summary.getPendingAmount(), labelStyle, valueStyle);

	    rowIdx += 2;

	    // ================= FULL HEADERS (ALL DTO FIELDS) =================
	    String[] headers = {

	            // BUYER INFO
	            "Invoice Number",
	            "Customer ID",
	            "Buyer Name",
	            "Mobile Number",
	            "Email ID",
	            "Company Name",
	            "GST Number",
	            "State",
	            "Payment Modes",
	            "Paid Amount",
	            "Pending Amount",

	            // SALES INFO
	            "Item Code",
	            "Brand",
	            "Model Name",
	            "Category Type",

	            "Quantity",
	            "Initial Price",
	            "Selling Price",
	            "Sold Price",

	            "Vendor Name",
	            "Vendor GST Number",

	            "Serial Number",
	            "Warranty (Month)",

	            "Created At"
	    };

	    Row headerRow = sheet.createRow(rowIdx++);

	    for (int i = 0; i < headers.length; i++) {
	        Cell cell = headerRow.createCell(i);
	        cell.setCellValue(headers[i]);
	        cell.setCellStyle(headerStyle);
	        sheet.setColumnWidth(i, 6000);
	    }

	    // ================= DATA =================
	    List<SalesInfoDtoResponseList> salesList = summary.getSalesList();

	    if (salesList != null && !salesList.isEmpty()) {

	        int count = 0;

	        for (SalesInfoDtoResponseList s : salesList) {

	            Row row = sheet.createRow(rowIdx++);
	            int col = 0;

	            // -------- BUYER --------
	            setCell(row, col++, s.getInvoiceNumber(), dataStyle);
	            setCell(row, col++, s.getCustumberId(), dataStyle);
	            setCell(row, col++, s.getBuyerName(), dataStyle);
	            setCell(row, col++, s.getMobileNumber(), dataStyle);
	            setCell(row, col++, s.getEmailId(), dataStyle);
	            setCell(row, col++, s.getCompanyName(), dataStyle);
	            setCell(row, col++, s.getGstNumber(), dataStyle);
	            setCell(row, col++, s.getState(), dataStyle);
	            setCell(row, col++, s.getPaymentModes(), dataStyle);
	            setCell(row, col++, s.getPaidAmount(), dataStyle);
	            setCell(row, col++, s.getBorrowAmount(), dataStyle);

	            // -------- SALES --------
	            setCell(row, col++, s.getItemCode(), dataStyle);
	            setCell(row, col++, s.getBrand(), dataStyle);
	            setCell(row, col++, s.getModelName(), dataStyle);
	            setCell(row, col++, s.getCategoryType(), dataStyle);

	            setCell(row, col++, s.getQuantity(), dataStyle);
	            setCell(row, col++, s.getInitialPrice(), dataStyle);
	            setCell(row, col++, s.getSellingPrice(), dataStyle);
	            setCell(row, col++, s.getSoldPrice(), dataStyle);

	            setCell(row, col++, s.getVendorName(), dataStyle);
	            setCell(row, col++, s.getVendorGSTNumber(), dataStyle);

	            setCell(row, col++, s.getSerialNumber(), dataStyle);
	            setCell(row, col++, s.getWarrantyInMonth(), dataStyle);

	            setCell(row, col++, s.getCreatedAt(), dataStyle);

	            if (++count % 1000 == 0) {
	                ((SXSSFSheet) sheet).flushRows(1000);
	            }
	        }
	    }

	    // ================= SAVE =================
	    String fileName = "report_" + System.currentTimeMillis() + ".xlsx";

	    Path path = Paths.get(
	            System.getProperty("user.home"),
	            "Downloads",
	            fileName
	    );

	    try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
	        workbook.write(fos);
	    } finally {
	        workbook.close();
	        workbook.dispose();
	    }
	}

	private int writeRow(Sheet sheet, int rowIdx, String label, Object value, CellStyle labelStyle,
			CellStyle valueStyle) {

		Row row = sheet.createRow(rowIdx++);

		Cell c1 = row.createCell(0);
		c1.setCellValue(label);
		c1.setCellStyle(labelStyle);

		Cell c2 = row.createCell(1);
		c2.setCellValue(value != null ? value.toString() : "0");
		c2.setCellStyle(valueStyle);

		return rowIdx;
	}

	private void setCell(Row row, int col, Object value, CellStyle style) {
		Cell cell = row.createCell(col);
		cell.setCellStyle(style);
		if (value != null)
			cell.setCellValue(value.toString());
	}

	private String safe(String v) {
		return v == null ? "" : v;
	}

}