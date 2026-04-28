package com.rigel.app.util;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.rigel.app.model.BuyerInfo;
import com.rigel.app.model.Items;
import com.rigel.app.model.SalesInfo;

import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;

public class ExcelDirectSave {

	public static void exportItemsToExcel(List<Items> items) {

	    try (Workbook workbook = new XSSFWorkbook()) {
	    	
	    	CellStyle dateCellStyle = workbook.createCellStyle();
	    	CreationHelper createHelper = workbook.getCreationHelper();
	    	dateCellStyle.setDataFormat(
	    	        createHelper.createDataFormat().getFormat("dd-MM-yyyy HH:mm")
	    	);
	    	CellStyle moneyStyle = workbook.createCellStyle();
	    	DataFormat format = workbook.createDataFormat();

	    	// ₹ with Indian grouping
	    	moneyStyle.setDataFormat(format.getFormat("₹ #,##,##0.00"));

	        Sheet sheet = workbook.createSheet("Items");

	        // 🔥 Header Style
	        Font headerFont = workbook.createFont();
	        headerFont.setBold(true);
	        headerFont.setColor(IndexedColors.WHITE.getIndex());

	        CellStyle headerStyle = workbook.createCellStyle();
	        headerStyle.setFont(headerFont);
	        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
	        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	        headerStyle.setAlignment(HorizontalAlignment.CENTER);

	        // 📌 Columns
	        String[] headers = {
	                "Item Code", "Category", "Category Type", "Measure Type",
	                "Brand", "Model", "Condition", "Source",
	                "RAM",
	                "Storage", "Storage Type",
	                "Quantity", "Initial Price", "Selling Price",
	                "Description", "Color",
	                "Processor", "OS", "Screen Size",
	                "Generation", "Serial No", "Created At"
	        };

	        // 🔹 Create Header Row
	        Row headerRow = sheet.createRow(0);

	        for (int i = 0; i < headers.length; i++) {
	            Cell cell = headerRow.createCell(i);
	            cell.setCellValue(headers[i]);
	            cell.setCellStyle(headerStyle);
	        }

	        // 🔹 Data Rows
	        int rowIndex = 1;

	        for (Items item : items) {

	            Row row = sheet.createRow(rowIndex++);

	            int col = 0;
	            row.createCell(col++).setCellValue(nvl(item.getItemCode()));
	            row.createCell(col++).setCellValue(nvl(item.getCategory()));
	            row.createCell(col++).setCellValue(nvl(item.getCategoryType()));
	            row.createCell(col++).setCellValue(nvl(item.getMeasureType()));

	            row.createCell(col++).setCellValue(nvl(item.getBrand()));
	            row.createCell(col++).setCellValue(nvl(item.getModelName()));
	            row.createCell(col++).setCellValue(nvl(item.getItemCondition()));
	            row.createCell(col++).setCellValue(nvl(item.getItemSource()));

	            row.createCell(col++).setCellValue(nvl(item.getRam()+""+item.getRamUnit()));

	            row.createCell(col++).setCellValue(nvl(item.getStorage()+""+item.getStorageUnit()));
	            row.createCell(col++).setCellValue(nvl(item.getStorageType()));

	            row.createCell(col++).setCellValue(item.getQuantity() != null ? item.getQuantity() : 0);
	          
	         // Initial Price
	            Cell initPriceCell = row.createCell(col++);
	            if (item.getInitialPrice() != null) {
	                initPriceCell.setCellValue(item.getInitialPrice());
	                initPriceCell.setCellStyle(moneyStyle);
	            } else {
	                initPriceCell.setCellValue(0);
	            }

	            // Selling Price
	            Cell sellPriceCell = row.createCell(col++);
	            if (item.getSellingPrice() != null) {
	                sellPriceCell.setCellValue(item.getSellingPrice());
	                sellPriceCell.setCellStyle(moneyStyle);
	            } else {
	                sellPriceCell.setCellValue(0);
	            }
	            
//	            row.createCell(col++).setCellValue(item.getInitialPrice() != null ? item.getInitialPrice() : 0);
//	            row.createCell(col++).setCellValue(item.getSellingPrice() != null ? item.getSellingPrice() : 0);

	            row.createCell(col++).setCellValue(nvl(item.getDescription()));
	            row.createCell(col++).setCellValue(nvl(item.getItemColor()));

	            row.createCell(col++).setCellValue(nvl(item.getProcessor()));
	            row.createCell(col++).setCellValue(nvl(item.getOperatingSystem()));
	            row.createCell(col++).setCellValue(nvl(item.getScreenSize()));

	            row.createCell(col++).setCellValue(nvl(item.getItemGen()));
	            row.createCell(col++).setCellValue(nvl(item.getSerialNumber()));
	            
	            Cell dateCell = row.createCell(col++);

	            if (item.getCreatedAt() != null) {
	                dateCell.setCellValue(
	                    java.sql.Timestamp.valueOf(item.getCreatedAt())
	                );
	                dateCell.setCellStyle(dateCellStyle);
	            } else {
	                dateCell.setCellValue("");
	            }
	         
	        }

	        // 📏 Auto-size columns
	        for (int i = 0; i < headers.length; i++) {
	            sheet.autoSizeColumn(i);
	        }
	        
            String userHome = System.getProperty("user.home");
            Path folder = Paths.get(userHome, "Downloads", Constaints.DOWNLOAD_FOLDER_NAME);

            // 🔥 create folder if not exists
            if (!Files.exists(folder)) {
                Files.createDirectories(folder);
            }

            String fileName = "items_report_" + System.currentTimeMillis() + ".xlsx";
            Path filePath = folder.resolve(fileName);

            // 💾 Save file
            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                workbook.write(fos);
            }
            
	        System.out.println("Excel file created: " + filePath);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	
    public static void exportSalesToExcel(List<SalesInfo> salesList) {

	    try (Workbook workbook = new XSSFWorkbook()) {

	        Sheet sheet = workbook.createSheet("Items");

	        // 🔥 Header Style
	        Font headerFont = workbook.createFont();
	        headerFont.setBold(true);
	        headerFont.setColor(IndexedColors.WHITE.getIndex());

	        CellStyle headerStyle = workbook.createCellStyle();
	        headerStyle.setFont(headerFont);
	        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
	        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	        headerStyle.setAlignment(HorizontalAlignment.CENTER);

	        // 📌 Columns
	        String[] headers = {
	        		"Invoice Number","Item Code", "Category", "Category Type", "Measure Type",
	                "Brand", "Model", "Condition", "Source",
	                "RAM",
	                "Storage", "Storage Type",
	                "Quantity", "Initial Price", "Selling Price","Sold Price",
	                "Description", "Color",
	                "Processor", "OS", "Screen Size",
	                "Generation", "Serial No", "Created At"
	        };

	        // 🔹 Create Header Row
	        Row headerRow = sheet.createRow(0);

	        for (int i = 0; i < headers.length; i++) {
	            Cell cell = headerRow.createCell(i);
	            cell.setCellValue(headers[i]);
	            cell.setCellStyle(headerStyle);
	        }

	        // 🔹 Data Rows
	        int rowIndex = 1;

	        for (SalesInfo item : salesList) {

	            Row row = sheet.createRow(rowIndex++);

	            int col = 0;
	            row.createCell(col++).setCellValue(nvl(item.getBuyerInfo().getInvoiceNumber()));
	            row.createCell(col++).setCellValue(nvl(item.getItemCode()));
	            row.createCell(col++).setCellValue(nvl(item.getCategory()));
	            row.createCell(col++).setCellValue(nvl(item.getCategoryType()));
	            row.createCell(col++).setCellValue(nvl(item.getMeasureType()));

	            row.createCell(col++).setCellValue(nvl(item.getBrand()));
	            row.createCell(col++).setCellValue(nvl(item.getModelName()));
	            row.createCell(col++).setCellValue(nvl(item.getItemCondition()));
	            row.createCell(col++).setCellValue(nvl(item.getItemSource()));

	            row.createCell(col++).setCellValue(nvl(item.getRam()+""+item.getRamUnit()));

	            row.createCell(col++).setCellValue(nvl(item.getStorage()+""+item.getStorageUnit()));
	            row.createCell(col++).setCellValue(nvl(item.getStorageType()));

	            row.createCell(col++).setCellValue(item.getQuantity() != null ? item.getQuantity() : 0);
	            row.createCell(col++).setCellValue(item.getInitialPrice() != null ? item.getInitialPrice() : 0);
	            row.createCell(col++).setCellValue(item.getSellingPrice() != null ? item.getSellingPrice() : 0);
	            row.createCell(col++).setCellValue(item.getSoldPrice() != null ? item.getSoldPrice() : 0);

	            row.createCell(col++).setCellValue(nvl(item.getDescription()));
	            row.createCell(col++).setCellValue(nvl(item.getItemColor()));

	            row.createCell(col++).setCellValue(nvl(item.getProcessor()));
	            row.createCell(col++).setCellValue(nvl(item.getOperatingSystem()));
	            row.createCell(col++).setCellValue(nvl(item.getScreenSize()));

	            row.createCell(col++).setCellValue(nvl(item.getItemGen()));
	            row.createCell(col++).setCellValue(nvl(item.getSerialNumber()));

	            row.createCell(col++).setCellValue(
	                    item.getCreatedAt() != null ? item.getCreatedAt().now().toString() : ""
	            );
	        }

	        // 📏 Auto-size columns
	        for (int i = 0; i < headers.length; i++) {
	            sheet.autoSizeColumn(i);
	        }
	        
            String userHome = System.getProperty("user.home");
            Path folder = Paths.get(userHome, "Downloads", Constaints.DOWNLOAD_FOLDER_NAME);

            // 🔥 create folder if not exists
            if (!Files.exists(folder)) {
                Files.createDirectories(folder);
            }

            String fileName = "sales_report_" + System.currentTimeMillis() + ".xlsx";
            Path filePath = folder.resolve(fileName);

            // 💾 Save file
            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                workbook.write(fos);
            }
            
	        System.out.println("Excel file created: " + filePath);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
    }
    
    private static String nvl(String val) {
        return (val != null && !val.trim().isEmpty()) ? val : "-";
    }
}