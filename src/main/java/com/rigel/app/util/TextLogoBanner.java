package com.rigel.app.util;
import java.awt.*;
import java.awt.event.*;

public class TextLogoBanner extends Frame {

    public TextLogoBanner() {
        setTitle("Styled Laptop Logo Banner");
        setSize(1200, 600); // 4x2 inch @300 DPI feel

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose();
            }
        });

        setVisible(true);
    }

    public void paint(Graphics g) {

        int w = getWidth();
        int h = getHeight();

        int cols = 4;
        int rows = 2;

        int cellW = w / cols;
        int cellH = h / rows;

        int index = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                int x = c * cellW;
                int y = r * cellH;

                g.drawRect(x, y, cellW, cellH);

                switch (index) {

                    // 🍏 APPLE (simple clean text)
                    case 0:
                        g.setFont(new Font("SansSerif", Font.BOLD, 40));
                        g.setColor(Color.BLACK);
                        drawCenter(g, "Apple", x, y, cellW, cellH);
                        break;

                    // 🔵 DELL (blue text)
                    case 1:
                        g.setFont(new Font("Arial", Font.BOLD, 38));
                        g.setColor(new Color(0, 102, 204));
                        drawCenter(g, "DELL", x, y, cellW, cellH);
                        break;

                    // 🔵 HP (circle style)
                    case 2:
                        g.setColor(new Color(0, 102, 204));
                        g.drawOval(x + 50, y + 20, cellW - 100, cellH - 40);
                        g.setFont(new Font("Arial", Font.BOLD, 36));
                        drawCenter(g, "hp", x, y, cellW, cellH);
                        break;

                    // 🔴 LENOVO (red bar style)
                    case 3:
                        g.setColor(Color.RED);
                        g.fillRect(x + 10, y + 20, cellW - 20, cellH - 40);
                        g.setColor(Color.WHITE);
                        g.setFont(new Font("Arial", Font.BOLD, 34));
                        drawCenter(g, "Lenovo", x, y, cellW, cellH);
                        break;

                    // 🔵 ASUS
                    case 4:
                        g.setColor(Color.BLUE);
                        g.setFont(new Font("Arial", Font.BOLD, 36));
                        drawCenter(g, "ASUS", x, y, cellW, cellH);
                        break;

                    // 🟢 ACER
                    case 5:
                        g.setColor(new Color(0, 153, 0));
                        g.setFont(new Font("Arial", Font.BOLD, 36));
                        drawCenter(g, "acer", x, y, cellW, cellH);
                        break;

                    // 🔴 MSI
                    case 6:
                        g.setColor(Color.RED);
                        g.setFont(new Font("Arial", Font.BOLD, 36));
                        drawCenter(g, "MSI", x, y, cellW, cellH);
                        break;

                    // 🔵 SAMSUNG
                    case 7:
                        g.setColor(new Color(0, 51, 153));
                        g.setFont(new Font("Arial", Font.BOLD, 34));
                        drawCenter(g, "SAMSUNG", x, y, cellW, cellH);
                        break;
                }

                index++;
            }
        }
    }

    // Center text helper
    public void drawCenter(Graphics g, String text, int x, int y, int w, int h) {
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        int tx = x + (w - textWidth) / 2;
        int ty = y + (h + textHeight) / 2;

        g.drawString(text, tx, ty);
    }

    public static void main(String[] args) {
        new TextLogoBanner();
    }
}