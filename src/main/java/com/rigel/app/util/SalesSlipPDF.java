package com.rigel.app.util;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.rigel.app.model.*;
import com.itextpdf.barcodes.Barcode128;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import javax.imageio.ImageIO;

public class SalesSlipPDF {

    public static void createSlip(String fileName, User user, BuyerInfo buyer, List<SalesInfo> sales) {
        try {
//        	NumberFormat nf = NumberFormat.getNumberInstance(new Locale("en", "IN"));
//        	 nf.setMinimumFractionDigits(2);
//             nf.setMaximumFractionDigits(2);

        	IndianNumberFormat nf=new IndianNumberFormat();
        	
        	String folderPath = System.getProperty("user.home")
        	        + "/Downloads/"+Constaints.DOWNLOAD_FOLDER_NAME+"/Bill/";

        	Path path = Paths.get(folderPath);

        	// folder create if not exists
        	if (!Files.exists(path)) {
        	    Files.createDirectories(path);
        	}

        	String fullFilePath = folderPath + fileName + ".pdf";

        	PdfWriter writer = new PdfWriter(new FileOutputStream(fullFilePath));
        	
//            PdfWriter writer = new PdfWriter(new FileOutputStream(fileName + ".pdf"));
            PdfDocument pdf = new PdfDocument(writer);

            // Page size, A4 width but narrow for receipt (288 points ~ 4 inch)
            PageSize pageSize = new PageSize(288,576);// PageSize.A4.getHeight());
            Document document = new Document(pdf, pageSize);
            document.setMargins(5, 5, 5, 5);

            // Fonts
            PdfFont bold = PdfFontFactory.createFont("Helvetica-Bold");
            PdfFont normal = PdfFontFactory.createFont("Helvetica");

            /* ----------- HEADER ----------- */
            Table header = new Table(UnitValue.createPercentArray(new float[]{1.5f, 4f, 2f}))
                    .useAllAvailableWidth();

            // Logo cell (try catch to avoid failure)
            try {
                Image logo = new Image(com.itextpdf.io.image.ImageDataFactory.create("C:\\Users\\Ramesh\\Downloads\\logo.jpg"));
                logo.scaleToFit(40, 40);
                Cell logoCell = new Cell().add(logo).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
                header.addCell(logoCell);
            } catch (Exception e) {
                header.addCell(new Cell().setBorder(Border.NO_BORDER));
            }

            // Company info cell
            Paragraph cname = new Paragraph(user.getCompanyName() == null ? "RAHUL MOBILE GALLERY" : user.getCompanyName())
                    .setFont(bold)
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.CENTER);

            String addressStr = (user.getAddressLine1() == null ? "1743, Dhuri Line Rd, Abdullapur Basti, Azad Nagar," : user.getAddressLine1())
                    + "\n" + (user.getCity() == null ? "Mohali" : user.getCity()) + ", " + (user.getState() == null ? "Punjab" : user.getState())
                    + " - " + (user.getPincode() == null ? "160055" : user.getPincode());

            Paragraph addr = new Paragraph(addressStr)
                    .setFont(normal)
                    .setFontSize(6)
                    .setTextAlignment(TextAlignment.CENTER);

            Paragraph phone = new Paragraph("Mob. : " + (user.getMobile_no() == null ? "9876543210" : user.getMobile_no()))
                    .setFont(normal)
                    .setFontSize(6)
                    .setTextAlignment(TextAlignment.CENTER);

            Cell companyCell = new Cell().setBorder(Border.NO_BORDER)
                    .add(cname).add(addr).add(phone);

            header.addCell(companyCell);

            // Slip + Barcode cell
            Paragraph slipTitle = new Paragraph("E BILL")
                    .setFont(bold)
                    .setFontSize(7)
                    .setTextAlignment(TextAlignment.CENTER);

            Barcode128 barcode = new Barcode128(pdf);
            barcode.setCode(buyer.getInvoiceNumber());

            Image barcodeImage = new Image(barcode.createFormXObject(null, null, pdf))
                    .setAutoScale(true)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER);

