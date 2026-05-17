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
import com.rigel.app.util.Constaints;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rigel.app.dao.ILoginInfoDao;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;

@Service
public class ExcelDirectSave {
	
	@Autowired
	private ILoginInfoDao loginInfoDao;
	
	@Autowired
	private ObjectMapper objectMapper;

	public void exportItemsToExcel(List<Items> items) {
		LoginActivity loginActivity=loginInfoDao.findLoginActivityByuserId(items.get(0).getOwnerId());
		User user=null;
		try {
			user = objectMapper.readValue(loginActivity.getUserObject(), User.class);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  
		try (Workbook workbook = new XSSFWorkbook()) {

	        Sheet sheet = workbook.createSheet("Items");

	        // =========================================================
	        // STYLES
	        // =========================================================

	        // Company Title Style
	        Font companyFont = workbook.createFont();
	        companyFont.setBold(true);
	        companyFont.setFontHeightInPoints((short) 18);
	        companyFont.setColor(IndexedColors.DARK_BLUE.getIndex());

	        CellStyle companyStyle = workbook.createCellStyle();
	        companyStyle.setFont(companyFont);
	        companyStyle.setAlignment(HorizontalAlignment.CENTER);

	        // Report Title Style
	        Font titleFont = workbook.createFont();
	        titleFont.setBold(true);
	        titleFont.setFontHeightInPoints((short) 13);

	        CellStyle titleStyle = workbook.createCellStyle();
	        titleStyle.setFont(titleFont);
	        titleStyle.setAlignment(HorizontalAlignment.CENTER);

	        // Header Style
	        Font headerFont = workbook.createFont();
	        headerFont.setBold(true);
	        headerFont.setColor(IndexedColors.WHITE.getIndex());

	        CellStyle headerStyle = workbook.createCellStyle();
	        headerStyle.setFont(headerFont);
	        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
	        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	        headerStyle.setAlignment(HorizontalAlignment.CENTER);

	        headerStyle.setBorderTop(BorderStyle.THIN);
	        headerStyle.setBorderBottom(BorderStyle.THIN);
	        headerStyle.setBorderLeft(BorderStyle.THIN);
	        headerStyle.setBorderRight(BorderStyle.THIN);

	        // Normal Cell Style
	        CellStyle dataStyle = workbook.createCellStyle();
	        dataStyle.setBorderTop(BorderStyle.THIN);
	        dataStyle.setBorderBottom(BorderStyle.THIN);
	        dataStyle.setBorderLeft(BorderStyle.THIN);
	        dataStyle.setBorderRight(BorderStyle.THIN);

	        // Money Style
	        CellStyle moneyStyle = workbook.createCellStyle();
	        DataFormat format = workbook.createDataFormat();
	        moneyStyle.setDataFormat(format.getFormat("₹ #,##,##0.00"));

	        moneyStyle.setBorderTop(BorderStyle.THIN);
	        moneyStyle.setBorderBottom(BorderStyle.THIN);
	        moneyStyle.setBorderLeft(BorderStyle.THIN);
	        moneyStyle.setBorderRight(BorderStyle.THIN);

	        // Date Style
	        CellStyle dateCellStyle = workbook.createCellStyle();
	        CreationHelper createHelper = workbook.getCreationHelper();

	        dateCellStyle.setDataFormat(
	                createHelper.createDataFormat().getFormat("dd-MM-yyyy HH:mm"));

	        // =========================================================
	        // CALCULATE TOTAL SALES
	        // =========================================================

	        double totalSales = items.stream()
	                .mapToDouble(i -> i.getInitialPrice() != null
	                        ? i.getSellingPrice()
	                        : 0)
	                .sum();

	        // =========================================================
	        // TOP PROFESSIONAL HEADER
	        // =========================================================

	        // Company Name
	        Row companyRow = sheet.createRow(0);
	        companyRow.setHeightInPoints(28);

	        Cell companyCell = companyRow.createCell(0);
	        companyCell.setCellValue(user.getCompanyName().toUpperCase());
	        companyCell.setCellStyle(companyStyle);

	        // Merge company row
	        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 23));

	        // Report Title
	        Row titleRow = sheet.createRow(1);

	        Cell titleCell = titleRow.createCell(0);
	        titleCell.setCellValue("Items Purchase Report");
	        titleCell.setCellStyle(titleStyle);

	        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 23));

	        // Report Info Row
	        Row infoRow = sheet.createRow(2);

	        infoRow.createCell(0).setCellValue(
	                "Generated On: " +
	                        java.time.LocalDateTime.now()
	        );

	        infoRow.createCell(10).setCellValue(
	                "Total Purchase Value:"
	        );

	        Cell totalCell = infoRow.createCell(12);
	        totalCell.setCellValue(totalSales);
	        totalCell.setCellStyle(moneyStyle);

	        // Empty Row
	        sheet.createRow(3);

	        // =========================================================
	        // TABLE HEADER
	        // =========================================================

	     // 📌 Columns
	        String[] headers = {
	                "Item Code","Vendor Name","GSTIN Number", "Category", "Category Type", "Measure Type",
	                "Brand", "Model", "Condition", "Source",
	                "RAM",
	                "Storage", "Storage Type",
	                "Quantity", "Initial Price", "Selling Price",
	                "Description", "Color",
	                "Processor", "OS", "Screen Size",
	                "Generation", "Serial No", "Created At"
	        };
	        Row headerRow = sheet.createRow(4);

	        for (int i = 0; i < headers.length; i++) {

	            Cell cell = headerRow.createCell(i);

	            cell.setCellValue(headers[i]);
	            cell.setCellStyle(headerStyle);
	        }

	        // 🔹 Data Rows
	        int rowIndex = 5;

	        for (Items item : items) {

	            Row row = sheet.createRow(rowIndex++);

	            int col = 0;
	            row.createCell(col++).setCellValue(nvl(item.getItemCode()));
	            

	            row.createCell(col++).setCellValue(nvl(item.getVendorName()));
	            row.createCell(col++).setCellValue(nvl(item.getVendorGSTNumber()));
	            
	            row.createCell(col++).setCellValue(nvl(item.getCategory()));
	            row.createCell(col++).setCellValue(nvl(item.getCategoryType()));
	            row.createCell(col++).setCellValue(nvl(item.getMeasureType()));

	            row.createCell(col++).setCellValue(nvl(item.getBrand()));
	            row.createCell(col++).setCellValue(nvl(item.getModelName()));
	            row.createCell(col++).setCellValue(nvl(item.getItemCondition()));
	            row.createCell(col++).setCellValue(nvl(item.getItemSource()));

	            row.createCell(col++).setCellValue(nvl(item.getRam()!="null"&&item.getRam()!=null?(item.getRam()+""+item.getRamUnit()):"-"));
	            row.createCell(col++).setCellValue(nvl(item.getStorage()!="null"&&item.getStorage()!=null?(item.getStorage()+""+item.getStorageUnit()):"-"));
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
	                "items_report_" + System.currentTimeMillis() + ".xlsx";

	        Path filePath = folder.resolve(fileName);

	        try (FileOutputStream fos =
	                     new FileOutputStream(filePath.toFile())) {

	            workbook.write(fos);
	        }

	        System.out.println("Excel file created: " + filePath);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
    public void exportSalesToExcel(List<SalesInfo> salesList) {

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
	        		"Invoice Number","Item Code", "Vendor Name","GSTIN Number","Category", "Category Type", "Measure Type",
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
	         
	            row.createCell(col++).setCellValue(nvl(item.getVendorName()));
	            row.createCell(col++).setCellValue(nvl(item.getVendorGSTNumber()));
	         
	            row.createCell(col++).setCellValue(nvl(item.getCategory()));
	            row.createCell(col++).setCellValue(nvl(item.getCategoryType()));
	            row.createCell(col++).setCellValue(nvl(item.getMeasureType()));

	            row.createCell(col++).setCellValue(nvl(item.getBrand()));
	            row.createCell(col++).setCellValue(nvl(item.getModelName()));
	            row.createCell(col++).setCellValue(nvl(item.getItemCondition()));
	            row.createCell(col++).setCellValue(nvl(item.getItemSource()));

	            row.createCell(col++).setCellValue(nvl(item.getRam()!="null"&&item.getRam()!=null?(item.getRam()+""+item.getRamUnit()):"-"));
	            row.createCell(col++).setCellValue(nvl(item.getStorage()!="null"&&item.getStorage()!=null?(item.getStorage()+""+item.getStorageUnit()):"-"));

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