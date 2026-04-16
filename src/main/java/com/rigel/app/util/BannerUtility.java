package com.rigel.app.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.imageio.ImageIO;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.io.font.constants.StandardFonts;

public class BannerUtility {

    public static Image bannerPrintPDF() {
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
            g.drawString("vivo", 80, 85);
            g.setFont(brandFontSmall);
            g.drawString("Camera & Music", 80, 107);

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

            // ---------------------------
            // 2. Create PDF
            // ---------------------------
//            PdfWriter writer = new PdfWriter(pdfFileName + ".pdf");
//            PdfDocument pdfDoc = new PdfDocument(writer);
//            Document document = new Document(pdfDoc);

         // Apple logo AWT Graphics2D me
            int logoX = 10;
            int logoY = 10;

            // Apple body
            Polygon apple = new Polygon();
            apple.addPoint(logoX + 10, logoY + 30);
            apple.addPoint(logoX + 20, logoY + 30);
            apple.addPoint(logoX + 25, logoY + 15);
            apple.addPoint(logoX + 10, logoY);
            apple.addPoint(logoX - 5, logoY + 15);
            apple.addPoint(logoX, logoY + 30);
            g.setColor(Color.BLACK);
            g.fillPolygon(apple);

            // Leaf
            g.fillOval(logoX + 5, logoY - 5, 10, 8);

            // Bite
            g.setColor(Color.WHITE);
            g.fillOval(logoX + 12, logoY + 10, 4, 4);

            // iPhone text
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("iPhone", logoX + 10, logoY + 10);
            
            // ---------------------------
            // 5. Add the AWT image below
            // ---------------------------
            
            awtImage.setFixedPosition(0, 0);
//            document.add(awtImage);

//            document.close();
            System.out.println("PDF Banner Created Successfully!");
            
            return awtImage;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//    public static void main(String[] args) {
//        bannerPrintPDF("brands_banner_pdf");
//    }
}