            Cell slipCell = new Cell().setBorder(Border.NO_BORDER)
                    .add(slipTitle)
                    .add(barcodeImage);

            header.addCell(slipCell);

            document.add(header);

            addLine(document);

            /* ----------- INVOICE INFO ----------- */
            Table invoiceTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
            invoiceTable.addCell(new Cell().add(new Paragraph("Invoice Number: " + buyer.getInvoiceNumber()).setFont(bold).setFontSize(7)).setBorder(Border.NO_BORDER));
            invoiceTable.addCell(new Cell().add(new Paragraph("Invoice Date : " + LocalDate.now()).setFont(bold).setFontSize(7)).setBorder(Border.NO_BORDER));
            document.add(invoiceTable);

            addLine(document);

            /* ----------- CUSTOMER DETAILS ----------- */
            if(buyer.getGstNumber()==null) {
                
            Table customerTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
            customerTable.addCell(new Cell().add(new Paragraph("Customer : " + buyer.getBuyerName()).setFont(normal).setFontSize(6)).setBorder(Border.NO_BORDER));
            customerTable.addCell(new Cell().add(new Paragraph("Phone : " + buyer.getMobileNumber()).setFont(normal).setFontSize(6)).setBorder(Border.NO_BORDER));
            customerTable.addCell(new Cell().add(new Paragraph("Address : " + buyer.getBuyerAddress()).setFont(normal).setFontSize(6)).setBorder(Border.NO_BORDER));
            customerTable.addCell(new Cell().add(new Paragraph("Email'Id : "+ buyer.getEmailId())).setFont(normal).setFontSize(6).setBorder(Border.NO_BORDER));
            document.add(customerTable);
            addLine(document);
            
            }
            /* ----------- BILL TO DETAILS ----------- */
            String shipName = (buyer.getCompanyName() == null || buyer.getCompanyName().trim().isEmpty())
                    ? "RS Communication Pvt Ltd" : buyer.getCompanyName();
            String shipAddress = (buyer.getCompanyAddress() == null || buyer.getCompanyAddress().trim().isEmpty())
                    ? "Mohali Punjab" : buyer.getCompanyAddress();
            String shipState = (buyer.getState() == null || buyer.getState().trim().isEmpty())
                    ? "Punjab" : buyer.getState();
            String shipDist = (buyer.getDistric() == null || buyer.getDistric().trim().isEmpty())
                    ? "Mohali" : buyer.getDistric();
          
            String shipPincode = (buyer.getPinCode() == null || buyer.getPinCode().trim().isEmpty())
                    ? "160055" : buyer.getPinCode();

            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .useAllAvailableWidth();

            // BILL TO Column
            Cell billCell = new Cell()
                    .add(new Paragraph("BILL TO").setFont(bold).setFontSize(7).setMarginBottom(4))
                    .add(new Paragraph()
                            .setFont(normal)
                            .setFontSize(6)
                            .add("Name : " + shipName + "\n")
                            .add("State : " + shipState + "\n")
                            .add("Distic : " + shipDist + "\n")
                            .add("Address : " + shipAddress + "\n")
                            .add("Pin Code : " + shipPincode))
                    .setBorder(null); // No border, optional

            // SHIP TO Column
            Cell shipCell = new Cell()
                    .add(new Paragraph("SHIP TO").setFont(bold).setFontSize(7).setMarginBottom(4))
                    .add(new Paragraph()
                            .setFont(normal)
                            .setFontSize(6)
                            .add("Name : " + shipName + "\n")
                            .add("State : " + shipState + "\n")
                            .add("Distic : " + shipDist + "\n")
                            .add("Address : " + shipAddress + "\n")
                            .add("Pin Code : " + shipPincode))
                    .setBorder(null);

            // Add cells to table
            table.addCell(billCell);
            table.addCell(shipCell);

