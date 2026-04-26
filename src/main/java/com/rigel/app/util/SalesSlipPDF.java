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
import com.rigel.app.model.dto.GstRate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.barcodes.Barcode128;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

//@Service
public class SalesSlipPDF {

    public static void createSlip(String fileName, User user, BuyerInfo buyer, List<SalesInfo> sales) {
        try {
        	boolean gstApplicable=user.getGstNumber()==null?false:true;
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
            
            Table gstTable = new Table(2).useAllAvailableWidth();

//            Table gstTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
           
            gstTable.addCell(new Cell().add(new Paragraph("GSTN Number: " + user.getGstNumber())
                    .setFont(bold).setFontSize(6))
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.RIGHT));
            document.add(gstTable);
            
            
            
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
            customerTable.addCell(new Cell().add(new Paragraph("Customer : " + (buyer.getBuyerName()!=null?buyer.getBuyerName():buyer.getCustumberId())).setFont(normal).setFontSize(6)).setBorder(Border.NO_BORDER));
            customerTable.addCell(new Cell().add(new Paragraph("Phone : " + (buyer.getMobileNumber()!=null?buyer.getMobileNumber():"N/A")).setFont(normal).setFontSize(6)).setBorder(Border.NO_BORDER));
            customerTable.addCell(new Cell().add(new Paragraph("Address : " + (buyer.getBuyerAddress()!=null?buyer.getBuyerAddress():"N/A")).setFont(normal).setFontSize(6)).setBorder(Border.NO_BORDER));
            customerTable.addCell(new Cell().add(new Paragraph("Email'Id : "+ (buyer.getEmailId()!=null?buyer.getEmailId():"N/A"))).setFont(normal).setFontSize(6).setBorder(Border.NO_BORDER));
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
            Table itemTable = null;
            		if(gstApplicable) {
            			itemTable=new Table(UnitValue.createPercentArray(new float[]{1f, 4f, 1f, 1f, 1f, 1f, 1f, 1f}))
                    .useAllAvailableWidth();
            		}else {
            			itemTable=new Table(UnitValue.createPercentArray(new float[]{1f, 5f, 1f, 2f, 2f}))
                                .useAllAvailableWidth();            			
            		}

            // Header Row with background color
            itemTable.addHeaderCell(new Cell().add(new Paragraph("SR").setFont(bold).setFontSize(6)).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            itemTable.addHeaderCell(new Cell().add(new Paragraph("ITEM").setFont(bold).setFontSize(6)).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            itemTable.addHeaderCell(new Cell().add(new Paragraph("QTY").setFont(bold).setFontSize(6)).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            itemTable.addHeaderCell(new Cell().add(new Paragraph("PRICE").setFont(bold).setFontSize(6)).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
           
            if(gstApplicable) {
         // Create header row
            itemTable.addHeaderCell(new Cell().add(new Paragraph("CGST")
                    .setFont(bold).setFontSize(6))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));

            itemTable.addHeaderCell(new Cell().add(new Paragraph("SGST")
                    .setFont(bold).setFontSize(6))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));

            itemTable.addHeaderCell(new Cell().add(new Paragraph("IGST")
                    .setFont(bold).setFontSize(6))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
            }
            
//            itemTable.addHeaderCell(new Cell().add(new Paragraph(buyer.getState()!=null?(buyer.getState().equalsIgnoreCase(user.getState())?"SGST+CGST":"IGST"):"SGST+CGST").setFont(bold).setFontSize(6)).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            itemTable.addHeaderCell(new Cell().add(new Paragraph("AMOUNT").setFont(bold).setFontSize(6)).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));

            BigDecimal total = BigDecimal.ZERO;
            ObjectMapper mapper = new ObjectMapper();
	          
            for (int i = 0; i < sales.size(); i++) {
                SalesInfo s = sales.get(i);

                
	            // Jackson library use karke
	            GstRate gstValue = mapper.readValue(s.getGstRate(), GstRate.class);
   
                BigDecimal soldPrice = BigDecimal.valueOf(s.getSoldPrice());
                BigDecimal gstRate = BigDecimal.valueOf(Integer.valueOf(gstValue.getIgst()));
             // 1 + GST/100
                BigDecimal divisor = BigDecimal.ONE.add(
                        gstRate.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                );

                // Base price
                BigDecimal basePrice = soldPrice.divide(divisor, 2, RoundingMode.HALF_UP);

                // GST amount
//                BigDecimal gstAmount = soldPrice.subtract(basePrice);
                
                
                BigDecimal qty = BigDecimal.valueOf(s.getQuantity());

                BigDecimal amt = gstApplicable?basePrice.multiply(qty):new BigDecimal(s.getSoldPrice()).multiply(qty);

                total = total.add(amt);
                StringBuilder itemDesc = new StringBuilder();

                if (s.getSerialNumber() != null) {
                    itemDesc.append("S/No-").append(s.getSerialNumber());
                }
                itemDesc.append(",").append(s.getBrand()).append("/").append(s.getModelName()).append(",");

                if (s.getRam() != null) {
                    itemDesc.append(s.getRam()).append("/");
                }
                if (s.getStorage() != null) {
                    itemDesc.append(s.getStorage()).append(s.getStorageUnit()).append(",");
                }
                if (s.getItemColor() != null) {
                    itemDesc.append(s.getItemColor()).append(",");
                }
                if (s.getItemGen() != null) {
                    itemDesc.append(s.getItemGen()).append(",");
                }
                if (s.getProcessor() != null) {
                    itemDesc.append(s.getProcessor()).append(",");
                }
                if (s.getScreenSize() != null) {
                    itemDesc.append(s.getScreenSize()).append(",");
                }
                if (s.getDescription() != null) {
                    itemDesc.append(s.getDescription());
                }

                String finalDesc = itemDesc.toString();

                                
                itemTable.addCell(new Cell().add(new Paragraph(String.valueOf(i + 1))).setFont(normal).setFontSize(6).setTextAlignment(TextAlignment.CENTER));
                itemTable.addCell(new Cell().add(new Paragraph(finalDesc)).setFont(normal).setFontSize(6));
                itemTable.addCell(new Cell().add(new Paragraph(String.valueOf(s.getQuantity()))).setFont(normal).setFontSize(6).setTextAlignment(TextAlignment.CENTER));
                itemTable.addCell(new Cell().add(new Paragraph(nf.format(gstApplicable?basePrice:new BigDecimal(s.getSoldPrice())).toString())).setFont(normal).setFontSize(6).setTextAlignment(TextAlignment.RIGHT));
                
                if(gstApplicable) {
	                BigDecimal cgstValue = basePrice
	            	        .multiply(new BigDecimal(gstValue.getCgst()))
	            	        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
	                BigDecimal sgstValue = basePrice
	            	        .multiply(new BigDecimal(gstValue.getSgst()))
	            	        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
	                BigDecimal igstValue = basePrice
	            	        .multiply(new BigDecimal(gstValue.getIgst()))
	            	        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
	                // Create value row based on condition
	                if (buyer.getState() == null) {
	                	 itemTable.addCell(new Cell().add(new Paragraph(cgstValue+"").setFontSize(6).setTextAlignment(TextAlignment.CENTER)));
	                     itemTable.addCell(new Cell().add(new Paragraph(sgstValue+"").setFontSize(6).setTextAlignment(TextAlignment.CENTER)));
	                     itemTable.addCell(new Cell().add(new Paragraph("-").setFontSize(6).setTextAlignment(TextAlignment.CENTER)));
	                } else if(buyer.getState().equalsIgnoreCase(user.getState())) {
	            	    itemTable.addCell(new Cell().add(new Paragraph(cgstValue+"").setFontSize(6).setTextAlignment(TextAlignment.CENTER)));
	                    itemTable.addCell(new Cell().add(new Paragraph(sgstValue+"").setFontSize(6).setTextAlignment(TextAlignment.CENTER)));
	                    itemTable.addCell(new Cell().add(new Paragraph("-").setFontSize(6).setTextAlignment(TextAlignment.CENTER)));
	                }else {
	                    // Different state → IGST apply
	                    itemTable.addCell(new Cell().add(new Paragraph("-").setFontSize(6).setTextAlignment(TextAlignment.CENTER)));
	                    itemTable.addCell(new Cell().add(new Paragraph("-").setFontSize(6).setTextAlignment(TextAlignment.CENTER)));
	                    itemTable.addCell(new Cell().add(new Paragraph(igstValue+"").setFontSize(6).setTextAlignment(TextAlignment.CENTER)));
	                }
	                
	             }
	             itemTable.addCell(new Cell().add(new Paragraph(nf.format(amt).toString())).setFont(normal).setFontSize(6).setTextAlignment(TextAlignment.RIGHT));
            }
            document.add(itemTable);
            
            addLine(document);
            
            /* ---------------- PRICE SUMMARY ---------------- */
             double gst = sales.stream()
                    .mapToDouble(s -> {
                    	GstRate gstValue2=null;
						try {
							gstValue2 = mapper.readValue(s.getGstRate(), GstRate.class);
						} catch (JsonMappingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JsonProcessingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                         
                        double rate = s.getSoldPrice() /(1 + Integer.valueOf(gstValue2.getIgst()) / 100.0);
                        double gstAmt=rate*(Integer.valueOf(gstValue2.getIgst()) / 100.0);
                        return gstAmt*s.getQuantity();
                    })
                    .sum();
            
          double subTotal = sales.stream()
                      .mapToDouble(s -> {
                    	  
                    	GstRate gstValue2=null;
	  						try {
	  							gstValue2 = mapper.readValue(s.getGstRate(), GstRate.class);
	  						} catch (JsonMappingException e) {
	  							// TODO Auto-generated catch block
	  							e.printStackTrace();
	  						} catch (JsonProcessingException e) {
	  							// TODO Auto-generated catch block
	  							e.printStackTrace();
	  						}
  						
                          double rate = gstApplicable?(s.getSoldPrice() /(1 + Integer.valueOf(gstValue2.getIgst()) / 100.0)):s.getSoldPrice();
                          return rate*s.getQuantity();
                      })
                      .sum();
            
            double discount = 0;
            double grandTotal = subTotal + (gstApplicable?gst:0.0) - discount;

            Table summary = new Table(2).useAllAvailableWidth();

            summary.addCell(cell("Subtotal", normal));
            summary.addCell(cell(nf.format(new BigDecimal(String.valueOf(subTotal))).toString(), normal));

            if(gstApplicable) {
	            summary.addCell(cell("GST", normal));
	            summary.addCell(cell(nf.format(new BigDecimal(String.valueOf(gst))).toString(), normal));
            }
            
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
           
            
           if(user.getShopType()!=null) {
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
          }else {
        	  try {
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

        		    // Top & Bottom Border
        		    g.setColor(Color.GRAY);
        		    g.setStroke(new BasicStroke(2));
        		    g.drawLine(0, 5, width, 5);
        		    g.drawLine(0, height - 5, width, height - 5);

        		    int cols = 8;
        		    int cellW = width / cols;

        		    for (int i = 0; i < cols; i++) {

        		        int x = i * cellW;

        		        // Divider
        		        g.setColor(Color.LIGHT_GRAY);
        		        g.drawLine(x, 10, x, height - 10);

        		        switch (i) {

        		            // APPLE
        		            case 0:
        		                g.setFont(new Font("SansSerif", Font.BOLD, 14));
        		                g.setColor(Color.BLACK);
        		                drawCenter(g, "Apple", x, 0, cellW, height);
        		                break;

        		            // DELL
        		            case 1:
        		                g.setFont(new Font("Arial", Font.BOLD, 14));
        		                g.setColor(new Color(0, 102, 204));
        		                drawCenter(g, "DELL", x, 0, cellW, height);
        		                break;

        		            // HP (circle)
        		            case 2:
        		                g.setColor(new Color(0, 102, 204));
        		                g.drawOval(x + 5, 30, cellW - 10, 50);
        		                g.setFont(new Font("Arial", Font.BOLD, 13));
        		                drawCenter(g, "hp", x, 0, cellW, height);
        		                break;

        		            // LENOVO (red bar)
        		            case 3:
        		                g.setColor(Color.RED);
        		                g.fillRect(x + 2, 30, cellW - 4, 50);
        		                g.setColor(Color.WHITE);
        		                g.setFont(new Font("Arial", Font.BOLD, 12));
        		                drawCenter(g, "Lenovo", x, 0, cellW, height);
        		                break;

        		            // ASUS
        		            case 4:
        		                g.setColor(Color.BLUE);
        		                g.setFont(new Font("Arial", Font.BOLD, 14));
        		                drawCenter(g, "ASUS", x, 0, cellW, height);
        		                break;

        		            // ACER
        		            case 5:
        		                g.setColor(new Color(0, 153, 0));
        		                g.setFont(new Font("Arial", Font.BOLD, 14));
        		                drawCenter(g, "acer", x, 0, cellW, height);
        		                break;

        		            // MSI
        		            case 6:
        		                g.setColor(Color.RED);
        		                g.setFont(new Font("Arial", Font.BOLD, 14));
        		                drawCenter(g, "MSI", x, 0, cellW, height);
        		                break;

        		            // SAMSUNG
        		            case 7:
        		                g.setColor(new Color(0, 51, 153));
        		                g.setFont(new Font("Arial", Font.BOLD, 12));
        		                drawCenter(g, "SAMSUNG", x, 0, cellW, height);
        		                break;
        		        }
        		    }

        		    g.dispose();

        		    // Convert to iText Image
        		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
        		    ImageIO.write(img, "png", baos);
        		    Image awtImage = new Image(ImageDataFactory.create(baos.toByteArray()));
        		    awtImage.setAutoScale(true);

        		    awtImage.setFixedPosition(0, 0);
        		    document.add(awtImage);

        		} catch (Exception e) {
        		    e.printStackTrace();
        		}
          }
           
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
      
    }

    public static void drawCenter(Graphics2D g, String text, int x, int y, int w, int h) {
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        int tx = x + (w - textWidth) / 2;
        int ty = y + (h + textHeight) / 2;

        g.drawString(text, tx, ty);
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
