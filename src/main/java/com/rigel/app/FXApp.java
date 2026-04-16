package com.rigel.app;

import java.time.LocalDateTime;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


import org.springframework.context.ConfigurableApplicationContext;

import com.rigel.app.util.Constaints;

import javafx.application.Platform;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class FXApp {

    private static JFrame frame = null;
    private static JFXPanel jfxPanel = null;
    private static boolean fxStarted = false;

    public static synchronized void startBrowser(ConfigurableApplicationContext context,String[] args) {

        System.out.println("startBrowser() called");

        // Already open → bring forward
        if (frame != null) {
            frame.setVisible(true);
            frame.toFront();
            return;
        }

        // Start JavaFX Platform only once
        if (!fxStarted) {
            fxStarted = true;
            try {
                Platform.startup(() -> {});
            } catch (IllegalStateException e) {
                // already running
            }
        }

        // Create Swing frame
        frame = ModernFrame.createModernFrame(context,args);//new JFrame("Dashboard");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        jfxPanel = new JFXPanel();   // creates JavaFX panel
        frame.add(jfxPanel);

        // Load WebView only once
        Platform.runLater(() -> {

            WebView view = new WebView();
            WebEngine engine = view.getEngine();

            String url = "http://127.0.0.1:8088/login";

            engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            LocalDateTime currentDateTime=LocalDateTime.now().minusSeconds(30);
            
            
                if (newState == State.SUCCEEDED) {
                	createLogFile();
                    System.out.println(currentDateTime+": Loaded UI");

                    // Show Swing frame only after page loads
                    SwingUtilities.invokeLater(() -> frame.setVisible(true));

                    // Remove listener (avoid memory leak)
                    obs.removeListener((o) -> {});
                }

                if (newState == State.FAILED) {
                    System.out.println("Load Failed → retry in 1 sec");

                    new Thread(() -> {
                        try { Thread.sleep(500); } catch (Exception ignored) {}
                        Platform.runLater(() -> engine.reload());
                    }).start();
                }
            });

            engine.load(url);
            jfxPanel.setScene(new Scene(view));
        });
    }

    public static void createLogFile() {
    	    try {
                File file = new File(Constaints.START_LOG);

                // Create directory if not exists
                File parent = file.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }

                // Create file + write
                try (FileWriter writer = new FileWriter(file, true)) {
                    writer.write("Loaded UI\n");
                    writer.flush();
                }

                System.out.println("Log file created/updated successfully.");

            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    
}


//package com.app.todoapp;
//
//import javax.swing.JFrame;
//import javafx.application.Platform;
//import javafx.beans.value.ChangeListener;
//import javafx.beans.value.ObservableValue;
//import javafx.concurrent.Worker.State;
//import javafx.embed.swing.JFXPanel;
//import javafx.scene.Scene;
//import javafx.scene.web.WebEngine;
//import javafx.scene.web.WebView;
//
//public class FXApp {
//
//	private static JFrame frame = null; // SINGLE FRAME
//	private static JFXPanel jfxPanel = null; // SINGLE PANEL
//	private static boolean initialized = false;
//
//	public static synchronized void startBrowser() {
//
//		System.out.println("ui loading called");
//
//		// Already created → show same window
//		if (frame != null) {
//			frame.setVisible(true);
//			frame.toFront();
//			return;
//		}
//
//		// Load JavaFX WebView only once
//		if (!initialized) {
//			initialized = true; // Mark initialized
//
//			try {
//				Platform.startup(() -> {
//				});
//			} catch (IllegalStateException e) {
//				// JavaFX already started
//			}
//
//			// Create panel only once
//			if (jfxPanel == null) {
//				jfxPanel = new JFXPanel(); // JavaFX init
//			}
//
//			Platform.runLater(() -> {
//
//				WebView webView = new WebView();
//				WebEngine webEngine = webView.getEngine();
//
//				String url = "http://127.0.0.1:8088/login";
//				// Create listener variable so it can remove itself
//				ChangeListener<State> listener = new ChangeListener<State>() {
//					@Override
//					public void changed(ObservableValue<? extends State> obs, State oldState, State newState) {
//
//						if (newState == State.SUCCEEDED) {
//							System.out.println("Page Loaded Successfully");
//							obs.removeListener(this); // remove listener
//						}
//
//						if (newState == State.FAILED) {
//							System.out.println("Load Failed → Retrying in 2 sec...");
//							// retry after delay
//							new Thread(() -> {
//								try {
//									Thread.sleep(2000);
//								} catch (Exception ignored) {
//								}
//								Platform.runLater(() -> webEngine.reload());
//							}).start();
//						}
//					}
//				};
//
//				webEngine.getLoadWorker().stateProperty().addListener(listener);
//
//				// Load only once
//				webEngine.load(url);
//
//				jfxPanel.setScene(new Scene(webView));
//			});
//		}
//
//		// Create only one JFrame
//		frame = new JFrame("React Browser");
//		frame.setSize(1200, 800);
//		frame.setLocationRelativeTo(null);
//		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		frame.add(jfxPanel);
//		frame.setVisible(true);
//	}
//}