            // Add table to document
            if(buyer.getGstNumber()!=null) {
               document.add(table);
               addLine(document);
            }

           
            /* ----------- ITEMS TABLE ----------- */
            Table itemTable = new Table(UnitValue.createPercentArray(new float[]{1f, 4f, 1f, 2f, 1f, 2f}))
                    .useAllAvailableWidth();

            // Header Row with background color
            itemTable.addHeaderCell(new Cell().add(new Paragraph("SR").setFont(bold).setFontSize(6)).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            itemTable.addHeaderCell(new Cell().add(new Paragraph("ITEM").setFont(bold).setFontSize(6)).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            itemTable.addHeaderCell(new Cell().add(new Paragraph("QTY").setFont(bold).setFontSize(6)).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            itemTable.addHeaderCell(new Cell().add(new Paragraph("PRICE").setFont(bold).setFontSize(6)).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            itemTable.addHeaderCell(new Cell().add(new Paragraph("GST").setFont(bold).setFontSize(6)).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            itemTable.addHeaderCell(new Cell().add(new Paragraph("AMOUNT").setFont(bold).setFontSize(6)).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));

            BigDecimal total = BigDecimal.ZERO;
            
            for (int i = 0; i < sales.size(); i++) {
                SalesInfo s = sales.get(i);
                BigDecimal sellingPrice = BigDecimal.valueOf(s.getSellingPrice());
                BigDecimal gstRate = BigDecimal.valueOf(Integer.valueOf(s.getGstRate()));
             // 1 + GST/100
                BigDecimal divisor = BigDecimal.ONE.add(
                        gstRate.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                );

                // Base price
                BigDecimal basePrice = sellingPrice.divide(divisor, 2, RoundingMode.HALF_UP);

                // GST amount
                BigDecimal gstAmount = sellingPrice.subtract(basePrice);
                
                
                
                System.out.println(s.getSellingPrice()+""+basePrice+"----"+gstAmount);
                
                BigDecimal qty = BigDecimal.valueOf(s.getQuantity());

                BigDecimal amt = basePrice.multiply(qty);

                total = total.add(amt);
                                
                itemTable.addCell(new Cell().add(new Paragraph(String.valueOf(i + 1))).setFont(normal).setFontSize(6).setTextAlignment(TextAlignment.CENTER));
                itemTable.addCell(new Cell().add(new Paragraph(s.getDescription())).setFont(normal).setFontSize(6));
                itemTable.addCell(new Cell().add(new Paragraph(String.valueOf(s.getQuantity()))).setFont(normal).setFontSize(6).setTextAlignment(TextAlignment.CENTER));
                itemTable.addCell(new Cell().add(new Paragraph(nf.format(basePrice).toString())).setFont(normal).setFontSize(6).setTextAlignment(TextAlignment.RIGHT));
                itemTable.addCell(new Cell().add(new Paragraph("18%")).setFont(normal).setFontSize(6).setTextAlignment(TextAlignment.CENTER));
                itemTable.addCell(new Cell().add(new Paragraph(nf.format(amt).toString())).setFont(normal).setFontSize(6).setTextAlignment(TextAlignment.RIGHT));
            }
            document.add(itemTable);
            
            addLine(document);

            /* ---------------- PRICE SUMMARY ---------------- */
             double gst = sales.stream()
                    .mapToDouble(s -> {
                        double rate = s.getSellingPrice() /(1 + Integer.valueOf(s.getGstRate()) / 100.0);
                        double gstAmt=rate*(Integer.valueOf(s.getGstRate()) / 100.0);
//                        System.out.println(s.getSellingPrice()+" rate--"+rate+",gst--"+gstAmt);
                        return gstAmt*s.getQuantity();
                    })
                    .sum();
            
          double subTotal = sales.stream()
                      .mapToDouble(s -> {
                          double rate = s.getSellingPrice() /(1 + Integer.valueOf(s.getGstRate()) / 100.0);
                          double gstAmt=rate*(Integer.valueOf(s.getGstRate()) / 100.0);
                          System.out.println(" rate--"+rate);
                          return rate*s.getQuantity();
                      })
                      .sum();
            
            double discount = 0;
            double grandTotal = subTotal + gst - discount;

            Table summary = new Table(2).useAllAvailableWidth();

            summary.addCell(cell("Subtotal", normal));
            summary.addCell(cell(nf.format(new BigDecimal(String.valueOf(subTotal))).toString(), normal));

            summary.addCell(cell("GST 18%", normal));
            summary.addCell(cell(nf.format(new BigDecimal(String.valueOf(gst))).toString(), normal));

            summary.addCell(cell("Discount", normal));
            summary.addCell(cell(nf.format(new BigDecimal(String.valueOf(discount))).toString(), normal));

            summary.addCell(cell("Grand Total", bold));
            summary.addCell(cell(nf.format(new BigDecimal(String.valueOf(grandTotal))).toString(), bold));

            document.add(summary);

            addLine(document);

            /* ---------------- PAYMENT ---------------- */

            Table payment = new Table(2).useAllAvailableWidth();

            payment.addCell(cell("Payment Mode", normal));
            payment.addCell(cell(buyer.getPaymentModes()==null?"UPI":buyer.getPaymentModes(), normal));

            payment.addCell(cell("Paid Amount", normal));
            payment.addCell(cell(String.valueOf("Rs "+nf.format(new BigDecimal(String.valueOf(grandTotal))).toString()), normal));

            payment.addCell(cell("Balance", normal));
            payment.addCell(cell(String.valueOf("0.0"), normal));

            document.add(payment);

            addLine(document);

           

            /* ---------------- NOTES ---------------- */

            Paragraph notes = new Paragraph()
                    .add(new Text("Notes : ").setFont(bold).setFontSize(7))
                    .add(new Text(buyer.getNoteComment() == null||buyer.getNoteComment().isEmpty()? "" :
                            buyer.getNoteComment()).setFontSize(6));

            document.add(notes);

            addLine(document);
            
            
            /* ---------------- WARRANTY ---------------- */

            Paragraph warranty = new Paragraph()
                    .setFontSize(6)
                    .add(new Text("Warranty & Return Policy\n").setFont(bold))
                    .add("• Warranty as per company policy\n")
                    .add("• No return after sale\n")
                    .add("• Keep invoice safe\n");

            document.add(warranty);

            addLine(document);

//            addLine(document);

//            /* ----------- TOTAL ----------- */
//            Table totalTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
//            totalTable.addCell(new Cell().add(new Paragraph("TOTAL").setFont(bold).setFontSize(7)).setBorder(Border.NO_BORDER));
//            totalTable.addCell(new Cell().add(new Paragraph(String.format("%.2f", total)).setFont(bold).setFontSize(7)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
//            document.add(totalTable);
//
//            addLine(document);
//
//            /* ----------- NOTES ----------- */
//            Paragraph notes = new Paragraph()
//                    .add(new Text("Notes : ").setFont(bold).setFontSize(7))
//                    .add(new Text(buyer.getNoteComment() == null ? "" : buyer.getNoteComment()).setFont(normal).setFontSize(6));
//            document.add(notes);
//
//            addLine(document);

            /* ----------- THANK YOU ----------- */
            Paragraph thanks = new Paragraph("***** THANK YOU VISIT AGAIN *****")
                    .setFont(bold)
                    .setFontSize(7)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(thanks);
           
            try {
                // ---------------------------
                // 1. Create AWT Banner (all brands except iPhone)
                // ---------------------------
                int width = 700;
                int height = 120;
                BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = img.createGraphics();

                // Anti-aliasing
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // Background
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, width, height);

                // Borders
                g.setColor(Color.GRAY);
                g.setStroke(new BasicStroke(2));
                g.drawLine(0, 5, width, 5);
                g.drawLine(0, height - 5, width, height - 5);

                // Fonts
                Font brandFontLarge = new Font("Arial", Font.BOLD, 28);
                Font brandFontMedium = new Font("Arial", Font.BOLD, 22);
                Font brandFontSmall = new Font("Arial", Font.PLAIN, 12);
                g.setColor(Color.BLACK);

                // Other brands
                g.setFont(brandFontLarge);
                g.drawString("OPPO", 160, 40);
                g.setFont(brandFontSmall);
                g.drawString("SMARTPHONE", 160, 57);

                g.setFont(brandFontMedium);
                g.drawOval(300, 13, 140, 40);
                g.drawString("SAMSUNG", 315, 40);
                
                g.setFont(brandFontLarge);
                g.drawString("Xiaomi", 480, 40);
                g.setFont(brandFontSmall);
                g.drawString("Innovation for Everyone", 480, 57);
                
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 24));
                g.drawString("iPhone", 60, 85);

                g.setFont(brandFontLarge);
                g.drawString("vivo", 170, 85);
                g.setFont(brandFontSmall);
                g.drawString("Camera & Music", 170, 107);

                g.setFont(brandFontLarge);
                g.drawString("REALME", 260, 85);

                g.drawString("POCO", 400, 85);
                g.setFont(brandFontSmall);
                g.drawString("Power & Performance", 400, 107);

                g.setFont(brandFontLarge);
                g.drawString("OnePlus", 520, 85);

                g.dispose();

                // Convert AWT image to iText Image
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(img, "png", baos);
                Image awtImage = new Image(ImageDataFactory.create(baos.toByteArray()));
                awtImage.setAutoScale(true);

                
                awtImage.setFixedPosition(0, 0);
                document.add(awtImage);

            } catch (Exception e) {
                e.printStackTrace();
            }
           
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addLine(Document document) {
        LineSeparator ls = new LineSeparator(new SolidLine());
        ls.setMarginTop(4);
        ls.setMarginBottom(4);
        document.add(ls);
    }
    
    private static Cell cell(String text, PdfFont font) {

        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(6))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);
    }
}
//package com.rigel.app.util;
//
//import com.itextpdf.kernel.colors.ColorConstants;
//import com.itextpdf.kernel.font.PdfFont;
//import com.itextpdf.kernel.font.PdfFontFactory;
//import com.itextpdf.kernel.geom.PageSize;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.layout.Document;
//import com.itextpdf.layout.borders.Border;
//import com.itextpdf.layout.element.*;
//import com.itextpdf.layout.properties.*;
//import com.itextpdf.barcodes.Barcode128;
//import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
//
//import com.rigel.app.model.*;
//
//import java.io.FileOutputStream;
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.time.LocalDate;
//import java.util.List;
//
//public class SalesSlipPDF {
//
//    // ✅ Cached fonts (performance optimized)
//    private static PdfFont BOLD;
//    private static PdfFont NORMAL;
//
//    static {
//        try {
//            BOLD = PdfFontFactory.createFont("Helvetica-Bold");
//            NORMAL = PdfFontFactory.createFont("Helvetica");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void createSlip(String fileName, User user, BuyerInfo buyer, List<SalesInfo> sales) {
//
//        // ✅ Prevent null crash
//        if (buyer == null || sales == null) {
//            throw new RuntimeException("Invalid data for PDF");
//        }
//
//        // ✅ try-with-resources (NO MEMORY / FILE LEAK)
//        try (FileOutputStream fos = new FileOutputStream(fileName + ".pdf")) {
//
//            PdfWriter writer = new PdfWriter(fos);
//            PdfDocument pdf = new PdfDocument(writer);
//            Document document = new Document(pdf, new PageSize(288, 576));
//            document.setMargins(5, 5, 5, 5);
//
//            /* ================= HEADER ================= */
//            Table header = new Table(UnitValue.createPercentArray(new float[]{2, 5}))
//                    .useAllAvailableWidth();
//
//            String companyName = user != null && user.getCompanyName() != null
//                    ? user.getCompanyName()
//                    : "RAHUL MOBILE GALLERY";
//
//            header.addCell(new Cell()
//                    .add(new Paragraph(companyName).setFont(BOLD).setFontSize(9))
//                    .setTextAlignment(TextAlignment.CENTER)
//                    .setBorder(Border.NO_BORDER));
//
//            Barcode128 barcode = new Barcode128(pdf);
//            barcode.setCode(buyer.getInvoiceNumber() == null ? "NA" : buyer.getInvoiceNumber());
//
//            Image barcodeImage = new Image(barcode.createFormXObject(null, null, pdf))
//                    .setAutoScale(true);
//
//            header.addCell(new Cell()
//                    .add(new Paragraph("E BILL").setFont(BOLD).setFontSize(7))
//                    .add(barcodeImage)
//                    .setBorder(Border.NO_BORDER)
//                    .setTextAlignment(TextAlignment.CENTER));
//
//            document.add(header);
//            addLine(document);
//
//            /* ================= INVOICE ================= */
//            document.add(new Paragraph("Invoice No: " + safe(buyer.getInvoiceNumber()))
//                    .setFont(BOLD).setFontSize(7));
//
//            document.add(new Paragraph("Date: " + LocalDate.now())
//                    .setFont(NORMAL).setFontSize(6));
//
//            addLine(document);
//
//            /* ================= CUSTOMER ================= */
//            document.add(new Paragraph("Customer: " + safe(buyer.getBuyerName()))
//                    .setFont(NORMAL).setFontSize(6));
//
//            document.add(new Paragraph("Phone: " + safe(buyer.getMobileNumber()))
//                    .setFont(NORMAL).setFontSize(6));
//
//            addLine(document);
//
//            /* ================= ITEMS ================= */
//            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 4, 1, 2}))
//                    .useAllAvailableWidth();
//
//            table.addHeaderCell(headerCell("SR"));
//            table.addHeaderCell(headerCell("ITEM"));
//            table.addHeaderCell(headerCell("QTY"));
//            table.addHeaderCell(headerCell("AMOUNT"));
//
//            BigDecimal total = BigDecimal.ZERO;
//
//            for (int i = 0; i < sales.size(); i++) {
//                SalesInfo s = sales.get(i);
//
//                BigDecimal price = BigDecimal.valueOf(
//                        s.getSellingPrice() == null ? 0 : s.getSellingPrice()
//                );
//
//                BigDecimal qty = BigDecimal.valueOf(
//                        s.getQuantity() == null ? 0 : s.getQuantity()
//                );
//
//                BigDecimal amt = price.multiply(qty);
//                total = total.add(amt);
//
//                table.addCell(cell(String.valueOf(i + 1)));
//                table.addCell(cell(safe(s.getDescription())));
//                table.addCell(cell(String.valueOf(qty)));
//                table.addCell(cell(amt.toString()));
//            }
//
//            document.add(table);
//            addLine(document);
//
//            /* ================= TOTAL ================= */
//            document.add(new Paragraph("Total: " + total)
//                    .setFont(BOLD).setFontSize(8)
//                    .setTextAlignment(TextAlignment.RIGHT));
//
//            addLine(document);
//
//            /* ================= FOOTER ================= */
//            document.add(new Paragraph("***** THANK YOU VISIT AGAIN *****")
//                    .setFont(BOLD)
//                    .setFontSize(7)
//                    .setTextAlignment(TextAlignment.CENTER));
//
//            document.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("PDF generation failed");
//        }
//    }
//
//    /* ================= HELPERS ================= */
//
//    private static void addLine(Document doc) {
//        doc.add(new LineSeparator(new SolidLine()));
//    }
//
//    private static Cell cell(String text) {
//        return new Cell()
//                .add(new Paragraph(text).setFont(NORMAL).setFontSize(6))
//                .setBorder(Border.NO_BORDER);
//    }
//
//    private static Cell headerCell(String text) {
//        return new Cell()
//                .add(new Paragraph(text).setFont(BOLD).setFontSize(6))
//                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
//                .setTextAlignment(TextAlignment.CENTER);
//    }
//
//    private static String safe(String val) {
//        return val == null ? "" : val;
//    }
//}
