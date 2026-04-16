package com.rigel.app.util;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.rigel.app.model.BuyerInfo;
import com.rigel.app.model.Items;
import com.rigel.app.model.SalesInfo;

import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class ExcelDirectSave {

    public static void saveItemsToDownloads(List<Items> items) {

        try {
            // 🔹 User Downloads path
            String userHome = System.getProperty("user.home");
            Path downloadDir = Paths.get(userHome, "Downloads", Constaints.DOWNLOAD_FOLDER_NAME);

            // 🔥 Folder check + create (if not exists)
            if (!Files.exists(downloadDir)) {
                Files.createDirectories(downloadDir);
                System.out.println("Folder created: " + downloadDir);
            }

            // 🔹 File name (unique bana diya to avoid overwrite)
            String fileName = "entryItems_" + System.currentTimeMillis() + ".xlsx";
            Path filePath = downloadDir.resolve(fileName);

            // 🔥 Streaming workbook (memory safe)
            try (SXSSFWorkbook workbook = new SXSSFWorkbook(100);
                 FileOutputStream fos = new FileOutputStream(filePath.toFile())) {

                Sheet sheet = workbook.createSheet("Items");

                // Header
                Row header = sheet.createRow(0);
                String[] cols = {
                        "ItemCode", "Category", "Brand",
                        "Model", "Quantity", "Selling Price"
                };

                for (int i = 0; i < cols.length; i++) {
                    header.createCell(i).setCellValue(cols[i]);
                }

                // Data
                int rowNum = 1;
                for (Items item : items) {
                    Row row = sheet.createRow(rowNum++);

                    row.createCell(0).setCellValue(item.getItemCode());
                    row.createCell(1).setCellValue(item.getCategory());
                    row.createCell(2).setCellValue(item.getBrand());
                    row.createCell(3).setCellValue(item.getModelName());
                    row.createCell(4).setCellValue(
                            item.getQuantity() != null ? item.getQuantity() : 0
                    );
                    row.createCell(5).setCellValue(
                            item.getSellingPrice() != null ? item.getSellingPrice() : 0
                    );
                }

                workbook.write(fos);

                // 🔥 VERY IMPORTANT (temp cleanup)
                workbook.dispose();

                System.out.println("File saved at: " + filePath);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String exportSalesToExcel(List<SalesInfo> list) {

        String fileName = "sales_" + System.currentTimeMillis() + ".xlsx";

        SXSSFWorkbook workbook = new SXSSFWorkbook(100);

        try {
            Sheet sheet = workbook.createSheet("Sales Report");

            // ✅ Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            int rowNum = 0;

            String[] columns = {
                    "Invoice No", "Item Code", "Brand", "Model",
                    "Quantity", "Purchase Price", "Sold Price",
                    "Buyer Name", "Mobile", "Date"
            };

            // ✅ Header
            Row header = sheet.createRow(rowNum++);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // ✅ Data
            for (SalesInfo s : list) {

                Row row = sheet.createRow(rowNum++);
                BuyerInfo b = s.getBuyerInfo();

                row.createCell(0).setCellValue(nvl(b != null ? b.getInvoiceNumber() : null));
                row.createCell(1).setCellValue(nvl(s.getItemCode()));
                row.createCell(2).setCellValue(nvl(s.getBrand()));
                row.createCell(3).setCellValue(nvl(s.getModelName()));

                row.createCell(4).setCellValue(s.getQuantity() != null ? s.getQuantity() : 0);
                row.createCell(5).setCellValue(s.getInitialPrice() != null ? s.getInitialPrice() : 0);
                row.createCell(6).setCellValue(s.getSoldPrice() != null ? s.getSoldPrice() : 0);

                row.createCell(7).setCellValue(nvl(b != null ? b.getBuyerName() : null));
                row.createCell(8).setCellValue(nvl(b != null ? b.getMobileNumber() : null));

                row.createCell(9).setCellValue(
                        s.getCreatedAt() != null ? s.getCreatedAt().toString() : "-"
                );
            }

            // ✅ Auto size
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // ✅ Folder Path (Downloads/RigelEMISM)
            String userHome = System.getProperty("user.home");
            Path folder = Paths.get(userHome, "Downloads", "RigelEMISM");

            // ✅ Create if not exists
            if (!Files.exists(folder)) {
                Files.createDirectories(folder);
            }

            Path filePath = folder.resolve(fileName);

            // ❌ Replace नहीं करना (unique name already)
            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                workbook.write(fos);
            }

            System.out.println("File saved at: " + filePath);

            return filePath.toString(); // return path if needed

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        } finally {
            // ✅ VERY IMPORTANT (memory cleanup)
            workbook.dispose();
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static String nvl(String val) {
        return (val != null && !val.trim().isEmpty()) ? val : "-";
    }
}