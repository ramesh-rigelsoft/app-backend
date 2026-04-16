package com.rigel.app;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;
import javax.swing.border.LineBorder;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.rigel.app.util.Constaints;

public class ModernFrame {

    public static JFrame createModernFrame(ConfigurableApplicationContext context,String[] args) {
    	
    	JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        Image icon = null;
        try {
            String path = Constaints.APP_LOGO;// + Constaints.LOGO_PATH + "logo.png";
            File file = new File(path);
            if (!file.exists()) {
                System.out.println("File NOT found: " + file.getAbsolutePath());
            } else {
                icon = new ImageIcon(file.getAbsolutePath()).getImage();
                frame.setIconImage(icon);
                System.out.println("Loaded from: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
      
        // Border on all sides
        frame.getRootPane().setBorder(
            BorderFactory.createCompoundBorder(
                new LineBorder(new Color(70, 70, 70), 2, true),
                BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(50, 50, 50))
            )
        );

        frame.getContentPane().setBackground(new Color(32, 32, 32)); // dark background


        //-----------------------------------------
        // TOP BAR
        //-----------------------------------------
        JPanel topBar = new JPanel();
        topBar.setBackground(new Color(45, 45, 45));
        topBar.setPreferredSize(new Dimension(frame.getWidth(), 45));
        topBar.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 8));

        JButton minBtn = createIconButton("–", new Color(255, 215, 0));     // Minimize
        JButton closeBtn = createIconButton("✕", new Color(255, 50, 50));   // Close

        // Minimize action
        minBtn.addActionListener(e -> frame.setState(JFrame.ICONIFIED));

		// Close action
		try {

			closeBtn.addActionListener(e -> {
				deleteLogFile();
				context.close(); // Spring Boot band
				System.exit(0); // JVM band
			});

		} catch (Exception e) {
			// TODO: handle exception
		}

        buttonPanel.add(minBtn);
        buttonPanel.add(closeBtn);  // Only minimize + close


        //-----------------------------------------
        // DRAG WINDOW
        //-----------------------------------------
        final int[] mouseX = new int[1];
        final int[] mouseY = new int[1];

        topBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseX[0] = e.getX();
                mouseY[0] = e.getY();
            }
        });

        topBar.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                frame.setLocation(
                    e.getXOnScreen() - mouseX[0],
                    e.getYOnScreen() - mouseY[0]
                );
            }
        });


        topBar.add(buttonPanel, BorderLayout.EAST);
        frame.add(topBar, BorderLayout.NORTH);

        return frame;
    }


    //-----------------------------------------
    // BUTTON STYLE
    //-----------------------------------------
    private static JButton createIconButton(String symbol, Color bgColor) {
        JButton btn = new JButton(symbol);

        btn.setPreferredSize(new Dimension(45, 30));
        btn.setFont(new Font("SansSerif", Font.BOLD, 18));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);

        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(new LineBorder(new Color(30, 30, 30), 1, true));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bgColor.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });

        return btn;
    }
    
    public static boolean deleteLogFile() {
	    File file = new File(Constaints.START_LOG);
	
	    if (!file.exists()) {
	        System.out.println("File not found: " + file.getAbsolutePath());
	        return false;
	    }
	
	    if (file.delete()) {
	        System.out.println("File deleted successfully");
	       
	        return true;
	    } else {
	        System.out.println("Failed to delete file (might be in use)");
	        return false;
	    }
	}
}
